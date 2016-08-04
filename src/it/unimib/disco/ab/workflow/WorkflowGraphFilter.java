package it.unimib.disco.ab.workflow;

import it.unimib.disco.ab.graphs.EntityTopicGraph;

public class WorkflowGraphFilter {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		for(int i = 0; i < 30; i++){
		EntityTopicGraph g = new EntityTopicGraph("Topic" + i + ".dat");
		System.out.println(g.getTopic());
		g.pctFilter(0.98);
		g.serializeForPajec("Topic" + i + ".net");
		}
	}

}
