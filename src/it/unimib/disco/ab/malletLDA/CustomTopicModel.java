package it.unimib.disco.ab.malletLDA;

import java.io.File;
import java.io.IOException;

import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.InstanceList;


public class CustomTopicModel {
	private String dataSet;
	private int nTopics;
	public ParallelTopicModel model;
	public CustomTopicModel(String dataSet){
		this.dataSet = dataSet;
	}
	
	public void modella(int nTopics, File stopWordFile){
		this.nTopics = nTopics;
		InstanceList instances = InstancesBuilder.getInstances(this.dataSet, stopWordFile);
		
		double alpha = 1.0;
		double beta = 0.01;
		this.model = new ParallelTopicModel(nTopics, alpha, beta);
		this.model.setNumThreads(4);
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
