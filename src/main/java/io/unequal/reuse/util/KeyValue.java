package io.unequal.reuse.util;


public class KeyValue<T> {

	private final String _name;
	private T _value;
	
	public KeyValue(String name, T value) {
		Checker.checkEmpty(name);
		_name = name;
		_value = value;
	}
	
	public String getName() {
		return _name;
	}
	
	public T getValue() {
		return _value;
	}
	
	public KeyValue<T> setValue(T value) {
		_value = value;
		return this;
	}
}
