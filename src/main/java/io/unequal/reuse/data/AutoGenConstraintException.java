package io.unequal.reuse.data;


public class AutoGenConstraintException extends ConstraintException {

	AutoGenConstraintException(Property<?> prop, Object value) {
		super("auto-generated constraint: property '"+prop.name()+"' cannot be set", prop, value);
	}

	private static final long serialVersionUID = 1L;
}
