package it.unimib.disco.ab.malletLDA;

import it.unimib.disco.ab.datasetParser.Article;
import it.unimib.disco.ab.datasetParser.ReutersParserHandler;

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
	//Data dell'articolo
	private Date articleDate;
	
	private long sentenceConunter;
	
	public FileIteratorSplitter(File[] directory, FileFilter filt, Pattern p) {
		super(directory, filt, p);
		this.textPosition = 0;
		this.splittedText = null;
		this.articles = new LinkedList<Article>();
		this.sentenceConunter = 0;
	}
	
	
	public Instance next(){
		while(this.splittedText == null || this.textPosition >= this.splittedText.length){
			this.splitText();
		}
		//Crea un contenitore che al suo interno avrà il valore della stringa di testo
		//e la data corrispondente all'articolo
		InstanceSourceContainer sc = new InstanceSourceContainer();
		sc.text = this.splittedText[this.textPosition];
		sc.date = this.articleDate;
		//Crea l'istanza vera e propria; la quale ha come targetName il targetName ereditato da FileIterator, 
		//e come nome il titolo dell'articolo unito alla posizione dellla frase nell'articolo
		Instance i = new Instance(this.splittedText[this.textPosition],this.targetName, this.sentenceConunter, sc);
		this.sentenceConunter++;
		this.textPosition++;
		return i;
		
	}
	
	//Questo metodo splitta gli articoli di un file
	//Se gli articoli sono finiti parsa il file successivo e splitta le frasi dell'articolo
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
