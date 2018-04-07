package es.tododev.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.text.documentiterator.LabelledDocument;

import es.tododev.model.dl4j.CategorizerModel;
import es.tododev.model.utils.ZipUtils;

public class Main {
	
	private final static Logger log = LogManager.getLogger();
	private final static String CATEGORIZE = "categorize";
	private final static String MODEL = "model";
	private final static String TRAIN = "train";
	
	public static void main(String[] args) throws IOException {
		Actions action = Actions.getAction(args);
		action.execute(args);
	}
	
	private static String findValue(String key, String[] args, boolean required) {
		String k = "-"+key;
		for(int i=0; i<args.length; i++) {
			if(args[i].equals(k) && args.length >= (i+1)) {
				return args[i+1];
			}
		}
		if(required) {
			throw new IllegalArgumentException(k+" is a required argument");
		}
		return null;
	}
	
	private static List<LabelledDocument> getFromZip(String zipPath) throws IOException{
		List<LabelledDocument> documents = new ArrayList<>();
		try(InputStream inputStream = new FileInputStream(new File(zipPath))){
			ZipUtils.walkInZip(inputStream, entry -> {
				LabelledDocument doc = new LabelledDocument();
				doc.setContent(entry.getContent());
				doc.addLabel(entry.getDirectory());
				documents.add(doc);
			});
		}
		return documents;
	}
	
	private static enum Actions{
		train {
			@Override
			public void execute(String[] args) throws IOException {
				String model = findValue(MODEL, args, true);
				String train = findValue(TRAIN, args, true);
				ParagraphVectors paragraphVectors = CategorizerModel.createFromFile(new File(train));
				CategorizerModel.saveModel(new File(model), paragraphVectors);
			}
		}, 
		categorize {
			@Override
			public void execute(String[] args) throws IOException {
				String text = findValue(CATEGORIZE, args, true);
				String model = findValue(MODEL, args, true);
				ParagraphVectors paragraphVectors = CategorizerModel.loadModel(new File(model));
				log.info("Text: {}", text);
				log.info(paragraphVectors.predict(text));
			}
		};
		
		public abstract void execute(String[] args) throws IOException;
		
		public static Actions getAction(String[] args) {
			for(Actions action : Actions.values()) {
				for(String arg : args) {
					if(arg.startsWith("-") && arg.contains(action.name())) {
						return action;
					}
				}
			}
			throw new IllegalArgumentException(printHelp());
		}
	}
	
	private static String printHelp() {
		return "Need to add argument -create or -categorize";
	}

}
