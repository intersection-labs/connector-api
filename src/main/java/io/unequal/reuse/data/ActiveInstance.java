package io.unequal.reuse.data;


public abstract class ActiveInstance<E extends ActiveEntity<?>> extends Instance<E> {

	public Boolean active() { return get(entity().active); }
	public ActiveInstance<E> active(Boolean value) { set(entity().active, value); return this; }
}
