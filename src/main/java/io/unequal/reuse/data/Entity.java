package io.unequal.reuse.data;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;
import java.lang.reflect.ParameterizedType;
import io.unequal.reuse.data.Property.Flag;
import io.unequal.reuse.data.Property.OnDelete;
import io.unequal.reuse.util.Checker;
import io.unequal.reuse.util.IllegalUsageException;
import io.unequal.reuse.util.ImmutableList;
import io.unequal.reuse.util.ImmutableMap;
import io.unequal.reuse.util.IntegrityException;
import io.unequal.reuse.util.Strings;
import static io.unequal.reuse.util.Util.*;


public abstract class Entity<I extends Instance<?>> {

	private final Logger _logger;
	// Data structure:
	public final Property<Long> id;
	public final Property<Timestamp> timeCreated;
	public final Property<Timestamp> timeUpdated;
	private final String _tableName;
	private final Class<I> _instanceType;
	private final Map<String,Property<?>> _propertyMap;
	private final List<Property<?>> _propertyList;
	private final Set<String> _columns;
	private final List<Dependency> _dependencies;
	private final Set<UniqueConstraint> _uConstraints;
	private Database _db;
	// Data management:
	private String _insertSql;
	private String _deleteSql;
	private Query<I> _findById;
	

	@SuppressWarnings("unchecked")
	protected Entity(String tableName) {
		Checker.empty(tableName);
		_logger = Logger.getLogger(getClass().getName());
		// Data structure:
		_tableName = tableName;
		_instanceType = ((Class<I>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
		_propertyMap = new HashMap<>();
		_propertyList = new ArrayList<>();
		_columns = new HashSet<>();
		_dependencies = new ArrayList<>();
		_uConstraints = new HashSet<>();
		// Common properties:
		id = property(Long.class, "id", "id", Flag.MANDATORY, Flag.AUTO_GENERATED, Flag.READ_ONLY);
		timeCreated = property(Timestamp.class, "timeCreated", "time_created", new Generators.Now(), Flag.MANDATORY, Flag.READ_ONLY);
		timeUpdated = property(Timestamp.class, "timeUpdated", "time_updated", new Generators.Now(), Flag.MANDATORY, Flag.READ_ONLY);		
		// Data management:
		_insertSql = null;
		_deleteSql = null;
		_findById = null;
	}

	protected Logger logger() {
		return _logger;
	}

	// Data structure methods:
	public abstract Property<?>[] naturalKey();
	
	public String name() {
		return getClass().getSimpleName();
	}

	public String instanceName() {
		return _instanceType.getSimpleName();
	}

	public Class<I> instanceClass() {
		return _instanceType;
	}
	
	public String tableName() {
		return _tableName;
	}
	
	public ImmutableMap<String,Property<?>> propertyMap() {
		return new ImmutableMap<String,Property<?>>(_propertyMap);
	}

	public List<Property<?>> propertyList() {
		return new ImmutableList<>(_propertyList);
	}

	public ImmutableList<Dependency> getDependencies() {
		return new ImmutableList<Dependency>(_dependencies);
	}
	
	protected <T> Property<T> property(Class<T> c, String name, String columnName, Flag ... constraints) {
		return _property(c, name, columnName, (Generators.Direct<T>)null, null, constraints);
	}

	protected <T> Property<T> property(Class<T> c, String name, String columnName, T def, Flag ... constraints) {
		return _property(c, name, columnName, new Generators.Direct<T>(def), null, constraints);
	}

	protected <T> Property<T> property(Class<T> c, String name, String columnName, Generator<T> def, Flag ... constraints) {
		return _property(c, name, columnName, def, null, constraints);
	}

	protected <T> Property<T> property(Class<T> c, String name, String columnName, OnDelete onDelete, Flag ...constraints) {
		return _property(c, name, columnName, null, onDelete, constraints);
	}
	
	private <T> Property<T> _property(Class<T> c, String name, String columnName, Generator<T> def, OnDelete onDelete, Flag ... constraints) {
		Checker.nil(c);
		Checker.empty(name);
		Checker.empty(columnName);
		Checker.hasNull(constraints);
		if(!TypeMappings.supported(c)) {
			throw new IllegalArgumentException(x("type '{}' is not supported", c.getSimpleName()));
		}
		if(_propertyMap.containsKey(name)) {
			throw new IllegalArgumentException(x("property named '{}' already exists", name));
		}
		if(_columns.contains(columnName)) {
			throw new IllegalArgumentException(x("column named '{}' already exists", columnName));
		}
		Property<T> prop = new Property<T>(this, c, name, columnName, def, onDelete, constraints);
		_propertyMap.put(name, prop);
		_propertyList.add(prop);
		if(prop.unique() && !prop.name().equals("id")) {
			_unique(prop);
		}
		return prop;
	}

	protected void unique(Property<?> ... props) {
		Checker.empty(props);
		Checker.hasNull(props);
		Checker.hasDuplicates(props);
		if(props.length == 1) {
			throw new IllegalArgumentException("unique constraints must have more than one property (use UNIQUE instead)");
		}
		for(Property<?> prop : props) {
			if(prop.unique()) {
				throw new IllegalArgumentException(x("property '{}' is already unique", prop.name()));
			}
		}
		_unique(props);
	}

	private void _unique(Property<?> ... props) {
		UniqueConstraint uc = new UniqueConstraint(query(), props);
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
		logger().log(info("loading entity '{}'", name()));
		if(_db != null) {
			throw new IllegalStateException(x("entity '{}' has already been loaded into another database", name()));
		}
		_db = db;
		// Prepare insert SQL:
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO ").append(tableName()).append("(");
		// Load all related entities and dependencies:
		int count = 0;
		for(Property<?> prop : _propertyList) {
			if(prop.foreignKey()) {
				// Get the foreign entity:
				Entity<?> foreignEntity = _db.entityForInstance(prop.type());
				// Store related entity:
				prop.relatedEntity(foreignEntity);
				// Record a delete dependency:
				foreignEntity._dependencies.add(new Dependency((Entity<Instance<?>>)this, prop));
			}
			if(!prop.autoGenerated()) {
				if(count > 0) {
					sb.append(",");
				}
				sb.append(prop.columnName());
				count++;
			}
		}
		sb.append(") VALUES (?");
		if(count > 1) {
			sb.append(Strings.repeat(",?", count-1));
		}
		sb.append(")");
		_insertSql = sb.toString();
		// Prepare delete SQL:
		_deleteSql = x("DELETE FROM {} WHERE id = ?", tableName());
		// Prepare find by id query:
		_findById = query().where(id.equalTo());
		// Add the natural key as a constraint:
		Property<?>[] key = naturalKey();
		if(key == null || key.length == 0) {
			return;
		}
		if(key.length == 1) {
			if(!key[0].unique()) {
				throw new IllegalUsageException(x("property '{}' cannot be a natural key because it is not unique", key[0].name()));
			}
			// We don't want to add a single unique property as a constraint:
			return;
		}
		for(Property<?> prop : key) {
			if(prop.unique()) {
				// No need to add the constraint, since one of the properties is already unique:
				return;
			}
		}
		_unique(key);
	}

	// Data management methods:	
	public void insert(I i, Connection c) {
		Checker.nil(i);
		Checker.nil(c);
		_checkLoadedInto(c);
		if(i.persisted()) {
			throw new IllegalArgumentException("entity has already been persisted");
		}
		Object[] args = new Object[_propertyList.size()-1];
		int[] sqlTypes = new int[_propertyList.size()-1];
		// Check properties:
		// Note: the following checks are already done on Instance.setValue:
		// Data type, format, read-only, auto-generated. Mandatory is also checked
		// on Instance.setValue, but we need to process omitted properties.
		Iterator<Property<?>> it = _propertyList.iterator();
		// Skip the primary key:
		it.next();
		for(int j=0; it.hasNext(); j++) {
			Property<?> prop = it.next();
			Object value = i.get(prop, c);
			// Automatically generate:
			if(prop.autoGenerated()) {
				// TODO add generators
				throw new IntegrityException();
			}
			// Default value:
			if(value == null) {
				value = prop.defaultValue();
				if(value != null) {
					i.update(prop, value, false);
				}
			}
			// Mandatory:
			if(prop.mandatory()) {
				if(value == null) {
					throw new MandatoryConstraintException(prop);
				}
			}
			// All checks ok:
			sqlTypes[j] = prop.typeMapping().sqlType();
			args[j] = prop.unwrap(value);
		}
		// Check unique constraints:
		_checkUniqueConstraintsFor(i, c);
		// Insert instance:
		logger().log(info("inseting {}", instanceName()));
		Long id = c.insert(_insertSql, sqlTypes, args);
		i.primaryKey(id);
		i.flush();
	}

	public boolean update(I i, Connection c) {
		Checker.nil(i);
		Checker.nil(c);
		_checkLoadedInto(c);
		_checkPersisted(i);
		if(!i.updated()) {
			return false;
		}
		logger().log(info("updating {} with id {}", instanceName(), i.id()));
		_checkUniqueConstraintsFor(i, c);
		i.update(timeUpdated, Timestamp.from(Instant.now()), false);
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE ").append(tableName()).append(" SET ");
		Iterator<Entry<Property<?>,Object>> it = i.updates().iterator();
		while(it.hasNext()) {
			Entry<Property<?>,Object> entry = it.next();
			sb.append(entry.getKey().columnName());
			sb.append("='").append(entry.getKey().unwrap(entry.getValue())).append("'");
			if(it.hasNext()) {
				sb.append(",");
			}
		}
		sb.append(" WHERE id=").append(i.id());		
		c.update(sb.toString());
		i.flush();
		return true;
	}
	
	public void delete(I i, Connection c) {
		Checker.nil(i);
		Checker.nil(c);
		_checkLoadedInto(c);
		_checkPersisted(i);
		logger().log(info("deleting {} with id {}", instanceName(), i.id()));
		// Process dependencies:
		for(Dependency d : _dependencies) {
			logger().log(info("found dependency with {}", d.entity().name()));
			Iterator<Instance<?>> it = d.instancesRelatedTo(i, c).iterate();
			while(it.hasNext()) {
				Instance<?> related = it.next();
				OnDelete onDeleteAction = d.foreignKey().onDeleteAction();
				if(onDeleteAction == OnDelete.CASCADE) {
					// TODO use a multiple delete statement for better performance
					d.entity().delete(related, c);
				}
				else if(onDeleteAction == OnDelete.RESTRICT) {
					// TODO onDeleteException?? RestictConstraintException?
					throw new RuntimeException(x("{} cannot be deleted because there is a related {}", related.entity().instanceName(), instanceName()));
				}
				else if(onDeleteAction == OnDelete.SET_NULL) {
					// TODO use a multiple update statement for better performance
					d.entity().update(related.set(d.foreignKey(), null), c);
				}
				else {
					throw new IntegrityException(onDeleteAction);
				}
			}
		}
		// Delete the instance:
		int[] types = new int[] {id.typeMapping().sqlType()};
		Object[] args = new Object[] {i.id()};
		c.delete(_deleteSql, types, args);
	}

	private void _checkPersisted(I i) {
		if(!i.persisted()) {
			throw new IllegalArgumentException("instance has not been persisted");
		}
	}

	private void _checkLoadedInto(Connection c) {
		if(_db != c.database()) {
			throw new IllegalArgumentException(x("entity '{}' is not available in this connection's database", name()));
		}
	}

	private void _checkUniqueConstraintsFor(I i, Connection c) {
		Iterator<UniqueConstraint> it = _uConstraints.iterator();
		while(it.hasNext()) {
			UniqueConstraint uc = it.next();
			boolean forInsert = i.id() == null;
			// Check if any of the constraint's properties have been updated:
			boolean updated = false;
			if(forInsert) {
				updated = true;
			}
			else {
				checkUpdates:
				for(Property<?> prop : uc.properties()) {
					if(i.updated(prop)) {
						updated = true;
						break checkUpdates;
					}
				}
			}
			if(updated) {
				// They have, we need to check if the constraint has been violated:
				Instance<?> existing = c.run(uc.query(), uc.args(i, c)).single();
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
	
	protected Query<I> query() {
		return new Query<I>(this);
	}
	
	public I find(Long id, Connection c) {
		Checker.nil(c);
		Checker.min(id, 1);
		return c.run(_findById, id).single();
	}
	
	
	// OLD

	/*
	public boolean save(I i, Connection c) {
		Checker.checkNull(i);
		Checker.checkNull(c);
		_checkLoadedInto(c);

		I existing = findSingle(i.getNaturalKeyAsArg());
		if(existing == null) {
			insert(i, c);
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

	}

	public void deleteWhere(Predicate ... params) {
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
	
	public I findSingle(Predicate ... args) {
		Checker.checkEmpty(args);
		Checker.checkNullElements(args);
		_checkLoaded();
		// Check if id is an argument:
		Predicate idArg = null;
		for(Predicate arg : args) {
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
			for(Predicate arg : args) {
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
	
	public QueryResult<I> findWhere(Predicate ... args) {
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
	*/
}
