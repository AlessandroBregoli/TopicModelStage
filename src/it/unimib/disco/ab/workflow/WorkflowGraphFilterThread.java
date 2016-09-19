package it.unimib.disco.ab.workflow;

import java.io.IOException;

import it.unimib.disco.ab.graphs.CustomEntityMatcherByClass;
import it.unimib.disco.ab.graphs.EntityTopicGraph;
import it.unimib.disco.ab.graphs.comunityDetectionLib.GraphComunityExtractor;

public class WorkflowGraphFilterThread extends Thread {
	WorkflowGraphFilter monitor;
	public WorkflowGraphFilterThread(WorkflowGraphFilter monitor) {
		this.monitor = monitor;
	}
	
	@Override
	public void run(){
		while(true){
			int i = this.monitor.getGraphIndex();
			if(i < 0)
				return;
			EntityTopicGraph g = new EntityTopicGraph("Topic" + i + ".dat");
			if(this.monitor.classFilter != null){
				g = g.getFilteredGraph(new CustomEntityMatcherByClass(this.monitor.classFilter));
			}
				
			System.out.println(i);
			if(this.monitor.pctFilter > 0.0){
				g.pctFilter(this.monitor.pctFilter);
			}
			
		
			if(this.monitor.generateNetFile){
				g.serializeForPajec("Topic" + i + ".net");
			}
			if(this.monitor.generateJSONFile){
				try {
					g.serializeForSigma("Topic" + i + ".json");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(this.monitor.generateComunities){
				GraphComunityExtractor gce = new GraphComunityExtractor();
				if(g.getVertexDictionary().size() == 0)
					continue;
				try {
					gce.generateComunities(g, 1.0, 57, 1, 50);
				
					EntityTopicGraph cg = gce.getGraphBasedOnCentrality();
					if(this.monitor.generateNetFile){
						cg.serializeForPajec("Topic" + i + "CentralityFilt.net");
					}
					if(this.monitor.generateJSONFile){
						cg.serializeForSigma("Topic" + i + "CentralityFilt.json");
					}
					for(int j = 0; j < gce.getComunities().size(); j++){
						if(this.monitor.generateJSONFile){
							gce.getComunities().get(j).serializeForSigma("Topic" + i + "C" + j + ".json");
						}
						if(this.monitor.generateNetFile){
							gce.getComunities().get(j).serializeForPajec("Topic" + i + "C" + j + ".net");
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	}

}
