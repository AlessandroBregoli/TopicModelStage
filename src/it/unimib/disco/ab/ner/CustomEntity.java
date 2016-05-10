package it.unimib.disco.ab.ner;

import java.io.Serializable;

//this class contain the single instance of a entity
public class CustomEntity implements Comparable{
	public String entityString;
	public String entityClass;
	
	@Override
	public boolean equals(Object o){
		CustomEntity c = (CustomEntity) o;
		return this.entityString.equals(c.entityString) && this.entityClass.equals(c.entityClass);
	}

	@Override
	public int compareTo(Object o) {
		CustomEntity c = (CustomEntity) o;
		return c.entityString.compareTo(this.entityString) != 0?c.entityString.compareTo(this.entityString):c.entityClass.compareTo(this.entityClass);
		
		
	}
}
