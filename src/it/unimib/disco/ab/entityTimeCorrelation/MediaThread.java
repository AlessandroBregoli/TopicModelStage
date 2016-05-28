package it.unimib.disco.ab.entityTimeCorrelation;

import it.unimib.disco.ab.ner.CustomEntity;
import it.unimib.disco.ab.ner.TopicStat;

public class MediaThread extends Thread{
	ParallelMedia p;
	double purity;
	public MediaThread(ParallelMedia p, double purity){
		this.p = p;
		this.purity = purity;
	}
	
	public void run(){
		while(true){
			CustomEntity c = this.p.next();
			if(c == null)
				break;
			TopicStat ts = this.p.relation.get(c);
			if(ts.getCalculatedMean()[ts.getBestTopic()] < this.purity)
				this.p.addBadEntity(c);
			
		}
	}
}
