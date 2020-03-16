package com.istef.southpark.repo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.istef.southpark.exception.RepoLoadException;
import com.istef.southpark.model.json.RepoModel;

import javafx.beans.property.ObjectProperty;

public class RepoLoader {
	private static final Logger logger = LoggerFactory.getLogger(RepoLoader.class);
	
	private static class RepoLoaderExtension extends Repo {

		public RepoLoaderExtension(Path relativePath) throws RepoLoadException {
			super(relativePath);
		}

		@Override
		protected RepoModel initRepoModel() throws RepoLoadException {
			try {
				return new Gson().fromJson(Files.readString(getPath()), RepoModel.class);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				throw new RepoLoadException(getPath(), e);
			}	
		}

	}

	public static List<Repo> getRepos(Path folder, ObjectProperty<Exception> exceptionProperty) {
		List<Repo> ret = new ArrayList<>();
		try (Stream<Path> stream = Files.walk(folder)) {
			List<Path> paths = stream.filter(Files::isRegularFile)
					.filter(p -> p.getFileName().toString().endsWith(".json"))
					.collect(Collectors.toList());
			
			for (Path p : paths) {
				try {
					Repo r = new RepoLoaderExtension(p);
					if (r.getRepoModel() == null)
						throw new RepoLoadException(r.getPath(), "Repomodel null");
					ret.add(r);
					
				} catch (RepoLoadException e) {
					logger.error(e.getMessage(), e);
					exceptionProperty.set(e);
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			exceptionProperty.set(e);
		}
		return ret;
	}

}
