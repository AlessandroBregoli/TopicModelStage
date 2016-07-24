package it.unimib.disco.ab.ner;

import java.util.Iterator;
import java.util.Set;

public class EntitySetIterator implements Iterator<String> {
	private Iterator<CustomEntity> it;
	public EntitySetIterator(Set<CustomEntity> entities) {
		// TODO Auto-generated constructor stub
		it = entities.iterator();
	}
	@Override
	public boolean hasNext() {
		return it.hasNext();
	}

	@Override
	public String next() {
		return it.next().entityString;
	}

}
