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
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import it.unimib.disco.ab.ner.CustomEntity;
import it.unimib.disco.ab.ner.CustomEntityMatcher;

//TODO definire una superclasse Graph se si intende usare grafi in più di un'occasione
//Questa classe gestisce prevalentemente grafi non diretti quindi di fatto solo metà della matrice di adiacenza viene considerata
public class EntityTopicGraph implements Serializable{
	//Questo è stato fatto in quanto altrimenti ad ogni ricompilazione java si lamentava
	//e non riusciva ad aprire Grafi generati con versioni precedenti anche se identiche
	public static final long serialVersionUID = 1L;
	//Matrice di adiacenza che contiene i pesi degli archi
	double adiacentMatrix[][];
	//Dizionario dei nodi; gli indici dei nodi di questo ArrayList sono corrsispondenti alla patrice
	//di adiacenza; quindi se la matrice viene chiamata con adiacentMatrix[1][2] si può andare nel
	//vertexDictionary per vedere quali entità ci sono in posizione 1 ed in posizione 2
	ArrayList<CustomEntity> vertexDictionary;
	int topic;
	
	//Questo costruttore prepara un grafo vuoto 
	public EntityTopicGraph(int topic){
		this.vertexDictionary = new ArrayList<CustomEntity>();
		this.adiacentMatrix = null;
		this.topic = topic;
	}
	
	//Questo cotruttore carica un grafo serializzato da un file
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
	
	//Con questo metodo si riempie il vertexDictionary; questo è possibile solo fino a quando
	//la matrice di adiacenza non viene generata
	public void addVertex(CustomEntity ent) throws Exception{
		if(this.adiacentMatrix != null)
			throw new Exception("Matrice già generata; impossibile aggiungere vertici");
		this.vertexDictionary.add(ent);
	}
	
	//Questo metodo è equivalente a quello sopra ma invece di aggiungere un  nodo alla volta
	//ne aggiunge una collezione
	public void addVertices(Collection <CustomEntity> ents)throws Exception{
		if(this.adiacentMatrix != null)
			throw new Exception("Matrice già generata; impossibile aggiungere vertici");
		this.vertexDictionary.addAll(ents);
	}
	
	//Una volta che tutti i nodi sono stati aggiunti viene generata una matrice di adiacenza
	//(bidimensionale) che ha sia per lunnghezza che per altezza la dimensione del vertexDictionary
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
	
	//Questo metodo è di utilità interna all'oggetto e aggiunge ad una lista 
	//ordinata un elemento in modo che la lista resti ordinata
	private static void addElement(double value, ArrayList<Double> list){
		if(list.size() == 0){
			list.add(value);
			return;
		}
		EntityTopicGraph.addElementRic(value, 0, list.size() - 1, list);
	}
	
	//Questo è il metodo ricorsivo che che esegue effettivamente l'inserimento di un valore in
	//una lista ordinata
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
	
	//Questo metodo ritorna la lista ordinata di archi
	private ArrayList<Double> getOrderedListOfEdge(){
		ArrayList<Double> lista = new ArrayList<Double>();
		for(int i = 0; i < this.adiacentMatrix.length - 1; i++){
			for(int j = i+1; j < this.adiacentMatrix.length; j++){
				if(this.adiacentMatrix[i][j] > 0.0){
					EntityTopicGraph.addElement(this.adiacentMatrix[i][j],lista);
				}
			}
		}
		return lista;
	}
	
	//Questo metodo, filtra la matrice di adiacenza utilizzando un valore di riferimento.
	//Ogni valore strettamente minore di filtValue viene impostato a 0
	private void filtMatrixEdge(double filtValue){
		for(int i = 0; i < this.adiacentMatrix.length -1 ; i++)
			for(int j = i+1; j < this.adiacentMatrix.length; j++){
				if(this.adiacentMatrix[i][j] < filtValue)
					this.adiacentMatrix[i][j] = 0.0;
					this.adiacentMatrix[j][i] = 0.0;
			}
	}
	
	//Questo metodo filtra gli archi in modo da lasciare gli 1-pct archi più pesanti
	//dove pct varia tra 0 e 1
	public void pctFilter(double pct){
		ArrayList<Double> lista = this.getOrderedListOfEdge();
		double filtValue = lista.get((int)((lista.size()-1) * pct));
		this.filtMatrixEdge(filtValue);
	}
	
	//Questo metodo filtra gli archi lascianto solo i primi filt archi più pesanti
	public void constFilter(int filt){
		ArrayList<Double> lista = this.getOrderedListOfEdge();
		double filtValue = lista.get(lista.size() - filt);
		this.filtMatrixEdge(filtValue);
	}
	
	public double[][] getAdiacentMatrix(){
		return this.adiacentMatrix;
	}
	
	public ArrayList<CustomEntity> getVertexDictionary(){
		return this.vertexDictionary;
	}
	
	//implementato per grafi non diretti
	//dunque considera solo metà matrice
	//Questo metodo calcola la centralità di ogni nodo come somma dei pesi degli archi connessi
	//al nodo
	public double[] getCentrality() throws Exception{
		if(this.adiacentMatrix == null)
			throw new Exception("Matrice di adiacenza non generata");
		double centrality[] = new double[this.adiacentMatrix[0].length];
		Arrays.fill(centrality, 0.0);
		for(int i = 0; i < this.adiacentMatrix.length - 1; i++){
			for(int j = i + 1; j < this.adiacentMatrix.length; j++){
				if(this.adiacentMatrix[i][j] > 0){
					centrality[i] += this.adiacentMatrix[i][j];
					centrality[j] += this.adiacentMatrix[i][j];
				}
			}
		}
		
		return centrality;
	}
	
	//Serializza il grafo in formato JSON leggibile dalla libreria sigma
	public void serializeForSigma(String path) throws IOException{
		JSONObject root = new JSONObject();
		JSONArray nodes = new JSONArray();
		JSONArray edges = new JSONArray();
		for(int i = 0; i < this.vertexDictionary.size(); i++){
			JSONObject node = new JSONObject();
			node.put("id", "n"+Integer.toString(i));
			node.put("label", this.vertexDictionary.get(i).entityString);
			node.put("x", Math.cos(Math.PI * 2 * i / this.vertexDictionary.size()));
			node.put("y", Math.sin(Math.PI * 2 * i / this.vertexDictionary.size()));
			node.put("size", new Integer(1));
			nodes.add(node);			
		}
		for(int i = 0; i < this.vertexDictionary.size() -1; i++){
			for(int j = i + 1; j < this.vertexDictionary.size(); j++){
				
				if(this.adiacentMatrix[i][j] > 0.0){
					
					JSONObject edge = new JSONObject();
					edge.put("id", "e"+Integer.toString(i) + "-" +  Integer.toString(j));
					edge.put("source", "n"+Integer.toString(i));
					edge.put("target", "n"+Integer.toString(j));
					edges.add(edge);
				}
				
				
			}
		}
		root.put("nodes", nodes);
		root.put("edges", edges);
		/*
		StringWriter out = new StringWriter();
		root.writeJSONString(out);
		File f = new File(path);
		FileWriter fw = new FileWriter(f);
		fw.write(out.toString());
		fw.close();*/
		File f = new File(path);
		FileWriter fw = new FileWriter(f);
		fw.write(root.toJSONString());
		fw.close();
		
		
	}
	//seleziona con la percentuale
		public EntityTopicGraph getCentralityFilteredGraph(double pct) throws Exception{
			double[] centrality = this.getCentrality();
			Arrays.sort(centrality);
			double filt = centrality[(int) (pct * centrality.length)];
			return this.getCentralityFilteredGraphByValue(filt);
		}
		//Seleziona un numero costante di nodi
		public EntityTopicGraph getCentralityFilteredGraph(int val) throws Exception{
			double[] centrality = this.getCentrality();
			Arrays.sort(centrality);
			double filt = centrality[centrality.length - val];
			return this.getCentralityFilteredGraphByValue(filt);
		}
	
		//Questo metodo filtra i nodi per centralità andado ad eliminare tutti quelli minori o
		//uguali a filt e tutti quelli con centralità pari a 0
	public EntityTopicGraph getCentralityFilteredGraphByValue(double filt) throws Exception{
		EntityTopicGraph ret = new EntityTopicGraph(this.topic);
		double[] centrality = this.getCentrality();
		for(int i = 0; i < centrality.length; i++){
			if(centrality[i] >= filt && centrality[i] > 0.0){
				ret.addVertex(this.vertexDictionary.get(i));
			}
		}
		ret.initializeMatrix();
		for(int i = 0; i < ret.getVertexDictionary().size() - 1; i++){
			int idOriginalMatrixI = this.vertexDictionary.indexOf(ret.getVertexDictionary().get(i));
			for(int j = i + 1; j < ret.getVertexDictionary().size(); j++){
				int idOriginalMatrixJ = this.vertexDictionary.indexOf(ret.getVertexDictionary().get(j));
				ret.getAdiacentMatrix()[i][j] = this.adiacentMatrix[idOriginalMatrixI][idOriginalMatrixJ];
				ret.getAdiacentMatrix()[j][i] = ret.getAdiacentMatrix()[i][j];
			}
		}
		return ret;
	}
	
	//Filtra il grafo utilizzando le classe di entità riconosciute dal ner utilizzando una whitelist.
	//Se la variabile interclassOnly viene settata a true tutti gli archi che connettono entità
	//dello stesso tipo vengono eliminati
	public EntityTopicGraph getFilteredGraph(CustomEntityMatcher cem, boolean interclassOnly){
		EntityTopicGraph ret = new EntityTopicGraph(this.topic);
		Iterator<CustomEntity> it = this.vertexDictionary.iterator();
		while(it.hasNext()){
			CustomEntity ce = it.next();
			if(cem.match(ce))
				try {
					ret.addVertex(ce);
				} catch (Exception e) {}
		}
		ret.initializeMatrix();
		for(int i = 0; i < ret.getVertexDictionary().size() - 1; i++){
			int idOriginalMatrixI = this.vertexDictionary.indexOf(ret.getVertexDictionary().get(i));
			for(int j = i + 1; j < ret.getVertexDictionary().size(); j++){
			
				if(!(interclassOnly && ret.getVertexDictionary().get(i).entityClass.equals(ret.getVertexDictionary().get(j).entityClass))){
					int idOriginalMatrixJ = this.vertexDictionary.indexOf(ret.getVertexDictionary().get(j));
					ret.getAdiacentMatrix()[i][j] = this.adiacentMatrix[idOriginalMatrixI][idOriginalMatrixJ];
					ret.getAdiacentMatrix()[j][i] = ret.getAdiacentMatrix()[i][j];
					
				}
				else{
					ret.getAdiacentMatrix()[i][j] = 0.0;
					ret.getAdiacentMatrix()[j][i] = 0.0;
				}
			}
		}
		return ret;
	}

}
