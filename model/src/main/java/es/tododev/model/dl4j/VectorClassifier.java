package es.tododev.model.dl4j;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.text.documentiterator.FileLabelAwareIterator;
import org.deeplearning4j.text.documentiterator.LabelAwareIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

public class VectorClassifier {

	private final static Logger log = LogManager.getLogger();
	private final TokenizerFactory tokenizerFactory;
	private final ParagraphVectors paragraphVectors;

	public VectorClassifier(File training) {
		log.debug("Training model {}", training.getAbsolutePath());
		LabelAwareIterator iterator = new FileLabelAwareIterator.Builder().addSourceFolder(training).build();
		tokenizerFactory = new DefaultTokenizerFactory();
		tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());

		paragraphVectors = new ParagraphVectors.Builder().learningRate(0.025).minLearningRate(0.001).batchSize(1000)
				.epochs(20).iterate(iterator).trainWordVectors(true).tokenizerFactory(tokenizerFactory).build();

		paragraphVectors.fit();
		log.debug("Model trained");
	}

	public String categorize(String text) {
		// TODO
		return null;
	}

}
