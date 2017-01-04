// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package im.connector.api.data;
import java.sql.Timestamp;
import io.unequal.reuse.data.Instance;
import io.unequal.reuse.data.Connection;
import im.connector.api.data.Accounts.Type;


public class Account extends Instance<Accounts> {

	public Account(Type type, User user, UserField email) {
		this.set(entity().type, type);
		this.set(entity().user, user);
		this.set(entity().email, email);
	}

	public Account() {
	}

	// Impl:
	public Accounts entity() { return Accounts.get(); }
	public String describe(Connection c) { return type()+"/"+email(c).value(); }

	// Getters and setters:
	public Type type() { return get(entity().type); }
	public User user(Connection c) { return get(entity().user, c); }
	public UserField email(Connection c) { return get(entity().email, c); }
	public String accessToken() { return get(entity().accessToken); }
	public Account accessToken(String value) { set(entity().accessToken, value); return this; }
	public Timestamp accessTokenTime() { return get(entity().accessTokenTime); }
	public Account accessTokenTime(Timestamp value) { set(entity().accessTokenTime, value); return this; }
	public String refreshToken() { return get(entity().refreshToken); }
	public Account refreshToken(String value) { set(entity().refreshToken, value); return this; }
	public Timestamp lastSyncTime() { return get(entity().lastSyncTime); }
	public Account lastSyncTime(Timestamp value) { set(entity().lastSyncTime, value); return this; }
}
