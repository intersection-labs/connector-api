// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package im.connector.api.data;
import io.unequal.reuse.data.ActiveInstance;
import io.unequal.reuse.data.Connection;


public class UserField extends ActiveInstance<UserFields> {

	public UserField() {
	}

	public UserField(User user, FieldType type, String label, String value) {
		this.set(entity().user, user);
		type(type);
		label(label);
		value(value);
	}

	// Impl:
	public UserFields entity() { return UserFields.get(); }
	public String describe(Connection c) { return label() + ": " + value(); }

	// Getters and setters:
	public User user(Connection c) { return get(entity().user, c); }
	public FieldType type() { return get(entity().type); }
	public UserField type(FieldType value) { set(entity().type, value); return this; }
	public String value() { return get(entity().value); }
	public UserField value(String value) { set(entity().value, value); return this; }
	public String label() { return get(entity().label); }
	public UserField label(String value) { set(entity().label, value); return this; }
	public Boolean validated() { return get(entity().validated); }
	public UserField validated(Boolean value) { set(entity().validated, value); return this; }
	
	// Custom methods:
	public String calculatedLabel() {
		final String label = label();
		if(label != null) {
			return label;
		}
		return type().toString().toLowerCase();
	}	
}
