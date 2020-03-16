package com.istef.southpark.ui.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.istef.southpark.Const;
import com.istef.southpark.repo.Repo;
import com.istef.southpark.repo.RepoLoader;

import javafx.beans.property.ObjectProperty;

public class RepoModel {
	public static List<Repo> loadRepos(ObjectProperty<Exception> exceptionProperty) {
		return RepoLoader.getRepos(Paths.get(Const.REPO_DIR), exceptionProperty);
	}
	
	public static void deleteRepo(Repo repo) throws IOException {
		Files.delete(repo.getPath());
	}
}
