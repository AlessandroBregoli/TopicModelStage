package it.unimib.disco.ab.ner;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.stanford.nlp.util.Triple;
import it.unimib.disco.ab.textPreprocessing.Sentence;

public class ParallelNerThread extends Thread {
	ParallelNer monitor;
	public ParallelNerThread(ParallelNer monitor){
		this.monitor = monitor;
	}
	
	@Override
	public void run(){
		while(true){
			long sentenceID = this.monitor.getSentenceId();
			if(sentenceID < 0)
				return;
			Sentence sentence = this.monitor.sentences.sentences.get(sentenceID);
			NerMerge nerMerge = new NerMerge(sentence.text);
			
			for(int i = 0; i < this.monitor.classifier.length; i++){
				List<Triple<String,Integer,Integer>> out = this.monitor.classifier[i].classifyToCharacterOffsets(sentence.text);
				for(Triple<String,Integer,Integer> triple: out){
					nerMerge.add(triple);
				}
			}
			this.monitor.sentencesAcquisition();{
				Iterator<CustomEntity> it = nerMerge.iterator();
				while(it.hasNext()){
					CustomEntity customEntity = it.next();
		        	if(customEntity.entityClass.equals("0"))
		        		continue;
		        	
		        	LinkedList<Long> t = this.monitor.entities.get(customEntity);
		        	if(t == null){
		        		t = new LinkedList<Long>();
		        		t.add(sentenceID);
		        		this.monitor.entities.put(customEntity, t);
		        	}
		        	else{
		        		if(t.getLast() <= sentenceID){
		        			t.add(sentenceID);
		        		}
		        		else{ if(t.getFirst() >= sentenceID){
		        			t.addFirst(sentenceID);
		        		}else{
		        			for(int i = t.size()- 2; i >= 0; i--){
		        				if(t.get(i) <= sentenceID){
		        					t.add(i+1, sentenceID);
		        					break;
		        					
		        				}
		        			}
		        		}
		        		}
		        		
		        	}

				}
			
			}this.monitor.sentencesRelease();
		}
	}

}
