// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package im.connector.api.data;
import java.util.List;
import java.util.Iterator;
import io.unequal.reuse.data.ActiveInstance;
import io.unequal.reuse.data.Query;
import io.unequal.reuse.data.QueryResult;
import io.unequal.reuse.data.Connection;
import io.unequal.reuse.http.JsonObject;


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

	public QueryResult<Contact> findContacts(boolean includeDeleted) {
		Contacts cs = Contacts.get();
		Query<Contact> q = new Query<>(cs);
		q.addWhere(cs.owner.equalTo(this));
		q.addWhere(cs.active.equalTo(!includeDeleted));
		return q.run();
	}

	public QueryResult<Contact> findContacts() {
		return findContacts(false);
	}

	public QueryResult<Contact> findConnections() {
		Contacts cs = Contacts.get();
		Query<Contact> q = new Query<>(cs);
		q.addWhere(cs.owner.equalTo(this));
		q.addWhere(cs.active.equalTo(true));
		q.addWhere(cs.status.equalTo(Contacts.Status.CONNECTED));
		return q.run();
	}

	public QueryResult<Account> findAccounts() {
		return accounts.findWhere(accounts.user.equalTo(this));
	}


	public UserField findField(String value) {
		return _findField(value, true);
	}

	public UserField findDeletedField(String value) {
		return _findField(value, false);
	}
	
	// TODO should be in the endpoint. Remove from the testing Servlet
	public void connectWith(User anotherUser, Contact contact, List<UserField> shared) {
		// TODO insert invitation
		invitations.insert(new Invitation(this, anotherUser));
		contacts.update(contact.connection(anotherUser));
		for(UserField f : shared) {
			SharedField sf = new SharedField(f, this, anotherUser);
			sharedFields.insert(sf);
		}
	}
	
	// TODO this logic should be in the endpoint
	public void acceptInvitation(Invitation i, Contact contact, List<UserField> sharedBack) {
		User inviter = i.findFrom();
		i.setAcceptedOn();
		invitations.update(i);
		contacts.update(contact.connection(inviter));
		for(UserField f : sharedBack) {
			// Register a new shared field:
			SharedField sf = new SharedField(f, this, inviter);
			sharedFields.insert(sf);
			// TODO Remove the corresponding saved field, if any:
			// _replaceSharedField(contact, f);
			// _replaceSharedField(inviterContact, f);
		}
	}


	private UserField _findField(String value, boolean active) {
		UserFields fields = UserFields.get();
		return fields.findSingle(fields.user.equalTo(this), fields.value.equalTo(value), fields.active.equalTo(active));
	}

	/*
	private void _replaceSharedField(Contact contact, UserField f) {
		UserField existing = contact.findField(f.getValue());
		if(existing != null) {
			// TODO if label is different, register an update for the timeline
			userFields.update((UserField)existing.setActive(false));
		}
	}
	*/
}
