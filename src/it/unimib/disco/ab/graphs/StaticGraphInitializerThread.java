package it.unimib.disco.ab.graphs;

import it.unimib.disco.ab.ner.CustomEntity;

public class StaticGraphInitializerThread extends Thread{
	private StaticGraphGenerator monitor;
	public StaticGraphInitializerThread(StaticGraphGenerator monitor){
		this.monitor = monitor;
	}
	
	//Questo metodo richiede un CustomEntity e cerca a quali topic è connessa andando ad
	//analizzare le frasi in cui è contenuta; se tale entità è contenuta almeno in un frase
	//connessa ad un certo topic tale entità viene aggiunta al VertexDictionary del grafo
	//relativo al topic
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
						this.monitor.updateVertexDictionary(ce, topic);
					} catch (Exception e) {	e.printStackTrace();}
				}
				if(i >= this.monitor.nTopics)
					break;
			}
		}
	}
}
