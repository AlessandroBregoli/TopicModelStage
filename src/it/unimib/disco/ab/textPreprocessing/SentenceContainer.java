package it.unimib.disco.ab.textPreprocessing;

import it.unimib.disco.ab.ner.CustomEntity;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;


//Questa classe è il contenitore di frasi; espone anche una serie di metodi per il filtraggio
public class SentenceContainer {
	public TreeMap<Long, Sentence> sentences;
	private Iterator<String> filterWords;
	private Iterator<Long> sentenceIterator;
	private Pattern filter;
	private int nThreads;
	public SentenceContainer(){
		this.sentences = new TreeMap<Long, Sentence>();
	}
	
	public synchronized void filterUsingIterator(Iterator<String> filterWords, int nThreads){
		this.filterWords = filterWords;
		
		while(this.filterWords.hasNext()){
			this.nThreads = nThreads;
			String stopWord = this.filterWords.next();
			//System.out.println(stopWord);
			this.filter = Pattern.compile("(?i)\\b" + Pattern.quote(stopWord) + "\\b");
			this.sentenceIterator = this.sentences.keySet().iterator();
			for(int i = 0; i < nThreads; i++){
				new FilteringUsingIteratorThread(this).start();
			}
			while(this.nThreads > 0){
				try {
					wait();
				} catch (InterruptedException e) {e.printStackTrace();}
			}
			
		}			
	}
	
	Pattern getPattern(){
		return this.filter;
	}
	
	synchronized long nextSentence(){
		if(!this.sentenceIterator.hasNext()){
			this.nThreads--;
			notifyAll();
			return -1;
		}
		return this.sentenceIterator.next();
	}
	 
	@Override
	public Object clone(){
		SentenceContainer sc = new SentenceContainer();
		for(long sentenceID: this.sentences.keySet()){
			sc.sentences.put(sentenceID, (Sentence) this.sentences.get(sentenceID).clone());
		}
		return sc;
		
	}
}
