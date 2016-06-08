package it.unimib.disco.ab.entityTimeCorrelation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.TreeMap;

//This class is a container for the data relation between entities and topics 
//TODO Si potrebbero implementare le eccezioni nel caso in cui le dimensioni delle statistiche delle sentence non combaciassero 

public class TopicStat implements Serializable{
	
	private TreeMap<Integer, ArrayList<TopicStatTuple>> data;
	private double[] mean;
	private final int nTopics;
	private int bestTopic;
	private boolean updated;
	public TopicStat(double[] stat, Date date, long sentenceID, int sentenceBestTopic){
		this.data = new TreeMap<Integer, ArrayList<TopicStatTuple>>();
		TopicStatTuple t = new TopicStatTuple();
		t.stat = stat;
		t.date = date;
		t.sentenceID = sentenceID;
		ArrayList<TopicStatTuple> tmp = new ArrayList<TopicStatTuple>();
		tmp.add(t);
		this.data.put(sentenceBestTopic, tmp);
		this.mean = null;
		this.bestTopic = -1;
		this.mean = new double[stat.length];
		this.nTopics = stat.length;
		this.updated = true;
	}
	
	public void add(double[] stat, Date date, long sentenceID, int sentenceBestTopic){
		TopicStatTuple t = new TopicStatTuple();
		t.stat = stat;
		t.date = date;
		t.sentenceID = sentenceID;
		ArrayList<TopicStatTuple> tmp = this.data.get(sentenceBestTopic);
		if(tmp == null){
			tmp = new ArrayList<TopicStatTuple>();
			this.data.put(sentenceBestTopic, tmp);
		}
		tmp.add(t);
		this.updated = true;
	}
	
	private void calcMean(){	
		this.bestTopic = 0;
		int size = this.getNumEl();
		for(int key: this.data.keySet())
			for( TopicStatTuple d: this.data.get(key))
				for(int i = 0; i < d.stat.length; i++){
					this.mean[i] += d.stat[i]/size;
				}
		
		
	}
	//Questa cosa si potrebbe rendere più efficiente se si calcolasse il bestTopic in calcMean
	//ma non così tanto
	public double[] getMean(){
		if(this.updated){
			this.getBestTopic();
			this.updated = false;
		}
		
			return this.mean;
	}
	public int getBestTopic(){
		if(this.updated){
			this.calcMean();
			this.bestTopic = 0;
			for(int i = 1; i < this.mean.length; i++)
				if(this.mean[i] > this.mean[this.bestTopic])
					this.bestTopic = i;
			this.updated = false;
		}
		return this.bestTopic;
	}
	public int getNumEl(){
		int size = 0;
		for(int i:this.data.keySet()){
			size = size + this.data.get(i).size();
		}
		return size;
	}

	public int getNTopics(){
		return this.nTopics;
	}

	public TreeMap<Integer, ArrayList<TopicStatTuple>> getData() {
		return data;
	}
	
}

