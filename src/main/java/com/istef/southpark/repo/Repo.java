package com.istef.southpark.repo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.istef.southpark.exception.RepoLoadException;
import com.istef.southpark.model.json.RepoModel;

public abstract class Repo {
	private Path path;
	protected RepoModel repoModel;
	
	public Repo(Path relativePath) throws RepoLoadException {
		if (!relativePath.toString().endsWith(".json"))
			throw new RepoLoadException(relativePath);
		this.path = relativePath;
		repoModel = initRepoModel();
	}
	
	public final void build() throws IOException {
		String jsonStr = new Gson().toJson(repoModel);
		Files.writeString(path, jsonStr);
	}
	
	public RepoModel getRepoModel() {
		return repoModel;
	}

	public Path getPath() {
		return path;
	}

	protected abstract RepoModel initRepoModel() throws RepoLoadException;
	
}
