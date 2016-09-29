package it.prova;

import it.unimib.disco.ab.datasetParser.*;

import java.io.File;
import java.util.LinkedList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;



public class SaxParserProva {
	 public static void main(String[] args){

	      try {	
	         File inputFile = new File("/home/alessandro/Schifezze/Stage/Dataset/miniset/reut2-000.xml");
	         SAXParserFactory factory = SAXParserFactory.newInstance();
	         SAXParser saxParser = factory.newSAXParser();
	         LinkedList<Article> articleList = new LinkedList<Article>();
	         ReutersParserHandler userhandler = new ReutersParserHandler(articleList);
	         saxParser.parse(inputFile, userhandler); 
	         System.out.println(articleList.get(0).title);
	         System.out.println(articleList.get(0).text);
	         System.out.println(articleList.get(0).date.toString());
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
	   }   
}
