package it.unimib.disco.ab.ner;

import it.unimib.disco.ab.textPreprocessing.SentenceContainer;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeMap;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

public class ParallelNer {
	private int nThread;
	AbstractSequenceClassifier<CoreLabel>[] classifier;
	SentenceContainer sentences;
	TreeMap<CustomEntity, LinkedList<Long>> entities;
	Iterator iter;
	private boolean entitiesLights;
	public ParallelNer(String serializedClassifier[], SentenceContainer sentences) throws ClassCastException, ClassNotFoundException, IOException{
		this.classifier = new AbstractSequenceClassifier[serializedClassifier.length];
		this.nThread = nThread;
		for(int i = 0; i < serializedClassifier.length; i++){
			this.classifier[i] = CRFClassifier.getClassifier(serializedClassifier[i]);
		}
		this.sentences = sentences;
		this.entities = new TreeMap<CustomEntity, LinkedList<Long>>();
		
	}
	public synchronized TreeMap<CustomEntity, LinkedList<Long>> getEntities(int nThreads){
		this.nThread = nThread;
		this.iter = this.sentences.sentences.keySet().iterator();
		while(this.nThread > 0 ){
			try {
				wait();
			} catch (InterruptedException e) {}
		}
		return this.entities;
	}
	
	public synchronized long getSentenceId(){
		if(!this.iter.hasNext()){
			this.nThread--;
			notifyAll();
			return -1;
		}
		return (long) this.iter.next();
	}
	public synchronized void sentencesAcquisition(){
		while(this.entitiesLights){
			try {
				wait();
			} catch (InterruptedException e) {}
		}
		this.entitiesLights = true;
	}
	
	public synchronized void sentencesRelease(){
		this.entitiesLights = false;
		notifyAll();
	}
	
}
