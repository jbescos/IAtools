package es.tododev.model.nlp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
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
	private final Map<String, String> tests = new HashMap<>();
	
	public NlpTextCategorizerTest() throws IOException {
		File trainData = Files.createTempFile("trainData", ".txt").toFile();
		log.debug("Creating training file in {}", trainData.getAbsolutePath());
		URL zipFileURL = getClass().getResource(TRAIN_DATA);
		try(InputStream inputStream = zipFileURL.openStream()){
			ZipUtils.walkInZip(inputStream, entry -> {
				int rnd = getRandom(0, 9);
				if(rnd % 3 == 0) {
					tests.put(entry.getDirectory(), entry.getContent());
				}else {
					try {
						nlpTextCategorizer.addInTraininFile(trainData, entry.getDirectory(), entry.getContent());
					} catch (IOException e) {
						log.error("Cannotwrite in training file "+entry, e);
					}
				}
				
			});
		}
		try(OutputStream modelOut = new FileOutputStream(model)){
			nlpTextCategorizer.trainCategorizer(trainData, modelOut);
		}
		
	}
	
	@Test
	public void modelTest() throws IOException {
		int success = 0;
		int total = 0;
		for(Entry<String, String> test : tests.entrySet()) {
			total++;
			Entry<Double, String> entry = nlpTextCategorizer.categorize(model, new String[]{test.getValue()});
			if(entry.getValue().equals(test.getKey())) {
				success++;
			}
		}
		
		log.debug("Success {} of {}", success, total);
	}
	
	private int getRandom(int lower, int upper) {
		return (int) (Math.random() * (upper - lower)) + lower;
	}
	
}
