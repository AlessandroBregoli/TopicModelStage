package it.unimib.disco.ab.workflow;

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
			System.out.println(g.getTopic());
			g.pctFilter(0.98);
			g.serializeForJava("Topic" + i + "Filtered.dat");
			GraphComunityExtractor gce = new GraphComunityExtractor();
			try {
				gce.generateComunities(g, 1.0, 57, 1, 50);
			
				EntityTopicGraph cg= gce.getGraphBasedOnCentrality();
				cg.serializeForSigma("Topic" + i + ".json");
				for(int j = 0; j < gce.getComunities().size(); j++){
					gce.getComunities().get(i).serializeForSigma("Topic" + i + "C" + j + ".json");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			g.serializeForPajec("Topic" + i + ".net");
		}
	}

}
