package it.unimib.disco.ab.graphs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import it.unimib.disco.ab.ner.CustomEntity;

//TODO definire una superclasse Graph se si intende usare grafi in più di un'occasione
public class EntityTopicGraph {
	double adiacentMatrix[][];
	ArrayList<CustomEntity> vertexDictionary;
	public EntityTopicGraph(){
		this.vertexDictionary = new ArrayList<CustomEntity>();
		this.adiacentMatrix = null;
	}
	
	public void addVertex(CustomEntity ent) throws Exception{
		if(this.adiacentMatrix != null)
			throw new Exception("Matrice già generata; impossibile aggiungere vertici");
		this.vertexDictionary.add(ent);
	}
	public void initializeMatrix(){
		this.adiacentMatrix = new double[this.vertexDictionary.size()][this.vertexDictionary.size()];
	}
	
	public void serializeForPajec(String path){
		File f = new File(path);
		FileWriter fw;
		try {
			fw = new FileWriter(f);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("*Vertices " + this.vertexDictionary.size() + "\n");
			for(int i = 0; i < this.vertexDictionary.size(); i++){
				bw.write(" " + (i +1) + " \"" + this.vertexDictionary.get(i).entityString + "\"\n");
			}
			bw.write("*Edges\n");
			for(int i = 0; i < this.vertexDictionary.size() - 1; i++){
				for(int j = i+1; j < this.vertexDictionary.size(); j++){
					if(this.adiacentMatrix[i][j] > 0){
						bw.write(" " + (i+1) + "   " + (j+1) + " " + this.adiacentMatrix[i][j] + "\n");
					}
				}
			}
			bw.close();
			fw.close();
		} catch (IOException e) {e.printStackTrace();}
		
	}

}
