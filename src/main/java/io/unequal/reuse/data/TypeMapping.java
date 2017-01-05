package io.unequal.reuse.data;

abstract class TypeMapping<W, U> {

	private final Class<W> _wrappedType;
	private final Class<U> _unwrappedType;
	private final int _sqlType;

	protected TypeMapping(Class<W> wrappedType, Class<U> unrappedType, int sqlType) {
		_wrappedType = wrappedType;
		_unwrappedType = unrappedType;
		_sqlType = sqlType;
	}
	
	public Class<W> wrappedType() {
		return _wrappedType;
	}

	public Class<U> unwrappedType() {
		return _unwrappedType;
	}

	public int sqlType() {
		return _sqlType;
	}

	@SuppressWarnings("unchecked")
	public W wrap(Object value, Class<?> type, Connection c) {
		return (W)value;
	}

	public Object unwrap(Object arg) {
		return arg;
	}
	
	public abstract int compare(Object a, Object b);
}
