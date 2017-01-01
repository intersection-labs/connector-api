package io.unequal.reuse.data;
import io.unequal.reuse.util.Arrays;


class UniqueConstraint {

	private final Query<?> _query;
	private final Property<?>[] _props;

	public UniqueConstraint(Query<?> query, Property<?> ... props) {
		// Precondition checks are done on Entity.
		_query = query;
		_props = props;
		for(Property<?> prop : props) {
			_query.where(prop.isEqualTo());
		}
	}

	public Query<?> query() {
		return _query;
	}

	public String toString() {
		return Arrays.toString(_props);
	}
	
	public int hashCode() {
		int hash = 7;
		for(int i=0; i<_props.length; i++) {
			hash = 31 * hash + _props[i].hashCode();
		}
		return hash;
	}
	
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		if(o == null) {
			return false;
		}
		if(o instanceof UniqueConstraint) {
			return Arrays.equalsIgnoreOrder(_props, ((UniqueConstraint) o)._props);
		}
		return false;
	}
	
	public Property<?>[] getProperties() {
		return _props;
	}

	public Predicate[] toArgs(Instance<?> i) {
		Predicate[] args = new Predicate[_props.length];
		for(int j=0; j<_props.length; j++) {
			args[j] = new Predicate(_props[j], Predicate.Operator.EQUAL, i.getValue(_props[j]));
		}
		return args;
	}
}
