package it.unimib.disco.ab.graphs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

import it.unimib.disco.ab.ner.CustomEntity;

//TODO definire una superclasse Graph se si intende usare grafi in più di un'occasione
//Questa classe gestisce prevalentemente grafi non diretti quindi di fatto solo metà della matrice di adiacenza viene considerata
public class EntityTopicGraph implements Serializable{
	double adiacentMatrix[][];
	ArrayList<CustomEntity> vertexDictionary;
	int topic;
	public EntityTopicGraph(int topic){
		this.vertexDictionary = new ArrayList<CustomEntity>();
		this.adiacentMatrix = null;
		this.topic = topic;
	}
	
	public EntityTopicGraph(String serializedObj){
		EntityTopicGraph etg = null;
		try {
			FileInputStream fileIn = new FileInputStream(serializedObj);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			etg = (EntityTopicGraph) in.readObject();
			this.adiacentMatrix = etg.adiacentMatrix;
			this.vertexDictionary = etg.vertexDictionary;
			this.topic = etg.topic;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	public void addVertex(CustomEntity ent) throws Exception{
		if(this.adiacentMatrix != null)
			throw new Exception("Matrice già generata; impossibile aggiungere vertici");
		this.vertexDictionary.add(ent);
	}
	public void initializeMatrix(){
		this.adiacentMatrix = new double[this.vertexDictionary.size()][this.vertexDictionary.size()];
	}
	
	//Questo metodo serializza in formato pajec un grafo non diretto(quindi solo la prima metà della matrice)
	//eliminando archi nulli e nodi senza archi
	public void serializeForPajec(String path){
		File f = new File(path);
		FileWriter fw;
		try {
			fw = new FileWriter(f);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("*Vertices " + this.vertexDictionary.size() + "\n");
			for(int i = 0; i < this.vertexDictionary.size(); i++){
				for(int j = 0; j < this.vertexDictionary.size(); j++){
					if(this.adiacentMatrix[i][j] > 0){
						bw.write(" " + (i +1) + " \"" + this.vertexDictionary.get(i).entityString + "\"\n");
						break;
					}
				}
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
	public int getTopic(){
		return this.topic;
	}
	public void serializeForJava(String path){
		try {
			FileOutputStream f = new FileOutputStream(path);
			ObjectOutputStream out = new ObjectOutputStream(f);
			out.writeObject(this);
			out.close();
			f.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	private static void addElement(double value, ArrayList<Double> list){
		if(list.size() == 0){
			list.add(value);
			return;
		}
		EntityTopicGraph.addElementRic(value, 0, list.size() - 1, list);
	}
	private static void addElementRic(double value, int index1, int index2, ArrayList<Double> list){
		if(index2 <= index1){
			list.add(index1, value);
			return;
		}
		if(list.get((index2 + index1)/2) > value){
			addElementRic(value, index1, (index2 + index1)/2 - 1, list);
		}
		else{
			addElementRic(value, (index2 + index1)/2 + 1, index2, list);
		}
	}
	public void pctFilter(double pct){
		ArrayList<Double> lista = new ArrayList<Double>();
		for(int i = 0; i < this.adiacentMatrix.length - 1; i++){
			for(int j = i+1; j < this.adiacentMatrix.length; j++){
				EntityTopicGraph.addElement(this.adiacentMatrix[i][j],lista);
			}
		}
		double filtValue = lista.get((int)((lista.size()-1) * pct));
		for(int i = 0; i < this.adiacentMatrix.length -1 ; i++)
			for(int j = i+1; j < this.adiacentMatrix.length; j++){
				if(this.adiacentMatrix[i][j] < filtValue)
					this.adiacentMatrix[i][j] = 0.0;
					this.adiacentMatrix[j][i] = 0.0;
			}
	}
	
	public double[][] getAdiacentMatrix(){
		return this.adiacentMatrix;
	}
	
	public ArrayList<CustomEntity> getVertexDictionary(){
		return this.vertexDictionary;
	}

}
