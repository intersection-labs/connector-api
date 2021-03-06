package io.unequal.reuse.data;
import io.unequal.reuse.util.Arrays;
import io.unequal.reuse.util.Checker;
import io.unequal.reuse.util.IntegrityException;
import static io.unequal.reuse.util.Util.*;



public class Property<T> {

	// TODO add IGNORE_CASE constraint
	public enum Flag { MANDATORY, READ_ONLY, UNIQUE, AUTO_GENERATED };
	public enum OnDelete { CASCADE, RESTRICT, SET_NULL };
	private final Entity<?> _parent;
	private final Class<T> _type;
	private final String _name;
	private final String _columnName;
	private final Generator<T> _default;
	private final boolean _mandatory;
	private final boolean _readOnly;
	private final boolean _unique;
	private final boolean _autoGenerated;
	private final boolean _foreignKey;
	private final OnDelete _onDelete;
	private TypeMapping<?,?> _mapping; 
	private Entity<?> _related;

	// For Entity:
	Property(Entity<?> parent, Class<T> cl, String name, String columnName, Generator<T> def, OnDelete onDelete, Flag ... constraints) {
		// Note: null and empty checks are carried out on Entity.addProperty 
		_parent = parent;
		_type = cl;
		_name = name;
		_columnName = columnName;
		_default = def;
		_mandatory = Arrays.contains(Flag.MANDATORY, constraints);
		_readOnly = Arrays.contains(Flag.READ_ONLY, constraints);
		_autoGenerated = Arrays.contains(Flag.AUTO_GENERATED, constraints);
		_unique = Arrays.contains(Flag.UNIQUE, constraints) || _autoGenerated;
		_foreignKey = Instance.class.isAssignableFrom(cl);
		_onDelete = onDelete;
		// Extra checks:
		if(_autoGenerated && _type != Long.class) {
			throw new IllegalArgumentException("auto-generated properties must be of data type Long");
		}
		if(_autoGenerated && def != null) {
			throw new IllegalArgumentException("auto-generated properties cannot have a default value");
		}
		if(_foreignKey) {
			if(_onDelete == null) {
				throw new IllegalArgumentException("foreign keys require an on-delete constraint");
			}
		}
		// Related entities are loaded on Entity.loadInto
		_related = null;
	}

	public Entity<?> entity() {
		return _parent;
	}

	public Class<T> type() {
		return _type;
	}

	public String name() {
		return _name;
	}

	public String columnName() {
		return _columnName;
	}
	
	public String fullName() {
		return entity().name()+'.'+name();
	}
	
	public T defaultValue() {
		return _default == null ? null : _default.generate();
	}
	
	public boolean mandatory() {
		return _mandatory;
	}

	public boolean readOnly() {
		return _readOnly;
	}
	
	public boolean unique() {
		return _unique;
	}
	
	public boolean autoGenerated() {
		return _autoGenerated;
	}
	
	public boolean foreignKey() {
		return _foreignKey;
	}
	
	public Entity<?> relatedEntity() {
		return _related;
	}

	public OnDelete onDeleteAction() {
		return _onDelete;
	}
	
	public Predicate equalTo(T value) {
		if(value == null) {
			_checkMandatory();
		}
		return new Predicate(this, Predicate.Operator.EQUAL, value==null ? Predicate.NULL : value);
	}

	public Predicate equalTo() {
		return new Predicate(this, Predicate.Operator.EQUAL, null);
	}

	public Predicate notEqualTo(T value) {
		if(value == null) {
			_checkMandatory();
		}
		return new Predicate(this, Predicate.Operator.NOT_EQUAL, value);
	}

	public Predicate notEqualTo() {
		return new Predicate(this, Predicate.Operator.NOT_EQUAL, null);
	}

	public Predicate greaterThan(T value) {
		Checker.nil(value);
		return new Predicate(this, Predicate.Operator.GREATER_THAN, value);
	}

	public Predicate lessThan(T value) {
		Checker.nil(value);
		return new Predicate(this, Predicate.Operator.LESS_THAN, value);
	}

	public String toString() {
		return fullName();
	}

	// For Entity:
	void relatedEntity(Entity<?> related) {
		if(related == null) {
			throw new IntegrityException();
		}
		_related = related;
	}
	
	private void _checkMandatory() {
		if(mandatory()) {
			throw new IllegalArgumentException(x("property '{}' cannot be NULL", name()));
		}
	}

	// For Self and Connection:
	TypeMapping<?,?> typeMapping() {
		if(_mapping == null) {
			_mapping = TypeMappings.get(_type);
		}
		return _mapping;
	}

	// For Instance:
	@SuppressWarnings({ "unchecked" })
	T wrap(Object value, Connection c) {
		if(value == null) {
			return null;
		}
		return (T)typeMapping().wrap(value, _type, c);
	}

	// For Connection:
	Object unwrap(Object value) {
		if(value == null) {
			_checkMandatory();
			return null;
		}
		if(value.getClass() != _type) {
			throw new IllegalArgumentException(x("expected {} parameter, found {}", _type.getSimpleName(), value.getClass().getSimpleName()));
		}
		return typeMapping().unwrap(value);
	}
}
