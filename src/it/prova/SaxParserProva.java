package it.prova;

import it.unimib.disco.ab.xmlParser.*;

import java.io.File;
import java.util.LinkedList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;



public class SaxParserProva {
	 public static void main(String[] args){

	      try {	
	         File inputFile = new File("/home/alessandro/data.xml");
	         SAXParserFactory factory = SAXParserFactory.newInstance();
	         SAXParser saxParser = factory.newSAXParser();
	         LinkedList<Article> articleList = new LinkedList<Article>();
	         ReutersParserHandler userhandler = new ReutersParserHandler(articleList);
	         saxParser.parse(inputFile, userhandler); 
	         System.out.println(articleList.get(0).title);
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
	   }   
}
