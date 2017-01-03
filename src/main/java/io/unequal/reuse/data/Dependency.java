package io.unequal.reuse.data;


public class Dependency {

	private final Entity<Instance<?>> _entity;
	private final Property<?> _foreignKey;

	// For Entity:
	Dependency(Entity<Instance<?>> entity, Property<?> foreignKey) {
		_entity = entity;
		_foreignKey = foreignKey;
	}
	
	public Entity<Instance<?>> entity() {
		return _entity;
	}

	public Property<?> foreignKey() {
		return _foreignKey;
	}

	public String toString() {
		return "Dependency: "+_foreignKey.fullName();
	}
	
	//public QueryResult<Instance<?>> findInstancesRelatedTo(Instance<?> i) {
	//	return _entity.findWhere(new Predicate(_foreignKey, Predicate.Operator.EQUAL, i));
	//}
}
