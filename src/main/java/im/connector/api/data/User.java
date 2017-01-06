// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package im.connector.api.data;
import java.util.Date;
import java.util.List;
import io.unequal.reuse.data.ActiveInstance;
import io.unequal.reuse.data.QueryResult;
import io.unequal.reuse.data.Connection;


public class User extends ActiveInstance<Users> implements Person<UserField> {

	public User() {
	}
	

	// Impl:
	public Users entity() { return Users.get(); }
	public String describe(Connection c) { return Common.description(this); }

	// Getters and setters:
	public String firstName() { return get(entity().firstName); }
	public User firstName(String firstName) { set(entity().firstName, firstName); return this; }
	public String lastName() { return get(entity().lastName); }
	public User lastName(String lastName) { set(entity().lastName, lastName); return this; }
	public String organization() { return get(entity().organization); }
	public User organization(String org) { set(entity().organization, org); return this; }
	public Users.Status status() { return get(entity().status); }
	public User status(Users.Status value) { set(entity().status, value); return this; }
		
	// Custom methods:
	public String fullName() {
		return Common.fullName(this);
	}

	public User copyFrom(Person<?> p) {
		Common.copy(p, this);
		return this;
	}
	
	public QueryResult<UserField> emails(Connection c) {
		return UserFields.get().allFor(this, FieldType.EMAIL, c);
	}

	public QueryResult<Account> accounts(Connection c) {
		return Accounts.get().listFor(this, c);
	}

	public QueryResult<Contact> contacts(Connection c) {
		return Contacts.get().listActiveFor(this, c);
	}

	// TODO should this logic be in the endpoint only?
	public Invitation connectWith(User anotherUser, Contact contact, List<UserField> shared, Connection c) {
		Contacts.get().update(contact.connection(anotherUser), c);
		for(UserField f : shared) {
			SharedFields.get().insert(new SharedField(f, this, anotherUser), c);
		}
		return Invitations.get().insert(new Invitation(this, anotherUser), c);
	}
	
	// TODO should this logic be in the endpoint only?
	public void accept(Invitation i, Contact contact, List<UserField> sharedBack, Connection c) {
		User inviter = i.from(c);
		i.timeAccepted(new Date());
		Invitations.get().update(i, c);
		Contacts.get().update(contact.connection(inviter), c);
		for(UserField f : sharedBack) {
			// Register a new shared field:
			SharedFields.get().insert(new SharedField(f, this, inviter), c);
			// TODO Remove the corresponding saved field, if any:
			// _replaceSharedField(contact, f);
			// _replaceSharedField(inviterContact, f);
		}
	}
}
