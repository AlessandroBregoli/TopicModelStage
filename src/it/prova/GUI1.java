package it.prova;
class GUI1{
	
}
/*
import it.unimib.disco.ab.entityTopicStatistics.NerStats;
import it.unimib.disco.ab.entityTopicStatistics.TopicStat;
import it.unimib.disco.ab.graphs.StaticGraphAnalyzer;
import it.unimib.disco.ab.graphs.StaticGraphAnalyzerThread;
import it.unimib.disco.ab.graphs.StaticGraphGenerator;
import it.unimib.disco.ab.malletLDA.InstancesBuilder;
import it.unimib.disco.ab.ner.CustomEntity;
import it.unimib.disco.ab.ner.Ner;
import it.unimib.disco.ab.textPreprocessing.SentenceSplitter;
import it.unimib.disco.ab.datasetParser.DirectoryScanner;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JButton;
import javax.swing.JTextArea;

import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.Alphabet;
import cc.mallet.types.IDSorter;
import cc.mallet.types.InstanceList;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Formatter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JScrollPane;
import javax.swing.SpinnerNumberModel;

public class GUI1 {

	private JFrame frame;
	private JTextField textField;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI1 window = new GUI1();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public GUI1() {
		initialize();
		
	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 583, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(148, 12, 334, 19);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JLabel lblCartella = new JLabel("Cartella");
		lblCartella.setBounds(59, 12, 70, 15);
		frame.getContentPane().add(lblCartella);
		
		JSpinner spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		spinner.setBounds(148, 55, 36, 20);
		frame.getContentPane().add(spinner);
		
		JLabel lblTopics = new JLabel("Topics");
		lblTopics.setBounds(59, 57, 70, 15);
		frame.getContentPane().add(lblTopics);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 126, 559, 130);
		frame.getContentPane().add(scrollPane);

		JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		textArea.setEditable(false);
		
		JButton btnModella = new JButton("Modella");
		btnModella.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				
				File sw = new File("/home/alessandro/Schifezze/mallet-2.0.7/stoplists/en.txt");

				LinkedList<String> stopWords= new LinkedList<String>();
				try{
				FileReader fr = new FileReader(sw);
				BufferedReader br = new BufferedReader(fr);
				
				String stopword;
				while((stopword = br.readLine())!=null){
					stopWords.add(stopword);
				}
				br.close();
				fr.close();
				}catch(Exception e){}
				DirectoryScanner ds = new DirectoryScanner(new File(textField.getText()));
				ds.startScan();
				SentenceSplitter senteceSplitted = new SentenceSplitter(ds.getArticles());
				senteceSplitted.filterUsingList(stopWords);
				InstanceList instances = InstancesBuilder.getInstances(senteceSplitted);
		
				int nTopics = (Integer)spinner.getValue();
				double alpha = 0.0001;
				double beta = 0.01;
				ParallelTopicModel model = new ParallelTopicModel(nTopics, alpha * nTopics, beta);
				model.setSymmetricAlpha(true);
				model.setNumThreads(8);
				model.setNumIterations(1000);
				model.addInstances(instances);
				model.optimizeInterval = 0;
				try {
					model.estimate();
				} catch (IOException e) {}
				Formatter out;
				ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
				Alphabet dataAlphabet = instances.getDataAlphabet();
				for (int topic = 0; topic < model.numTopics; topic++) {
		            Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();
		            
		            out = new Formatter(new StringBuilder(), Locale.US);
		           
		            int rank = 0;
		            while (iterator.hasNext() && rank < 10) {
		                IDSorter idCountPair = iterator.next();
		                out.format("%s (%.0f)", dataAlphabet.lookupObject(idCountPair.getID()), idCountPair.getWeight());
		                rank++;
		            }
		           textArea.setText(textArea.getText() + "\n"+ topic + ")\t" + out);
				}
		        
				Ner ner;
				try {
					File f = new File("relazione.csv");
					FileWriter fw = new FileWriter(f);
					BufferedWriter bw = new BufferedWriter(fw);
				
					String[] serialNer = {
							"/home/alessandro/Schifezze/stanford-ner-2015-12-09/classifiers/english.all.3class.distsim.crf.ser.gz",
							"/home/alessandro/Schifezze/stanford-ner-2015-12-09/classifiers/english.conll.4class.distsim.crf.ser.gz",
							"/home/alessandro/Schifezze/stanford-ner-2015-12-09/classifiers/english.muc.7class.distsim.crf.ser.gz"
					};
					ner = new Ner(serialNer,model.getInferencer(),instances, stopWords,4);
					NerStats nerStats;
					nerStats = ner.entityTopicRelation();
					
					try{
						
						FileOutputStream fos = new FileOutputStream("relation.dat");
						ObjectOutputStream oos = new ObjectOutputStream(fos);
						oos.writeObject(nerStats);
						oos.close();
						fos.close();
					}catch(Exception e){
						e.printStackTrace();
					}
					bw.write("entita\tclasse\tBestTopic\tBestTopic%\tnumElement\ttopic\n");
					for(CustomEntity ce:nerStats.relation.keySet()){
						bw.write(ce.entityString);
						bw.write("\t");
						bw.write(ce.entityClass);
						bw.write("\t");
						double[] stat = nerStats.relation.get(ce).getMean();
						
						StringBuilder b = new StringBuilder();
						for(int stat_i = 0; stat_i < stat.length; stat_i++){
							b.append("\t");
							b.append(Double.toString(stat[stat_i]));
						}
						bw.write(Integer.toString(nerStats.relation.get(ce).getBestTopic()));
						bw.write("\t");
						bw.write(Double.toString(stat[nerStats.relation.get(ce).getBestTopic()]*100));
						bw.write("\t");
						bw.write(Integer.toString(nerStats.relation.get(ce).getNumEl()));
						bw.write(b.toString());
						bw.write("\n");
					}
					bw.close();
					fw.close();
					StaticGraphGenerator tgg = new StaticGraphGenerator(nerStats, 8);
					tgg.waitUntillEnd();

					StaticGraphAnalyzer sga = new StaticGraphAnalyzer(nerStats, tgg.graphs, 2);
					sga.analizeGraph(8);
				} catch (ClassCastException | ClassNotFoundException
						| IOException e) {
					
					e.printStackTrace();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
			}
		});
		btnModella.setBounds(365, 52, 117, 25);
		frame.getContentPane().add(btnModella);
		
	}
}
*/