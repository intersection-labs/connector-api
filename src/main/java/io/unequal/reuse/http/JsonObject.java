package io.unequal.reuse.http;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import io.unequal.reuse.util.Checker;


public class JsonObject {

	// TYPE:
	public static JsonObject parse(String s) {
		Checker.empty(s);
		try {
			return new JsonObject((JSONObject)new JSONParser().parse(s));
		}
		catch(ParseException pe) {
			throw new IllegalArgumentException(pe);
		}
	}
	
	// INSTANCE:
	private final JSONObject _source;
	
	// For Self and JsonList:
	JsonObject(JSONObject json) {
		_source = json;
	}
	
	public JsonObject() {
		_source = new JSONObject();
	}
	
	public int hashCode() {
		return _source.hashCode();
	}
	
	public String toString() {
		return _source.toString();
	}

	@SuppressWarnings("unchecked")
	public JsonObject put(String name, Object value) {
		Checker.empty(name);
		if(value != null) {
			if(value.getClass() == JsonObject.class) {
				value = ((JsonObject)value)._source;
			}
			else if(value.getClass() == JsonList.class) {
				value = ((JsonList<?>)value).getSource();
			}
			else if(value.getClass() != String.class && !(value instanceof Number) && value.getClass() != Boolean.class) {
				value = value.toString();
			}
		}
		_source.put(name, value);
		return this;
	}
	
	public Object remove(String name) {
		Checker.empty(name);
		Object value = _source.remove(name);
		if(value != null) {
			if(value instanceof JSONObject) {
				value = new JsonObject((JSONObject)value);
			}
		}
		return value;
	}

	@SuppressWarnings("unchecked")
	public JsonObject addChild(String name) {
		Checker.empty(name);
		JsonObject child = new JsonObject();
		_source.put(name, child._source);
		return child;
	}
	
	@SuppressWarnings("unchecked")
	public <E> List<E> addChildListOf(String name, Class<E> type) {
		Checker.empty(name);
		Checker.nil(type);
		JSONArray array = new JSONArray();
		_source.put(name, array);		
		return new JsonList<E>(array, type);
	}
		
	public String getString(String name) {
		Checker.empty(name);
		return (String)_source.get(name);
	}
	
	public Long getLong(String name) {
		Checker.empty(name);
		return (Long)_source.get(name);
	}

	public JsonObject getJsonObject(String name) {
		Checker.empty(name);
		JSONObject json = (JSONObject)_source.get(name);
		return new JsonObject(json);
	}
	
	public <E> List<E> getListOf(String name, Class<E> type) {
		Checker.empty(name);
		Checker.nil(type);
		final JSONArray array = (JSONArray)_source.get(name);
		if(array == null) {
			return null;
		}
		return new JsonList<E>(array, type);
	}

	public void write(Writer out, boolean prettyPrint) throws IOException {
		Checker.nil(out);
		_source.writeJSONString(out);
	}

	public void write(Writer out) throws IOException {
		write(out, false);
	}
	
	// For JsonList:
	JSONObject getSource() {
		return _source;
	}
}
