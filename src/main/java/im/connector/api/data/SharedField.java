package im.connector.api.data;
import io.unequal.reuse.data.Instance;
import io.unequal.reuse.data.Connection;


public class SharedField extends Instance<SharedFields> {

	public SharedField() {
	}
	
	public SharedField(UserField field, User sharedBy, User sharedWith) {
		set(entity().field, field);
		set(entity().sharedBy, sharedBy);
		set(entity().sharedWith, sharedWith);
	}

	// Impl:
	public SharedFields entity() { return SharedFields.get(); }
	public String describe(Connection c) { return field(c).describe(c) + " shared by " + sharedBy(c).describe(c) + " with " + sharedWith(c).describe(c); }

	// Getters and setters:
	public UserField field(Connection c) { return get(entity().field, c); }
	public User sharedBy(Connection c) { return get(entity().sharedBy, c); }
	public User sharedWith(Connection c) { return get(entity().sharedWith, c); }
}
