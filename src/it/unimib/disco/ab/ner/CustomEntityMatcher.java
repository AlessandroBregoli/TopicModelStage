package it.unimib.disco.ab.ner;

//Questa interfaccia è pensata per confrontare una entità
public interface CustomEntityMatcher {
	public boolean match(CustomEntity ce);
}
