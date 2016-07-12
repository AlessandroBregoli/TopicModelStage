package it.unimib.disco.ab.textPreprocessing;

import java.util.List;
import java.util.TreeMap;

//TODO implementare multithread del filtro
public class SentenceContainer {
	public TreeMap<Long, Sentence> sentences;
	
	public void filterUsingArray(String[] filteredWord){
		for(long sentenceID: this.sentences.keySet()){
			for(String s: filteredWord){
				this.sentences.get(sentenceID).text = SentenceContainer.replaceAll(this.sentences.get(sentenceID).text, s);
			}
			
		}
	}
	public void filterUsingList(List<String> filteredWord){
		for(long sentenceID: this.sentences.keySet()){
			for(String s: filteredWord){
				this.sentences.get(sentenceID).text = SentenceContainer.replaceAll(this.sentences.get(sentenceID).text, s);
			}
			
		}
	}
	private static String replaceAll(String string, String replace){
		return string.replaceAll("\\b" + replace + "\\b", "");
	}
}
