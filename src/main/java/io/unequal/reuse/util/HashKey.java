package io.unequal.reuse.util;
import java.util.Arrays;


public class HashKey {

	private final Object[] _keys;

	public HashKey(Object ... keys) {
		Checker.empty(keys);
		_keys = keys;
	}
	
	public boolean equals(Object o) {
		if(this == o ) {
			return true;
		}
		if(o == null) {
			return false;
		}
		if(!(o instanceof HashKey)) {
			return false;
		}
		HashKey other = (HashKey)o;
		return Arrays.equals(_keys, other._keys);
	}
	
	public int hashCode() {
		int hash = 7;
		for(int i=0; i<_keys.length; i++) {
			int hashCode = _keys[i]==null ? 0 : _keys[i].hashCode();
			hash = 31 * hash + hashCode;
		}
		return hash;
	}
}
