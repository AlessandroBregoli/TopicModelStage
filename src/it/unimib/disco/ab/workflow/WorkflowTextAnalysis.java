package it.unimib.disco.ab.workflow;

import it.unimib.disco.ab.datasetParser.DirectoryScanner;
import it.unimib.disco.ab.datasetParser.DirectoryScannerForBBC;
import it.unimib.disco.ab.graphs.StaticGraphGenerator;
import it.unimib.disco.ab.malletLDA.CustomTopicModel;
import it.unimib.disco.ab.malletLDA.ParallelInferencer;
import it.unimib.disco.ab.malletLDA.SentenceTopicRelation;
import it.unimib.disco.ab.ner.CustomEntity;
import it.unimib.disco.ab.ner.EntitySetIterator;
import it.unimib.disco.ab.ner.ParallelNer;
import it.unimib.disco.ab.textPreprocessing.SentenceContainer;
import it.unimib.disco.ab.textPreprocessing.SentenceSplitter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.TreeMap;

public class WorkflowTextAnalysis {
	String datasetFolder;
	String preNerStopWordFile;
	int nThreads;
	String[] serialNer;
	String stopWordFile;
	int minNumberOfTopics;
	boolean perplexityAnalysis;
	int maxNumberOfTopics;
	int nTopics;
	boolean serializeTopicsWordForJSON;	

/*
	public static void main(String[] args) throws Exception{
		String[] serialNer = {
				"/home/alessandro/Schifezze/stanford-ner-2015-12-09/classifiers/english.all.3class.distsim.crf.ser.gz",
				"/home/alessandro/Schifezze/stanford-ner-2015-12-09/classifiers/english.conll.4class.distsim.crf.ser.gz",
				"/home/alessandro/Schifezze/stanford-ner-2015-12-09/classifiers/english.muc.7class.distsim.crf.ser.gz"
		};
		WorkflowTextAnalysis.startWorkflow(3, 30, "/home/alessandro/MEGAsync/Stage/Dataset/miniset", "/home/alessandro/MEGAsync/Stage/Stoplist/en-preNer.txt", "/home/alessandro/MEGAsync/Stage/Stoplist/en.txt", serialNer, true, false, 0, 0);
	}*/
	public void startWorkflow() throws Exception{
		
		System.out.println("Loading xml");
		DirectoryScanner ds = new DirectoryScannerForBBC(new File(datasetFolder));
		ds.startScan();
		System.out.println("Splitting sentences");
		SentenceContainer sc = new SentenceSplitter(ds.getArticles());
		SentenceContainer scCopy = (SentenceContainer) sc.clone();
		if(preNerStopWordFile != null){
			System.out.println("Loading pre-ner stopwords");
			File sw = new File(preNerStopWordFile);
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
			System.out.println("Filtering sentences from pre-ner stopwords");
			sc.filterUsingIterator(stopWords.iterator(), nThreads);
		}
		System.out.println("Using ner");
		TreeMap<CustomEntity, LinkedList<Long>> entities = null;
		try {
			ParallelNer pn = new ParallelNer(serialNer, sc);
			entities = pn.getEntities(nThreads);
		} catch (ClassCastException | ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		System.out.println("Filtering sentences from eitities");
		EntitySetIterator esi = new EntitySetIterator(entities.keySet());
		sc.filterUsingIterator(esi, nThreads);
		if(stopWordFile != null){
			System.out.println("Loading stop-words");
			File sw = new File(stopWordFile);
	
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
		}

		/*Iterator<CustomEntity> it =  entities.keySet().iterator();
		while(it.hasNext()){
			CustomEntity ce = it.next();
			System.out.println(ce.entityString);
		}*/

		CustomTopicModel ctm = new CustomTopicModel(sc);
		if(this.perplexityAnalysis){
			System.out.println("Using perplexity analisis");
			nTopics = ctm.findBestNTopics(minNumberOfTopics, maxNumberOfTopics, nThreads);
			
		}
		System.out.println("Using LDA");
		ctm.modella(nTopics, nThreads);
		if(serializeTopicsWordForJSON){
			ctm.serializeTopicsWordForJSON("topics.json");
		}
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
