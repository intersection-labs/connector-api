package io.unequal.reuse.data;
import java.util.List;
import java.util.Iterator;
import io.unequal.reuse.util.ImmutableList;
import io.unequal.reuse.util.IntegrityException;


public class QueryResult<I extends Instance<?>> {

	private final ImmutableList<I> _results;

	// For Connection:
	QueryResult(List<I> results) {
		_results = new ImmutableList<>(results);
	}
	
	public ImmutableList<I> list() {
		return _results;
	}
	
	public Iterator<I> iterate() {
		return _results.iterator();
	}
	
	public I single() {
		if(_results.isEmpty()) {
			return null;
		}
		if(_results.size() > 1) {
			throw new IntegrityException(_results.size());
		}
		return _results.get(0);
	}
}
