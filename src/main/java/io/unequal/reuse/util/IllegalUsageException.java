package io.unequal.reuse.util;


public class IllegalUsageException extends RuntimeException {

	public IllegalUsageException(String message) {
		super(message);
	}

	public IllegalUsageException(String message, Throwable cause) {
		super(message, cause);
	}

	private static final long serialVersionUID = 1L;
}
