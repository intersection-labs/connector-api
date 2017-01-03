package io.unequal.reuse.data;
import static io.unequal.reuse.util.Util.*;


public class UniqueConstraintException extends ConstraintException {

	UniqueConstraintException(Property<?> prop, Object value) {
		super(x("unique constraint: entity with '{}' = '{}' already exists", prop.name(), value), prop, value);
	}

	UniqueConstraintException(UniqueConstraint uc) {
		super(x("unique constraint: entity with properties {} already exists", uc), null, null);
	}

	private static final long serialVersionUID = 1L;
}
