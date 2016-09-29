package it.unimib.disco.ab.datasetParser;

import java.util.Date;
import java.util.LinkedList;

public class Article {
	public Date date;
	public LinkedList<String> topics;
	public LinkedList<String> places;
	public LinkedList<String> people;
	public LinkedList<String> orgs;
	public LinkedList<String> exchanges;
	public LinkedList<String> companies;
	public String title;
	public String text;
	
	
	public Article() {
		this.date = new Date();
		this.topics = new LinkedList<String>();
		this.places = new LinkedList<String>();
		this.people = new LinkedList<String>();
		this.orgs = new LinkedList<String>();
		this.exchanges = new LinkedList<String>();
		this.companies = new LinkedList<String>();
		this.title = "";
		this.text = "";
	}
	
	
}
