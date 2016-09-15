package it.unimib.disco.ab.graphs;

import it.unimib.disco.ab.ner.CustomEntity;
import it.unimib.disco.ab.ner.CustomEntityMatcher;

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
