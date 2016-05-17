package it.prova;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.Triple;
import it.unimib.disco.ab.ner.CustomEntity;
import it.unimib.disco.ab.ner.Ner;

public class NerProva {

	public static void main(String[] args) throws ClassCastException, ClassNotFoundException, IOException {
		// TODO Auto-generated method stub
		AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifier("/home/alessandro/Schifezze/stanford-ner-2015-12-09/classifiers/english.conll.4class.distsim.crf.ser.gz");
		List<Triple<String,Integer,Integer>> out = classifier.classifyToCharacterOffsets("New York, the city that never slip; home for Stephen Hawking");
		for (Triple<String,Integer,Integer> label : out) {
	        System.out.println(label.first);
	      }
	}

}
