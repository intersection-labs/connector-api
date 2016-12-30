package io.unequal.reuse.data;


public class Dependency {

	private final Entity<Instance<?>> _entity;
	private final Property<?> _foreignKey;

	// For Entity:
	Dependency(Entity<Instance<?>> entity, Property<?> foreignKey) {
		_entity = entity;
		_foreignKey = foreignKey;
	}
	
	public Entity<Instance<?>> getEntity() {
		return _entity;
	}

	public Property<?> getForeignKey() {
		return _foreignKey;
	}

	public String toString() {
		return "Dependency: "+_foreignKey.getFullName();
	}
	
	public QueryResult<Instance<?>> findInstancesRelatedTo(Instance<?> i) {
		return _entity.findWhere(new QueryArg(_foreignKey, i, QueryArg.Operator.EQUAL));
	}
}
