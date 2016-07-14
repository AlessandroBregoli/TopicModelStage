package it.unimib.ab.workflow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import it.unimib.disco.ab.ner.CustomEntity;
import it.unimib.disco.ab.ner.ParallelNer;
import it.unimib.disco.ab.textPreprocessing.SentenceContainer;
import it.unimib.disco.ab.textPreprocessing.SentenceSplitter;
import it.unimib.disco.ab.xmlParser.DirectoryScanner;

public class Workflow {
	public static void main(String[] args){
		DirectoryScanner ds = new DirectoryScanner(new File("/home/alessandro/Dropbox/Stage/Dataset/reuters toXml"));
		ds.startScan();
		
		SentenceContainer sc = new SentenceSplitter(ds.getArticles());
		File sw = new File("/home/alessandro/Schifezze/mallet-2.0.7/stoplists/en.txt");

		LinkedList<String> stopWords= new LinkedList<String>();
		try{
		FileReader fr = new FileReader(sw);
		BufferedReader br = new BufferedReader(fr);
		
		String stopword;
		while((stopword = br.readLine())!=null){
			stopWords.add(stopword);
		}
		br.close();
		fr.close();
		}catch(Exception e){}
		sc.filterUsingList(stopWords);
		
		String[] serialNer = {
				"/home/alessandro/Schifezze/stanford-ner-2015-12-09/classifiers/english.all.3class.distsim.crf.ser.gz",
				"/home/alessandro/Schifezze/stanford-ner-2015-12-09/classifiers/english.conll.4class.distsim.crf.ser.gz",
				"/home/alessandro/Schifezze/stanford-ner-2015-12-09/classifiers/english.muc.7class.distsim.crf.ser.gz"
		};
		TreeMap<CustomEntity, LinkedList<Long>> entities = null;
		try {
			ParallelNer pn = new ParallelNer(serialNer, sc);
			entities = pn.getEntities(4);
		} catch (ClassCastException | ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		Iterator<CustomEntity> it =  entities.keySet().iterator();
		while(it.hasNext()){
			CustomEntity ce = it.next();
			System.out.println(ce.entityString);
		}
	}

}
