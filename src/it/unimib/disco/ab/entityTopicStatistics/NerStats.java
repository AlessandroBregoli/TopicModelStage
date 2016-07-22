package it.unimib.disco.ab.entityTopicStatistics;

import it.unimib.disco.ab.ner.CustomEntity;

import java.io.Serializable;
import java.util.TreeMap;

public class NerStats implements Serializable{
	public TreeMap<CustomEntity, TopicStat> relation = null;
	public long sentencePerTopic[] = null;
}
