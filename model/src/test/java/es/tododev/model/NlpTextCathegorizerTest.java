package es.tododev.model;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import es.tododev.model.utils.ZipUtils;

public class NlpTextCathegorizerTest {
	
	private final static Logger log = LogManager.getLogger();

	@Before
	public void createModel() throws IOException {
		URL zipFileURL = getClass().getResource("/data/bbc-fulltext.zip");
		Set<String> cathegorys = new HashSet<>();
		try(InputStream inputStream = zipFileURL.openStream()){
			ZipUtils.walkInZip(inputStream, entry -> cathegorys.add(entry.getDirectory()));
		}
		log.debug("Data {}", cathegorys);
	}
	
	@Test
	public void dummy() {
		
	}
	
}
