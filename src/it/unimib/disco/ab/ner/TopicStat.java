package it.unimib.disco.ab.ner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

//This class is a container for the data relation between entities and topics 
public class TopicStat implements Serializable{
	
	private ArrayList<double[]> stats;
	private ArrayList<Date> date;
	public TopicStat(double[] stat, Date date){
		this.stats = new ArrayList<double[]>();
		this.stats.add(stat);
		this.date = new ArrayList<Date>();
		this.date.add(date);
	}
	
	public void add(double[] stat, Date date){
		this.stats.add(stat);
		this.date.add(date);
	}
	
	public double[] mean(){
		double ret[] = new double[this.stats.get(0).length];
		for( double d[]: this.stats)
			for(int i = 0; i < d.length; i++){
				ret[i] += d[i]/stats.size();
			}
		return ret;
	}
	public int getNumEl(){
		return this.stats.size();
	}
}
