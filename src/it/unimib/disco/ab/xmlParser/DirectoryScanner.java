package it.unimib.disco.ab.xmlParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class DirectoryScanner {
	private File directory;
	private ArrayList<Article> articles;
	
	public DirectoryScanner(File directory){
		this.articles = new ArrayList<Article>();
		this.directory = directory;
	}
	
	public void startScan(){
		this.listFilesForFolder(this.directory);
	}
	
	private void listFilesForFolder(final File folder) {
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            this.listFilesForFolder(fileEntry);
	        } else {
	        	SAXParserFactory factory = SAXParserFactory.newInstance();
		        SAXParser saxParser;
		        System.out.println(fileEntry.getName());
				try {
					saxParser = factory.newSAXParser();
					ReutersParserHandler userhandler = new ReutersParserHandler(this.articles);
					saxParser.parse(fileEntry, userhandler);
				} catch (ParserConfigurationException | SAXException | IOException e) {				
					e.printStackTrace();
				}
	        }
	    }
	}
	
	public List<Article> getArticles(){
		return this.articles;
	}

}
