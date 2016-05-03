package it.unimib.disco.ab;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.regex.*;

import cc.mallet.pipe.iterator.FileIterator;
import cc.mallet.types.Instance;

public class FileIteratorSplitter extends FileIterator {
	//splitted text
	private String[] splittedText;
	//name of the instance in the super class
	private URI name;
	//target name of the instance in the super class
	private String targetName;
	//The iterator position into the splitted text sequence
	private int textPosition;
	public FileIteratorSplitter(File[] directory, FileFilter filt, Pattern p) {
		super(directory, filt, p);
		this.textPosition = 0;
		this.splittedText = null;
	}
	
	
	public Instance next(){
		while(this.splittedText == null || this.textPosition >= this.splittedText.length){
			this.splitText();
		}
		System.out.println(this.splittedText[this.textPosition]);
		Instance i = new Instance(this.splittedText[this.textPosition],this.targetName, this.name + "_" + this.textPosition, null);
		this.textPosition++;
		return i;
		
	}
	
	private void splitText(){
		this.textPosition = 0;
		Instance s = super.next();
		File f = (File) s.getData();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(f));
			final int BUFSIZE = 2048;
			char[] buf = new char[BUFSIZE];
			int count;
			StringBuffer sb = new StringBuffer (BUFSIZE);
			do {
				count = reader.read (buf, 0, BUFSIZE);
				if (count == -1)
					break;
				sb.append (buf, 0, count);
			} while (count == BUFSIZE);
			reader.close();
			this.splittedText = sb.toString().split("(?<=\\p{Ll}[.?!;])\\s+(?=\\p{Lu})");
			this.name = (URI) s.getName();
			this.targetName = (String) s.getTarget();
			
		} catch (IOException e) {
			this.splittedText = null;}
		
	}

}
