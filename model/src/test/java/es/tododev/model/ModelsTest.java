package es.tododev.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.junit.Test;

import com.google.common.io.Files;

import es.tododev.model.dl4j.CategorizerModel;
import es.tododev.model.dl4j.VectorCategorizer;
import es.tododev.model.utils.ZipUtils;

public class ModelsTest {
	
	private final static Logger log = LogManager.getLogger();
	private final static String TRAIN_DATA_1 = "/data/labeled.zip";
	private final URL TRAIN_DATA_1_URL = getClass().getResource(TRAIN_DATA_1);
	private final static String TRAINED_MODEL_1 = "/models/labeledModel.zip";
	private final static String SENTENCE = "This is a test example";
	
	@Test
	public void createModelTestSaveAndLoadTest() throws IOException {
		File unzipped = Files.createTempDir();
		unzipped.deleteOnExit();
		try(InputStream inputStream = TRAIN_DATA_1_URL.openStream()){
			ZipUtils.unzip(inputStream, unzipped);
		}
		ParagraphVectors paragraphVectors = CategorizerModel.createFromFile(unzipped);
		ICategorizer categorizer = new VectorCategorizer(paragraphVectors);
		String category = categorizer.categorize(SENTENCE);
		log.info("Category: {}", category);
		assertNotNull(category);
		File model = File.createTempFile("model", ".zip");
		log.debug("Created model in {}", model.getAbsolutePath());
		model.deleteOnExit();
		CategorizerModel.saveModel(model, paragraphVectors);
		paragraphVectors = CategorizerModel.loadModel(model);
		categorizer = new VectorCategorizer(paragraphVectors);
		String category2 = categorizer.categorize(SENTENCE);
		log.info("Category2: {}", category);
		assertEquals(category, category2);
	}
	
	@Test
	public void loadModelAndUse() throws IOException, URISyntaxException {
		URI modelUri = getClass().getResource(TRAINED_MODEL_1).toURI();
		log.debug("Path to modelUri {}", modelUri.getPath());
		File model = Paths.get(modelUri).toFile();
		log.debug("Path to model {}", model.getAbsolutePath());
		ICategorizer categorizer = getCategorizer(model);
		String category = categorizer.categorize(SENTENCE);
		assertEquals("finance", category);
	}
	
	private ICategorizer getCategorizer(File model) throws IOException {
		ParagraphVectors paragraphVectors = CategorizerModel.loadModel(model);
		ICategorizer categorizer = new VectorCategorizer(paragraphVectors);
		categorizer = new VectorCategorizer(paragraphVectors);
		return categorizer;
	}

}
