// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package im.connector.api.data;
import java.util.List;
import java.util.Iterator;
import io.unequal.reuse.data.Instance;
import io.unequal.reuse.data.Query;
import io.unequal.reuse.data.QueryResult;
import io.unequal.reuse.http.JsonObject;


public class User extends Instance<Users> implements Person<UserField> {

	public User() {
	}
	
	public String describe() {
		return Common.getDescription(this);
	}

	// Getters / setters:
	public String getFirstName() { return getValue(getEntity().firstName); }
	public User setFirstName(String firstName) { setValue(getEntity().firstName, firstName); return this; }
	public String getLastName() { return getValue(getEntity().lastName); }
	public User setLastName(String lastName) { setValue(getEntity().lastName, lastName); return this; }
	public String getOrganization() { return getValue(getEntity().organization); }
	public User setOrganization(String org) { setValue(getEntity().organization, org); return this; }
	public Users.Status getStatus() { return getValue(getEntity().status); }
	public User setStatus(Users.Status value) { setValue(getEntity().status, value); return this; }
		
	// Custom methods:
	public String getFullName() {
		return Common.getFullName(this);
	}

	public User copyFrom(Person<?> p) {
		Common.copy(p, this);
		return this;
	}

	public QueryResult<Contact> findContacts(boolean includeDeleted) {
		Contacts cs = Contacts.get();
		Query<Contact> q = new Query<>(cs);
		q.addWhere(cs.owner.isEqualTo(this));
		q.addWhere(cs.active.isEqualTo(!includeDeleted));
		return q.run();
	}

	public QueryResult<Contact> findContacts() {
		return findContacts(false);
	}

	public QueryResult<Contact> findConnections() {
		Contacts cs = Contacts.get();
		Query<Contact> q = new Query<>(cs);
		q.addWhere(cs.owner.isEqualTo(this));
		q.addWhere(cs.active.isEqualTo(true));
		q.addWhere(cs.status.isEqualTo(Contacts.Status.CONNECTED));
		return q.run();
	}

	public QueryResult<Account> findAccounts() {
		return accounts.findWhere(accounts.user.isEqualTo(this));
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
		contacts.update(contact.setConnection(anotherUser));
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
		contacts.update(contact.setConnection(inviter));
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
		return fields.findSingle(fields.user.isEqualTo(this), fields.value.isEqualTo(value), fields.active.isEqualTo(active));
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
