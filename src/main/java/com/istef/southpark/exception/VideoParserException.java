package com.istef.southpark.exception;

public class VideoParserException extends ParserException {
	private static final long serialVersionUID = -162974006694976160L;

	public VideoParserException() {
		super();
	}

	public VideoParserException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public VideoParserException(String message, Throwable cause) {
		super(message, cause);
	}

	public VideoParserException(String message) {
		super(message);
	}

	public VideoParserException(Throwable cause) {
		super(cause);
	}

}
