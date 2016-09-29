package it.unimib.disco.ab.datasetParser;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class DirectoryScannerForRetuers extends DirectoryScanner{

	public DirectoryScannerForRetuers(File directory) {
		super(directory);
	}

	@Override
	protected void listFilesForFolder(File folder)  {
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
	
}
