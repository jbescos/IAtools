package es.tododev.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.text.documentiterator.LabelledDocument;
import org.junit.Ignore;
import org.junit.Test;

import es.tododev.model.dl4j.VectorClassifier;
import es.tododev.model.nlp.NlpTextCategorizer;
import es.tododev.model.utils.ZipUtils;
import es.tododev.model.utils.ZipUtils.ZipInfo;

public class ModelsTest {
	
	private final static Logger log = LogManager.getLogger();
	// [tech, politics, business, entertainment, sport]
	private final static String TRAIN_DATA_1 = "/data/bbc-fulltext.zip";
	private final URL zipFileURL = getClass().getResource(TRAIN_DATA_1);
	private final static String TRAIN_DATA_2 = "/data/paravec/labeled";
	
	@Test
	@Ignore
	public void openNlpTest() throws IOException {
		List<ZipInfo> tests = new ArrayList<>();
		NlpTextCategorizer nlpTextCategorizer = createNlpTextCategorizer(tests);
		int success = 0;
		int total = 0;
		for(ZipInfo test : tests) {
			total++;
			Entry<Double, String> entry = nlpTextCategorizer.categorize(new String[]{test.getContent()});
			if(entry.getValue().equals(test.getDirectory())) {
				success++;
			}
		}
		
		log.debug("Success {} of {}", success, total);
	}
	
	@Test
	public void vectorClassifierTest() throws IOException {
		List<ZipInfo> tests = new ArrayList<>();
		List<LabelledDocument> documents = new ArrayList<>();
		try(InputStream inputStream = zipFileURL.openStream()){
			ZipUtils.walkInZip(inputStream, entry -> {
				int rnd = getRandom(0, 9);
				if(rnd == 1) {
					tests.add(entry);
				}else {
					LabelledDocument doc = new LabelledDocument();
					doc.setContent(entry.getContent());
					doc.addLabel(entry.getDirectory());
					documents.add(doc);
				}
			});
		}
		VectorClassifier vector = VectorClassifier.createFromList(documents);
		int success = 0;
		int total = 0;
		for(ZipInfo test : tests) {
			total++;
			String label = vector.categorize(test.getContent());
			if(label.equals(test.getDirectory())) {
				success++;
			}
		}
		log.debug("Success {} of {}", success, total);
	}
	
	private NlpTextCategorizer createNlpTextCategorizer(List<ZipInfo> tests) throws FileNotFoundException, IOException {
		File model = Files.createTempFile("trainedModel", ".model").toFile();
		File trainData = Files.createTempFile("trainData", ".txt").toFile();
		log.debug("Creating training file in {}", trainData.getAbsolutePath());
		try(InputStream inputStream = zipFileURL.openStream()){
			ZipUtils.walkInZip(inputStream, entry -> {
				int rnd = getRandom(0, 9);
				if(rnd == 1) {
					tests.add(entry);
				}else {
					try {
						NlpTextCategorizer.addInTraininFile(trainData, entry.getDirectory(), entry.getContent());
					} catch (IOException e) {
						log.error("Cannotwrite in training file "+entry, e);
					}
				}
				
			});
		}
		try(OutputStream modelOut = new FileOutputStream(model)){
			NlpTextCategorizer.trainCategorizer(trainData, modelOut);
		}
		trainData.delete();
		return new NlpTextCategorizer(model);
	}
	
	private int getRandom(int lower, int upper) {
		return (int) (Math.random() * (upper - lower)) + lower;
	}
	
}
