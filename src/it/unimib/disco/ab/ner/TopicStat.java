package it.unimib.disco.ab.ner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

//This class is a container for the data relation between entities and topics 
public class TopicStat implements Serializable{
	
	private ArrayList<double[]> stats;
	private ArrayList<Date> date;
	private double[] mean;
	private int bestTopic;
	public TopicStat(double[] stat, Date date){
		this.stats = new ArrayList<double[]>();
		this.stats.add(stat);
		this.date = new ArrayList<Date>();
		this.date.add(date);
		this.mean = null;
		this.bestTopic = -1;
	}
	
	public void add(double[] stat, Date date){
		this.stats.add(stat);
		this.date.add(date);
	}
	
	public double[] getMean(){
		this.mean = new double[this.stats.get(0).length];
		for( double d[]: this.stats)
			for(int i = 0; i < d.length; i++){
				this.mean[i] += d[i]/stats.size();
			}
		return this.mean;
	}
	public double[] getCalculatedMean(){
		if(this.mean == null)
			return this.getMean();
		else
			return this.mean;
	}
	public int getBestTopic(){
		if(this.bestTopic == -1)
			this.getCalculatedMean();
			this.bestTopic = 0;
			for(int i = 1; i < this.mean.length; i++)
				if(this.mean[i] > this.mean[this.bestTopic])
					this.bestTopic = i;
		return this.bestTopic;
	}
	public int getNumEl(){
		return this.stats.size();
	}
}
