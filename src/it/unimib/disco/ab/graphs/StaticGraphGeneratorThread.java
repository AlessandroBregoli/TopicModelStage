package it.unimib.disco.ab.graphs;

import java.util.ArrayList;
import java.util.LinkedList;

import edu.stanford.nlp.ling.CoreAnnotations.PrevChildAnnotation;
import it.unimib.disco.ab.ner.CustomEntity;

public class StaticGraphGeneratorThread extends Thread {
	private StaticGraphGenerator monitor;
	
	public StaticGraphGeneratorThread(StaticGraphGenerator monitor){
		this.monitor = monitor;
	}
	
	//Questo metodo dato un topic genera un grafo partendo dai nodi selezionati da 
	//StaticGraphInizializerThread.
	//Calcola il peso degli archi andando a cercare le cooccorrenze nelle frasi e poi
	//utilizza l'impormazione mutua per generare il valore.
	public void run(){
		while(true){
			int topic = this.monitor.nextTopic();
			
			if(topic == -1)
				return;
			System.err.println("Topic " + topic + " " + this.monitor.sentenceTopicRelation.sentencePerTopic[topic]);
			//double epsilon = (double)1/(Math.pow(this.monitor.sentenceTopicRelation.sentencePerTopic[topic],2));
			double epsilon = Double.MIN_NORMAL;
			//double epsilon = 0.0;
		
		
			this.monitor.graphs[topic].initializeMatrix();
			for(int i = 0; i < (this.monitor.graphs[topic].vertexDictionary.size() - 1); i++ ){
				LinkedList<Long> tse1 = this.monitor.entities.get(this.monitor.graphs[topic].vertexDictionary.get(i));
				this.monitor.graphs[topic].adiacentMatrix[i][i] = 0.0;
				
				for(int j = i+1; j < this.monitor.graphs[topic].vertexDictionary.size(); j++){
					LinkedList<Long> tse2 = this.monitor.entities.get(this.monitor.graphs[topic].vertexDictionary.get(j));
					int intersection = 0;
					int sentencePointer1 = 0;
					int sentencePointer2 = 0;
					int nSentenceEntity1 = 0;
					int nSentenceEntity2 = 0;
					while(sentencePointer1 < tse1.size() && sentencePointer2 < tse2.size()){
						if(tse1.get(sentencePointer1).equals(tse2.get(sentencePointer2))){
							if(this.monitor.sentenceTopicRelation.senteceTopicRelation.get(tse1.get(sentencePointer1)) == topic){
								nSentenceEntity1++;
								nSentenceEntity2++;
								intersection++;
							}
							sentencePointer1++;
							sentencePointer2++;
							
						}else if(tse1.get(sentencePointer1).compareTo(tse2.get(sentencePointer2)) > 0 ){
							if(this.monitor.sentenceTopicRelation.senteceTopicRelation.get(tse2.get(sentencePointer2)) == topic)
								nSentenceEntity2++;
							sentencePointer2++;
						}else{
							if(this.monitor.sentenceTopicRelation.senteceTopicRelation.get(tse1.get(sentencePointer1)) == topic)
								nSentenceEntity1++;
							sentencePointer1++;
						}
					}
					while(sentencePointer1 < tse1.size()){
						if(this.monitor.sentenceTopicRelation.senteceTopicRelation.get(tse1.get(sentencePointer1)) == topic)
							nSentenceEntity1++;
						sentencePointer1++;
					}
					while(sentencePointer2 < tse2.size()){
						if(this.monitor.sentenceTopicRelation.senteceTopicRelation.get(tse2.get(sentencePointer2)) == topic)
							nSentenceEntity2++;
						sentencePointer2++;
					}
					
					
					double pe1 = (double)nSentenceEntity1/this.monitor.sentenceTopicRelation.sentencePerTopic[topic];
					double pe2 = (double)nSentenceEntity2/this.monitor.sentenceTopicRelation.sentencePerTopic[topic];
					double pe1e2 = (double)intersection/this.monitor.sentenceTopicRelation.sentencePerTopic[topic];
					double pe1ne2 = (double)(nSentenceEntity1 - intersection)/this.monitor.sentenceTopicRelation.sentencePerTopic[topic];
					double pne1e2 = (double)(nSentenceEntity2 - intersection)/this.monitor.sentenceTopicRelation.sentencePerTopic[topic];
					double pne1ne2 = (double) ( this.monitor.sentenceTopicRelation.sentencePerTopic[topic] - nSentenceEntity1 - nSentenceEntity2 + intersection) / this.monitor.sentenceTopicRelation.sentencePerTopic[topic];
					
					this.monitor.graphs[topic].adiacentMatrix[j][i] = 0;
					/*if(pe1e2 > 0 && pe1ne2 > 0 && pne1e2 > 0 && pne1ne2 > 0){
						
					}*/
					this.monitor.graphs[topic].adiacentMatrix[j][i]  = pe1e2 * Math.log((pe1e2 + epsilon)/(pe1*pe2))/Math.log(2);
					this.monitor.graphs[topic].adiacentMatrix[j][i]  += pe1ne2 * Math.log((pe1ne2 + epsilon)/(pe1*(1-pe2)))/Math.log(2);
					this.monitor.graphs[topic].adiacentMatrix[j][i]  += pne1e2 * Math.log((pne1e2 + epsilon)/((1-pe1)*pe2))/Math.log(2);
					this.monitor.graphs[topic].adiacentMatrix[j][i]  += pne1ne2 * Math.log((pne1ne2 + epsilon)/((1-pe1)*(1-pe2)))/Math.log(2);
					this.monitor.graphs[topic].adiacentMatrix[i][j] = this.monitor.graphs[topic].adiacentMatrix[j][i];
				}
			}
			/*
			double mean = 0;
			for(int i = 0; i < this.monitor.graphs[topic].adiacentMatrix.length; i++)
				for(int j = 0; j < this.monitor.graphs[topic].adiacentMatrix.length; j++)
					mean += this.monitor.graphs[topic].adiacentMatrix[i][j];
			mean /= this.monitor.graphs[topic].adiacentMatrix.length * this.monitor.graphs[topic].adiacentMatrix.length;
			for(int i = 0; i < this.monitor.graphs[topic].adiacentMatrix.length; i++)
				for(int j = 0; j < this.monitor.graphs[topic].adiacentMatrix.length; j++)
					if(this.monitor.graphs[topic].adiacentMatrix[i][j] <=  mean)
						this.monitor.graphs[topic].adiacentMatrix[i][j] = 0;
			*/		
			//this.monitor.graphs[topic].serializeForPajec("Topic" + topic + ".net");
			this.monitor.graphs[topic].serializeForJava("Topic" + topic + ".dat");
			
		}
	}
}
