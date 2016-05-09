package it.unimib.disco.ab.ner;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.sequences.DocumentReaderAndWriter;
import edu.stanford.nlp.util.Triple;
import it.unimib.disco.ab.malletLDA.CustomTopicModel;

import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

import cc.mallet.topics.TopicInferencer;
	
public class Ner {
	private AbstractSequenceClassifier<CoreLabel> classifier;
	private TopicInferencer inferencer;
	private String dataSet;
	public Ner(String serializedClassifier, TopicInferencer inferencer, String dataSet) throws ClassCastException, ClassNotFoundException, IOException{
		this.classifier = CRFClassifier.getClassifier(serializedClassifier);
		this.inferencer = inferencer;
		this.dataSet = dataSet;
		
	}
	
	
	public 	TreeMap<CustomEntity, Double[]>  ciao(){
		
		
	}
}
