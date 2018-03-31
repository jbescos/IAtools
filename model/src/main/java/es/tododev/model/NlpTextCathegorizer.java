package es.tododev.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.AbstractMap;
import java.util.Map;
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

public class NlpTextCathegorizer {

	private final static Logger log = LogManager.getLogger();
	
	public Map.Entry<Double, String> categorize(File readFileModel, String ... texts) throws IOException {
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
	
	public void trainCategorizer(File trainingFile, File writeFileModel) throws IOException {
		trainCategorizer(trainingFile, writeFileModel, "en");
	}

	public void trainCategorizer(File trainingFile, File writeFileModel, String language) throws IOException {
		try (OutputStream modelOut = new FileOutputStream(writeFileModel)) {
			ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(trainingFile), "UTF-8");
			ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);
			TrainingParameters parameters = new TrainingParameters();
			parameters.put(AbstractTrainer.CUTOFF_PARAM, "1");
			DoccatFactory factory = new DoccatFactory();
			DoccatModel model = DocumentCategorizerME.train(language, sampleStream, parameters, factory);
			model.serialize(modelOut);
		}
	}
}
