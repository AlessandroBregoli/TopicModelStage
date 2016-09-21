package it.unimib.disco.ab.malletLDA;

import it.unimib.disco.ab.textPreprocessing.SentenceContainer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Alphabet;
import cc.mallet.types.IDSorter;
import cc.mallet.types.InstanceList;


public class CustomTopicModel {
	private SentenceContainer dataSet;
	public ParallelTopicModel model;
	private int nTopics;
	private InstanceList instances;
	public CustomTopicModel(SentenceContainer dataSet){
		this.dataSet = dataSet;
	}
	
	public void modella(int nTopics, int nThreads){
		InstanceList instances = InstancesBuilder.getInstances(this.dataSet);
		this.nTopics = nTopics;
		double alpha = 0.0001* nTopics;
		double beta = 0.01;
		this.model = new ParallelTopicModel(nTopics, alpha, beta);
		this.model.setSymmetricAlpha(true);
		this.model.setNumThreads(nThreads);
		this.model.optimizeInterval = 0;
		this.model.setNumIterations(1000);
		this.model.addInstances(instances);	
		try {
			this.model.estimate();
		} catch (IOException e) {}
		this.instances = instances;
	}
	
	public int findBestNTopics(int minNTopics, int maxNTopics, int nThreads){
		int max = 0;
		double maxPerplexity = 0.0;
		for(int i = minNTopics; i < maxNTopics; i++){
			this.modella(i, nThreads);
			int tmp = 0;
			for(int j = 0; j < this.model.tokensPerTopic.length; j++){
				tmp += this.model.tokensPerTopic[j];
			}
			double perplexity = Math.exp(-this.model.modelLogLikelihood()/tmp);
			if( perplexity > maxPerplexity){
				maxPerplexity = perplexity;
				max = i;
			}
			
		}
		System.out.println("Perplexity: " + maxPerplexity);
		System.out.println("Number of topics: " + max);
		return max;
	}
	//TODO In rank dovrebbe essere un parametro e non hard coded
	public void serializeTopicsWordForJSON(String path) throws IOException{
		JSONObject root = new JSONObject();
		ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
		Alphabet dataAlphabet = this.instances.getDataAlphabet();
		for(int i = 0; i < this.model.numTopics; i++){
			JSONArray topic = new JSONArray();
			Iterator<IDSorter> iterator = topicSortedWords.get(i).iterator();
			int rank = 0;
			while(iterator.hasNext() && rank < 10){
				 IDSorter idCountPair = iterator.next();
				 topic.add(dataAlphabet.lookupObject(idCountPair.getID()));
				 rank++;
			}
			root.put(i, topic);
		}
		File f = new File(path);
		FileWriter fw = new FileWriter(f);
		fw.write(root.toJSONString());
		fw.close();
	}
	public TopicInferencer getInferencer(){
		return this.model.getInferencer();
	}
	public int getNTopics(){
		return this.nTopics;
	}

}
