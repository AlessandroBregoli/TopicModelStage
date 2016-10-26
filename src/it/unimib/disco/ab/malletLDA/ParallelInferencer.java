package it.unimib.disco.ab.malletLDA;

import it.unimib.disco.ab.textPreprocessing.SentenceContainer;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeMap;

import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
//Questo è il monitor della classe ParallelInferencerThread
public class ParallelInferencer {
	private int nThreads;
	SentenceTopicRelation senteceTopicRelation;
	TopicInferencer inferencer;
	InstanceList sentences;
	Iterator<Instance> iter;
	double purity;
	public ParallelInferencer(TopicInferencer inferencer, SentenceContainer sentences, double purity){
		this.senteceTopicRelation = new SentenceTopicRelation();
		this.senteceTopicRelation.senteceTopicRelation = new TreeMap<Long, Integer>();
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
	
	public synchronized SentenceTopicRelation getSenteceTopicRelation(int nThreads){
		this.nThreads = nThreads;
		this.iter = this.sentences.iterator();
		for(int i = this.nThreads; i > 0; i--){
			new ParallelInferencerThread(this).start();
		}
		while(this.nThreads > 0){
			try {
				wait();
			} catch (InterruptedException e) {e.printStackTrace();}
		}
		return this.senteceTopicRelation;
	}
	synchronized void addSenteceTopic(long sentenceID, int topic, int nTopics){
		this.senteceTopicRelation.senteceTopicRelation.put(sentenceID, topic);
		if(this.senteceTopicRelation.sentencePerTopic == null){
			this.senteceTopicRelation.sentencePerTopic = new long[nTopics];
			Arrays.fill(this.senteceTopicRelation.sentencePerTopic, 0);
		}
		this.senteceTopicRelation.sentencePerTopic[topic]++;
	}
}
