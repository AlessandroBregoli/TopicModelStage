package it.unimib.disco.ab.graphs;

import java.util.ArrayList;

public class GraphUtils {
	
	
	public static ArrayList<ArrayList<Integer>> connectedComponents(double[][] adjacentMatrix){
		ArrayList<ArrayList<Integer>> connectedComponents = new ArrayList<ArrayList<Integer>>();
		boolean analyzedVertex[] =  new boolean[adjacentMatrix[0].length];
		for(int i = 0; i < analyzedVertex.length; i++){
			ArrayList<Integer >connectedComponent = new ArrayList<Integer>();
			if(!analyzedVertex[i]){
				dfs(adjacentMatrix, analyzedVertex, connectedComponent, i);
				connectedComponents.add(connectedComponent);
			}
		}
		return connectedComponents;
	}
	private static void dfs(double[][] adjacentMatrix, boolean analyzedVertex[], ArrayList<Integer> connectedComponent, int vertex){
		connectedComponent.add(vertex);
		analyzedVertex[vertex] = true;
		for(int i = 0; i < analyzedVertex.length; i++){
			if(!analyzedVertex[i] && adjacentMatrix[vertex][i] > 0){
				dfs(adjacentMatrix, analyzedVertex, connectedComponent, i);
			}
		}
		
	}
}
