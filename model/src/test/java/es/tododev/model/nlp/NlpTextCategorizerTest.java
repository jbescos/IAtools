package es.tododev.model.nlp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import es.tododev.model.nlp.NlpTextCategorizer;
import es.tododev.model.utils.ZipUtils;

public class NlpTextCategorizerTest {
	
	private final static Logger log = LogManager.getLogger();
	// [tech, politics, business, entertainment, sport]
	private final static String TRAIN_DATA = "/data/bbc-fulltext.zip";
	private final NlpTextCategorizer nlpTextCategorizer = new NlpTextCategorizer();
	private final File model = Files.createTempFile("trainedModel", ".model").toFile();
	
	public NlpTextCategorizerTest() throws IOException {
		File trainData = Files.createTempFile("trainData", ".txt").toFile();
		log.debug("Creating training file in {}", trainData.getAbsolutePath());
		URL zipFileURL = getClass().getResource(TRAIN_DATA);
		try(InputStream inputStream = zipFileURL.openStream()){
			ZipUtils.walkInZip(inputStream, entry -> {
				try {
					nlpTextCategorizer.addInTraininFile(trainData, entry.getDirectory(), entry.getContent());
				} catch (IOException e) {
					log.error("Cannotwrite in training file "+entry, e);
				}
			});
		}
		try(OutputStream modelOut = new FileOutputStream(model)){
			nlpTextCategorizer.trainCategorizer(trainData, modelOut);
		}
		
	}
	
	@Test
	public void modelTest() throws IOException {
		Entry<Double, String> entry = nlpTextCategorizer.categorize(model, new String[]{"Futbol is a good sport to play"});
		log.debug("Result {}", entry);
	}
	
}
