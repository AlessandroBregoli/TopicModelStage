package it.unimib.disco.ab.graphs;

import java.util.ArrayList;

import it.unimib.disco.ab.entityTopicStatistics.NerStats;

public class StaticGraphAnalyzer {
	NerStats entityTopicRelation;
	EntityTopicGraph[] graphs;
	private int nThreads;
	private int analizedTopic;
	ConnectedComponentSentences ccSentences[];
	int minCCSize;
	public StaticGraphAnalyzer(NerStats entityTopicRelation, EntityTopicGraph[] graphs, int minCCSize){
		this.entityTopicRelation = entityTopicRelation;
		this.graphs = graphs;
		this.ccSentences = new ConnectedComponentSentences[this.graphs.length];
		this.minCCSize = minCCSize;
	}
	
	public synchronized void analizeGraph(int nThreads){
		this.nThreads = nThreads > 0 ? nThreads : 1;
		this.analizedTopic = 0;
		for(int i = 0; i < this.nThreads ; i++){
			new StaticGraphAnalyzerThread(this).start();
		}
		while(this.nThreads > 0){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	//Dato che gli indici di un array così come gli  indici topics sono numeri interi positivi
	//mi riservo il -1 come carattere per terminare i thread alla fine del processo;
	public synchronized int getTopic(){
		if( this.analizedTopic < this.graphs.length){
			return this.analizedTopic++;
		}
		this.nThreads--;
		notifyAll();
		return -1;
	}
	
	
}
