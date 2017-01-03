package io.unequal.reuse.util;
import java.util.Set;
import java.util.AbstractSet;
import java.util.Iterator;


public class ImmutableSet<T> extends AbstractSet<T> {

	private final Set<T> _set;

	public ImmutableSet(Set<T> c) {
		Checker.nil(c);
		_set = c;
	}

	public Iterator<T> iterator() {
		return new ImmutableIterator<T>(_set.iterator());
	}
	
	public int size() {
		return _set.size();
	}
}
