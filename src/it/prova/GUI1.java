package it.prova;

import it.unimib.disco.ab.malletLDA.InstancesBuilder;
import it.unimib.disco.ab.ner.CustomEntity;
import it.unimib.disco.ab.ner.Ner;
import it.unimib.disco.ab.ner.TopicStat;

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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Formatter;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JScrollPane;
import javax.swing.SpinnerNumberModel;

public class GUI1 {

	private JFrame frame;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
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

	/**
	 * Create the application.
	 */
	public GUI1() {
		initialize();
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
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
				InstanceList instances = InstancesBuilder.getInstances(textField.getText());
				int nTopics = (Integer)spinner.getValue();
				double alpha = 1.0;
				double beta = 0.01;
				ParallelTopicModel model = new ParallelTopicModel(nTopics, alpha, beta);
				model.setNumThreads(4);
				model.setNumIterations(1000);
				model.addInstances(instances);	
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
		            while (iterator.hasNext() && rank < 5) {
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
					ner = new Ner("/home/alessandro/Schifezze/stanford-ner-2015-12-09/classifiers/english.all.3class.distsim.crf.ser.gz",model.getInferencer(),instances);
					TreeMap<CustomEntity, TopicStat> relation = ner.entityTopicRelation();
					bw.write("entita\tclasse\tBestTopic\tnumElement\ttopic\n");
					for(CustomEntity ce:relation.keySet()){
						bw.write(ce.entityString);
						bw.write("\t");
						bw.write(ce.entityClass);
						bw.write("\t");
						double[] stat = relation.get(ce).mean();
						
						StringBuilder b = new StringBuilder();
						int max = 0;
						for(int stat_i = 0; stat_i < stat.length; stat_i++){
							b.append("\t");
							b.append(Double.toString(stat[stat_i]));
							if(stat[max] < stat[stat_i])
								max = stat_i;
						}
						bw.write(Integer.toString(max));
						bw.write("\t");
						bw.write(Integer.toString(relation.get(ce).getNumEl()));
						bw.write(b.toString());
						bw.write("\n");
					}
					bw.close();
					fw.close();
				} catch (ClassCastException | ClassNotFoundException
						| IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		btnModella.setBounds(365, 52, 117, 25);
		frame.getContentPane().add(btnModella);
		
	}
}
