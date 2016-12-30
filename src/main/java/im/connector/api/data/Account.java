// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package im.connector.api.data;
import java.util.Date;

import im.connector.api.data.Accounts.Type;
import io.unequal.reuse.data.Instance;


public class Account extends Instance<Accounts> {

	public Account(Type type, User user, UserField email) {
		this.setValue(getEntity().type, type);
		this.setValue(getEntity().user, user);
		this.setValue(getEntity().email, email);
	}

	public Account() {
	}
	
	public String describe() { return getType()+"/"+findGoogleEmail().getValue(); }

	// Getters / setters:
	public Type getType() { return getValue(getEntity().type); }
	public User findUser() { return getValue(getEntity().user); }
	public UserField findGoogleEmail() { return getValue(getEntity().email); }
	public String getAccessToken() { return getValue(getEntity().accessToken); }
	public Account setAccessToken(String value) { setValue(getEntity().accessToken, value); return this; }
	public Date getAccessTokenDate() { return getValue(getEntity().accessTokenDate); }
	public Account setAccessTokenDate(Date value) { setValue(getEntity().accessTokenDate, value); return this; }
	public String getRefreshToken() { return getValue(getEntity().refreshToken); }
	public Account setRefreshToken(String value) { setValue(getEntity().refreshToken, value); return this; }
	public Date getLastSyncDate() { return getValue(getEntity().lastSyncDate); }
	public Account setLastSyncDate() { setValue(getEntity().lastSyncDate, new Date()); return this; }
}
