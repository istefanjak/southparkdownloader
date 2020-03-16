package com.istef.southpark.exception;

public class NotParsedException extends RuntimeException {
	private static final long serialVersionUID = -1410046634889130842L;

	public NotParsedException() {
		super();
	}

	public NotParsedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NotParsedException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotParsedException(String message) {
		super(message);
	}

	public NotParsedException(Throwable cause) {
		super(cause);
	}

}
