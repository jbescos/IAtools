package es.tododev.model.dl4j;

import java.io.File;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.text.documentiterator.FileLabelAwareIterator;
import org.deeplearning4j.text.documentiterator.LabelAwareIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

public class CategorizerModel {

	private final static Logger log = LogManager.getLogger();

	public static ParagraphVectors createFromFile(File trainingData) {
		LabelAwareIterator iterator = new FileLabelAwareIterator.Builder().addSourceFolder(trainingData).build();

		TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
		tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());

		ParagraphVectors paragraphVectors = new ParagraphVectors.Builder().learningRate(0.025).minLearningRate(0.001).batchSize(1000)
				.epochs(20).iterate(iterator).trainWordVectors(true).tokenizerFactory(tokenizerFactory).build();
		paragraphVectors.fit();
		printLabels(paragraphVectors);
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
		log.debug("Model Load. Configuration {}", paragraphVectors.getConfiguration().toJson());
		printLabels(paragraphVectors);
		return paragraphVectors;
	}
	
	private static void printLabels(ParagraphVectors paragraphVectors) {
		for (VocabWord vWord : paragraphVectors.getVocab().vocabWords()) {
            if (vWord.isLabel()) {
               log.debug("Label: {}", vWord.getLabel());
            }
        }
	}

}
