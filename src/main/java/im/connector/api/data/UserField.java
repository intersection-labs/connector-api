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
		this.setValue(getEntity().user, user);
		setType(type);
		setLabel(label);
		setValue(value);
	}
	
	// Getters / setters:
	public User findUser(Connection c) { return getValue(getEntity().user, c); }
	public FieldType getType() { return getValue(getEntity().type); }
	public UserField setType(FieldType value) { setValue(getEntity().type, value); return this; }
	public String getValue() { return getValue(getEntity().value); }
	public UserField setValue(String value) { setValue(getEntity().value, value); return this; }
	public String getLabel() { return getValue(getEntity().label); }
	public UserField setLabel(String value) { setValue(getEntity().label, value); return this; }
	public Boolean getValidated() { return getValue(getEntity().validated); }
	public UserField setValidated(Boolean value) { setValue(getEntity().validated, value); return this; }
	public String describe() { return getLabel() + ": " + getValue(); }
	
	// Custom methods:
	public String getCalculatedLabel() {
		final String label = getLabel();
		if(label != null) {
			return label;
		}
		return getType().toString().toLowerCase();
	}	
}
