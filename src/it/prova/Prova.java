package it.prova;

import it.unimib.disco.ab.ner.NerStringMatcher;

import java.util.LinkedList;

public class Prova {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String[] regex = {".", "[\\s]*[0-9]*[\\s]*"};
		NerStringMatcher m = new NerStringMatcher(regex);
		System.out.println(m.match("a"));
	}

}