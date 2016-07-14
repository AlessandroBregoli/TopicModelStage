package it.unimib.disco.ab.malletLDA;

import it.unimib.disco.ab.textPreprocessing.Sentence;
import it.unimib.disco.ab.textPreprocessing.SentenceContainer;

import java.util.Iterator;
import java.util.Set;

import cc.mallet.types.Instance;

public class SentenceIterator implements Iterator {
	
	private SentenceContainer sentences;
	private Iterator<Long> keysIt;
	public  SentenceIterator(SentenceContainer sentences) {
		this.sentences = sentences;
		this.keysIt = this.sentences.sentences.keySet().iterator();
	}
	@Override
	public boolean hasNext() {
		return this.keysIt.hasNext();
	}

	@Override
	public Instance next() {
		Long l = this.keysIt.next();
		Sentence sent = this.sentences.sentences.get(l);
		Instance i = new Instance(sent.text, sent.articleID, l, null);
		return i;
	}

}
