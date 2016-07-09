package it.unimib.disco.ab.textPreprocessing;

import it.unimib.disco.ab.xmlParser.Article;

import java.util.ArrayList;
import java.util.TreeMap;

public class SentenceSplitter extends SentenceContainer{
	
	
	public SentenceSplitter(ArrayList<Article> articles){
		this.sentences = new TreeMap<Long, Sentence>();
		long sentenceID = 0;
		for(int i = 0; i < articles.size(); i++){
			String[] tmp = articles.get(i).text.split("(?<=\\p{Ll}[.?!;])\\s+(?=\\p{Lu})");
			for(String sentence: tmp){
				Sentence s = new Sentence();
				s.articleID = i;
				s.text = sentence;
				sentences.put(sentenceID++, s);
			}
		}
	}
}
