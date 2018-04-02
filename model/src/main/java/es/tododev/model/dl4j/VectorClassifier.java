package es.tododev.model.dl4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.text.documentiterator.FileLabelAwareIterator;
import org.deeplearning4j.text.documentiterator.LabelAwareIterator;
import org.deeplearning4j.text.documentiterator.LabelledDocument;
import org.deeplearning4j.text.documentiterator.SimpleLabelAwareIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

public class VectorClassifier {

	private final static Logger log = LogManager.getLogger();
	private final ParagraphVectors paragraphVectors;

	public VectorClassifier(ParagraphVectors paragraphVectors) {
		// SimpleLabelAwareIterator
		this.paragraphVectors = paragraphVectors;
	}
	
	private void scanSources(List<File> sources, File current) {
		for(File children : current.listFiles()) {
			if(!children.isDirectory()) {
				log.debug("Found source {}", current.getName());
				sources.add(current);
				break;
			}else {
				scanSources(sources, children);
			}
		}
	}

	public String categorize(String text) {
		return paragraphVectors.predict(text);
	}
	
	public static VectorClassifier createFromList(List<LabelledDocument> documents) {
		log.debug("Training model");
		LabelAwareIterator iterator = new SimpleLabelAwareIterator(documents);
		TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
		tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());

		ParagraphVectors paragraphVectors = new ParagraphVectors.Builder().learningRate(0.025).minLearningRate(0.001).batchSize(1000)
				.epochs(20).iterate(iterator).trainWordVectors(true).tokenizerFactory(tokenizerFactory).build();

		paragraphVectors.fit();
		log.debug("Model trained");
		return new VectorClassifier(paragraphVectors);
	}
	
	public static VectorClassifier createfromFile(File training) {
		log.debug("Training model {}", training.getAbsolutePath());
		LabelAwareIterator iterator = new FileLabelAwareIterator.Builder().addSourceFolder(training).build();
		TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
		tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());

		ParagraphVectors paragraphVectors = new ParagraphVectors.Builder().learningRate(0.025).minLearningRate(0.001).batchSize(1000)
				.epochs(20).iterate(iterator).trainWordVectors(true).tokenizerFactory(tokenizerFactory).build();

		paragraphVectors.fit();
		log.debug("Model trained");
		return new VectorClassifier(paragraphVectors);
	}

}
