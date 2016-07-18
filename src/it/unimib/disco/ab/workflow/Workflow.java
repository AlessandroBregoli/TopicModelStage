package it.unimib.disco.ab.workflow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import it.unimib.disco.ab.graphs.StaticGraphAnalyzer;
import it.unimib.disco.ab.graphs.StaticGraphGenerator;
import it.unimib.disco.ab.malletLDA.CustomTopicModel;
import it.unimib.disco.ab.malletLDA.ParallelInferencer;
import it.unimib.disco.ab.malletLDA.SentenceTopicRelation;
import it.unimib.disco.ab.ner.CustomEntity;
import it.unimib.disco.ab.ner.ParallelNer;
import it.unimib.disco.ab.textPreprocessing.SentenceContainer;
import it.unimib.disco.ab.textPreprocessing.SentenceSplitter;
import it.unimib.disco.ab.xmlParser.DirectoryScanner;

public class Workflow {
	public static void main(String[] args) throws Exception{
		int nThreads = 3;
		System.out.println("Loading xml");
		DirectoryScanner ds = new DirectoryScanner(new File("/home/alessandro/Dropbox/Stage/Dataset/reuters toXml"));
		ds.startScan();
		System.out.println("Splitting sentences");
		SentenceContainer sc = new SentenceSplitter(ds.getArticles());
		SentenceContainer scCopy = (SentenceContainer) sc.clone();
		System.out.println("Loading stop-words");
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
		System.out.println("Filtering sentences from stop-words");
		sc.filterUsingList(stopWords);
		String[] serialNer = {
				"/home/alessandro/Schifezze/stanford-ner-2015-12-09/classifiers/english.all.3class.distsim.crf.ser.gz",
				"/home/alessandro/Schifezze/stanford-ner-2015-12-09/classifiers/english.conll.4class.distsim.crf.ser.gz",
				"/home/alessandro/Schifezze/stanford-ner-2015-12-09/classifiers/english.muc.7class.distsim.crf.ser.gz"
		};
		System.out.println("Using ner");
		TreeMap<CustomEntity, LinkedList<Long>> entities = null;
		try {
			ParallelNer pn = new ParallelNer(serialNer, sc);
			entities = pn.getEntities(nThreads);
		} catch (ClassCastException | ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		/*Iterator<CustomEntity> it =  entities.keySet().iterator();
		while(it.hasNext()){
			CustomEntity ce = it.next();
			System.out.println(ce.entityString);
		}*/
		System.out.println("Using LDA");
		CustomTopicModel ctm = new CustomTopicModel(sc);
		ctm.modella(50, nThreads);
		System.out.println("Using inferencer");
		ParallelInferencer pi = new ParallelInferencer(ctm.getInferencer(), sc,(double)1/50);
		SentenceTopicRelation str = pi.getSenteceTopicRelation(nThreads);
		System.out.println("Using static graph generator");
		StaticGraphGenerator sgg = new StaticGraphGenerator(entities, str, ctm.getNTopics());
		sgg.waitUntillEnd(nThreads);
		System.out.println("Using static graph analyzer");
		StaticGraphAnalyzer sga = new StaticGraphAnalyzer(scCopy, entities, str, sgg.graphs, 2);
		sga.analizeGraph(nThreads);
	}

}
