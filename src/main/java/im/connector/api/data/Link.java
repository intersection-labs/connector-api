package im.connector.api.data;
import io.unequal.reuse.data.Instance;
import io.unequal.reuse.data.Connection;


public class Link extends Instance<Links> {

	public Link() {
	}
	
	public Link(String linkId, String value) {
		this.set(entity().linkId, linkId);
		this.set(entity().value, value);
	}
	
	// Impl:
	public Links entity() { return Links.get(); }
	public String describe(Connection c) { return linkId(); }

	// Getters and setters:
	public String linkId() { return get(entity().linkId); }
	public String value() { return get(entity().value); }
	public Link value(String value) { set(entity().value, value); return this; }	
}
