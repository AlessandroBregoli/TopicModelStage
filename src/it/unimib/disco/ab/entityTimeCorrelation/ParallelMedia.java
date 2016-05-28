package it.unimib.disco.ab.entityTimeCorrelation;

import it.unimib.disco.ab.ner.CustomEntity;
import it.unimib.disco.ab.ner.TopicStat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

public class ParallelMedia {
	private MediaThread[] mediaThread;
	private int nThreads;
	public TreeMap<CustomEntity, TopicStat> relation;
	private ArrayList<CustomEntity> badEntity;
	Iterator keySetIterator;
	public ParallelMedia(int nThreads, TreeMap<CustomEntity, TopicStat> relation, double purity) throws Exception{
		if(nThreads <= 0){
			throw new Exception("I thread  devono essere positivi");
		}
		if(purity < 0 || purity > 1)
			throw new Exception("Purity deve essere un numero compreso tra 0 e 1");
		this.nThreads = nThreads;
		this.mediaThread = new MediaThread[this.nThreads];
		this.relation = relation;
		this.keySetIterator = this.relation.keySet().iterator();
		this.badEntity = new ArrayList<CustomEntity>();
		for(int i = 0; i < this.nThreads; i++){
			this.mediaThread[i] = new MediaThread(this, purity);
			this.mediaThread[i].start();
		}
	
	}
	
	public synchronized CustomEntity next(){
		if(this.keySetIterator.hasNext()){
			return (CustomEntity) this.keySetIterator.next();
		}
		else{
			this.nThreads--;
			if(this.nThreads <= 0){
				notifyAll();
			}
			return null;
		}
	}
	
	public synchronized void addBadEntity(CustomEntity e){
		this.badEntity.add(e);
	}
	
	public synchronized void finalizeWork(){
		while(this.nThreads > 0)
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		for(CustomEntity c:this.badEntity){
			this.relation.remove(c);
		}
	}
}
