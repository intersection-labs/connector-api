package io.unequal.reuse.util;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;


public class Reflection {

	@SuppressWarnings("unchecked")
	public static <T> T object(Class<T> c, Class<?>[] params, Object[] args) {
		Checker.nil(c);
		Checker.hasNull(params);
		Checker.nil(args);
		// Check if the class is an interface:
		int modifiers = c.getModifiers();
		if(Modifier.isInterface(modifiers)) {
			throw new ConstructionTargetException("cannot instantiate an interface");
		}
		// Check if the class is abstract:
		// NOTE: this needs to be tested _after_ Modifier.isInterface,
		// because Modifier.isAbstract also returns true for interfaces.
		if(Modifier.isAbstract(modifiers)) {
			throw new ConstructionTargetException("cannot instantiate an abstract class");
		}
		try {
			// Create "regular" (no constructor) enum:
			if(c.isEnum() && params.length==1 && params[0]==String.class && args.length==1 && args[0].getClass()==String.class) {
				@SuppressWarnings("rawtypes")
				T result = (T)Enum.valueOf((Class<? extends Enum>)c, (String)args[0]); 
				return result;
			}
			else {
				try {
					Constructor<T> cons = c.getDeclaredConstructor(params);
					return cons.newInstance(args);
				}
				catch(NoSuchMethodException nsme) {
					// Did not find a direct match, let's look for a sub-class match:
					Constructor<T>[] constructors = (Constructor<T>[])c.getConstructors();
					for(Constructor<T> cons : constructors) {
						Class<?>[] consParams = cons.getParameterTypes();
						if(consParams.length == params.length) {
							boolean matchFound = true;
							for(int i=0; i<params.length; i++) {
								if(!consParams[i].isAssignableFrom(params[i])) {
									matchFound = false;
									break;
								}
							}
							if(matchFound) {
								return cons.newInstance(args);
							}
						}
					}
					// If it got here, no appropriate constructor has been found:
					throw new ConstructorNotFoundException();
				}
			}
		}
		catch(IllegalAccessException iae) {
			throw new ConstructorNotVisibleException(iae);
		}
		catch(InstantiationException ie) {
			// This can't happen, since we tested 
			// whether the class was abstract before:
			throw new IntegrityException(ie);
		}
		catch(InvocationTargetException ite) {
			throw new ConstructorInvocationException(ite.getCause());
		}
	}

	public static <T> T object(Class<T> c, Object ... args) {
		Checker.nil(c);
		Checker.hasNull(args);
		Class<?>[] params = new Class[args.length];
		for(int i=0; i<params.length; i++) {
			params[i] = args[i].getClass();
		}
		return object(c, params, args);
	}

	public static <T> T object(Class<T> c) {
		return object(c, new Class[0], new Object[0]);
	}
	
	public static Class<?> load(boolean lenient, String className) {
		Checker.empty(className);
		try {
			return Class.forName(className);
		}
		catch(ClassNotFoundException cnfe) {
			if(lenient) {
				return null;
			}
			else {
				throw new ReflectionException(cnfe);
			}
		}
	}
	
	public static Class<?> load(String className) {
		return load(false, className);
	}
	
	public static Method declaredMethod(boolean lenient, Class<?> c, String name, Class<?> ... parameterTypes) {
		Checker.nil(c);
		Checker.empty(name);
		try {
			return c.getDeclaredMethod(name, parameterTypes);
		}
		catch(Exception e) {
			if(lenient) {
				return null;
			}
			throw new ReflectionException(e);
		}
	}

	public static Method declaredMethod(Class<?> c, String name, Class<?> ... parameterTypes) {
		return declaredMethod(false, c, name, parameterTypes);
	}

	public static Method method(boolean lenient, Class<?> c, String name, Class<?> ... parameterTypes) {
		Checker.nil(c);
		Checker.empty(name);
		try {
			return c.getMethod(name, parameterTypes);
		}
		catch(Exception e) {
			if(lenient) {
				return null;
			}
			throw new ReflectionException(e);
		}
	}

	public static Method method(Class<?> c, String name, Class<?> ... parameterTypes) {
		return method(false, c, name, parameterTypes);
	}
	
	public static Object invoke(Method toCall, Object target, Object ... params) {
		Checker.nil(toCall);
		Checker.nil(target);
		try {
			return toCall.invoke(target, params);
		}
		catch(InvocationTargetException ite) {
			throw new ReflectionException(ite);
		}
		catch(IllegalAccessException iae) {
			throw new ReflectionException(iae);
		}
	}

	public static Object fieldValue(Class<?> c, String fieldName, Object o) {
		Checker.nil(c);
		Checker.empty(fieldName);
		try {
			return c.getField(fieldName).get(o);
		}
		catch(NoSuchFieldException nsfe) {
			throw new IllegalUsageException(Strings.expand("could not find field {} in class {}", fieldName, c.getName()));
		}
		catch (IllegalAccessException e) {
			throw new IllegalUsageException(Strings.expand("could not access field {} in class {}", fieldName, c.getName()));
		}
	}
}
