package io.unequal.reuse.data;


public class ReadOnlyConstraintException extends ConstraintException {

	ReadOnlyConstraintException(Property<?> prop, Object value) {
		super("read-only constraint: property '"+prop.getName()+"' cannot be set", prop, value);
	}

	private static final long serialVersionUID = 1L;
}
