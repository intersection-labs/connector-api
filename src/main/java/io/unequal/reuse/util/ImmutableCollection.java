package io.unequal.reuse.util;
import java.util.Collection;
import java.util.AbstractCollection;
import java.util.Iterator;


public class ImmutableCollection<T> extends AbstractCollection<T> {

	private final Collection<T> _source;
	
	public ImmutableCollection(Collection<T> source) {
		Checker.checkNull(source);
		_source = source;
	}

	public int size() {
		return _source.size();
	}

	public Iterator<T> iterator() {
		return _source.iterator();
	}
}
