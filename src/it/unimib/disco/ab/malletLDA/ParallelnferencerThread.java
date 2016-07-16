package it.unimib.disco.ab.malletLDA;

import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Instance;

public class ParallelnferencerThread extends Thread{
	private Parallelnferencer monitor;
	public static final int numIteration = 50;
	public static final int thinning = 5;
	public static final int burIn = 25;
	public ParallelnferencerThread(Parallelnferencer monitor){
		this.monitor = monitor;
	}
	
	@Override
	public void run(){
		double purity = this.monitor.purity;
		TopicInferencer inferencer = this.monitor.inferencer;
		while(true){
			Instance sentence = this.monitor.getSentenceId();
			if(sentence == null){
				return;
			}
			long sentenceID = (long) sentence.getName();
			double[] infer = inferencer.getSampledDistribution(sentence, ParallelnferencerThread.numIteration, 
																		 ParallelnferencerThread.thinning, 
																		 ParallelnferencerThread.burIn);
			int maxID = 0;
			for(int i = 1; i < infer.length; i++){
				if(infer[i] > infer[maxID])
					maxID = i;
			}
			if(infer[maxID] > purity){
				this.monitor.addSenteceTopic(sentenceID, maxID, infer.length);
			}
		}
	}
}
