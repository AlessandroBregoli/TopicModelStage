package it.unimib.disco.ab.workflow;

import it.unimib.disco.ab.graphs.EntityTopicGraph;

public class WorkflowGraphFilter {
/*
	public static void main(String[] args) {
		
		WorkflowGraphFilter f = new WorkflowGraphFilter();
		f.nTopics = 30;
		int nThreads = Integer.parseInt(args[0]);
		f.nThreads = nThreads;
		for(int i = 0; i < nThreads; i++)
			new WorkflowGraphFilterThread(f).start();
		f.waitUntillEnd();
	}*/
	public static void startWorkflow(int nThreads, int nTopics, double pctFilter, String[] classFilter, boolean generateComunities, boolean generateNetFile, boolean generateJSONFile, boolean interclassEdgeOnly ){
		WorkflowGraphFilter f = new WorkflowGraphFilter(nThreads, nTopics, pctFilter, classFilter, generateComunities, generateNetFile, generateJSONFile, interclassEdgeOnly);
		for(int i = 0; i < nThreads; i++)
			new WorkflowGraphFilterThread(f).start();
		f.waitUntillEnd();
	}
	
	public WorkflowGraphFilter(int nThreads, int nTopics,double pctFilter, String[] classFilter, boolean generateComunities, boolean generateNetFile, boolean generateJSONFile, boolean interclassEdgeOnly){
		this.nThreads = nThreads;
		this.nTopics = nTopics;
		this.pctFilter = pctFilter;
		this.classFilter = classFilter;
		this.generateComunities = generateComunities;
		this.generateNetFile = generateNetFile;
		this.generateJSONFile = generateJSONFile;
		this.interclassEdgeOnly = interclassEdgeOnly;
		
	}
	int currentTopic = 0;
	int nTopics = 0;
	int nThreads = 0;
	double pctFilter;
	String[] classFilter;
	boolean generateComunities;
	boolean generateNetFile;
	boolean generateJSONFile;
	boolean interclassEdgeOnly;
	public synchronized int getGraphIndex(){
		if(currentTopic < nTopics)
			return currentTopic++;
		nThreads--;
		notifyAll();
		return -1;
	}
	public synchronized void waitUntillEnd(){
		while(this.nThreads > 0)
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

}
