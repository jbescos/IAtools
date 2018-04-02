package es.tododev.model;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.deeplearning4j.text.documentiterator.LabelledDocument;
import org.junit.Test;

import es.tododev.model.dl4j.VectorClassifier;
import es.tododev.model.utils.ZipUtils;
import es.tododev.model.utils.ZipUtils.ZipInfo;

public class ModelsTest {
	
	private final static Logger log = LogManager.getLogger();
	// [tech, politics, business, entertainment, sport]
	private final static String TRAIN_DATA_1 = "/data/bbc-fulltext.zip";
	private final URL zipFileURL = getClass().getResource(TRAIN_DATA_1);
	
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
		log.debug("Success {}/{}={}", success, total, (double)success/total);
	}
	
	private int getRandom(int lower, int upper) {
		return (int) (Math.random() * (upper - lower)) + lower;
	}
	
}
