package com.istef.southpark.repo;

import java.nio.file.Path;
import java.util.ArrayList;

import com.istef.southpark.exception.RepoLoadException;
import com.istef.southpark.model.json.RepoModel;

public class EmptyRepo extends Repo {
	private String name, description;
	
	public EmptyRepo(Path relativePath, String name, String description) throws RepoLoadException {
		super(relativePath);
		this.name = name;
		this.description = description;
		repoModel = init();
	}

	@Override
	protected RepoModel initRepoModel() throws RepoLoadException {
		return null;
	}
	
	private RepoModel init() {
		RepoModel model = new RepoModel();
		model.name = name;
		model.description = description;
		model.seasons = new ArrayList<>();
		
		return model;
	}

}
