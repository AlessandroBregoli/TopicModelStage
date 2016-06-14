package it.unimib.disco.ab.graphs;

import java.util.ArrayList;

import it.unimib.disco.ab.entityTopicStatistics.TopicStatTuple;
import it.unimib.disco.ab.ner.CustomEntity;

public class StaticGraphGeneratorThread extends Thread {
	private StaticGraphGenerator monitor;
	
	public StaticGraphGeneratorThread(StaticGraphGenerator monitor){
		this.monitor = monitor;
	}
	
	public void run(){
		while(true){
			int topic = this.monitor.nextTopic();
			
			if(topic == -1)
				break;
			System.err.println("Topic " + topic + ": " + this.monitor.nerStats.sentencePerTopic[topic]);
			double epsilon = 1 / this.monitor.nerStats.sentencePerTopic[topic];
			EntityTopicGraph graph = new EntityTopicGraph();
			for(CustomEntity e:this.monitor.nerStats.relation.keySet()){
				if(this.monitor.nerStats.relation.get(e).getData().containsKey(topic)){
					try {
						graph.addVertex(e);
					} catch (Exception e1) {}
				}
			}
			graph.initializeMatrix();
			for(int i = 0; i < (graph.vertexDictionary.size() - 1); i++ ){
				ArrayList<TopicStatTuple> tse1 = this.monitor.nerStats.relation.get(graph.vertexDictionary.get(i)).getData().get(topic);
				for(int j = i; j < graph.vertexDictionary.size(); j++){
					ArrayList<TopicStatTuple> tse2 = this.monitor.nerStats.relation.get(graph.vertexDictionary.get(j)).getData().get(topic);
					double intersection = 0.0;
					int sentencePointer1 = 0;
					int sentencePointer2 = 0;
					while(sentencePointer1 < tse1.size() && sentencePointer2 < tse2.size()){
						if(tse1.get(sentencePointer1).sentenceID == tse2.get(sentencePointer2).sentenceID){
							intersection++;
							sentencePointer1++;
							sentencePointer2++;
						}else if(tse1.get(sentencePointer1).sentenceID > tse2.get(sentencePointer2).sentenceID){
							sentencePointer2++;
						}else{
							sentencePointer1++;
						}
					}
					
					
					double pe1 = (double)tse1.size()/this.monitor.nerStats.sentencePerTopic[topic];
					double pe2 = (double)tse2.size()/this.monitor.nerStats.sentencePerTopic[topic];
					double pe1e2 = (double)intersection/this.monitor.nerStats.sentencePerTopic[topic];
					double pe1ne2 = (double)(tse1.size() - intersection)/this.monitor.nerStats.sentencePerTopic[topic];
					double pne1e2 = (double)(tse2.size() - intersection)/this.monitor.nerStats.sentencePerTopic[topic];
					double pne1ne2 = (double) ( this.monitor.nerStats.sentencePerTopic[topic] - tse1.size() - tse2.size() + intersection) / this.monitor.nerStats.sentencePerTopic[topic];
					
					//graph.adiacentMatrix[j][i]  = jointProbability * Math.log(jointProbability/(probTst1*probTst2))/Math.log(2);
					graph.adiacentMatrix[j][i]  = pe1e2 * Math.log((pe1e2 + epsilon)/(pe1*pe2))/Math.log(2);
					graph.adiacentMatrix[j][i]  += pe1ne2 * Math.log((pe1ne2 + epsilon)/(pe1*(1-pe2)))/Math.log(2);
					graph.adiacentMatrix[j][i]  += pne1e2 * Math.log((pne1e2 + epsilon)/((1-pe1)*pe2))/Math.log(2);
					graph.adiacentMatrix[j][i]  += pne1ne2 * Math.log((pne1ne2 + epsilon)/((1-pe1)*(1-pe2)))/Math.log(2);
					//graph.adiacentMatrix[j][i] = Math.abs(graph.adiacentMatrix[j][i]);
					graph.adiacentMatrix[i][j] = graph.adiacentMatrix[j][i];
				}
			}
			this.monitor.graphs[topic] = graph;
			graph.serializeForPajec("Topic" + topic + ".net");
			
		}
	}
}
