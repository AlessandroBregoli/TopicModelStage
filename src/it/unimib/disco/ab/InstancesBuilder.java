package it.unimib.disco.ab;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.*;

public class InstancesBuilder {
	private InstancesBuilder(){}
	private static Pipe buildPipe() {
        ArrayList pipeList = new ArrayList();


        // Regular expression for what constitutes a token.
        //  This pattern includes Unicode letters, Unicode numbers, 
        //   and the underscore character. Alternatives:
        //    "\\S+"   (anything not whitespace)
        //    "\\w+"    ( A-Z, a-z, 0-9, _ )
        //    "[\\p{L}\\p{N}_]+|[\\p{P}]+"   (a group of only letters and numbers OR
        //                                    a group of only punctuation marks)
        Pattern tokenPattern =
            Pattern.compile("[\\p{L}\\p{N}_]+");

        // Tokenize raw strings
        pipeList.add(new CharSequence2TokenSequence(tokenPattern));

        // Normalize all tokens to all lowercase
        pipeList.add(new TokenSequenceLowercase());

        // Remove stopwords from a standard English stoplist.
        //  options: [case sensitive] [mark deletions]
        pipeList.add(new TokenSequenceRemoveStopwords(false, false));

        // Rather than storing tokens as strings, convert 
        //  them to integers by looking them up in an alphabet.
        pipeList.add(new TokenSequence2FeatureSequence());



        // Print out the features and the label
        pipeList.add(new PrintInputAndTarget());
        
        
        return new SerialPipes(pipeList);
    }
	
	public static InstanceList getInstances(String dirPath){
		InstanceList instances;
		File f = new File(dirPath);
		FileIteratorSplitter iterator = new FileIteratorSplitter(new File[]{f}, new TxtFilter(), FileIterator.LAST_DIRECTORY);
		Pipe p = InstancesBuilder.buildPipe();
		instances = new InstanceList(p);
		instances.addThruPipe(iterator);
		return instances;
	}
	
}

class  TxtFilter implements FileFilter {

    /** Test whether the string representation of the file 
     *   ends with the correct extension. Note that {@ref FileIterator}
     *   will only call this filter if the file is not a directory,
     *   so we do not need to test that it is a file.
     */
    public boolean accept(File file) {
        return file.toString().endsWith(".txt");
    }
}