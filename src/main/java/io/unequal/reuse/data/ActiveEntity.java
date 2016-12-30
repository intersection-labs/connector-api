package io.unequal.reuse.data;


public abstract class ActiveEntity<I extends ActiveInstance<?>> extends Entity<I> {

	public final Property<Boolean> active;

	protected ActiveEntity() {
		active = addProperty(Boolean.class, "active", Boolean.TRUE);
	}
}
