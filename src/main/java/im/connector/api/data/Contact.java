// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package im.connector.api.data;
import java.io.IOException;
import java.util.Iterator;
import io.unequal.reuse.data.ActiveInstance;
import io.unequal.reuse.data.QueryResult;
import im.connector.api.data.Contacts.Status;


public class Contact extends ActiveInstance<Contacts> implements Person<ContactField> {

	public Contact(User owner, boolean isMe) {
		setValue(getEntity().owner, owner);
		setValue(getEntity().me, isMe);
		if(isMe) {
			copyFrom(owner);
		}
	}

	public Contact(User owner) {
		this(owner, false);
	}

	public Contact() {
	}
	
	public String describe() {
		return Common.getDescription(this);
	}

	// Getters / setters:
	public User findOwner() { return getValue(getEntity().owner); }
	public String getFirstName() { return getValue(getEntity().firstName); }
	public Contact setFirstName(String firstName) { setValue(getEntity().firstName, firstName); return this; }
	public String getLastName() { return getValue(getEntity().lastName); }
	public Contact setLastName(String lastName) { setValue(getEntity().lastName, lastName); return this; }
	public String getOrganization() { return getValue(getEntity().organization); }
	public Contact setOrganization(String org) { setValue(getEntity().organization, org); return this; }
	public Status getStatus() { return getValue(getEntity().status); }
	public Contact setStatus(Status value) { setValue(getEntity().status, value); return this; }
	public User findConnection() { return getValue(getEntity().connection); }
	public Contact setConnection(User user) { setValue(getEntity().connection, user); return this; }
	

	// Custom methods:
	public String getFullName() {
		return Common.getFullName(this);
	}

	public String getPhotoURL() throws IOException {
		Iterator<ContactMapping> it = findMappings().iterate();
		while(it.hasNext()) {
			ContactMapping cm = it.next();
			if(cm.getPhotoURL() != null) {
				return cm.getAuthorizedPhotoURL();
			}
		}
		return null;
	}

	public Contact copyFrom(Person<?> p) {
		Common.copy(p, this);
		return this;
	}

	public QueryResult<ContactField> findFields() {
		ContactFields fields = ContactFields.get();
		return fields.findWhere(fields.contact.isEqualTo(this), fields.active.isEqualTo(true));
	}

	public QueryResult<ContactField> findEmails() {
		ContactFields fields = ContactFields.get();
		return fields.findWhere(fields.contact.isEqualTo(this), fields.type.isEqualTo(FieldType.EMAIL), fields.active.isEqualTo(true));
	}

	public QueryResult<ContactField> findPhoneNumbers() {
		ContactFields fields = ContactFields.get();
		return fields.findWhere(fields.contact.isEqualTo(this), fields.type.isEqualTo(FieldType.PHONE), fields.active.isEqualTo(true));
	}

	public QueryResult<ContactField> findAddresses() {
		ContactFields fields = ContactFields.get();
		return fields.findWhere(fields.contact.isEqualTo(this), fields.type.isEqualTo(FieldType.ADDRESS), fields.active.isEqualTo(true));
	}

	public ContactField findField(String value) {
		return _findField(value, true);
	}

	public ContactField findDeletedField(String value) {
		return _findField(value, false);
	}
	
	public QueryResult<ContactMapping> findMappings() {
		ContactMappings cm = ContactMappings.get();
		return cm.findWhere(cm.contact.isEqualTo(this));
	}
	
	private ContactField _findField(String value, boolean active) {
		ContactFields fields = ContactFields.get();
		return fields.findSingle(fields.contact.isEqualTo(this), fields.value.isEqualTo(value), fields.active.isEqualTo(active));
	}
}
