package io.unequal.reuse.data;
import java.util.Map;
import java.util.HashMap;
import io.unequal.reuse.util.Checker;
import io.unequal.reuse.util.ImmutableCollection;
import io.unequal.reuse.util.IntegrityException;
import io.unequal.reuse.util.Reflection;
import io.unequal.reuse.util.Util;


public abstract class Constant {

	// TYPE:
	private static final Map<Class<?>,Map<Integer,Constant>> _register = new HashMap<>();

	public static ImmutableCollection<Constant> getValuesFor(Class<?> c) {
		Checker.checkNull(c);
		return new ImmutableCollection<Constant>(_register.get(c).values());
	}
	
	public static Constant valueOf(Class<?> c, int code) {
		Map<Integer,Constant> map = _register.get(c);
		if(map == null) {
			Reflection.load(c.getName());
			map = _register.get(c);
			if(map == null) {
				throw new IntegrityException(c);
			}
		}
		return map.get(code);
	}
	
	private static void _register(Constant c) {
		Map<Integer,Constant> constants = _register.get(c.getClass());
		if(constants == null) {
			constants = new HashMap<>();
			_register.put(c.getClass(), constants);
		}
		if(constants.containsKey(c.getCode())) {
			throw new IllegalArgumentException(Util.x("constant with code '{}' already exists", c.getCode()));
		}
		constants.put(c.getCode(), c);
	}
	
	
	// INSTANCE:
	private final int _code;
	private final String _description;

	protected Constant(int code, String description) {
		_code = code;
		_description = description;
		_register(this);
	}
	
	public int getCode() {
		return _code;
	}
	
	public String getDescription() {
		return _description;
	}
	
	public String toString() {
		return Util.x("{}: {}", getCode(), getDescription());
	}

	public boolean equals(Object o) {
		return this == o;
	}

	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + getClass().hashCode();
		hash = 31 * hash + _code;
		return hash;
	}
}
