// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package im.connector.api.data;
import java.io.IOException;
import java.util.Date;
import io.unequal.reuse.data.Constant;
import io.unequal.reuse.data.Entity;
import io.unequal.reuse.data.Property;
import io.unequal.reuse.data.Property.Constraint;
import io.unequal.reuse.data.Property.OnDelete;
import io.unequal.reuse.data.Connection;
import io.unequal.reuse.data.Query;
import io.unequal.reuse.data.QueryResult;
import io.unequal.reuse.http.JsonObject;
import io.unequal.reuse.util.Checker;
import io.unequal.reuse.util.HttpClient;
import io.unequal.reuse.util.TimeValue;
import io.unequal.reuse.util.TimeValue.Measure;
import static io.unequal.reuse.util.Util.info;
import im.connector.api.rest.App;


public class Accounts extends Entity<Account> {


	// TYPE:
	private final static class SingletonHolder {
		private final static Accounts instance = new Accounts();
	}

	public static Accounts get() {
		return SingletonHolder.instance;
	}

	public static class Type extends Constant {
		public static Type GOOGLE = new Type(100, "Google");
		
		private Type(int code, String description) {
			super(code, description);
		}		
	}
	
	// INSTANCE:
	// Properties:
	public final Property<Type> type;
	public final Property<User> user;
	public final Property<UserField> email;
	public final Property<String> refreshToken;
	public final Property<String> accessToken;
	public final Property<Date> accessTokenDate;
	public final Property<Date> lastSyncDate;
	// Queries:
	private Query<Account> _listFor;
	
	public Accounts() {
		type = addProperty(Type.class, "type", Constraint.MANDATORY, Constraint.READ_ONLY);
		user = addProperty(User.class, "user", OnDelete.CASCADE, Constraint.MANDATORY, Constraint.READ_ONLY);
		email = addProperty(UserField.class, "email", OnDelete.CASCADE, Constraint.MANDATORY, Constraint.READ_ONLY, Constraint.UNIQUE);
		refreshToken = addProperty(String.class, "refreshToken");
		accessToken = addProperty(String.class, "accessToken");
		accessTokenDate = addProperty(Date.class, "accessTokenDate");
		lastSyncDate = addProperty(Date.class, "lastSyncDate");
	}

	public Property<?>[] getNaturalKeyProperties() { return new Property<?>[] { user, email }; }
	
	public String getAuthorizedAccessTokenFrom(Account account) throws IOException {
		Checker.checkNull(account);
		String accessToken = account.getAccessToken();
		if(accessToken == null) {
			// TODO handle this situation appropriately. This happens when the user has no access configured
			throw new IllegalStateException();
		}
		// Check access token:
		TimeValue now = new TimeValue();
		TimeValue tokenTime = new TimeValue(account.getAccessTokenDate());
		// Google access tokens expire after 1 hour (3600 seconds)
		if(now.getAs(Measure.SECONDS) - tokenTime.getAs(Measure.SECONDS) > 3500) {
			getLogger().info("access token no longer valid, refreshing");
			// Access token expired or about to expire, get another one:
			StringBuilder refreshUrl = new StringBuilder().append("https://www.googleapis.com/oauth2/v4/token")
				// Client ID from the API console registration:
				.append("?client_id=").append(App.googleClientId())
				// Client Secret from the API console registration:
				.append("&client_secret=").append(App.googleClientSecret())
				// Grant Type needs to be "refresh_token"
				.append("&grant_type=refresh_token")
				// The refresh token we received when authorizing access to the Contacts API:
				.append("&refresh_token=").append(account.getRefreshToken());
			// Retrieve the new access token:
			getLogger().log(info("request: {}", refreshUrl.toString()));			
			String body = HttpClient.post(refreshUrl.toString(), null);
			JsonObject json = JsonObject.parse(body);
			accessToken = json.getString("access_token");
			getLogger().log(info("retrived new access token: {}", accessToken));
			// Save new access token:
			account.setAccessToken(accessToken);
			account.setAccessTokenDate(new Date());
			account.setAccessToken(accessToken);
			update(account);
		}
		return account.getAccessToken();
	}
	
	public QueryResult<Account> listFor(User u, Connection c) {
		if(_listFor == null) {
			_listFor = query().where(user);
		}
		c.run(_listFor, u);
	}
}
