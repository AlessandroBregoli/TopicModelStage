package it.unimib.disco.ab.ner;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.sequences.DocumentReaderAndWriter;
import edu.stanford.nlp.util.Triple;
import it.unimib.disco.ab.entityTopicStatistics.NerStats;
import it.unimib.disco.ab.entityTopicStatistics.ParallelMedia;
import it.unimib.disco.ab.entityTopicStatistics.TopicStat;
import it.unimib.disco.ab.malletLDA.CustomTopicModel;
import it.unimib.disco.ab.malletLDA.InstanceSourceContainer;
import it.unimib.disco.ab.malletLDA.InstancesBuilder;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

//Questa classe è sfrutta lo standford-ner per estrarre entità e legarle hai topic della frase
//da cui sono state estratte sfruttando più classificatori e unendo i vari risultati
public class Ner { 
	private AbstractSequenceClassifier<CoreLabel>[] classifier;
	private TopicInferencer inferencer;
	private InstanceList dataSet;
	private List<String> stopWords;
	private int nThreads;
	public Ner(String serializedClassifier[], TopicInferencer inferencer, InstanceList dataSet, List<String> stopWords, int nThreads) throws ClassCastException, ClassNotFoundException, IOException{
		this.classifier = new AbstractSequenceClassifier[serializedClassifier.length];
		for(int i = 0; i < serializedClassifier.length; i++){
			this.classifier[i] = CRFClassifier.getClassifier(serializedClassifier[i]);
		}
		this.inferencer = inferencer;
		this.dataSet = dataSet;
		this.stopWords = stopWords;
		this.nThreads = nThreads;
		
	}
	
	
	public 	NerStats  entityTopicRelation() throws Exception{
		Iterator iter = this.dataSet.iterator();
		//Per ogni frase cerco le varie enity e le unisco tramite la casse NerMerge 
		//Faccio anche inferenza del topc sulla frase.
		NerStats ret = new NerStats();
		ret.relation = new TreeMap<CustomEntity, TopicStat>();
		while(iter.hasNext()){
			
			Instance inst = (Instance) iter.next();
			NerMerge nerMerge = new NerMerge(((InstanceSourceContainer)inst.getSource()).text);
			for(int i = 0; i < this.classifier.length; i++){
				List<Triple<String,Integer,Integer>> out = this.classifier[i].classifyToCharacterOffsets(((InstanceSourceContainer)inst.getSource()).text);
				for(Triple<String,Integer,Integer> triple: out){
					nerMerge.add(triple);
				}
			}
			
			if(!nerMerge.iterator().hasNext())
				continue;
			double[] testProb = this.inferencer.getSampledDistribution(inst, 50, 5, 25);
			/*
			 //Questo ciclo serve per il debug; in particolare stampa a video le probabilità dei singoli topic
			 //per ogni sentence; ai fini del progetto è bene che ogni sentence abbia un solo topic predominante
			for(int i = 0; i < testProb.length; i++)
				System.out.print(testProb[i] + "\t");
			System.out.println();
			*/
			//cerco il topic della frase:
			int sentenceBestTopic = 0;
			for(int i = 0; i < testProb.length; i++)
				if(testProb[i] > testProb[sentenceBestTopic])
					sentenceBestTopic = i;
			if(ret.sentencePerTopic == null)
				ret.sentencePerTopic = new long[testProb.length];
			ret.sentencePerTopic[sentenceBestTopic]++;
			Date d = ((InstanceSourceContainer)inst.getSource()).date;
			Iterator it = nerMerge.iterator();
			while(it.hasNext()){
				CustomEntity customEntity = (CustomEntity) it.next();
	        	if(this.stopWords.indexOf(customEntity.entityString.toLowerCase()) != -1 || customEntity.entityClass.equals("0"))
	        		continue;
	        	TopicStat t = ret.relation.get(customEntity);
	        	if(t == null){
	        		t = new TopicStat(testProb,d, (long)inst.getName(),((InstanceSourceContainer)inst.getSource()).text, sentenceBestTopic);
	        		ret.relation.put(customEntity, t);
	        	}
	        	else{
	        		try {
						t.add(testProb,d,(long)inst.getName(),((InstanceSourceContainer)inst.getSource()).text, sentenceBestTopic);
					} catch (Exception e) {
						e.printStackTrace();
					}
	        	}

			}
			
		}
		double purity = 1/ret.sentencePerTopic.length*1.001;
		ParallelMedia p = new ParallelMedia(this.nThreads, ret.relation, purity);
		p.finalizeWork();
		
		
		return ret;
	}
}
