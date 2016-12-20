package io.unequal.reuse.util;
import java.util.List;
import java.util.AbstractList;


public class ImmutableList<T> extends AbstractList<T> {

	private final List<T> _list;
	private final T[] _array;
	
	public ImmutableList(List<T> list) {
		Checker.checkNull(list);
		_list = list;
		_array = null;
	}
	
	public ImmutableList(T[] array) {
		Checker.checkNull(array);
		_list = null;
		_array = array;
	}

	public T get(int index) {
		return _list == null ? _array[index] : _list.get(index);
	}

	public int size() {
		return _list == null ? _array.length : _list.size();
	}
}
