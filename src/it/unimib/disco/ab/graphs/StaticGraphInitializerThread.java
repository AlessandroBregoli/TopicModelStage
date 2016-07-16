package it.unimib.disco.ab.graphs;

import it.unimib.disco.ab.ner.CustomEntity;

public class StaticGraphInitializerThread extends Thread{
	private StaticGraphGenerator monitor;
	public StaticGraphInitializerThread(StaticGraphGenerator monitor){
		this.monitor = monitor;
	}
	
	@Override
	public void run(){
		while(true){
			CustomEntity ce = this.monitor.nextEntity();
			if(ce == null)
				return;
			boolean entityTopicRelation[] = new boolean[this.monitor.nTopics];
			int i = 0;
			for(long l:this.monitor.entities.get(ce)){
				int topic = this.monitor.sentenceTopicRelation.senteceTopicRelation.get(l);
				if(!entityTopicRelation[topic]){
					entityTopicRelation[topic] = true;
					i++;
					try {
						this.monitor.graphs[topic].addVertex(ce);
					} catch (Exception e) {	e.printStackTrace();}
				}
				if(i >= this.monitor.nTopics)
					break;
			}
		}
	}
}