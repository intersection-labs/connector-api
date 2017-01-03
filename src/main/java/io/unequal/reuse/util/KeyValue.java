package io.unequal.reuse.util;


public class KeyValue<T> {

	private final String _name;
	private T _value;
	
	public KeyValue(String name, T value) {
		Checker.empty(name);
		_name = name;
		_value = value;
	}
	
	public String name() {
		return _name;
	}
	
	public T value() {
		return _value;
	}
	
	public KeyValue<T> value(T value) {
		_value = value;
		return this;
	}
}
