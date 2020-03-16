package com.istef.southpark.exception;

public class ValueException extends Exception {
	private static final long serialVersionUID = -2262279565837164287L;

	public ValueException() {
		super();
	}

	public ValueException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ValueException(String message, Throwable cause) {
		super(message, cause);
	}

	public ValueException(String message) {
		super(message);
	}

	public ValueException(Throwable cause) {
		super(cause);
	}

}
