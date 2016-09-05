package it.unimib.disco.ab.workflow;

import it.unimib.disco.ab.graphs.EntityTopicGraph;

public class WorkflowGraphFilter {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		WorkflowGraphFilter f = new WorkflowGraphFilter();
		f.max = 30;
		int nThreads = Integer.parseInt(args[0]);
		f.nThreads = nThreads;
		for(int i = 0; i < nThreads; i++)
			new WorkflowGraphFilterThread(f).start();
		while(f.nThreads > 0);
	}
	
	int i = 0;
	int max = 0;
	int nThreads = 0;
	public synchronized int getGraphIndex(){
		if(i < max)
			return i++;
		nThreads--;
		return -1;
	}

}
