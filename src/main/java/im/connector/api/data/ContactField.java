package im.connector.api.data;
import io.unequal.reuse.data.ActiveInstance;
import io.unequal.reuse.data.Connection;


public class ContactField extends ActiveInstance<ContactFields> {

	public ContactField() {
	}

	public ContactField(Contact contact, FieldType type, String label, String value) {
		this.set(entity().contact, contact);
		type(type);
		label(label);
		value(value);
	}
	
	// Impl:
	public ContactFields entity() { return ContactFields.get(); }
	public String describe(Connection c) { return label() + ": " + value(); }

	// Getters and setters:
	public Contact contact(Connection c) { return get(entity().contact, c); }
	public FieldType type() { return get(entity().type); }
	public ContactField type(FieldType value) { set(entity().type, value); return this; }
	public String value() { return get(entity().value); }
	public ContactField value(String value) { set(entity().value, value); return this; }
	public String label() { return get(entity().label); }
	public ContactField label(String value) { set(entity().label, value); return this; }
	
	// Custom methods:
	public String calculatedLabel() {
		final String label = label();
		if(label != null) {
			return label;
		}
		return type().toString().toLowerCase();
	}	
}
