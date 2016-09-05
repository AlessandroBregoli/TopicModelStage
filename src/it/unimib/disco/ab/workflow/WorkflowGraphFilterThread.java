package it.unimib.disco.ab.workflow;

import it.unimib.disco.ab.graphs.EntityTopicGraph;

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
			g.serializeForPajec("Topic" + i + ".net");
		}
	}

}
