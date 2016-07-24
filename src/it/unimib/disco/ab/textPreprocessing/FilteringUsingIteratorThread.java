package it.unimib.disco.ab.textPreprocessing;

import java.util.regex.Pattern;

public class FilteringUsingIteratorThread extends Thread {
	
	SentenceContainer monitor;
	public FilteringUsingIteratorThread(SentenceContainer monitor){
		this.monitor = monitor;
	}
	
	public void run(){
		Pattern p = this.monitor.getPattern();
		while(true){
			long sentenceID = this.monitor.nextSentence();
			if(sentenceID < 0)
				return;
			Sentence sentence = this.monitor.sentences.get(sentenceID);
			sentence.text = p.matcher(sentence.text).replaceAll(" ");
			//System.out.println(sentence.text);
		}
	}
}
