package it.unimib.disco.ab.entityTimeCorrelation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

//This class is a container for the data relation between entities and topics 
public class TopicStat implements Serializable{
	
	private ArrayList<TopicStatTuple> data;
	private double[] mean;
	private int bestTopic;
	private boolean updated;
	public TopicStat(double[] stat, Date date){
		this.data = new ArrayList<TopicStatTuple>();
		TopicStatTuple t = new TopicStatTuple();
		t.stat = stat;
		t.date = date;
		this.data.add(t);
		this.mean = null;
		this.bestTopic = -1;
		this.updated = true;
	}
	
	public void add(double[] stat, Date date){
		TopicStatTuple t = new TopicStatTuple();
		t.stat = stat;
		t.date = date;
		this.data.add(t);
		this.updated = true;
	}
	
	private void calcMean(){
		this.mean = new double[this.data.get(0).stat.length];
		this.bestTopic = 0;
		for( TopicStatTuple d: this.data)
			for(int i = 0; i < d.stat.length; i++){
				this.mean[i] += d.stat[i]/data.size();
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
		return this.data.size();
	}
}
