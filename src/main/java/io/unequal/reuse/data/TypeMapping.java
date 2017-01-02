package io.unequal.reuse.data;

abstract class TypeMapping<W, U> {

	private final Class<?> _wrappedType;
	private final Class<?> _unwrappedType;
	private final int _sqlType;

	protected TypeMapping(Class<W> wrappedType, Class<U> unrappedType, int sqlType) {
		_wrappedType = wrappedType;
		_unwrappedType = unrappedType;
		_sqlType = sqlType;
	}
	
	public Class<?> wrappedType() {
		return _wrappedType;
	}

	public Class<?> unwrappedType() {
		return _unwrappedType;
	}

	public int sqlType() {
		return _sqlType;
	}

	public Object wrap(Object value, Class<?> type, Connection c) {
		return value;
	}

	public Object unwrap(Object arg) {
		return arg;
	}
}
