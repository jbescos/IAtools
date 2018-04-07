package es.tododev.model;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
	// [tech, politics, business, entertainment, sport]
	private final static String TRAIN_DATA_1 = "/data/labeled.zip";
	private final URL zipFileURL = getClass().getResource(TRAIN_DATA_1);
	
	@Test
	public void vectorClassifierTest() throws IOException {
		File unzipped = Files.createTempDir();
		unzipped.deleteOnExit();
		try(InputStream inputStream = zipFileURL.openStream()){
			ZipUtils.unzip(inputStream, unzipped);
		}
		ParagraphVectors paragraphVectors = CategorizerModel.createFromFile(unzipped);
		File model = File.createTempFile("model", ".zip");
		model.deleteOnExit();
		CategorizerModel.saveModel(model, paragraphVectors);
		paragraphVectors = CategorizerModel.loadModel(model);
		ICategorizer categorizer = new VectorCategorizer(paragraphVectors);
		String category = categorizer.categorize("This is a test");
		log.info("Category: {}", category);
		assertNotNull(category);
	}
	
	private int getRandom(int lower, int upper) {
		return (int) (Math.random() * (upper - lower)) + lower;
	}
	
}
