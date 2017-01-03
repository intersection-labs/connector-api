package io.unequal.reuse.util;
import java.util.Set;
import java.util.Map;
import java.util.AbstractMap;


public class ImmutableMap<K,V> extends AbstractMap<K,V> {

	private final Map<K,V> _map;
	
	public ImmutableMap(Map<K,V> map) {
		Checker.nil(map);
		_map = map;
	}

	public Set<Map.Entry<K,V>> entrySet() {
		return new ImmutableSet<Map.Entry<K,V>>(_map.entrySet());
	}
}
