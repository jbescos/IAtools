package es.tododev.model.dl4j;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.text.documentiterator.LabelAwareIterator;
import org.deeplearning4j.text.documentiterator.LabelledDocument;
import org.deeplearning4j.text.documentiterator.SimpleLabelAwareIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

public class CategorizerModel {

	private final static Logger log = LogManager.getLogger();
	
	// FIXME Use other way to read from huge collections
	public static ParagraphVectors createFromList(List<LabelledDocument> documents) {
		log.debug("Training model");
		LabelAwareIterator iterator = new SimpleLabelAwareIterator(documents);
		TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
		tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());

		ParagraphVectors paragraphVectors = new ParagraphVectors.Builder().learningRate(0.025).minLearningRate(0.001).batchSize(1000)
				.epochs(20).iterate(iterator).trainWordVectors(true).tokenizerFactory(tokenizerFactory).build();

		paragraphVectors.fit();
		log.debug("Model trained");
		return paragraphVectors;
	}
	
	public static void saveModel(File output, ParagraphVectors paragraphVectors) {
		WordVectorSerializer.writeParagraphVectors(paragraphVectors, output);
	}
	
	public static ParagraphVectors loadModel(File model) throws IOException {
		ParagraphVectors paragraphVectors = WordVectorSerializer.readParagraphVectors(model);
		TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
		paragraphVectors.setTokenizerFactory(t);
		return paragraphVectors;
	}
	
}
