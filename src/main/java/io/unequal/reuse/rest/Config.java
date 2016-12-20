package io.unequal.reuse.rest;
import java.util.Map;
import java.util.HashMap;
import io.unequal.reuse.util.Checker;
import io.unequal.reuse.util.IntegrityException;
import static io.unequal.reuse.util.Util.*;


public class Config {

	private final Map<String,Object> _values;

	public Config() {
		_values = new HashMap<>();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object load(String name, Class<?> type) {
		Checker.checkEmpty(name);
		if(_values.containsKey(name)) {
			throw new IllegalArgumentException(x("configuration property named '{}' has already been loaded"));
		}
		if(!type.isEnum()) {
			Checker.checkIllegalValue(type, Integer.class, Long.class, String.class);
		}
		String value = System.getenv(name);
		if(value == null) {
			throw new IntegrityException(x("could not find environment variable '{}'", name));
		}
		if(type == String.class) {
			_values.put(name, value);
			return value;
		}
		Object converted = null;
		if(type == Integer.class) {
			converted = new Integer(value);
		}
		if(type == Long.class) {
			converted = new Long(value);
		}
		if(type.isEnum()) {
			converted = Enum.valueOf((Class<? extends Enum>)type, value);
		}
		if(converted == null) {
			throw new IntegrityException(type);
		}
		_values.put(name.toLowerCase(), converted);
		return converted;
	}
	
	public Object get(String name) {
		Checker.checkEmpty(name);
		return _values.get(name.toLowerCase());
	}
}
