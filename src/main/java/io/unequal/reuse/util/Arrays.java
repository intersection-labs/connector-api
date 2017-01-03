package io.unequal.reuse.util;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.lang.reflect.Array;



public class Arrays {

	private Arrays() {
		super();
	}

	public static <T> T last(T[] array) {
		Checker.empty(array);
		return array[array.length-1];
	}

	@SafeVarargs
	public static <T> int indexOf(T o, T ... in) {
		Checker.nil(o);
		Checker.nil(in);
		for(int i=0; i<in.length; i++) {
			if(o.equals(in[i])) {
				return i;
			}
		}
		return -1;
	}

	@SafeVarargs
	public static <T> boolean contains(T o, T ... in) {
		return indexOf(o, in) != -1;
	}

	// TODO use Generics
	public static Object[] of(Class<?> c, Object[] array) {
		Checker.nil(c);
		Checker.nil(array);
		Object[] result = (Object[])Array.newInstance(array.getClass().getComponentType(), array.length);
		for(int i=0; i<array.length; i++) {
			result[i] = array[i];
		}
		return result;
	}
	
	// TODO use Generics
	public static Object[] of(java.lang.Class<?> c, Iterator<?> it) {
		Checker.nil(c);
		Checker.nil(it);
		LinkedList<Object> list = new LinkedList<Object>();
		while(it.hasNext()) {
			list.add(it.next());
		}
		return of(c, list.toArray());
	}

	// TODO use Generics
	public static Object[] of(java.lang.Class<?> c, Iterable<?> it) {
		Checker.nil(it);
		return of(c, it.iterator());
	}

	public static Object[] asObject(boolean[] array) {
		Object[] objArray = new Object[array.length];
		for(int i=0; i<array.length; i++) {
			objArray[i] = new Boolean(array[i]);
		}
		return objArray;
	}

	public static Object[] asObject(char[] array) {
		Object[] objArray = new Object[array.length];
		for(int i=0; i<array.length; i++) {
			objArray[i] = new Character(array[i]);
		}
		return objArray;
	}

	public static Object[] asObject(short[] array) {
		Object[] objArray = new Object[array.length];
		for(int i=0; i<array.length; i++) {
			objArray[i] = new Short(array[i]);
		}
		return objArray;
	}

	public static Object[] asObject(int[] array) {
		Object[] objArray = new Object[array.length];
		for(int i=0; i<array.length; i++) {
			objArray[i] = new Integer(array[i]);
		}
		return objArray;
	}

	public static Object[] asObject(long[] array) {
		Object[] objArray = new Object[array.length];
		for(int i=0; i<array.length; i++) {
			objArray[i] = new Long(array[i]);
		}
		return objArray;
	}

	public static Object[] asObject(float[] array) {
		Object[] objArray = new Object[array.length];
		for(int i=0; i<array.length; i++) {
			objArray[i] = new Float(array[i]);
		}
		return objArray;
	}

	public static Object[] asObject(double[] array) {
		Object[] objArray = new Object[array.length];
		for(int i=0; i<array.length; i++) {
			objArray[i] = new Double(array[i]);
		}
		return objArray;
	}

	public static <T> T[] concat(T[] array1, T[] array2) {
		Checker.nil(array1);
		Checker.nil(array2);
		List<T> result = new ArrayList<T>();
		for(T elem : array1) {
			result.add(elem);
		}
		for(T elem : array2) {
			result.add(elem);
		}
		// toArray may be used with array1, because it's
		// not big enougth to hold the results of concat.
		return result.toArray(array1);
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] subtract(T[] array1, T[] array2) {
		List<T> result = new ArrayList<T>();
		for(int i=0; i<array2.length; i++) {
			boolean found = false;
			for(int j=0; j<array1.length; j++) {
				if(array1[j].equals(array2[i])) {
					found = true;
					break;
				}
			}
			if(!found) {
				result.add(array2[i]);
			}
		}
		return (T[])result.toArray((Object[])Array.newInstance(array1.getClass().getComponentType(), 0));
	}

	public static <T> void copy(T[] from, int fromIndex, T[] to, int toIndex, int length) {
		Checker.nil(to);
		Checker.nil(from);
		Checker.min(toIndex, 0);
		Checker.min(fromIndex, 0);
		Checker.min(length, 0);
		if(toIndex+length > to.length) {
			throw new IndexOutOfBoundsException("index: "+(toIndex+length)+"; array length: "+to.length);
		}
		if(fromIndex+length > from.length) {
			throw new IndexOutOfBoundsException("index: "+(fromIndex+length)+"; array length: "+from.length);
		}
		System.arraycopy(from, fromIndex, to, toIndex, length);
	}

	public static <T> void copy(T[] from, int fromIndex, T[] to, int toIndex) {
		copy(from, fromIndex, to, toIndex, from.length);
	}

	public static <T> void copy(T[] from, T[] to) {
		copy(from, 0, to, 0);
	}

	public static String toString(Object[] array) {
		Checker.nil(array);
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for(int i=0; i<array.length; i++) {
			sb.append(array[i]);
			if(i < array.length-1) {
				sb.append(", ");
			}
		}
		sb.append("]");
		return sb.toString();
	}

	// TODO this implementation does not consider duplicates
	public static <T> boolean equalsIgnoreOrder(T[] a, T[] b) {
		Checker.nil(a);
		Checker.nil(b);
		if(a.length != b.length) {
			return false;
		}
		Set<T> set = new HashSet<>();
		set.addAll(java.util.Arrays.asList(a));
		for(int i=0; i<b.length; i++) {
			if(!set.remove(b[i])) {
				return false;
			}
		}
		return set.isEmpty();
	}
}