package io.unequal.reuse.util;


public class ConstructorNotVisibleException extends ReflectionException {

	public ConstructorNotVisibleException(IllegalAccessException iae) {
		super("requested constructor is not visible", iae);
	}

	private static final long serialVersionUID = 1L;
}
