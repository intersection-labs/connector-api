package io.unequal.reuse.data;


public abstract class ConstraintException extends DataValidationException {

	protected ConstraintException(String message, Property<?> prop, Object value) {
		super(message, prop, value);
	}
	
	private static final long serialVersionUID = 1L;
}
