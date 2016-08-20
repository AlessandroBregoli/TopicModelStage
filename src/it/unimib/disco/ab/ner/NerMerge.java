package it.unimib.disco.ab.ner;

import java.util.ArrayList;
import java.util.Iterator;

import edu.stanford.nlp.util.Triple;

public class NerMerge implements Iterable<CustomEntity> {
	private ArrayList<Integer> indices = new ArrayList<Integer>();
	private ArrayList<String> labels = new ArrayList<String>();
	private String sentence;
	public NerMerge(String sentence){
		this.sentence = sentence;
	}
	
	//return the position if it  not occupied or -1
	private int findPosition(int value){
		if(this.indices.size() == 0){
			return 0;
		}
		return findPositionRic(value, 0, this.indices.size() - 1);
	}
	private int findPositionRic(int value, int index1, int index2){
		if(index2 < index1)
			return index1;
		if(this.indices.get((index2 + index1)/2) == value){
			return -1; 
		}
		if(this.indices.get((index2 + index1)/2) > value){
			return findPositionRic(value, index1, (index2 + index1)/2 - 1);
		}
		else{
			return findPositionRic(value, (index2 + index1)/2 + 1, index2);
		}
	}
	//Add data if all the constraint are respected
	public void add(Triple<String, Integer, Integer> data){
		int indexPosition = findPosition(data.second());
		if(indexPosition != -1 && (indexPosition % 2) == 0){
			if(this.indices.size() == 0 || this.indices.size() == indexPosition){
				this.indices.add(data.second());
				this.indices.add(data.third());
				this.labels.add(data.first());
			}
			else if(this.indices.get(indexPosition + 1) > data.third()){
				this.indices.add(indexPosition, data.second());
				this.indices.add(indexPosition + 1, data.third());
				this.labels.add(indexPosition / 2, data.first());
			}
		}
		
	}
	@Override
	public Iterator<CustomEntity> iterator() {
		
		return new NerMergeIterator();
	}
	
	private class NerMergeIterator implements Iterator<CustomEntity>{
		private int index;
		public NerMergeIterator() {
			this.index = 0;
		}
		@Override
		public boolean hasNext() {
			return NerMerge.this.labels.size() > this.index;
		}

		@Override
		public CustomEntity next() {
			CustomEntity ret = new CustomEntity();
			ret.entityClass = NerMerge.this.labels.get(this.index);
			ret.entityString = NerMerge.this.sentence.substring(NerMerge.this.indices.get(this.index * 2), 
														NerMerge.this.indices.get(this.index * 2 + 1)).
														replace("\n", " ");
			this.index++;
			return ret;
		}
		
	}
}
