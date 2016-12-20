package io.unequal.reuse.util;


public class ReflectionException extends RuntimeException {

	public ReflectionException(String message, Throwable cause) {
		super(message, cause);
	}

	public ReflectionException(String message) {
		super(message);
	}

	public ReflectionException(Throwable cause) {
		super(cause);
	}

	private static final long serialVersionUID = 1L;
}
