package com.istef.southpark.exception;

public class SubtitleParserException extends ParserException {
	private static final long serialVersionUID = 695877150163639576L;

	public SubtitleParserException() {
		super();
	}

	public SubtitleParserException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public SubtitleParserException(String message, Throwable cause) {
		super(message, cause);
	}

	public SubtitleParserException(String message) {
		super(message);
	}

	public SubtitleParserException(Throwable cause) {
		super(cause);
	}

}
