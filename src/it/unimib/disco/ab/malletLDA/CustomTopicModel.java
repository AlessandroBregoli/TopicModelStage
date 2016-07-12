package it.unimib.disco.ab.malletLDA;

import it.unimib.disco.ab.textPreprocessing.SentenceContainer;

import java.io.File;
import java.io.IOException;

import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.InstanceList;


public class CustomTopicModel {
	private SentenceContainer dataSet;
	private int nTopics;
	public ParallelTopicModel model;
	public CustomTopicModel(SentenceContainer dataSet){
		this.dataSet = dataSet;
	}
	
	public void modella(int nTopics){
		this.nTopics = nTopics;
		InstanceList instances = InstancesBuilder.getInstances(this.dataSet);
		
		double alpha = 0.001* nTopics;
		double beta = 0.01;
		this.model = new ParallelTopicModel(nTopics, alpha, beta);
		this.model.setSymmetricAlpha(true);
		this.model.setNumThreads(4);
		this.model.optimizeInterval = 0;
		this.model.setNumIterations(1000);
		this.model.addInstances(instances);	
		try {
			this.model.estimate();
		} catch (IOException e) {}
	}
	
	public TopicInferencer getInferencer(){
		return this.model.getInferencer();
	}

}
