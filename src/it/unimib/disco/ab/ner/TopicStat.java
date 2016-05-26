package it.unimib.disco.ab.ner;
//This class is a container for the data relation between entities and topics 
public class TopicStat {
	public double[] stat;
	int numEl;
	public TopicStat(double[] stat){
		this.stat = stat;
		this.numEl = 1;
	}
	
	public void add(double[] stat) throws Exception{
		if(this.stat.length != stat.length){
			throw new Exception();
		}
		for(int i = 0; i < this.stat.length; i++){
			this.stat[i] += stat[i];
		}
		this.numEl++;
	}
	
	public double[] mean(){
		double[] ret = this.stat.clone();
		for(int i = 0; i < this.stat.length; i++){
			ret[i] /= this.numEl;
		}
		return ret;
	}
	public int getNumEl(){
		return this.numEl;
	}
}
