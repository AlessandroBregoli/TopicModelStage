package it.unimib.disco.ab.textPreprocessing;

import it.unimib.disco.ab.ner.CustomEntity;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

//TODO implementare multithread del filtro
public class SentenceContainer {
	public TreeMap<Long, Sentence> sentences;
	private Iterator<String> filterWords;
	private Iterator<Long> sentenceIterator;
	private Pattern filter;
	private int nThreads;
	public SentenceContainer(){
		this.sentences = new TreeMap<Long, Sentence>();
	}
	

	public void filterUsingList(List<String> filteredWord){
		for(long sentenceID: this.sentences.keySet()){
			for(String s: filteredWord){
				this.sentences.get(sentenceID).text = SentenceContainer.replaceAll(this.sentences.get(sentenceID).text, s);
			}
			
		}
	}
	
	//TODO eliminare questo spaghetti-code; non credo sia giusto fare stare qui le CustomEntity
	public void filterUsingEntitySet(Set<CustomEntity> filteredWord){
		for(long sentenceID: this.sentences.keySet()){
			for(CustomEntity s: filteredWord){
				this.sentences.get(sentenceID).text = SentenceContainer.replaceAll(this.sentences.get(sentenceID).text, s.entityString);
			}
			
		}
	}
	
	private static String replaceAll(String string, String replace){
		return string.replaceAll("(?i)\\b" + Pattern.quote(replace) + "\\b", "");
	}
	public synchronized void filterUsingIterator(Iterator<String> filterWords, int nThreads){
		this.filterWords = filterWords;
		this.nThreads = nThreads;
		while(this.filterWords.hasNext()){
			String stopWord = this.filterWords.next();
			System.out.println(stopWord);
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
