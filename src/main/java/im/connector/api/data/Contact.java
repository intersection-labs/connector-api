// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package im.connector.api.data;
import java.io.IOException;
import java.util.Iterator;
import io.unequal.reuse.data.ActiveInstance;
import io.unequal.reuse.data.QueryResult;
import io.unequal.reuse.data.Connection;
import im.connector.api.data.Contacts.Status;


public class Contact extends ActiveInstance<Contacts> implements Person<ContactField> {

	public Contact(User owner, boolean isMe) {
		set(entity().owner, owner);
		set(entity().me, isMe);
		if(isMe) {
			copy(owner);
		}
	}

	public Contact(User owner) {
		this(owner, false);
	}

	public Contact() {
	}

	// Impl:
	public Contacts entity() { return Contacts.get(); }
	public String describe(Connection c) { return Common.description(this); }

	// Getters and setters:
	public User owner(Connection c) { return get(entity().owner, c); }
	public String firstName() { return get(entity().firstName); }
	public Contact firstName(String firstName) { set(entity().firstName, firstName); return this; }
	public String lastName() { return get(entity().lastName); }
	public Contact lastName(String lastName) { set(entity().lastName, lastName); return this; }
	public String organization() { return get(entity().organization); }
	public Contact organization(String org) { set(entity().organization, org); return this; }
	public Status status() { return get(entity().status); }
	public Contact status(Status value) { set(entity().status, value); return this; }
	public User connection(Connection c) { return get(entity().connection, c); }
	public Contact connection(User user) { set(entity().connection, user); return this; }
	

	// Custom methods:
	public String fullName() {
		return Common.fullName(this);
	}

	public String photoUrl() throws IOException {
		Iterator<ContactMapping> it = findMappings().iterate();
		while(it.hasNext()) {
			ContactMapping cm = it.next();
			if(cm.photoUrl() != null) {
				return cm.getAuthorizedPhotoURL();
			}
		}
		return null;
	}

	public Contact copy(Person<?> p) {
		Common.copy(p, this);
		return this;
	}

	public QueryResult<ContactField> findFields() {
		ContactFields fields = ContactFields.get();
		return fields.findWhere(fields.contact.equalTo(this), fields.active.equalTo(true));
	}

	public QueryResult<ContactField> findEmails() {
		ContactFields fields = ContactFields.get();
		return fields.findWhere(fields.contact.equalTo(this), fields.type.equalTo(FieldType.EMAIL), fields.active.equalTo(true));
	}

	public QueryResult<ContactField> findPhoneNumbers() {
		ContactFields fields = ContactFields.get();
		return fields.findWhere(fields.contact.equalTo(this), fields.type.equalTo(FieldType.PHONE), fields.active.equalTo(true));
	}

	public QueryResult<ContactField> findAddresses() {
		ContactFields fields = ContactFields.get();
		return fields.findWhere(fields.contact.equalTo(this), fields.type.equalTo(FieldType.ADDRESS), fields.active.equalTo(true));
	}

	public ContactField findField(String value) {
		return _findField(value, true);
	}

	public ContactField findDeletedField(String value) {
		return _findField(value, false);
	}
	
	public QueryResult<ContactMapping> findMappings() {
		ContactMappings cm = ContactMappings.get();
		return cm.findWhere(cm.contact.equalTo(this));
	}
	
	private ContactField _findField(String value, boolean active) {
		ContactFields fields = ContactFields.get();
		return fields.findSingle(fields.contact.equalTo(this), fields.value.equalTo(value), fields.active.equalTo(active));
	}
}
