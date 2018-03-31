package es.tododev.model.nlp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;

import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.ml.AbstractTrainer;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NlpTextCategorizer {

	private final static Logger log = LogManager.getLogger();
	
	public Entry<Double, String> categorize(File readFileModel, String ... texts) throws IOException {
		try (InputStream modelIn = new FileInputStream(readFileModel)) {
			DoccatModel model = new DoccatModel(modelIn);
			DocumentCategorizerME myCategorizer = new DocumentCategorizerME(model);
			SortedMap<Double, Set<String>> map = myCategorizer.sortedScoreMap(texts);
			log.debug(map.toString());
			double probability = map.lastKey();
			String category = map.get(probability).iterator().next();
			Map.Entry<Double, String> pair = new AbstractMap.SimpleImmutableEntry<>(probability, category);
			return pair;
		}
	}
	
	public void trainCategorizer(File trainingFile, OutputStream modelOut) throws IOException {
		trainCategorizer(trainingFile, modelOut, "en");
	}

	public void trainCategorizer(File trainingFile, OutputStream modelOut, String language) throws IOException {
		ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(trainingFile), "UTF-8");
		ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);
		TrainingParameters parameters = new TrainingParameters();
		parameters.put(AbstractTrainer.CUTOFF_PARAM, "1");
		DoccatFactory factory = new DoccatFactory();
		DoccatModel model = DocumentCategorizerME.train(language, sampleStream, parameters, factory);
		model.serialize(modelOut);
	}
	
	public void addInTraininFile(File trainDataFile, String category, String content) throws IOException {
		String[] lines = content.replaceAll("\r", "").split("\n");
		try(FileWriter fw = new FileWriter(trainDataFile, true)){
			for(String line : lines) {
				if(!line.trim().isEmpty()) {
					fw.write(category+" "+line+"\n");
				}
			}
		}
		
	}
}
