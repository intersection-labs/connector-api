package io.unequal.reuse.util;
import java.util.Map;
import java.util.HashMap;
import java.net.URL;
import java.net.MalformedURLException;
import static io.unequal.reuse.util.Util.*;


public class Config {

	// TYPE:
	private final static class SingletonHolder {
		private final static Config instance = new Config();
	}

	public static Config get() {
		return SingletonHolder.instance;
	}

	// INSTANCE:
	private final Map<String,Object> _values;

	private Config() {
		_values = new HashMap<>();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object load(String name, Class<?> type) {
		Checker.empty(name);
		if(_values.containsKey(name)) {
			throw new IllegalArgumentException(x("configuration property named '{}' has already been loaded", name));
		}
		if(!type.isEnum()) {
			Checker.in(type, Integer.class, Long.class, String.class, URL.class);
		}
		String value = System.getenv(name);
		name = name.toLowerCase();
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
		if(type == URL.class) {
			try {
				converted = new URL(value);
			}
			catch(MalformedURLException mue) {
				throw new IntegrityException(mue);
			}
		}
		if(type.isEnum()) {
			converted = Enum.valueOf((Class<? extends Enum>)type, value);
		}
		if(converted == null) {
			throw new IntegrityException(type);
		}
		_values.put(name, converted);
		return converted;
	}
	
	public Object get(String name) {
		Checker.empty(name);
		return _values.get(name.toLowerCase());
	}
}
