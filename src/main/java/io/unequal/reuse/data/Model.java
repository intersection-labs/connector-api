package io.unequal.reuse.data;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import io.unequal.reuse.util.Checker;
import static io.unequal.reuse.util.Util.*;



public abstract class Model {

	private final Map<String,Entity<?>> _entities;

	protected Model() {
		_entities = new HashMap<>();
	}
	
	protected void add(Entity<?> e) {
		Checker.nil(e);
		if(_entities.containsKey(e.name())) {
			throw new IllegalArgumentException(x("entity '{}' has already been registered", e.getClass().getSimpleName()));
		}
		_entities.put(e.name(), e);
	}
	
	// For Database:
	Collection<Entity<?>> entities() {
		return _entities.values();
	}
}
