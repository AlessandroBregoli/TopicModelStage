package it.unimib.disco.ab.ner;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

//this class contain the single instance of a entity
public class CustomEntity implements Comparable, Serializable{
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
		return c.entityString.toLowerCase().compareTo(this.entityString.toLowerCase()) != 0?c.entityString.toLowerCase().compareTo(this.entityString.toLowerCase()):c.entityClass.compareTo(this.entityClass);
		
		
	}
}
