package it.unimib.disco.ab.graphs;

import it.unimib.disco.ab.entityTopicStatistics.TopicStatTuple;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

public class StaticGraphAnalyzerThread extends Thread{
	StaticGraphAnalyzer monitor;
	public StaticGraphAnalyzerThread(StaticGraphAnalyzer monitor) {
		this.monitor = monitor;
	}
	public void run(){
		while(true){
			int topic = this.monitor.getTopic();
			if(topic < 0){
				return;
			}
			File f = new File("ConnectedComponents topic " + topic + ".txt");
			FileWriter fw = null;
			try {
				fw = new FileWriter(f);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			BufferedWriter bw = new BufferedWriter(fw);
			ConnectedComponentSentences ccSentences = new ConnectedComponentSentences();
			ArrayList<ArrayList<Integer>> connectedComponents = GraphUtils.connectedComponents(this.monitor.graphs[topic].adiacentMatrix);
			for(ArrayList<Integer> connectedComponent:connectedComponents){
				if(connectedComponent.size() < this.monitor.minCCSize){
					System.out.println("CIAONE");
					continue;
				}
				

				TreeMap<Long, String> sentences = new TreeMap<Long, String>();
				try {
					bw.write("Componente connessa formata da: \n");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				for(int i: connectedComponent){
					//TODO Oscenità hardcoded non so se la sistemerai mai
					try {
						bw.write(this.monitor.graphs[topic].vertexDictionary.get(i).entityString + "\t");
					} catch (IOException e) {
						e.printStackTrace();
					}					
					for(TopicStatTuple ele: this.monitor.entityTopicRelation.relation.get(this.monitor.graphs[topic].vertexDictionary.get(i)).getData().get(topic)){
						sentences.put(ele.sentenceID, ele.sentenceText);
						
					}
					
				}
				try {
					bw.write("\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
				for(long sentence:sentences.keySet()){
					try {
						bw.write(sentences.get(sentence) + "\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
						
				}
				try {
					bw.write("\n\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
			bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}				
		}		
	}	
}
