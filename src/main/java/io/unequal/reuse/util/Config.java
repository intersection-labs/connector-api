package io.unequal.reuse.util;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.net.URL;
import java.net.MalformedURLException;
import static io.unequal.reuse.util.Util.*;


public class Config {

	private static final Map<String,Object> _values = new HashMap<>();
	private static Properties _source = null;

	public static void source(Properties props) {
		_source = props;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object load(String name, Class<?> type) {
		Checker.empty(name);
		if(_values.containsKey(name)) {
			throw new IllegalArgumentException(x("configuration property named '{}' has already been loaded", name));
		}
		if(!type.isEnum()) {
			Checker.in(type, Integer.class, Long.class, String.class, URL.class);
		}
		String value = _source == null ? System.getenv(name) : _source.getProperty(name);
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
	
	public static Object get(String name) {
		Checker.empty(name);
		return _values.get(name);
	}
}
