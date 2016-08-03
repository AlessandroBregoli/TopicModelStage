package it.unimib.disco.ab.workflow;

import it.unimib.disco.ab.graphs.StaticGraphGenerator;
import it.unimib.disco.ab.malletLDA.CustomTopicModel;
import it.unimib.disco.ab.malletLDA.ParallelInferencer;
import it.unimib.disco.ab.malletLDA.SentenceTopicRelation;
import it.unimib.disco.ab.ner.CustomEntity;
import it.unimib.disco.ab.ner.EntitySetIterator;
import it.unimib.disco.ab.ner.ParallelNer;
import it.unimib.disco.ab.textPreprocessing.SentenceContainer;
import it.unimib.disco.ab.textPreprocessing.SentenceSplitter;
import it.unimib.disco.ab.xmlParser.DirectoryScanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.TreeMap;

public class WorkflowForServer {
	public static void main(String[] args) throws Exception{
		if(args.length < 5){
			System.out.println("Inserire i seguenti parametri: [numero Thread] [numero topics] [Cartella dataset] [file stopword] [Files ner]");
				return;
		}
		int nThreads = Integer.parseInt(args[0]);
		int nTopic = Integer.parseInt(args[1]);
		System.out.println("Loading xml");
		DirectoryScanner ds = new DirectoryScanner(new File(args[2]));
		ds.startScan();
		System.out.println("Splitting sentences");
		SentenceContainer sc = new SentenceSplitter(ds.getArticles());
		SentenceContainer scCopy = (SentenceContainer) sc.clone();
		String[] serialNer = new String[args.length - 4];
		for(int i = 0; i  < serialNer.length; i++){
			serialNer[i] = args[4 + i]; 
		}
		System.out.println("Using ner");
		TreeMap<CustomEntity, LinkedList<Long>> entities = null;
		try {
			ParallelNer pn = new ParallelNer(serialNer, sc);
			entities = pn.getEntities(nThreads);
		} catch (ClassCastException | ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		System.out.println("Loading stop-words");
		File sw = new File(args[3]);

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
		sc.filterUsingIterator(stopWords.iterator(), nThreads);
		System.out.println("Filtering sentences from eitities");
		EntitySetIterator esi = new EntitySetIterator(entities.keySet());
		sc.filterUsingIterator(esi, nThreads);;

		/*Iterator<CustomEntity> it =  entities.keySet().iterator();
		while(it.hasNext()){
			CustomEntity ce = it.next();
			System.out.println(ce.entityString);
		}*/
		System.out.println("Using LDA");
		CustomTopicModel ctm = new CustomTopicModel(sc);
		ctm.modella(nTopic, nThreads);
		System.out.println("Using inferencer");
		ParallelInferencer pi = new ParallelInferencer(ctm.getInferencer(), sc,0);
		SentenceTopicRelation str = pi.getSenteceTopicRelation(nThreads);
		System.out.println("Using static graph generator");
		StaticGraphGenerator sgg = new StaticGraphGenerator(entities, str, ctm.getNTopics());
		sgg.waitUntillEnd(nThreads);
		/*System.out.println("Using static graph analyzer");
		StaticGraphAnalyzer sga = new StaticGraphAnalyzer(scCopy, entities, str, sgg.graphs, 2);
		sga.analizeGraph(nThreads);*/
	}
}
