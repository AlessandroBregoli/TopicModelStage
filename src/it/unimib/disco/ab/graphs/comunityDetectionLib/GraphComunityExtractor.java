package it.unimib.disco.ab.graphs.comunityDetectionLib;

import it.unimib.disco.ab.graphs.EntityTopicGraph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class GraphComunityExtractor {
	private ArrayList<EntityTopicGraph> comunities;
	private Network network;
	public Clustering clustering;
	private EntityTopicGraph graph;
	public GraphComunityExtractor(){
		this.comunities = new ArrayList<EntityTopicGraph>();
	}
	
	private void loadGraph(EntityTopicGraph graph){
		this.graph = graph;
        double[] edgeWeight1, edgeWeight2;
        int i, j, nEdges, nLines, nNodes;
        int[] firstNeighborIndex, neighbor, nNeighbors, node1, node2;
    

        nLines = 0;
        for(i = 0; i < graph.getVertexDictionary().size() - 1; i++){
        	for(j = i+1; j < graph.getVertexDictionary().size(); j++){
        		if(graph.getAdiacentMatrix()[i][j] > 0.0){
        			nLines++;
        		}
        	}
        }
        	


        node1 = new int[nLines];
        node2 = new int[nLines];
        edgeWeight1 = new double[nLines];
  
        
        int countNLines = 0;
        for(i = 0; i < graph.getVertexDictionary().size() - 1; i++){
        	for(j = i+1; j < graph.getVertexDictionary().size(); j++){
        		if(graph.getAdiacentMatrix()[i][j] > 0.0){
        			node1[countNLines] = i;
        			node2[countNLines] = j;
        			edgeWeight1[countNLines] = graph.getAdiacentMatrix()[i][j];
        			countNLines++;
        		}
        	}
        }
        nNodes = graph.getAdiacentMatrix().length;


        nNeighbors = new int[nNodes];
        for (i = 0; i < nLines; i++)
            if (node1[i] < node2[i])
            {
                nNeighbors[node1[i]]++;
                nNeighbors[node2[i]]++;
            }

        firstNeighborIndex = new int[nNodes + 1];
        nEdges = 0;
        for (i = 0; i < nNodes; i++)
        {
            firstNeighborIndex[i] = nEdges;
            nEdges += nNeighbors[i];
        }
        firstNeighborIndex[nNodes] = nEdges;

        neighbor = new int[nEdges];
        edgeWeight2 = new double[nEdges];
        Arrays.fill(nNeighbors, 0);
        for (i = 0; i < nLines; i++)
            if (node1[i] < node2[i])
            {
                j = firstNeighborIndex[node1[i]] + nNeighbors[node1[i]];
                neighbor[j] = node2[i];
                edgeWeight2[j] = edgeWeight1[i];
                nNeighbors[node1[i]]++;
                j = firstNeighborIndex[node2[i]] + nNeighbors[node2[i]];
                neighbor[j] = node1[i];
                edgeWeight2[j] = edgeWeight1[i];
                nNeighbors[node2[i]]++;
            }
        this.network = new Network(nNodes, firstNeighborIndex, neighbor, edgeWeight2);
	}
	
	private void detectComunities(double resolution, long randomSeed, int nRandomStarts, int nIterations){
		double resolution2 = resolution / (2 * this.network.getTotalEdgeWeight() + this.network.totalEdgeWeightSelfLinks);
		Clustering clustering = null;
		double maxModularity = Double.NEGATIVE_INFINITY;
		double modularity;
		Random random = new Random(randomSeed);
		VOSClusteringTechnique VOSClusteringTechnique;
		boolean update;
		int j;
		 for (int i = 0; i < nRandomStarts; i++)
		 {
	            VOSClusteringTechnique = new VOSClusteringTechnique(network, resolution2);

	            j = 0;
	            update = true;
	            do
	            {

	                update = VOSClusteringTechnique.runLouvainAlgorithm(random);
	                j++;

	                modularity = VOSClusteringTechnique.calcQualityFunction();

	            }
	            while ((j < nIterations) && update);

	            if (modularity > maxModularity)
	            {
	                clustering = VOSClusteringTechnique.getClustering();
	                maxModularity = modularity;
	            }

	      }
		 this.clustering = clustering;
	}
	
	private void generateSubGraphs() throws Exception{
		this.comunities = new ArrayList<EntityTopicGraph>();
		for(int i = 0; i < this.clustering.cluster.length; i++){
			if(this.comunities.size() <= this.clustering.cluster[i]){
				for(int j = this.comunities.size(); j <= this.clustering.cluster[i]; j++)
					this.comunities.add(new EntityTopicGraph(this.graph.getTopic()));
			}
			this.comunities.get(this.clustering.cluster[i]).addVertex(this.graph.getVertexDictionary().get(i));
		}
		for(int i = 0; i < this.comunities.size(); i++){
			this.comunities.get(i).initializeMatrix();
			for(int j = 0; j < this.comunities.get(i).getVertexDictionary().size()-1; j++){
				int indexJ = this.graph.getVertexDictionary().indexOf(this.comunities.get(i).getVertexDictionary().get(j));
				for(int k = j+1; k < this.comunities.get(i).getVertexDictionary().size(); k++){
					int indexK = this.graph.getVertexDictionary().indexOf(this.comunities.get(i).getVertexDictionary().get(k));
					double weight = this.graph.getAdiacentMatrix()[indexJ][indexK] > this.graph.getAdiacentMatrix()[indexK][indexJ] ? this.graph.getAdiacentMatrix()[indexJ][indexK] : this.graph.getAdiacentMatrix()[indexK][indexJ];
					this.comunities.get(i).getAdiacentMatrix()[j][k] = weight;
					this.comunities.get(i).getAdiacentMatrix()[k][j] = weight;
					
				}
			}
		}
		
		
	}
	
	public void generateComunities(EntityTopicGraph graph, double resolution, long randomSeed, int nRandomStarts, int nIterations ) throws Exception{
		if(graph.getVertexDictionary().size() == 0)
			return;	
		this.loadGraph(graph);
		this.detectComunities(resolution, randomSeed, nRandomStarts, nIterations);
		this.generateSubGraphs();
	}
	
	public ArrayList<EntityTopicGraph> getComunities(){
		return this.comunities;
	}
	
	//Anche questo grafo è non diretto e dunque viene generata solo metà matrice
	public EntityTopicGraph getGraphBasedOnCentrality() throws Exception{
		EntityTopicGraph ret = new EntityTopicGraph(this.graph.getTopic());
		if(this.comunities == null)
			throw new Exception("Comunità non generate");
		for(EntityTopicGraph comunity: this.comunities){
			int idMaxCentrality = 0;
			double[] centrality = comunity.getCentrality();
			for(int i = 1; i < centrality.length; i++){
				if(centrality[i] > centrality[idMaxCentrality])
					idMaxCentrality = i;
			}
			ret.addVertex(comunity.getVertexDictionary().get(idMaxCentrality));
		}
		ret.initializeMatrix();
		//La matrice di adiacenza viene generata considerando solo i legami tra i nodi eletti e non considerando le relazioni dei nodi rappresentati
		for(int i = 0; i < ret.getVertexDictionary().size() - 1; i++){
			int idOriginalMatrixI = this.graph.getVertexDictionary().indexOf(ret.getVertexDictionary().get(i));
			for(int j = i + 1; j < ret.getVertexDictionary().size(); j++){
				int idOriginalMatrixJ = this.graph.getVertexDictionary().indexOf(ret.getVertexDictionary().get(j));
				ret.getAdiacentMatrix()[i][j] = this.graph.getAdiacentMatrix()[idOriginalMatrixI][idOriginalMatrixJ];
				ret.getAdiacentMatrix()[j][i] = ret.getAdiacentMatrix()[i][j];
			}
		}
		return ret;
	}
	
}
