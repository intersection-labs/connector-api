package io.unequal.reuse.data;
import java.util.List;
import java.util.Iterator;
import java.util.Comparator;
import java.util.Collections;
import io.unequal.reuse.util.ImmutableList;
import io.unequal.reuse.util.IntegrityException;
import io.unequal.reuse.data.Query.Direction;


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
	
	public QueryResult<I> sortByAsc(Property<?> prop) {
		Collections.sort(_results, new _InstanceComparator(prop, Direction.ASC));
		return this;
	}

	public QueryResult<I> sortByDesc(Property<?> prop) {
		Collections.sort(_results, new _InstanceComparator(prop, Direction.DESC));
		return this;
	}
	
	private class _InstanceComparator implements Comparator<I> {
		
		private final Property<?> _prop;
		private final int _multiplier;
		
		public _InstanceComparator(Property<?> prop, Direction d) {
			_prop = prop;
			if(d == Direction.ASC) {
				_multiplier = 1;
			}
			else if(d == Direction.DESC) {
				_multiplier = -1;
			}
			else {
				throw new IntegrityException(d);
			}
		}

		public int compare(I a, I b) {
			//a.unwrapped(_prop);
			//return -1 * _multiplier;
			throw new RuntimeException();
		}
	}
}
