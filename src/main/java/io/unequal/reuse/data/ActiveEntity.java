package io.unequal.reuse.data;


public abstract class ActiveEntity<I extends ActiveInstance<?>> extends Entity<I> {

	public final Property<Boolean> active;

	protected ActiveEntity(String tableName) {
		super(tableName);
		active = property(Boolean.class, "active", "active", Boolean.TRUE);
	}
}
