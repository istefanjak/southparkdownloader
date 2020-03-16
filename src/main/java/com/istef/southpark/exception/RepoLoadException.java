package com.istef.southpark.exception;

import java.nio.file.Path;

public class RepoLoadException extends Exception {
	private static final long serialVersionUID = 6550223518249628999L;
	
	private Path path;
	
	public RepoLoadException(Path path) {
		super();
		this.path = path;
	}

	public RepoLoadException(Path path, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.path = path;
	}

	public RepoLoadException(Path path, String message, Throwable cause) {
		super(message, cause);
		this.path = path;
	}

	public RepoLoadException(Path path, String message) {
		super(message);
		this.path = path;
	}

	public RepoLoadException(Path path, Throwable cause) {
		super(cause);
		this.path = path;
	}

	public Path getPath() {
		return path;
	}
	
}
