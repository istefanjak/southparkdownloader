package com.istef.southpark;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Const {
	public static final String SEGMENT_URI_FORMAT = "https://media-utils.mtvnservices.com/services/MediaGenerator/%s?aspectRatio=16:9&lang=en&context=Array&format=json&acceptMethods=hls";
	public static Path MKVMERGE;
	public static final Path MKVMERGE_LOG = Paths.get("log", "mkvmerge.log");
	public static final String REPO_DIR = "repo";
	public static final String TMP_DIR = "tmp";
	public static Path DL_DIR;
}
