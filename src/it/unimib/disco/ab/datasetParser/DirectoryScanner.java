package it.unimib.disco.ab.datasetParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public abstract class DirectoryScanner {
	protected File directory;
	protected ArrayList<Article> articles;
	
	public DirectoryScanner(File directory){
		this.articles = new ArrayList<Article>();
		this.directory = directory;
	}
	
	public void startScan(){
		this.listFilesForFolder(this.directory);
	}
	
	protected abstract void listFilesForFolder(final File folder);
	
	public List<Article> getArticles(){
		return this.articles;
	}

}
