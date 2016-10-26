package it.unimib.disco.ab.graphs;

import java.util.ArrayList;

//Classe che contiene i testi delle frasi; nello specifico è stata pensata per essere
//utilizzata con le componenti connesse; in modo da generare riassunti.
//Tuttavia le componenti connesse non sono effettivamente utilizzabili; forse di potrebbe
//fare la stessa cosa con le comunità
public class ConnectedComponentSentences {
	public ArrayList<ArrayList<String>> ccSentences;
	public ConnectedComponentSentences(){
		this.ccSentences = new ArrayList<ArrayList<String>>();
	}

}
