package it.unimib.disco.ab.xmlParser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ReutersParserHandler extends DefaultHandler{
	private List<Article> articleList;
	private Article article;
	private boolean date;
	private boolean topics;
	private boolean d;
	private boolean places;
	private boolean people;
	private boolean orgs;
	private boolean exchanges;
	private boolean companies;
	private boolean text;
	private boolean title;
	private boolean body;
	
	public ReutersParserHandler(List<Article> articleList) {
		super();
		this.articleList = articleList;
		this.date = false;
		this.topics = false;
		this.d = false;
		this.places = false;
		this.people = false;
		this.orgs = false;
		this.exchanges = false;
		this.companies = false;
		this.text = false;
		this.title = false;
		this.body = false;
	} 
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes)throws SAXException{
		if(qName.equalsIgnoreCase("reuters")){
			this.article = new Article();
		}else 
		if(qName.equalsIgnoreCase("date")){
				this.date = true;
		}else
		if(qName.equalsIgnoreCase("topics")){
			this.topics = true;
		}else
		if(qName.equalsIgnoreCase("d")){
			this.d = true;
		}else
		if(qName.equalsIgnoreCase("places")){
			this.places = true;
		}else
		if(qName.equalsIgnoreCase("people")){
			this.people = true;
		}else
		if(qName.equalsIgnoreCase("orgs")){
			this.orgs = true;
		}else
		if(qName.equalsIgnoreCase("exchanges")){
			this.exchanges = true;
		}else
		if(qName.equalsIgnoreCase("companies")){
			this.companies = true;
		}else
		if(qName.equalsIgnoreCase("text")){
			this.text = true;
		}else
		if(qName.equalsIgnoreCase("title")){
			this.title = true;
		}else
		if(qName.equalsIgnoreCase("body")){
			this.body = true;
		}
		
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if(qName.equalsIgnoreCase("reuters")){
			this.articleList.add(this.article);
		}else
		if(qName.equalsIgnoreCase("date")){
			this.date = false;
		}else
		if(qName.equalsIgnoreCase("topics")){
			this.topics = false;
		}
		else
		if(qName.equalsIgnoreCase("d")){
			this.d = false;
		}else
		if(qName.equalsIgnoreCase("places")){
			this.places = false;
		}else
		if(qName.equalsIgnoreCase("people")){
			this.people = false;
		}else
		if(qName.equalsIgnoreCase("orgs")){
			this.orgs = false;
		}else
		if(qName.equalsIgnoreCase("exchanges")){
			this.exchanges = false;
		}else
		if(qName.equalsIgnoreCase("companies")){
			this.companies = false;
		}else
		if(qName.equalsIgnoreCase("text")){
			this.text = false;
		}else
		if(qName.equalsIgnoreCase("title")){
			this.title = false;
		}else
		if(qName.equalsIgnoreCase("body")){
			this.body = false;
		}
	}
	
	public void characters(char ch[], int start, int length) throws SAXException {
		if(this.date){
			DateFormat d = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss.SS");
			try {
				this.article.date = d.parse(new String(ch, start, length));
			} catch (ParseException e) {}
		}else
		if(this.topics && this.d){
			this.article.topics.add(new String(ch, start, length));
		}else
		if(this.places && this.d){
			this.article.places.add(new String(ch, start, length));
		}else
		if(this.people && this.d){
			this.article.people.add(new String(ch, start, length));
		}else
		if(this.orgs && this.d){
			this.article.orgs.add(new String(ch, start, length));
		}else
		if(this.exchanges && this.d){
			this.article.exchanges.add(new String(ch, start, length));
		}else
		if(this.companies && this.d){
			this.article.companies.add(new String(ch, start, length));
		}else
		if(this.text && this.title){
			this.article.title = new String(ch, start, length);
		}else
		if(this.text && this.body){
			//This part can be improved using StringBuffer
			this.article.text = this.article.text + new String(ch, start, length);
			
		}
	}
	
}


