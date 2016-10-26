package it.unimib.disco.ab.graphs;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeMap;

import it.unimib.disco.ab.malletLDA.SentenceTopicRelation;
import it.unimib.disco.ab.ner.CustomEntity;
import it.unimib.disco.ab.textPreprocessing.SentenceContainer;

//Questa classe viene utilizzata come monitor dalla classe StaticGraphAnalyzerThread
//E server per fare il filtraggio dei grafi.
//Ogni topic, e quindi ogni grafo, è indipendente dagli altri dunque è possibile filtrarli separatamente
//utilizzando diversi tread.
public class StaticGraphAnalyzer {
	SentenceContainer sentences;
	EntityTopicGraph[] graphs;
	private int nThreads;
	private int analizedTopic;
	ConnectedComponentSentences ccSentences[];
	int minCCSize;
	TreeMap<CustomEntity, LinkedList<Long>> entities;
	SentenceTopicRelation str;
	public StaticGraphAnalyzer(SentenceContainer sentences, TreeMap<CustomEntity, LinkedList<Long>> entities, SentenceTopicRelation str, EntityTopicGraph[] graphs, int minCCSize){
		this.sentences = sentences;
		this.graphs = graphs;
		this.ccSentences = new ConnectedComponentSentences[this.graphs.length];
		this.minCCSize = minCCSize;
		this.entities = entities;
		this.str = str;
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
