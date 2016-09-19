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
			System.out.println("Inserire i seguenti parametri: [numero Thread] [numero topics] [Cartella dataset] [file prener stopword] [file stopword] [Files ner]");
				return;
		}
		int nThreads = Integer.parseInt(args[0]);
		int nTopic = Integer.parseInt(args[1]);
		String datasetFolder = args[2];
		String prenerStopWordFile = args[3];
		String stopWordFile = args[4];
		String[] serialNer = new String[args.length - 5];
		for(int i = 0; i  < serialNer.length; i++){
			serialNer[i] = args[5 + i]; 
		}
		WorkflowTextAnalysis.startWorkflow(nThreads, nTopic, datasetFolder, prenerStopWordFile, stopWordFile, serialNer, true);
		
	}
}
