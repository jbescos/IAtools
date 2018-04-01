package es.tododev.model.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ZipUtils {

	private final static Logger log = LogManager.getLogger();
	private final static int BUFFER = 1048000;

	public static void walkInZip(InputStream zipFile, Consumer<ZipInfo> zipEntryConsumer) throws IOException {
		log.debug("Processing zip");
		try (ZipInputStream zis = new ZipInputStream(new BufferedInputStream(zipFile))) {
			ZipEntry entry;
			String directory = null;
			while ((entry = zis.getNextEntry()) != null) {
				if (!entry.isDirectory()) {
					StringBuilder text = new StringBuilder();
					int read = 0;
					byte[] buffer = new byte[BUFFER];
					while ((read = zis.read(buffer, 0, 1024)) >= 0) {
						text.append(new String(buffer, 0, read));
					}
					zipEntryConsumer.accept(new ZipInfo(directory, text.toString()));
				} else {
					directory = new File(entry.getName()).getName();
				}
			}
		}
		log.debug("Finish processing zip");
	}

	public static void unzip(InputStream zip, File outputFolder) throws ZipException, IOException {
		if(!outputFolder.isDirectory()) {
			throw new IllegalArgumentException(outputFolder.getAbsolutePath()+" must be a directory");
		}
		byte[] buffer = new byte[BUFFER];
		try (ZipInputStream zis = new ZipInputStream(zip)) {
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				String fileName = entry.getName();
				File newFile = new File(outputFolder.getAbsolutePath() + File.separator + fileName);
				log.debug("File unzip: {}", newFile.getAbsoluteFile());
				new File(newFile.getParent()).mkdirs();
				try(FileOutputStream fos = new FileOutputStream(newFile)){
					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
				}
			}
		}
	}

	public static class ZipInfo {
		private final String directory;
		private final String content;

		public ZipInfo(String directory, String content) {
			this.directory = directory;
			this.content = content;
		}

		public String getDirectory() {
			return directory;
		}

		public String getContent() {
			return content;
		}

		@Override
		public String toString() {
			return "directory=" + directory + ", content=" + content;
		}
	}

}
