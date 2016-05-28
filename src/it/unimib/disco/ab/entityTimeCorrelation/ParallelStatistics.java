package it.unimib.disco.ab.entityTimeCorrelation;

import it.unimib.disco.ab.ner.CustomEntity;
import it.unimib.disco.ab.ner.TopicStat;

import java.util.TreeMap;

public class ParallelStatistics extends Thread{
	private int nThreads;
	//La purezza è un valore tra 0 e 1 ed indica la probabilità media minima per cui consideriamo un'entità
	private double purity;
	private TreeMap<CustomEntity, TopicStat> relation;
	public ParallelStatistics(int nThreads, double purity,TreeMap<CustomEntity, TopicStat> relation) throws Exception{
		if(nThreads <= 0){
			throw new Exception("I thread  devono essere positivi");
		}
		this.nThreads = nThreads; 
		if(purity < 0 || purity > 1)
			throw new Exception("Purity deve essere un numero compreso tra 0 e 1");
		this.purity = purity;
		this.relation = relation;
		
	}
	public void run(){
		ParallelMedia pm;
		try {
			pm = new ParallelMedia(this.nThreads, this.relation, this.purity);
			pm.finalizeWork();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
