package it.unimib.disco.ab.graphs;

import it.unimib.disco.ab.malletLDA.SentenceTopicRelation;
import it.unimib.disco.ab.ner.CustomEntity;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeMap;

//Questa classe serve per generare i grafi; può utilizzare uno o più thread
//La generazione si svolge in due fasi una di generazione; una di inizializzazione
//(StaticGraphInitializerThread) e una di effettiva generazione (StaticGraphGeneratorThread) 
public class StaticGraphGenerator {
	private int nThreads;
	private int topicIndex;
	public EntityTopicGraph[] graphs;
	TreeMap<CustomEntity, LinkedList<Long>> entities;
	SentenceTopicRelation sentenceTopicRelation;
	int nTopics;
	Iterator<CustomEntity> entityIterator;
	public StaticGraphGenerator(TreeMap<CustomEntity, LinkedList<Long>> entities, SentenceTopicRelation topicSentenceRelation, int nTopics) throws Exception{
		this.entities = entities;
		this.nTopics = nTopics;
		this.sentenceTopicRelation = topicSentenceRelation;
		this.nThreads = nThreads;
		this.graphs = new EntityTopicGraph[this.nTopics];
		for(int topic = 0; topic < this.nTopics; topic++)
			this.graphs[topic] = new EntityTopicGraph(topic);
		
		
	}
	
	/*
	 * attende che la computazione sia completata
	 * Si potrebe fare un metodo non bloccante ma tanto è probabile che si voglia usare il 100% della CPU
	 * per completare questo processo alla massima velocità
	 */
	public synchronized void waitUntillEnd(int nThreads){
		this.nThreads = nThreads;
		this.topicIndex = 0;
		this.entityIterator = this.entities.keySet().iterator();
		for(int i = nThreads; i > 0; i--){
			new StaticGraphInitializerThread(this).start();
			
		}
		while(this.nThreads > 0)
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		this.nThreads = nThreads;
		for(int i = this.nThreads; i > 0; i--){
			new StaticGraphGeneratorThread(this).start();
			
		}
		while(this.nThreads > 0)
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}
	
	//Dato che gli indici di un array così come gli  indici topics sono numeri interi positivi
	//mi riservo il -1 come carattere per terminare i thread alla fine del processo;
	public synchronized int nextTopic(){
		if(this.topicIndex < this.graphs.length){
			System.err.println("Served topic" + this.topicIndex);
			return this.topicIndex++;
		}else{
			this.nThreads--;
			notifyAll();
			return -1;
		}
	}
	
	synchronized CustomEntity nextEntity(){
		if(!this.entityIterator.hasNext()){
			this.nThreads--;
			notifyAll();
			return null;
		}
		return this.entityIterator.next();
		
	}
	synchronized void updateVertexDictionary(CustomEntity ce, int topic){
		try {
			this.graphs[topic].addVertex(ce);
		} catch (Exception e) {	e.printStackTrace();}
	}
	
}
