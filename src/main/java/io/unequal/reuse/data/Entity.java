package io.unequal.reuse.data;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;
import java.lang.reflect.ParameterizedType;
import io.unequal.reuse.data.Property.Constraint;
import io.unequal.reuse.data.Property.OnDelete;
import io.unequal.reuse.util.Checker;
import io.unequal.reuse.util.IllegalUsageException;
import io.unequal.reuse.util.ImmutableList;
import io.unequal.reuse.util.ImmutableMap;
import io.unequal.reuse.util.IntegrityException;
import static io.unequal.reuse.util.Util.*;


public abstract class Entity<I extends Instance<?>> {

	private final Logger _logger;
	// Data structure:
	public final Property<Long> id;
	public final Property<Date> timeCreated;
	public final Property<Date> timeUpdated;
	private final String _tableName;
	private final Class<I> _instanceType;
	private final Map<String,Property<?>> _propertyMap;
	private final List<Property<?>> _propertyList;
	private final List<Dependency> _dependencies;
	private final Set<UniqueConstraint> _uConstraints;
	private boolean _naturalKeyAdded;
	private Database _db;
	// Data management:
	private String _insertSql;

	@SuppressWarnings("unchecked")
	protected Entity(String tableName) {
		Checker.checkEmpty(tableName);
		_logger = Logger.getLogger(getClass().getName());
		// Data structure:
		_tableName = tableName;
		_instanceType = ((Class<I>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
		_propertyMap = new HashMap<>();
		_propertyList = new ArrayList<>();
		_dependencies = new ArrayList<>();
		_uConstraints = new HashSet<>();
		// TODO remove
		_naturalKeyAdded = false;
		// Common properties:
		id = addProperty(Long.class, "id", Constraint.MANDATORY, Constraint.AUTO_GENERATED);
		timeCreated = addProperty(Date.class, "timeCreated", new Generators.Now(), Constraint.MANDATORY, Constraint.READ_ONLY);
		timeUpdated = addProperty(Date.class, "timeUpdated", new Generators.Now(), Constraint.MANDATORY, Constraint.READ_ONLY);		
		// Data management:
		_insertSql = null;
	}

	protected Logger getLogger() {
		return _logger;
	}

	// Data structure methods:
	public abstract Property<?>[] getNaturalKeyProperties();
	
	public String getName() {
		return getClass().getSimpleName();
	}

	public String getInstanceName() {
		return _instanceType.getSimpleName();
	}

	public Class<I> getInstanceClass() {
		return _instanceType;
	}
	
	public String getTableName() {
		return _tableName;
	}
	
	public ImmutableMap<String,Property<?>> getProperties() {
		return new ImmutableMap<String,Property<?>>(_propertyMap);
	}

	public ImmutableList<Dependency> getDependencies() {
		return new ImmutableList<Dependency>(_dependencies);
	}
	
	protected <T> Property<T> addProperty(Class<T> c, String name, Constraint ... constraints) {
		return _addProperty(c, name, (Generators.Direct<T>)null, null, constraints);
	}

	protected <T> Property<T> addProperty(Class<T> c, String name, T def, Constraint ... constraints) {
		return _addProperty(c, name, new Generators.Direct<T>(def), null, constraints);
	}

	protected <T> Property<T> addProperty(Class<T> c, String name, ValueGenerator<T> def, Constraint ... constraints) {
		return _addProperty(c, name, def, null, constraints);
	}

	protected <T> Property<T> addProperty(Class<T> c, String name, Property.OnDelete onDelete, Constraint ...constraints) {
		return _addProperty(c, name, null, onDelete, constraints);
	}
	
	private <T> Property<T> _addProperty(Class<T> c, String name, ValueGenerator<T> def, Property.OnDelete onDelete, Constraint ... constraints) {
		Checker.checkNull(c);
		Checker.checkEmpty(name);
		Checker.checkNullElements(constraints);
		if(_propertyMap.containsKey(name)) {
			throw new IllegalArgumentException("property named '"+name+"' already exists");
		}
		Property<T> prop = new Property<T>(this, c, name, def, onDelete, constraints);
		_propertyMap.put(name, prop);
		_propertyList.add(prop);
		if(prop.isUnique() && !prop.getName().equals("id")) {
			_unique(prop);
		}
		return prop;
	}

	protected void unique(Property<?> ... props) {
		Checker.checkEmpty(props);
		Checker.checkNullElements(props);
		Checker.checkDuplicateElements(props);
		if(props.length == 1) {
			throw new IllegalArgumentException("unique constraints must have more than one property (use UNIQUE instead)");
		}
		for(Property<?> prop : props) {
			if(prop.isUnique()) {
				throw new IllegalArgumentException(x("property '{}' is already unique", prop.getName()));
			}
		}
		_unique(props);
	}

	private void _unique(Property<?> ... props) {
		UniqueConstraint uc = new UniqueConstraint(props);
		if(_uConstraints.contains(uc)) {
			throw new IllegalArgumentException(x("a unique constraint with properties {} already exists", uc));
		}
		_uConstraints.add(uc);
	}

	public Database database() {
		return _db;
	}

	// For Database:
	@SuppressWarnings("unchecked")
	void loadInto(Database db) {
		if(_db != null) {
			throw new IllegalStateException(x("entity '{}' has already been loaded into another database", getName()));
		}
		_db = db;
		getLogger().log(info("loading entity {}", getName()));
		// Load all related entities and dependencies:
		for(Property<?> prop : _propertyList) {
			if(prop.isForeignKey()) {
				// Get the foreign entity:
				Entity<?> foreignEntity = _db.getEntityForInstance(prop.getType());
				// Store related entity:
				prop.setRelatedEntity(foreignEntity);
				// Record a delete dependency:
				foreignEntity._dependencies.add(new Dependency((Entity<Instance<?>>)this, prop));
			}
		}
		// Add the natural key as a constraint:
		Property<?>[] key = getNaturalKeyProperties();
		if(key == null) {
			return;
		}
		if(key.length == 1) {
			if(!key[0].isUnique()) {
				throw new IllegalUsageException(x("property '{}' cannot be a natural key because it is not unique", key[0].getName()));
			}
			// We don't want to add a single unique property as a constraint:
			return;
		}
		_unique(key);
	}

	// Data management methods:	
	public void insert(I i, Connection c) {
		Checker.checkNull(i);
		Checker.checkNull(c);
		_checkLoadedInto(c);
		if(i.persisted()) {
			throw new IllegalArgumentException("entity has already been persisted");
		}
		Object[] params = new Object[_propertyList.size()];
		// Check properties:
		// Note: the following checks are already done on Instance.setValue:
		// Data type, format, read-only, auto-generated. Mandatory is also checked
		// on Instance.setValue, but we need to process omitted properties.
		Iterator<Property<?>> it = _propertyList.iterator();
		for(int j=0; it.hasNext(); j++) {
			Property<?> prop = it.next();
			// Skip the primary key:
			if(prop == id) {
				continue;
			}
			Object value = i.getValue(prop);
			// Automatically generate:
			if(prop.isAutoGenerated()) {
				// TODO add generators
				throw new IntegrityException();
			}
			// Default value:
			if(value == null) {
				value = i.setDefaultValueFor(prop);
			}
			// Mandatory:
			if(prop.isMandatory()) {
				if(value == null) {
					throw new MandatoryConstraintException(prop);
				}
			}
			// All checks ok:
			params[j] = value;
		}
		// Check unique constraints:
		_checkUniqueConstraintsFor(i, c);
		// Insert instance:
		Long id = c.insert(_insertSql, params);
		i.setPrimaryKey(id);
		i.flush();
	}
	
	private void _checkLoadedInto(Connection c) {
		if(_db != c.database()) {
			throw new IllegalArgumentException(x("entity '{}' is not available in this connection's database", getName()));
		}
	}

	private void _checkUniqueConstraintsFor(I i, Connection c) {
		Iterator<UniqueConstraint> it = _uConstraints.iterator();
		while(it.hasNext()) {
			UniqueConstraint uc = it.next();
			boolean forInsert = i.getId() == null;
			// Check if any of the constraint's properties have been updated:
			boolean updated = false;
			if(forInsert) {
				updated = true;
			}
			else {
				checkUpdates:
				for(Property<?> prop : uc.getProperties()) {
					if(i.isUpdated(prop)) {
						updated = true;
						break checkUpdates;
					}
				}
			}
			if(updated) {
				// They have, we need to check if the constraint has been violated:
				Instance<?> existing = c.find(this, uc.toArgs(i));
				if(existing != null) {
					if(forInsert) {
						throw new UniqueConstraintException(uc);
					}
					if(!existing.equals(i)) {
						throw new UniqueConstraintException(uc);
					}
				}
			}
		}
	}
	

	
	
	
	
	
	// TODO performance - we retrieve "existing" multiple times
	private void _checkUnique(Instance<?> i, Property<?> prop, Object value, boolean insert) {
		Instance<?> existing = findSingle(new QueryArg(prop, value, QueryArg.Operator.EQUAL));
		if(existing != null) {
			if(insert) {
				throw new UniqueConstraintException(prop, value);				
			}
			if(!existing.equals(i)) {
				throw new UniqueConstraintException(prop, value);
			}
		}
	}
	
	private void _checkUniqueConstraints(Instance<?> i, boolean insert, Set<Property<?>> updatedProps) {
		Iterator<UniqueConstraint> it = _getUniqueConstraints();
		while(it.hasNext()) {
			UniqueConstraint uc = it.next();
			// Check if any of the constraint's properties have been updated:
			boolean updated = false;
			checkUpdates:
			for(Property<?> prop : uc.getProperties()) {
				if(updatedProps.contains(prop)) {
					updated = true;
					break checkUpdates;
				}
			}
			if(updated) {
				// They have, we need to check if the constraint has been violated:
				Instance<?> existing = findSingle(uc.toArgs(i));
				if(existing != null) {
					if(insert) {
						throw new UniqueConstraintException(uc);
					}
					if(!existing.equals(i)) {
						throw new UniqueConstraintException(uc);
					}
				}
			}
		}
	}	
	
	public void insert(I i) {
		Checker.checkNull(i);
		_checkLoaded();
		if(i.persisted()) {
			throw new IllegalArgumentException("entity has already been persisted");
		}
		// Get a snapshot of updates to check constraints:
		Set<Property<?>> updatedProps = i.getUpdatedProperties();
    	// Validate constraints:
		// Note: the following constraints are already validated on Instance.setValue:
		// Data type, format, read-only, auto-generated. Mandatory is also validated
		// on Instance.setValue, but we need to check for omitted properties.
		for(Property<?> prop : getProperties().values()) {
			// Skip the primary key:
			if(prop == id) {
				continue;
			}
			Object value = i.getValue(prop);
			// Automatically generate:
			if(prop.isAutoGenerated()) {
				// TODO add generators
				throw new IntegrityException();
			}
			// Default value:
			if(value == null) {
				Object def = prop.getDefaultValue();
				if(def != null) {
					value = def;
				}
			}
			// Mandatory:
			if(prop.isMandatory()) {
				if(value == null) {
					throw new MandatoryConstraintException(prop);
				}
			}
			// Unique:
			if(prop.isUnique() && value != null) {
				// If the property is auto-generated, we already checked for uniqueness above.
				if(!prop.isAutoGenerated()) {
					_checkUnique(i, prop, value, true);
				}
			}
			// All okay, set the value. Note that we will lose some updates in case
			// one property validation fails after others have been validated successfully.
			// However, because this is an unrecoverable exception, we don't care.
			i.flush(prop, value);
		}
		_checkUniqueConstraints(i, true, updatedProps);
		getLogger().log(info("inserting instance [{}]", i));
		_put(i);
		getLogger().log(info("persisted with id {}", i.getId()));
	}

	public boolean update(I i) {
		Checker.checkNull(i);
		_checkLoaded();
		_checkPersisted(i);
		if(!i.hasUpdates()) {
			return false;
		}
		// Get a snapshot of updates to check constraints:
		Set<Property<?>> updatedProps = i.getUpdatedProperties();
		// Check updated properties:
		for(Entry<Property<?>, Object> entry : i.getUpdates()) {
			Property<?> prop = entry.getKey();
			Object value = entry.getValue();
			// On update, we only need to validate unique constraints.
			// All others are validated on Instance.setValue.
			if(prop.isUnique() && value != null) {
				_checkUnique(i, prop, value, false);
			}
			i.flush(prop, value);
		}
		_checkUniqueConstraints(i, false, updatedProps);
		i.flush(timeUpdated, new Date());
		getLogger().log(info("updating instance [{}]", i));
		_put(i);
		return true;
	}

	public boolean save(I i) {
		Checker.checkNull(i);
		_checkLoaded();
		I existing = findSingle(i.getNaturalKeyAsArg());
		if(existing == null) {
			insert(i);
			return true;
		}
		existing.updateFrom(i);
		boolean updated = update(existing);
		if(!updated) {
			// We need to do this to carry over the primary key:
			i.setGoogleEntity(existing.getGoogleEntity());
		}
		return updated;
	}

	public void delete(I i) {
		Checker.checkNull(i);
		_checkLoaded();
		_checkPersisted(i);
		getLogger().log(info("deleting {} with id {}", getInstanceName(), i.getId()));
		// Process dependencies:
		for(Dependency d : _dependencies) {
			getLogger().log(info("found dependency in {}", d.getEntity().getName()));
			Iterator<Instance<?>> it = d.findInstancesRelatedTo(i).iterate();
			while(it.hasNext()) {
				Instance<?> related = it.next();
				OnDelete onDeleteAction = d.getForeignKey().getOnDeleteAction();
				if(onDeleteAction == Property.OnDelete.CASCADE) {
					d.getEntity().delete(related);
				}
				else if(onDeleteAction == Property.OnDelete.RESTRICT) {
					// TODO onDeleteException??
					throw new RuntimeException(x("{} cannot be deleted because there is a related {}", related.getEntity().getInstanceName(), getInstanceName()));
				}
				else if(onDeleteAction == Property.OnDelete.SET_NULL) {
					related.setValue(d.getForeignKey(), null);
					d.getEntity().update(related);
				}
				else {
					throw new IntegrityException(onDeleteAction);
				}
			}
		}
		// Delete the instance:
		getLogger().log(info("running query: DELETE FROM {} WHERE id = {}", getName(), i.getId()));
		_ds.delete(i.getGoogleEntity().getKey());
	}

	public void deleteWhere(QueryArg ... params) {
		_checkLoaded();
		getLogger().log(info("running query: DELETE FROM {} WHERE {}", getName(), params));
		// TODO use Query().setKeysOnly for better performance
		_ds.delete(_getKeysFrom(new Query<I>(this).addWhere(params).run().iterate()));
	}
	
	public void deleteAll() {
		deleteWhere();
	}

	public I find(Long id, Connection c) {
		return c.find(this, id);
	}

	public I find(Long id) {
		Checker.checkNull(id);
		Checker.checkMinValue(id, 1L);
		_checkLoaded();
		// TODO cache entities already retrieved by ID. This would seriously improve performance,
		// especially when retrieving foreign entities 
		try {
			getLogger().log(info("running query: SELECT * FROM {} WHERE id = {}", getName(), id));
			com.google.appengine.api.datastore.Entity e = _ds.get(KeyFactory.createKey(getName(), id));
			getLogger().log(info(e == null ? "{} not found" : "{} found", getInstanceName()));
			return _createSafely(e);
		}
		catch(EntityNotFoundException enfe) {
			return null;
		}
	}
	
	public I findSingle(QueryArg ... args) {
		Checker.checkEmpty(args);
		Checker.checkNullElements(args);
		_checkLoaded();
		// Check if id is an argument:
		QueryArg idArg = null;
		for(QueryArg arg : args) {
			if(arg.getProperty().getName().equals("id")) {
				idArg = arg;
				break;
			}
		}
		if(idArg != null) {
			// Retrieve entity by id and compare parameters since querying on id does not work:
			// TODO use Entity.KEY_RESERVED_PROPERTY for the id
			I i = find((Long)idArg.getValue());
			if(i == null) {
				return null;
			}
			// Check if entity matches filter values:
			for(QueryArg arg : args) {
				if(arg != idArg) {
					Object value = i.getValue(arg.getProperty());
					if(value == null && arg.getValue() != null) {
						getLogger().log(info("property {} does not match", arg.getProperty().getFullName()));
						return null;
					}
					if(!value.equals(arg.getValue())) {
						getLogger().log(info("property {} does not match", arg.getProperty().getFullName()));
						return null;
					}
				}
			}
			return i;
		}
		else {
			// Retrieve entity based on filters:
			Query<I> q = new Query<>(this).addWhere(args);
			List<I> results = q.run().list();
			if(results.isEmpty()) {
				return null;
			}
			if(results.size() == 1) {
				return results.get(0);
			}
			throw new IllegalStateException(x("expected 1 result, found {}", results.size()));
		}
	}
	
	public QueryResult<I> findWhere(QueryArg ... args) {
		Checker.checkNullElements(args);
		_checkLoaded();
		Query<I> q = new Query<>(this).addWhere(args);
		return q.run();
	}
	
	public QueryResult<I> findAll() {
		return findWhere();
	}


	
	private void _addNaturalKeyConstraint() {
		if(!_naturalKeyAdded) {
			_naturalKeyAdded = true;
			Property<?>[] props = getNaturalKeyProperties();
			if(props == null) {
				return;
			}
			if(props.length == 1) {
				if(!props[0].isUnique()) {
					throw new IllegalUsageException(x("property {} cannot be a natural key because it is not unique", props[0]));
				}
				// We don't want to add a single unique property as a constraint:
				return;
			}
			addUniqueConstraint(props);
		}
	}

	// TODO is this used more than once?
	private void _checkPersisted(I i) {
		if(!i.persisted()) {
			throw new IllegalArgumentException("instance has not been persisted");
		}
	}

	private void _put(I i) {
    	// Consistency check:
		if(i.hasUpdates()) {
			throw new IntegrityException();
		}
		// Persist:
    	_ds.put(i.getGoogleEntity());
	}


	
	private Iterator<UniqueConstraint> _getUniqueConstraints() {
		_addNaturalKeyConstraint();
		return _uConstraints.iterator();
	}

	private I _createSafely(com.google.appengine.api.datastore.Entity e) {
		if(e == null) {
			return null;
		}
		return Instance.newFrom(_instanceType, e);
	}
	
	// TODO rename to KeyIterator for clarity
	private Iterable<Key> _getKeysFrom(final Iterator<I> it) {
		return new Iterable<Key>() {
			public Iterator<Key> iterator() {
				return new Iterator<Key>() {
					private final Iterator<I> _it = it;					
					public boolean hasNext() { return _it.hasNext(); }
					public Key next() { return _it.next().getGoogleEntity().getKey(); }
					public void remove() { _it.remove(); }
				};
			}
		};
	}
}
