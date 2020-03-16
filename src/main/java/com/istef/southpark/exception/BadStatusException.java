package com.istef.southpark.exception;

public class BadStatusException extends Exception{
	private static final long serialVersionUID = -4415010439014060287L;

	public BadStatusException() {
		super();
	}

	public BadStatusException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public BadStatusException(String message, Throwable cause) {
		super(message, cause);
	}

	public BadStatusException(String message) {
		super(message);
	}

	public BadStatusException(Throwable cause) {
		super(cause);
	}
}
