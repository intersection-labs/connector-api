package io.unequal.reuse.data;


public abstract class DataValidationException extends RuntimeException {

	private final Property<?> _property;
	private final Object _value;
	
	protected DataValidationException(String message, Property<?> prop, Object value) {
		super(message);
		_property = prop;
		_value = value;
	}
	
	public Property<?> property() {
		return _property;
	}
	
	public Object value() {
		return _value;
	}

	private static final long serialVersionUID = 1L;
}
