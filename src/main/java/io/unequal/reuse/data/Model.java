package io.unequal.reuse.data;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Collection;
import io.unequal.reuse.util.Checker;
import static io.unequal.reuse.util.Util.*;



public abstract class Model {

	private final Map<String,Entity<?>> _entities;
	private final Set<String> _tables;

	protected Model() {
		_entities = new HashMap<>();
		_tables = new HashSet<>();
	}
	
	protected void add(Entity<?> e) {
		Checker.nil(e);
		if(_entities.containsKey(e.name())) {
			throw new IllegalArgumentException(x("entity '{}' has already been registered", e.name()));
		}
		if(_tables.contains(e.tableName())) {
			throw new IllegalArgumentException(x("table '{}' has already been registered", e.tableName()));
		}
		_entities.put(e.name(), e);
		_tables.add(e.tableName());
	}
	
	// For Database:
	Collection<Entity<?>> entities() {
		return _entities.values();
	}
}
