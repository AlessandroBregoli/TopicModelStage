package it.unimib.disco.ab.graphs;

import java.util.ArrayList;

import it.unimib.disco.ab.entityTimeCorrelation.TopicStatTuple;
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
			EntityTopicGraph graph = new EntityTopicGraph();
			for(CustomEntity e:this.monitor.relation.keySet()){
				if(this.monitor.relation.get(e).getData().containsKey(topic)){
					try {
						graph.addVertex(e);
					} catch (Exception e1) {}
				}
			}
			graph.initializeMatrix();
			for(int i = 0; i < (graph.vertexDictionary.size() - 1); i++ ){
				ArrayList<TopicStatTuple> tst1 = this.monitor.relation.get(graph.vertexDictionary.get(i)).getData().get(topic);
				for(int j = i; j < graph.vertexDictionary.size(); j++){
					ArrayList<TopicStatTuple> tst2 = this.monitor.relation.get(graph.vertexDictionary.get(j)).getData().get(topic);
					double intersection = 0.0;
					int sentencePointer1 = 0;
					int sentencePointer2 = 0;
					while(sentencePointer1 < tst1.size() && sentencePointer2 < tst2.size()){
						if(tst1.get(sentencePointer1).sentenceID == tst2.get(sentencePointer2).sentenceID){
							intersection++;
							sentencePointer1++;
							sentencePointer2++;
						}else if(tst1.get(sentencePointer1).sentenceID > tst2.get(sentencePointer2).sentenceID){
							sentencePointer2++;
						}else{
							sentencePointer1++;
						}
					}
					double nElement = (tst1.size() + tst2.size() - intersection);
					double jointProbability = intersection / nElement;
					double probTst1 = tst1.size() / nElement;
					double probTst2 = tst2.size() / nElement;
					graph.adiacentMatrix[j][i]  = jointProbability * Math.log(jointProbability/(probTst1*probTst2))/Math.log(2);
					graph.adiacentMatrix[i][j] = graph.adiacentMatrix[j][i];
				}
			}
			this.monitor.graphs[topic] = graph;
			graph.serializeForPajec("Topic" + topic + ".net");
			
		}
	}
}
