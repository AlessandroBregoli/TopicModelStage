package it.unimib.disco.ab.graphs;

import it.unimib.disco.ab.ner.CustomEntity;
import it.unimib.disco.ab.ner.CustomEntityMatcher;
//Implementazione di CustomEntityMatcher che confronta entità passate al metodo match
//per classe e ritorna True solo se la classe è contenuta nella whitelist

public class CustomEntityMatcherByClass implements CustomEntityMatcher{
	private String[] whiteList;
	public CustomEntityMatcherByClass(String[] whiteList) {
		this.whiteList = whiteList;
	}
	@Override
	public boolean match(CustomEntity ce) {
		for(String s: this.whiteList){
			if(s.equals(ce.entityClass))
				return true;
		}
		return false;
	}

}
