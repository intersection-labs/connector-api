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
	
	public QueryResult<UserField> emails(Connection c) {
		return UserFields.get().allFor(this, FieldType.EMAIL, c);
	}

	public QueryResult<Account> accounts(Connection c) {
		return Accounts.get().listFor(this, c);
	}

	public QueryResult<Contact> contacts(Connection c) {
		return Contacts.get().listActiveFor(this, c);
	}
}
