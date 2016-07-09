package it.unimib.disco.ab.textPreprocessing;

import java.util.TreeMap;

public class WordsFilter {
	public static void filterUsingArray(TreeMap<Long, Sentence> sentences, String[] filteredWord){
		for(long sentenceID: sentences.keySet()){
			for(String s: filteredWord){
				sentences.get(sentenceID).text = sentences.get(sentenceID).text.replace(s, "");
			}
			
		}
	}

}
