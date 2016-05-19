package it.unimib.disco.ab.ner;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.sequences.DocumentReaderAndWriter;
import edu.stanford.nlp.util.Triple;
import it.unimib.disco.ab.malletLDA.CustomTopicModel;
import it.unimib.disco.ab.malletLDA.InstancesBuilder;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
	
public class Ner { 
	private AbstractSequenceClassifier<CoreLabel>[] classifier;
	private TopicInferencer inferencer;
	private InstanceList dataSet;
	private List<String> stopWords;
	public Ner(String serializedClassifier[], TopicInferencer inferencer, InstanceList dataSet, List<String> stopWords) throws ClassCastException, ClassNotFoundException, IOException{
		this.classifier = new AbstractSequenceClassifier[serializedClassifier.length];
		for(int i = 0; i < serializedClassifier.length; i++){
			this.classifier[i] = CRFClassifier.getClassifier(serializedClassifier[i]);
		}
		this.inferencer = inferencer;
		this.dataSet = dataSet;
		this.stopWords = stopWords;
	}
	
	
	public 	TreeMap<CustomEntity, TopicStat>  entityTopicRelation(){
		TreeMap<CustomEntity, TopicStat> relation = new TreeMap<CustomEntity, TopicStat>();
		Iterator iter = this.dataSet.iterator();
		
		while(iter.hasNext()){
			Instance inst = (Instance) iter.next();
			NerMerge nerMerge = new NerMerge((String)inst.getSource());
			for(int i = 0; i < this.classifier.length; i++){
				List<Triple<String,Integer,Integer>> out = this.classifier[i].classifyToCharacterOffsets((String)inst.getSource());
				for(Triple<String,Integer,Integer> triple: out){
					nerMerge.add(triple);
				}
			}
			
			if(!nerMerge.iterator().hasNext())
				continue;
			double[] testProb = this.inferencer.getSampledDistribution(inst, 50, 5, 25);
			Iterator it = nerMerge.iterator();
			while(it.hasNext()){
				NerMergeElement ele = (NerMergeElement) it.next();
				CustomEntity customEntity = new CustomEntity();
	        	customEntity.entityString = ele.text;
	        	customEntity.entityClass = ele.label;
	        	if(this.stopWords.indexOf(customEntity.entityString.toLowerCase()) != -1 || customEntity.entityClass.equals("0"))
	        		continue;
	        	TopicStat t = relation.get(customEntity);
	        	if(t == null){
	        		t = new TopicStat(testProb);
	        		relation.put(customEntity, t);
	        	}
	        	else{
	        		try {
						t.add(testProb);
					} catch (Exception e) {
						e.printStackTrace();
					}
	        	}

			}
			
		}
		return relation;
	}
}
