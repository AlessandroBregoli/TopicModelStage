package it.unimib.disco.ab.ner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.unimib.disco.ab.textPreprocessing.StringMatcher;
//Questa classe dato  un array di pattern controlla se la stringa in ingresso
//matcha con qualcuno dei suddetti
public class NerStringMatcher extends StringMatcher {
	private Pattern[] pattern;
	public NerStringMatcher(String[] regex) {
		this.pattern = new Pattern[regex.length];
		for(int i = 0; i < regex.length; i++){
			this.pattern[i] = Pattern.compile(regex[i]);
		}
	}
	@Override
	public boolean match(String str) {
		for(int i = 0; i < this.pattern.length; i++){
			if(pattern[i].matcher(str).matches())
				return true;
		}
		return false;
	}

}
