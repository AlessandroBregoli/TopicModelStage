package it.unimib.disco.ab.workflow;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CliWorkflow {
	public static void main(String[] args) throws Exception{
		
		CliWorkflow c = new CliWorkflow(args);
		c.parseArgs();

		c.exec();

		System.out.println("CIAONE");
	}
	String[] args;
	private int numberOfTopics;
	private int numberOfThreads;
	private boolean textAnalysis;
	private String datasetFolder;
	private String preNerStopWordFile;
	private String stopWordFile;
	private String serializedNerFiles[];
	private boolean graphFilter;
	private double pctFilterValue;
	private double pctFilterCentralityValue;
	private String[] classFilters;
	private boolean generateComunities;
	private boolean generateNetFile;
	private boolean generateJSONFile;
	private boolean parameterOK;
	private boolean serializeTopicsWordForJSON;
	private boolean interclassEdgeOnly;
	private boolean perplexityAnalysis;
	private int minNumberOfTopics;
	private int maxNumberOfTopics;
	public CliWorkflow(String[] args){
		this.args = args;
	}
	
	//Le opzioni della cli
	private Options createOptions(){
		Options ops = new Options();
		ops.addOption("numThread", true, "Number of thread used(main thread excluded)");
		ops.addOption("numTopics", true, "Number of topics if the perplexity analisis is enabled insert 2 value minTopics,maxTopics");
		ops.addOption("textAnalysis", false,"Enable Text analysis" );
			ops.addOption("dataset", true, "The dataset folder");
			ops.addOption("pStopWord", true, "The text file witch contains the prener stopwords");
			ops.addOption("stopWord", true, "The text file witch contains the stopwords");
			ops.addOption("serializeTopicsWordForJSON", false, "Serialize the topics word in JSON format");
			/*
			Option snf = new Option("serializedNerFile", "Serialized ner file");
			snf.setArgs(Option.UNLIMITED_VALUES);
			snf.setValueSeparator(',');*/
			
			ops.addOption("serializedNerFile", true, "Serialized ner file separated by coma");
			ops.addOption("enablePerplexityAnalysis", false, "Enable the preplexity analysis for automatically select the best nuber of topics");
		ops.addOption("graphFilter", false, "Enable Graph Filtering");
			ops.addOption("pctFilter", true, "[0..1] number of arc(percentual) less weighted to be deleted");
			ops.addOption("pctFilterCentrality", true, "[0..1] number of node(percentual) less central to be deleted");
			/*
			Option cf = new Option("classFilter", "Enable white list of ner class");
			cf.setArgs(Option.UNLIMITED_VALUES);
			cf.setValueSeparator(',');*/
			ops.addOption("classFilter", true,  "Enable white list of ner class separated by coma");
			ops.addOption("generateC", false, "Generate graphs based on comunities");
			ops.addOption("generateNetFile", false, "Generate graph in pajec format");
			ops.addOption("generateJSONFile", false, "Generate graph in JSON Sigma format");
			ops.addOption("interclassEdgeOnly", false, "Filter all intraclass edge(only when class filter is used)");
		ops.addOption("help", false, "Generate this output");
		
		
		return ops;
	}
	//Il parse delle opzioni della cli; se ritorna falso vuol dire che qualcosa è andato storto
	public boolean parseArgs() throws ParseException{
		this.parameterOK = false;
		CommandLineParser parser = new DefaultParser();
		Options options = this.createOptions();
		CommandLine cmd = parser.parse(options , this.args);
		//help
		if(cmd.hasOption("help")){
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "TopicModelStage", options );
			return false;
		}
		this.perplexityAnalysis = cmd.hasOption("enablePerplexityAnalysis");
		if(!cmd.hasOption("numTopics")){
			System.err.println("Number of topics required");
			return false;
		}
		if(this.perplexityAnalysis){
			String[] tmpPer = cmd.getOptionValue("numTopics").split(",");
			this.minNumberOfTopics = Integer.parseInt(tmpPer[0]);
			this.maxNumberOfTopics = Integer.parseInt(tmpPer[1]);
			if(this.maxNumberOfTopics < this.minNumberOfTopics || this.minNumberOfTopics < 0){
				System.err.println("The number of topics must be integer and positive; first the min then the max");
				return false;
			}
		}
		else{
			this.numberOfTopics = Integer.parseInt(cmd.getOptionValue("numTopics"));
			if(this.numberOfTopics <= 0){
				System.err.println("Number of topics must be integer positive");
				return false;
			}
		}
		//NUMBER OF THREADS
		this.numberOfThreads = 1;
		if(cmd.hasOption("numThread")){
			this.numberOfThreads = Integer.parseInt(cmd.getOptionValue("numThread"));
			if(this.numberOfThreads < 1){
				this.numberOfThreads = 1;
			}
		}
		//TextAnalysis(ner lda & graph generation)
		this.textAnalysis = cmd.hasOption("textAnalysis");
		if(this.textAnalysis){
			if(!cmd.hasOption("dataset")){
				System.err.println("Dataset folder required for text analysis");
				return false;
			}
			this.datasetFolder = cmd.getOptionValue("dataset");
		}
		this.preNerStopWordFile = null;
		if(this.textAnalysis && cmd.hasOption("pStopWord")){
			this.preNerStopWordFile = cmd.getOptionValue("pStopWord");
		}
		this.stopWordFile = null;
		if(this.textAnalysis && cmd.hasOption("stopWord")){
			this.stopWordFile = cmd.getOptionValue("stopWord");
		}	
		if(this.textAnalysis){
			if(!cmd.hasOption("serializedNerFile")){
				System.err.println("serializedNerFile required for text analysis");
				return false;
			}
			this.serializedNerFiles = cmd.getOptionValue("serializedNerFile").split(",");
		}
		this.serializeTopicsWordForJSON = cmd.hasOption("serializeTopicsWordForJSON");
		
		//Graph filtering
		this.graphFilter = cmd.hasOption("graphFilter");
		this.pctFilterValue = 0.0;
		if(this.graphFilter && cmd.hasOption("pctFilter")){
			this.pctFilterValue = Double.parseDouble(cmd.getOptionValue("pctFilter"));
		}
		this.pctFilterCentralityValue = 0.0;
		if(this.graphFilter && cmd.hasOption("pctFilterCentrality")){
			this.pctFilterCentralityValue = Double.parseDouble(cmd.getOptionValue("pctFilterCentrality"));
		}
		this.classFilters = null;
		if(this.graphFilter && cmd.hasOption("classFilter")){
			this.classFilters = cmd.getOptionValue("classFilter").split(",");
		}
		this.generateComunities = cmd.hasOption("generateC");
		this.generateNetFile = cmd.hasOption("generateNetFile");
		this.generateJSONFile = cmd.hasOption("generateJSONFile");
		this.interclassEdgeOnly = cmd.hasOption("interclassEdgeOnly");
		this.parameterOK = true;
		return true;
	}
	//Esegue i comandi reperiti dalla cli
	private void exec() throws Exception{
		if(!this.parameterOK){
			throw new Exception("Parametri non correttamente inizializzati");
		}
		if(this.textAnalysis){
			this.numberOfTopics = WorkflowTextAnalysis.startWorkflow(this.numberOfThreads, this.numberOfTopics, this.datasetFolder, this.preNerStopWordFile, this.stopWordFile, this.serializedNerFiles, this.serializeTopicsWordForJSON, this.perplexityAnalysis, this.minNumberOfTopics, this.maxNumberOfTopics);
		}
		if(this.graphFilter){

			WorkflowGraphFilter.startWorkflow(this.numberOfThreads, this.numberOfTopics, this.pctFilterValue, this.classFilters,this.generateComunities, generateNetFile, this.generateJSONFile, this.interclassEdgeOnly, this.pctFilterCentralityValue);
		}
		
	}
}
