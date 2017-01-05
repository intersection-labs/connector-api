package io.unequal.reuse.data;
import java.util.Comparator;
import io.unequal.reuse.util.Checker;
import io.unequal.reuse.util.IntegrityException;
import io.unequal.reuse.data.Query.Direction;


class InstanceComparator<I extends Instance<?>> implements Comparator<I> {

	private final Property<?> _prop;
	private final int _multiplier;

	public InstanceComparator(Property<?> prop, Direction d) {
		Checker.nil(prop);
		Checker.nil(d);
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
		return _multiplier * _compare(a, b);
	}

	// 5.compareTo(4): 1
	// 5.compareTo(0): 1
	// 0.compareTo(5): -1
	private int _compare(I a, I b) {
		if(a == null) {
			return b==null ? 0 : -1;
		}
		if(b == null) {
			return a==null ? 0 : 1;
		}
		Object oa = a.unwrapped(_prop);
		Object ob = b.unwrapped(_prop);
		if(oa == null) {
			return ob==null ? 0 : -1;
		}
		if(ob == null) {
			return oa==null ? 0 : 1;
		}
		return _prop.typeMapping().compare(oa, ob);
	}	
}
