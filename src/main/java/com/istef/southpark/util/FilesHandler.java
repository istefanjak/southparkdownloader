package com.istef.southpark.util;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilesHandler {
	private static final Logger logger = LoggerFactory.getLogger(FilesHandler.class);
	
	public static void deleteUuidFromTmpDir(Path tmpDir, UUID uuid) {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(tmpDir)) {
			stream.forEach(p -> {
				if (Files.isRegularFile(p) && p.getFileName().toString().startsWith(uuid.toString()))
					try {
						Files.delete(p);
						
					} catch (IOException e) {
						logger.error(e.getMessage(), e);
					}
			});
			
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public static void deleteAllFromTmpDir(Path tmpDir) {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(tmpDir)) {
			stream.forEach(p -> {
				try {
					Files.delete(p);
					
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			});
			
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public static String findAppropriateFileName(Path dir, String fileNamePart, String extension) {
		int cnt = 0;
		String format = "%s%05d%s";
		Path check = Paths.get(dir.toString(), fileNamePart + extension);
		while (Files.exists(check)) {
			cnt++;
			check = Paths.get(dir.toString(), String.format(format, fileNamePart, cnt, extension));
		}
		return check.getFileName().toString();
	}

}
