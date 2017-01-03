package io.unequal.reuse.util;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;



public class Checker {

	public static Object nil(Object arg) {
		if(arg == null) {
			throw new IllegalArgumentException("null argument");
		}
		return arg;
	}
	
	public static String empty(String arg) {
		nil(arg);
		if(arg.equals(Strings.EMPTY)) {
			throw new IllegalArgumentException("empty argument");
		}
		return arg;
	}
	
	public static Object[] empty(Object[] arg) {
		nil(arg);
		if(arg.length == 0) {
			throw new IllegalArgumentException("empty argument");
		}
		return arg;
	}

	public static char[] empty(char[] arg) {
		nil(arg);
		if(arg.length == 0) {
			throw new IllegalArgumentException("empty argument");
		}
		return arg;
	}

	public static byte[] empty(byte[] arg) {
		nil(arg);
		if(arg.length == 0) {
			throw new IllegalArgumentException("empty argument");
		}
		return arg;
	}
	
	public static short[] empty(short[] arg) {
		nil(arg);
		if(arg.length == 0) {
			throw new IllegalArgumentException("empty argument");
		}
		return arg;
	}

	public static int[] empty(int[] arg) {
		nil(arg);
		if(arg.length == 0) {
			throw new IllegalArgumentException("empty argument");
		}
		return arg;
	}

	public static long[] empty(long[] arg) {
		nil(arg);
		if(arg.length == 0) {
			throw new IllegalArgumentException("empty argument");
		}
		return arg;
	}

	public static float[] empty(float[] arg) {
		nil(arg);
		if(arg.length == 0) {
			throw new IllegalArgumentException("empty argument");
		}
		return arg;
	}

	public static double[] empty(double[] arg) {
		nil(arg);
		if(arg.length == 0) {
			throw new IllegalArgumentException("empty argument");
		}
		return arg;
	}

	public static Collection<?> empty(Collection<?> arg) {
		nil(arg);
		if(arg.isEmpty()) {
			throw new IllegalArgumentException("empty argument");
		}
		return arg;
	}

	public static Map<?,?> empty(Map<?,?> arg) {
		nil(arg);
		if(arg.isEmpty()) {
			throw new IllegalArgumentException("empty argument");
		}
		return arg;
	}

	public static Object[] hasNull(Object[] array) {
		nil(array);
		for(int i=0; i<array.length; i++) {
			if(array[i] == null) {
				throw new IllegalArgumentException("position "+i+" is null");
			}
		}
		return array;
	}

	public static String[] hasEmpty(String[] array) {
		nil(array);
		for(int i=0; i<array.length; i++) {
			if(Strings.empty(array[i])) {
				throw new IllegalArgumentException(Strings.expand("position {} is {}", i, (array[i]==null ? "null" : "empty")));
			}
		}
		return array;
	}

	public static Object[] hasDuplicates(Object[] array) {
		nil(array);
		Map<Object,Integer> map = new HashMap<>();
		for(int i=0; i<array.length; i++) {
			Integer pos = map.get(array[i]);			
			if(pos != null) {
				throw new IllegalArgumentException(Strings.expand("positions {} and {} are duplicate", pos, i));
			}
			map.put(array[i], i);
		}
		return array;
	}

	public static Object[] length(Object[] array, int length) {
		nil(array);
		if(array.length != length) {
			throw new IllegalArgumentException(Strings.expand("illegal length {} (expected {})", array.length, length));
		}
		else {
			return array;
		}
	}

	public static Object[] matchingLength(Object[] array1, Object[] array2) {
		nil(array1);
		nil(array2);
		if(array1.length != array2.length) {
			throw new IllegalArgumentException(Strings.expand("expected {} elements, found {}", array1.length, array2.length));
		}
		else {
			return array1;
		}
	}

	public static long max(long value, long max) {
		if(value > max) {
			throw new IllegalArgumentException("illegal value "+value);
		}
		return value;
	}

	public static long min(long value, long min) {
		if(value < min) {
			throw new IllegalArgumentException("illegal value "+value);
		}
		return value;
	}

	public static Object in(Object value, Object ... allowed) {
		for(Object o : allowed) {
			if(value.equals(o)) {
				return value;
			}
		}
		throw new IllegalArgumentException("illegal value "+value);
	}

	// TODO share implementation with EAP BL validators
	private final static Pattern _iPattern  = Pattern.compile("[a-zA-Z0-9_]*");
	private final static Pattern _tiPattern = Pattern.compile("[a-zA-Z0-9_-]*");
	private final static Pattern _iPatternDot  = Pattern.compile("([.]|[a-zA-Z0-9_])*");
	private final static Pattern _tiPatternDot = Pattern.compile("([.]|[a-zA-Z0-9_-])*");

	public static String codeIdentifier(String name, boolean acceptDot) {
		empty(name);
		if(acceptDot) {
			if(name.contains("..")) {
				throw new IllegalArgumentException("identifier uses dot characters in sequence");
			}
			_checkIdentifier(name, _iPatternDot.matcher(name));
		}
		else {
			_checkIdentifier(name, _iPattern.matcher(name));
		}
		return name;
	}
	
	public static String codeIdentifier(String name) {
		return codeIdentifier(name, false);
	}
	
	public static String textIdentifier(String name, boolean acceptDot) {
		empty(name);
		// Check first letter:
		if(Character.isDigit(name.charAt(0))) {
			throw new IllegalArgumentException("identifier must start with a letter or _ and starts with '"+name.charAt(0)+"'");
		}
		if(acceptDot) {
			if(name.contains("-.") || name.contains(".-") || name.contains("--") || name.contains("..")) {
				throw new IllegalArgumentException("identifier uses dot and - characters in sequence");
			}
			_checkIdentifier(name, _tiPatternDot.matcher(name));
		}
		else {
			_checkIdentifier(name, _tiPattern.matcher(name));
		}
		return name;
	}

	public static String textIdentifier(String name) {
		return textIdentifier(name, false);
	}

	private static void _checkIdentifier(String name, Matcher m) {
		// Check first letter:
		if(Character.isDigit(name.charAt(0))) {
			throw new IllegalArgumentException("identifier must start with a letter or _ and starts with '"+name.charAt(0)+"'");
		}
		if(!m.matches()) {
			// show the illegal characters found:
			String illegalChars = m.replaceAll("");
			illegalChars = Strings.removeRepeatedChars(illegalChars);
			throw new IllegalArgumentException("identifier has illegal characters '"+illegalChars+"'");
		}
	}

}
