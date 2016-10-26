package it.unimib.disco.ab.ner;

import java.util.Iterator;
import java.util.Set;

//Questo iteratore ritorna le stringhe delle entità contenute in una collezione
public class EntitySetIterator implements Iterator<String> {
	private Iterator<CustomEntity> it;
	public EntitySetIterator(Set<CustomEntity> entities) {
		// TODO Auto-generated constructor stub
		this.it = entities.iterator();
	}
	@Override
	public boolean hasNext() {
		return this.it.hasNext();
	}

	@Override
	public String next() {
		return this.it.next().entityString;
	}

}
