package com.istef.southpark.ui;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.istef.southpark.Const;
import com.istef.southpark.util.FX;

public class SettingsCheck {
	
	public static boolean areDirsValid(Path dlDir, Path mkvMergeExe) {
		Path path = dlDir;
		StringBuilder errors = new StringBuilder();
		
		if (path == null || Files.notExists(path)) {
			errors.append("Download path not set or non existent.");
		}
		path = mkvMergeExe;
		if (path == null || Files.notExists(path)) {
			errors.append("\nMKVMerge executable not set or path non existent.");
		}
		if (errors.length() != 0) {
			FX.showErrorDialog(errors.toString());
			return false;
		}
		return true;
	}
	
	public static boolean areDirsValid() {
		return areDirsValid(Const.DL_DIR, Const.MKVMERGE);
	}
	
	public static boolean areDirsValid(File dlDir, File mkvMergeExe) {
		return areDirsValid(Paths.get(dlDir.getAbsolutePath()), Paths.get(mkvMergeExe.getAbsolutePath()));
	}
	
	public static boolean areDirsValid(String dlDir, String mkvMergeExe) {
		return areDirsValid(Paths.get(dlDir), Paths.get(mkvMergeExe));
	}
}
