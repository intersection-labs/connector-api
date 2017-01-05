package io.unequal.reuse.data;
import java.sql.Types;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import io.unequal.reuse.util.Strings;
import io.unequal.reuse.util.Util;
import io.unequal.reuse.util.IntegrityException;



@SuppressWarnings("rawtypes")
class TypeMappings {

	private static final Map<Class<?>,TypeMapping<?,?>> _mappings = new HashMap<>();
	
	public static TypeMapping<?,?> get(Class<?> arg) {
		if(Instance.class.isAssignableFrom(arg)) {
			return _mappings.get(Instance.class);
		}
		if(Constant.class.isAssignableFrom(arg)) {
			return _mappings.get(Constant.class);
		}
		TypeMapping<?,?> mapping = _mappings.get(arg);
		if(mapping == null) {
			throw new IntegrityException(arg);
		}
		return mapping;
	}
	
	public static boolean supported(Class<?> c) {
		if(Instance.class.isAssignableFrom(c)) {
			return true;
		}
		if(Constant.class.isAssignableFrom(c)) {
			return true;
		}
		return _mappings.containsKey(c);
	}
	
	private static void _register(Class<?> type, TypeMapping<?,?> mapping) {
		if(_mappings.containsKey(type)) {
			throw new IllegalArgumentException(Util.x("duplicate mapping for type '{}'", type.getSimpleName()));
		}
		_mappings.put(type, mapping);
	}
	
	static {
		// String:
		_register(String.class, new TypeMapping<String,String>(String.class, String.class, Types.LONGVARCHAR) {
			public String wrap(Object arg, Class<?> type, Connection c) {
				String s = (String)arg;
				if(Strings.empty(s)) {
					return null;
				}
				return s;
			}
			public String unwrap(Object arg) {
				String s = (String)arg;
				if(Strings.empty(s)) {
					return null;
				}
				return s;
			}
			public int compare(Object a, Object b) {
				return ((String)a).compareTo((String)b);
			}
		});
		// Boolean:
		_register(Boolean.class, new TypeMapping<Boolean,Boolean>(Boolean.class, Boolean.class, Types.BOOLEAN) {
			public int compare(Object a, Object b) {
				return ((Boolean)a).compareTo((Boolean)b);
			}
		});
		// Integer:
		_register(Integer.class, new TypeMapping<Integer,Integer>(Integer.class, Integer.class, Types.INTEGER) {
			public int compare(Object a, Object b) {
				return ((Integer)a).compareTo((Integer)b);
			}
		});
		// Long:
		_register(Long.class, new TypeMapping<Long,Long>(Long.class, Long.class, Types.BIGINT) {
			public int compare(Object a, Object b) {
				return ((Long)a).compareTo((Long)b);
			}
		});
		// Double:
		_register(Double.class, new TypeMapping<Double,Double>(Double.class, Double.class, Types.DOUBLE) {
			public int compare(Object a, Object b) {
				return ((Double)a).compareTo((Double)b);
			}
		});
		// Date:
		_register(Date.class, new TypeMapping<Date,Date>(Date.class, Date.class, Types.TIMESTAMP) {
			public int compare(Object a, Object b) {
				return ((Date)a).compareTo((Date)b);
			}
		});
		// Constant:
		_register(Constant.class, new TypeMapping<Constant,Integer>(Constant.class, Integer.class, Types.INTEGER) {
			public Object wrap(Object arg, Class<?> type, Connection c) {
				return Constant.valueOf(type, (Integer)arg);
			}
			public Object unwrap(Object arg) {
				return ((Constant)arg).code();
			}
			public int compare(Object a, Object b) {
				return ((Integer)a).compareTo((Integer)b);
			}
		});
		// Instance:
		_register(Instance.class, new TypeMapping<Instance,Long>(Instance.class, Long.class, Types.BIGINT) {
			public Instance wrap(Object arg, Class<?> type, Connection c) {
				return c.database().entityForInstance(type).find((Long)arg, c);
			}
			public Object unwrap(Object arg) {
				return ((Instance)arg).id();
			}
			public int compare(Object a, Object b) {
				return ((Long)a).compareTo((Long)b);
			}
		});
	}
}
