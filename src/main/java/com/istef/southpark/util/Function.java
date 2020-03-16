package com.istef.southpark.util;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.lindstrom.m3u8.model.Resolution;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

public class Function {
	private static final Logger logger = LoggerFactory.getLogger(Function.class);
	
	public static Path addToFileName(Path path, int num) {
		String pathStr = path.toString();
		String ext = "";
		if(pathStr.indexOf(".") > 0) {
			ext = pathStr.substring(pathStr.lastIndexOf("."));
			pathStr = pathStr.substring(0, pathStr.lastIndexOf("."));
		}
		return Paths.get(pathStr + num + ext);
	}
	
	public static List<Path> getAllFilesMatcher(Path path) {
		List<Path> ret = new ArrayList<>();
		String pathFileName = path.getFileName().toString();
		String ext = "";
		if(pathFileName.indexOf(".") > 0) {
			ext = pathFileName.substring(pathFileName.lastIndexOf("."));
			pathFileName = pathFileName.substring(0, pathFileName.lastIndexOf("."));
		}
		
		try( DirectoryStream<Path> stream = Files.newDirectoryStream(path.getParent()) ) {
			for(Path p : stream) {
				String curFileName = p.getFileName().toString();
				if(curFileName.startsWith(pathFileName) && curFileName.endsWith(ext)) {
					ret.add(p);
				}
			}
			
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return ret;
	}
	
	public static String strForFileName(String str) {
		return str.replaceAll("[^a-zA-Z0-9 ]", "");
	}
	
	public static Resolution consoleResolutionPicker(List<Resolution> resolutions) {
		Scanner scanner = new Scanner(System.in);
		for (int i = 0; i < resolutions.size(); i++) {
			System.out.println(String.valueOf(i+1) + ". " + resolutions.get(i));
		}
		int choice = 0;
		while (choice < 1 || choice > resolutions.size()) {
			System.out.print("Choice: ");
			choice = scanner.nextInt();
		}
		System.out.println();
		scanner.close();
		return resolutions.get(choice-1);
		
	}
	
	public static Integer calculate169Height(Integer width) {
		return (int)((9. / 16.) * width);
	}
	
	public static Integer calculate169Width(Integer height) {
		return (int)((16. / 9.) * height);
	}
	
	public static void setZeroAnchor(Node child) {
		AnchorPane.setTopAnchor(child, 0.0);
		AnchorPane.setRightAnchor(child, 0.0);
		AnchorPane.setBottomAnchor(child, 0.0);
		AnchorPane.setLeftAnchor(child, 0.0);
	}
}
