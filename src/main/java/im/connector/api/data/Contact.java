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

	public String authorizedPhotoUrl(Connection c) throws IOException {
		Iterator<ContactMapping> it = mappings(c).iterate();
		while(it.hasNext()) {
			ContactMapping cm = it.next();
			if(cm.photoUrl() != null) {
				return cm.authorizedPhotoUrl(c);
			}
		}
		return null;
	}

	public Contact copy(Person<?> p) {
		Common.copy(p, this);
		return this;
	}
	
	public QueryResult<ContactField> emails(Connection c) {
		return ContactFields.get().allFor(this, FieldType.EMAIL, c);
	}

	public QueryResult<ContactField> phoneNumbers(Connection c) {
		return ContactFields.get().allFor(this, FieldType.PHONE, c);
	}

	public QueryResult<ContactField> addresses(Connection c) {
		return ContactFields.get().allFor(this, FieldType.ADDRESS, c);
	}

	public QueryResult<ContactMapping> mappings(Connection c) {
		return ContactMappings.get().listFor(this, c);
	}
}
