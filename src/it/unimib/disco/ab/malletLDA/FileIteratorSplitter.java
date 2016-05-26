package it.unimib.disco.ab.malletLDA;

import it.unimib.disco.ab.xmlParser.Article;
import it.unimib.disco.ab.xmlParser.ReutersParserHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.LinkedList;
import java.util.regex.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import cc.mallet.pipe.iterator.FileIterator;
import cc.mallet.types.Instance;

public class FileIteratorSplitter extends FileIterator {
	//splitted text
	private String[] splittedText;
	//name of the instance in the super class
	private String name;
	//target name of the instance in the super class
	private String targetName;
	//The iterator position into the splitted text sequence
	private int textPosition;
	//The articles readed from the file
	private LinkedList<Article> articles;
	
	private Date articleDate;
	
	public FileIteratorSplitter(File[] directory, FileFilter filt, Pattern p) {
		super(directory, filt, p);
		this.textPosition = 0;
		this.splittedText = null;
		this.articles = new LinkedList<Article>();
	}
	
	
	public Instance next(){
		while(this.splittedText == null || this.textPosition >= this.splittedText.length){
			this.splitText();
		}
		InstanceSourceContainer sc = new InstanceSourceContainer();
		sc.text = this.splittedText[this.textPosition];
		sc.date = this.articleDate;
		Instance i = new Instance(this.splittedText[this.textPosition],this.targetName, this.name + "_" + this.textPosition, sc);
		this.textPosition++;
		return i;
		
	}
	
	private void splitText(){
		this.textPosition = 0;
		if(this.articles.size() == 0){
		
			Instance s = super.next();
			File f = (File) s.getData();
			SAXParserFactory factory = SAXParserFactory.newInstance();
	        SAXParser saxParser;
			try {
				saxParser = factory.newSAXParser();
				ReutersParserHandler userhandler = new ReutersParserHandler(this.articles);
				saxParser.parse(f, userhandler);
			} catch (ParserConfigurationException | SAXException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			this.targetName = (String) s.getTarget();
		}
		
		Article article = this.articles.pop();
		System.out.println(article.title);
		this.splittedText = article.text.split("(?<=\\p{Ll}[.?!;])\\s+(?=\\p{Lu})");
		this.name = article.title;
		this.articleDate = article.date;
		
			
		
	}
	public boolean hasNext(){
		return super.hasNext() || this.textPosition < this.splittedText.length || this.articles.size() > 0;
	}

}
