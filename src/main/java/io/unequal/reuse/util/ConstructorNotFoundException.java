package io.unequal.reuse.util;


public class ConstructorNotFoundException extends ReflectionException {

	public ConstructorNotFoundException() {
		super("requested constructor was not found");
	}

	private static final long serialVersionUID = 1L;
}
