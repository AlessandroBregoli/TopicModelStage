package it.prova;

import java.util.ArrayList;

import it.unimib.disco.ab.graphs.GraphUtils;

public class compconn {
	public static void main(String[] args){
		double[][] adj = {{0,1.3,0,0,0},{1,0,1,1,0},{0,1,0,0,0},{0,1,0,0,0},{0,0,0,0,0}};
		for(ArrayList<Integer> cc:GraphUtils.connectedComponents(adj)){
			System.out.println("\ncc");
			System.out.println(cc.size());
			for(int j = 0; j < cc.size(); j++){
				System.out.print(cc.get(j));
			}
		}
	}

}
