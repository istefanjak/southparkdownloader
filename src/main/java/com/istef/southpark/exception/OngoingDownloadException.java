package com.istef.southpark.exception;

public class OngoingDownloadException extends Exception {
	private static final long serialVersionUID = 4281102595465960887L;

	public OngoingDownloadException() {
		super();
	}

	public OngoingDownloadException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public OngoingDownloadException(String message, Throwable cause) {
		super(message, cause);
	}

	public OngoingDownloadException(String message) {
		super(message);
	}

	public OngoingDownloadException(Throwable cause) {
		super(cause);
	}

}
