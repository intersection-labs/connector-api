package io.unequal.reuse.data;


public class Dependency {

	private final Entity<Instance<?>> _entity;
	private final Property<?> _foreignKey;
	private final Query<Instance<?>> _instancesRelated;

	// For Entity:
	Dependency(Entity<Instance<?>> entity, Property<?> foreignKey) {
		_entity = entity;
		_foreignKey = foreignKey;
		_instancesRelated = _entity.query().where(_foreignKey.equalTo());
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
	
	public QueryResult<Instance<?>> instancesRelatedTo(Instance<?> i, Connection c) {
		return c.run(_instancesRelated, i.get(_foreignKey));
	}
}
