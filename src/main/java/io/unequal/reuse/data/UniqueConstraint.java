package io.unequal.reuse.data;
import io.unequal.reuse.util.Arrays;
import io.unequal.reuse.util.Checker;


class UniqueConstraint {

	private final Query<?> _query;
	private final Property<?>[] _props;

	public UniqueConstraint(Query<?> query, Property<?> ... props) {
		// Precondition checks are done on Entity.
		_query = query;
		_props = props;
		for(Property<?> prop : props) {
			_query.where(prop.equalTo());
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
	
	public Property<?>[] properties() {
		return _props;
	}

	public Object[] args(Instance<?> i, Connection c) {
		Checker.nil(i);
		Property<?>[] params = _query.params();
		Object[] args = new Object[params.length];
		for(int j=0; j<args.length; j++) {
			// TODO we are wapping the values here, only for them to be unrapped on c.run
			args[j] = i.get(params[j], c);
		}
		return args;
	}
}
