package io.unequal.reuse.data;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import io.unequal.reuse.util.Arrays;
import io.unequal.reuse.util.Checker;
import io.unequal.reuse.util.HashKey;
import io.unequal.reuse.util.Reflection;
import io.unequal.reuse.util.ConstructorNotFoundException;
import io.unequal.reuse.util.ConstructorNotVisibleException;
import io.unequal.reuse.util.IntegrityException;
import static io.unequal.reuse.util.Util.*;


public abstract class Instance<E extends Entity<?>> {

	// TYPE:
	static <I extends Instance<?>> I newFrom(Class<I> c) {
		Checker.checkNull(c);
		try {
			return Reflection.createObject(c);
		}
		catch(ConstructorNotFoundException cnfe) {
			throw new IntegrityException(c.getSimpleName()+" needs to have an empty constructor");
		}
		catch(ConstructorNotVisibleException cnfe) {
			throw new IntegrityException(c.getSimpleName()+"'s empty constructor must be visible");
		}
	}
	
	// INSTANCE:
	private final Map<Property<?>,Object> _values;
	private final Map<Property<?>,Object> _updates;
	private boolean _persisted;
	
	protected Instance() {
		_values = new HashMap<>();
		_updates = new HashMap<>();
		_persisted = false;
	}

	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		if(o == null) {
			return false;
		}
		if(!(o instanceof Instance)) {
			return false;
		}
		if(o.getClass() == getClass()) {
			return ((Instance<?>)o).getId().equals(getId());
		}
		return false;
	}
	
	public int hashCode() {
		return new HashKey(getClass(), getId()).hashCode();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder(getEntity().getInstanceName());
		sb.append(':');
		sb.append(' ');
		Iterator<Property<?>> it = getEntity().getProperties().values().iterator(); 
		while(it.hasNext()) {
			Property<?> prop = it.next();
			sb.append('[');
			sb.append(prop.getName());
			sb.append('=');
			sb.append(getValue(prop));
			sb.append(']');
			if(it.hasNext()) {
				sb.append(' ');
			}
		}
		return sb.toString();
	}

	public abstract E getEntity();
	public abstract String describe();

	public final Long getId() { return getValue(getEntity().id); }
	public final Date getTimeCreated() { return getValue(getEntity().timeCreated); }
	public final Date getTimeUpdated() { return getValue(getEntity().timeUpdated); }

	public <T> T getValue(Property<T> prop, Connection c) {
		Checker.checkNull(prop);
		_checkProperty(prop);
		if(prop.isForeignKey()) {
			Checker.checkNull(c);
		}
		// Check if we have the object cached:
		// (we need to use containsKey to cater for updates to NULL)
		if(_updates.containsKey(prop)) {
			return prop.wrap(_updates.get(prop), c);
		}
		// We don't:
		return prop.wrap(_values.get(prop), c);
	}

	public <T> T getValue(Property<T> prop) {
		return getValue(prop, null);
	}

	protected <T> void setValue(Property<T> prop, T value) {
		Checker.checkNull(prop);
		_checkProperty(prop);
		// Prevent updates to internal properties:
		if(Arrays.contains(prop, getEntity().id, getEntity().timeCreated, getEntity().timeUpdated)) {
			throw new IllegalArgumentException(x("cannot set property '{}'", prop.getName()));
		}
		// TODO remove this check. Should be enforced by the compiler
		// Validate data type (it should be enforced by the compiler):
		if(value != null) {
			if(!prop.getType().isAssignableFrom(value.getClass())) {
				throw new IntegrityException(value.getClass());
			}
		}
		// TODO Validate that data type is accepted by GAE: (note this will be done on Property, do we really need it here?		
		// Validate format:
		// TODO
		// if(f.getValidator() != null) {
		// Validate mandatory constraint:
		if(value == null && prop.isMandatory()) {
			throw new MandatoryConstraintException(prop);
		}
		// Validate read-only constraint:
		if(persisted()) {
			if(prop.isReadOnly()) {
				throw new ReadOnlyConstraintException(prop, value);
			}
		}
		// Validate auto-generated constraint:
		if(prop.isAutoGenerated()) {
			throw new AutoGenConstraintException(prop, value);
		}
		// Validate foreign key:
		if(prop.isForeignKey()) {
			Instance<?> i = (Instance<?>)value;
			if(!i.persisted()) {
				throw new IllegalStateException(x("foreign entity [{}] has not been persisted", i));
			}
		}
		// Track update:
		Object unwrapped = prop.unwrap(value);
		_updates.put(prop,  unwrapped);
		// Check if the property is being set to its current value:
		_removeIfNotUpdated(prop, unwrapped);
	}

	public boolean hasUpdates() {
		return _updates.size() > 0;
	}

	public boolean persisted() {
		return _persisted;
	}

	public Predicate[] getNaturalKeyAsArg() {
		Property<?>[] props = getEntity().getNaturalKeyProperties();
		Predicate[] arg = new Predicate[props.length];
		for(int i=0; i<props.length; i++) {
			arg[i] = new Predicate(props[i], Predicate.Operator.EQUAL, getValue(props[i]));
		}
		return arg;
	}
	
	// For Connection:
	void setPrimaryKey(Long key) {
		Checker.checkMinValue(key, 1);
		_values.put(getEntity().id, key);
		_persisted = true;
	}

	// For ???
	public Object[] args(Query<?> query, Connection c) {
		Checker.checkNull(query);
		Property<?>[] params = query.params();
		Object[] args = new Object[params.length];
		for(int i=0; i<args.length; i++) {
			args[i] = getValue(params[i], c);
		}
		return args;
	}
	
	// For Entity:
	Set<Map.Entry<Property<?>,Object>> getUpdates() {
		return new HashMap<>(_updates).entrySet();
	}

	// For Entity:
	Set<Property<?>> getUpdatedProperties() {
		return new HashSet<>(_updates.keySet());
	}

	// For Entity:
	void updateFrom(Instance<?> i) {
		_updates.clear();
		_updates.putAll(i._updates);
		// Remove any redundant updates:
		for(Property<?> prop : i._updates.keySet()) {
			_removeIfNotUpdated(prop, _updates.get(prop));
		}
	}

	// For Entity:
	boolean isUpdated(Property<?> prop) {
		return _updates.containsKey(prop);
	}

	// For Entity:
	void flush() {
		_values.putAll(_updates);
		_updates.clear();
	}

	// For Connection:
	void update(Property<?> prop, Object value, boolean flush) {
		if(flush) {
			_values.put(prop, value);
		}
		else {
			_updates.put(prop, value);
		}
	}
	
	private void _checkProperty(Property<?> prop) {
		// Check if setting a property that pertains to this entity:
		if(!getEntity().getProperties().containsValue(prop)) {
			throw new IllegalArgumentException(x("property '{}' cannot be used in entity '{}'", prop.getFullName(), getEntity().getName()));
		}
	}

	private void _removeIfNotUpdated(Property<?> prop, Object value) {
		if(_values.containsKey(prop)) {
			Object current = _values.get(prop);
			if(value == null) {
				if(current == null) {
					_updates.remove(prop);
				}
			}
			else {
				if(value.equals(current)) {
					_updates.remove(prop);
				}
			}
		}
	}
}
