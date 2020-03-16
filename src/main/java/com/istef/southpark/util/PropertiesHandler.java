package com.istef.southpark.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class PropertiesHandler {
	private static final String PROPERTY_FILE = "main.properties";

	public enum PropertiesVals {
		DL_DIR, MKV_MERGE_EXE
	}

	public static final class MyProperties {
		private Path downloadDir;
		private Path mkvMergeExecutable;

		public MyProperties(Path downloadDir, Path mkvMergeExecutable) {
			this.downloadDir = downloadDir;
			this.mkvMergeExecutable = mkvMergeExecutable;
		}

		public Path getDownloadDir() {
			return downloadDir;
		}

		public Path getMkvMergeExecutable() {
			return mkvMergeExecutable;
		}
	
	}

	public static MyProperties getProperties() throws Exception {
		try (InputStream in = new FileInputStream(PROPERTY_FILE)) {
			Properties prop = new Properties();
			prop.load(in);
			String dlprop = prop.getProperty(PropertiesVals.DL_DIR.toString());
			String mkvmergeprop = prop.getProperty(PropertiesVals.MKV_MERGE_EXE.toString());
			return new MyProperties(dlprop == null ? null : Paths.get(dlprop),
					mkvmergeprop == null ? null : Paths.get(mkvmergeprop));

		} catch (FileNotFoundException e) {
			Files.createFile(Paths.get(PROPERTY_FILE));
			if (Files.notExists(Paths.get(PROPERTY_FILE))) {
				throw new Exception("Can't load nor create properties file");
			}
			return new MyProperties(null, null);
		}
	}
	
	public static void setProperty(PropertiesVals property, String val) throws Exception {
		try (InputStream in = new FileInputStream(PROPERTY_FILE)) {
			Properties prop = new Properties();
			prop.load(in);
			
			try (OutputStream out = new FileOutputStream(PROPERTY_FILE)) {
				if (val == null) {
					prop.remove(property.toString());
				} else {
					prop.setProperty(property.toString(), val);
				}
				prop.store(out, "Main properties file");
			}
		}
	}
	
}
