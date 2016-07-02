package it.unimib.disco.ab.entityTopicStatistics;

import java.io.Serializable;
import java.util.Date;
// TODO implementare un comparatore per permettere di ordinare per data
public class TopicStatTuple implements Serializable{
	public double[] stat;
	public Date date;
	public long sentenceID;
	public String sentenceText;

}
