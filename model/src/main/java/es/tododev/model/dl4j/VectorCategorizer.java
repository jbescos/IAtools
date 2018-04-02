package es.tododev.model.dl4j;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import es.tododev.model.ICategorizer;

public class VectorCategorizer implements ICategorizer {

	private final static Logger log = LogManager.getLogger();
	private final ParagraphVectors paragraphVectors;

	public VectorCategorizer(ParagraphVectors paragraphVectors) {
		this.paragraphVectors = paragraphVectors;
	}

	@Override
	public String categorize(String text) {
		return paragraphVectors.predict(text);
	}

}
