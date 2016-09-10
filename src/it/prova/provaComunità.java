package it.prova;

import it.unimib.disco.ab.graphs.EntityTopicGraph;
import it.unimib.disco.ab.graphs.comunityDetectionLib.GraphComunityExtractor;
import it.unimib.disco.ab.ner.CustomEntity;

public class provaComunità {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		EntityTopicGraph et = new EntityTopicGraph(0);
		CustomEntity c = new CustomEntity();
		c.entityClass = "a";
		c.entityString = "coap";
		et.addVertex(c);
		c = new CustomEntity();
		c.entityClass = "a";
		c.entityString = "pota";
		et.addVertex(c);
		c = new CustomEntity();
		c.entityClass = "a";
		c.entityString = "coaps";
		et.addVertex(c);
		c = new CustomEntity();
		c.entityClass = "a";
		c.entityString = "coapd";
		et.addVertex(c);
		et.initializeMatrix();
		et.getAdiacentMatrix()[0][3] = 1.0;
		et.getAdiacentMatrix()[1][2] = 1.0;
		et.getAdiacentMatrix()[2][3] = 1.0;
		GraphComunityExtractor gce = new GraphComunityExtractor();
		gce.generateComunities(et, 1.0, 57, 1, 50);
		for(int i = 0; i < gce.clustering.cluster.length; i++){
			System.out.println(gce.clustering.cluster[i]);
		}

	}

}
