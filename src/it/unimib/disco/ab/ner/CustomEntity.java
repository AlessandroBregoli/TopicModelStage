package it.unimib.disco.ab.ner;

public class CustomEntity {
	public String entityString;
	public String entityClass;
	
	@Override
	public boolean equals(Object o){
		CustomEntity c = (CustomEntity) o;
		return this.entityString.equals(c.entityString) && this.entityClass.equals(c.entityClass);
	}
}
