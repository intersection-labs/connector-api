package io.unequal.reuse.util;
import java.util.Iterator;


public class ImmutableIterator<T> implements Iterator<T> {

	private final Iterator<T> _it;

	public ImmutableIterator(Iterator<T> it) {
		Checker.checkNull(it);
		this._it = it;
	}

	public boolean hasNext() {
		return _it.hasNext();
	}

	public T next() {
		return _it.next();
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
}
