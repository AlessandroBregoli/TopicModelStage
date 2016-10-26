package it.unimib.disco.ab.datasetParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
//Implementazione del directory scanner. Questo scanner è pensato per gli articoli della BBC
//ma in pratica non fa altro che prendere una cartella e leggere tutti i file 
public class DirectoryScannerForBBC extends DirectoryScanner{

	public DirectoryScannerForBBC(File directory) {
		super(directory);
	}

	@Override
	protected void listFilesForFolder(File folder)  {
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            this.listFilesForFolder(fileEntry);
	        } else {
		        System.out.println(fileEntry.getName());
				try {
					Article tmpArticle = new Article();
					FileInputStream fis = new FileInputStream(fileEntry);
					byte[] data = new byte[(int)fileEntry.length()];
					fis.read(data);
					fis.close();
					tmpArticle.text = new String(data, "UTF-8");
					this.articles.add(tmpArticle);
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
	    }
	}

}
