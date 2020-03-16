package com.istef.southpark.video;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.istef.southpark.Const;
import com.istef.southpark.exception.VideoParserException;

public class VideoParser {
	private List<Path> videoPaths;
	private List<Path> subtitlePaths;

	public VideoParser(List<Path> videoPaths, List<Path> subtitlePaths) {
		if(videoPaths == null) videoPaths = new ArrayList<>();
		if(subtitlePaths == null) subtitlePaths = new ArrayList<>();
		this.videoPaths = videoPaths;
		this.subtitlePaths = subtitlePaths;
	}

	public void merge(Path outPath) throws VideoParserException {
		try {
			Stream<String> videoFiles = videoPaths.stream().map(p -> "\"" + p.toAbsolutePath().toString() + "\"");
			Stream<String> subtitleFiles = subtitlePaths.stream().map(p -> "\"" + p.toAbsolutePath().toString() + "\"");
			
			String[] mkvmergeArgs = {
					String.format("\"%s\"", Const.MKVMERGE.toAbsolutePath().toString()),
					"-o \"" + outPath.toAbsolutePath().toString() + "\"",
					videoFiles.collect(Collectors.joining(" + ")),
					subtitleFiles.collect(Collectors.joining(" + "))
					};
			
			Process p = Runtime.getRuntime().exec(String.join(" ", mkvmergeArgs));
			
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			try (FileWriter fw = new FileWriter(Const.MKVMERGE_LOG.toFile())) {
				String line = null;
				while ((line = in.readLine()) != null) {
					fw.write(line + "\n");
				}
			}
			if(!Files.exists(outPath)) {
				throw new VideoParserException("Mkv merge raised an error");
			}
			
		} catch(Exception e) {
			throw new VideoParserException(e);
		}
	}
}
