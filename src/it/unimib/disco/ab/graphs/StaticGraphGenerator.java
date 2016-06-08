package it.unimib.disco.ab.graphs;

import it.unimib.disco.ab.entityTimeCorrelation.TopicStat;
import it.unimib.disco.ab.ner.CustomEntity;

import java.util.TreeMap;

public class StaticGraphGenerator {
	TreeMap<CustomEntity, TopicStat> relation;
	private int nThreads;
	private StaticGraphGeneratorThread[] thread;
	private int topicIndex;
	public EntityTopicGraph[] graphs;
	public StaticGraphGenerator(TreeMap<CustomEntity, TopicStat> relation, int nThreads) throws Exception{
		this.relation = relation;
		if(nThreads <= 0){
			throw new Exception("I thread  devono essere positivi");
		}
		this.nThreads = nThreads;
		this.thread = new StaticGraphGeneratorThread[this.nThreads];
		this.graphs = new EntityTopicGraph[this.relation.get(this.relation.firstKey()).getNTopics()];
		this.topicIndex = 0;
		for(int i = 0; i < this.nThreads; i++){
			this.thread[i] = new StaticGraphGeneratorThread(this);
			this.thread[i].start();
			
		}
		
	}
	
	/*
	 * attende che la computazione sia completata
	 * Si potrebe fare un metodo non bloccante ma tanto è probabile che si voglia usare il 100% della CPU
	 * per completare questo processo alla massima velocità
	 */
	public synchronized void waitUntillEnd(){
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
	
}
