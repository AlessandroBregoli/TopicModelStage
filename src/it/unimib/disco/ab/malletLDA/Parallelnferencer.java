package it.unimib.disco.ab.malletLDA;

import it.unimib.disco.ab.textPreprocessing.SentenceContainer;

import java.util.Iterator;
import java.util.TreeMap;

import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

public class Parallelnferencer {
	private int nThreads;
	TreeMap<Long,Integer> senteceTopicRelation;
	TopicInferencer inferencer;
	InstanceList sentences;
	Iterator<Instance> iter;
	double purity;
	public Parallelnferencer(TopicInferencer inferencer, SentenceContainer sentences, double purity){
		this.senteceTopicRelation = new TreeMap<Long, Integer>();
		this.inferencer = inferencer;
		this.sentences = InstancesBuilder.getInstances(sentences);
		this.purity = purity;
	}
	
	synchronized Instance getSentenceId(){
		if(!this.iter.hasNext()){
			this.nThreads--;
			notifyAll();
			return null;
		}
		return this.iter.next();
	}
	
	public synchronized TreeMap<Long,Integer> getSenteceTopicRelation(int nThreads){
		this.nThreads = nThreads;
		this.iter = this.sentences.iterator();
		for(int i = this.nThreads; i > 0; i--){
			new ParallelnferencerThread(this).start();
		}
		while(this.nThreads > 0){
			try {
				wait();
			} catch (InterruptedException e) {e.printStackTrace();}
		}
		return this.senteceTopicRelation;
	}
	synchronized void addSenteceTopic(long sentenceID, int topic){
		this.senteceTopicRelation.put(sentenceID, topic);
	}
}
