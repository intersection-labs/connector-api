// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package im.connector.api.data;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import io.unequal.reuse.data.Constant;
import io.unequal.reuse.data.Entity;
import io.unequal.reuse.data.Property;
import io.unequal.reuse.data.Property.Flag;
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
	public final Property<Timestamp> accessTokenTime;
	public final Property<Timestamp> lastSyncTime;
	// Queries:
	private Query<Account> _listFor;
	
	public Accounts() {
		super("accounts");
		type = property(Type.class, "type", "type", Flag.MANDATORY, Flag.READ_ONLY);
		user = property(User.class, "user", "user_id", OnDelete.CASCADE, Flag.MANDATORY, Flag.READ_ONLY);
		email = property(UserField.class, "email", "email_id", OnDelete.CASCADE, Flag.MANDATORY, Flag.READ_ONLY, Flag.UNIQUE);
		refreshToken = property(String.class, "refreshToken", "refresh_token");
		accessToken = property(String.class, "accessToken", "access_token");
		accessTokenTime = property(Timestamp.class, "accessTokenTime", "access_token_time");
		lastSyncTime = property(Timestamp.class, "lastSyncTime", "last_sync_time");
	}

	public Property<?>[] naturalKey() { return new Property<?>[] { user, email }; }
	
	public String googleAccessToken(Account account, Connection c) throws IOException {
		Checker.nil(account);
		Checker.nil(c);
		String accessToken = account.accessToken();
		if(accessToken == null) {
			// TODO handle this situation appropriately. This happens when the user has no access configured
			throw new IllegalStateException();
		}
		// Check access token:
		TimeValue now = new TimeValue();
		TimeValue tokenTime = new TimeValue(account.accessTokenTime());
		// Google access tokens expire after 1 hour (3600 seconds)
		if(now.as(Measure.SECONDS) - tokenTime.as(Measure.SECONDS) > 3500) {
			logger().info("access token no longer valid, refreshing");
			// Access token expired or about to expire, get another one:
			StringBuilder refreshUrl = new StringBuilder().append("https://www.googleapis.com/oauth2/v4/token")
				// Client ID from the API console registration:
				.append("?client_id=").append(App.googleClientId())
				// Client Secret from the API console registration:
				.append("&client_secret=").append(App.googleClientSecret())
				// Grant Type needs to be "refresh_token"
				.append("&grant_type=refresh_token")
				// The refresh token we received when authorizing access to the Contacts API:
				.append("&refresh_token=").append(account.refreshToken());
			// Retrieve the new access token:
			logger().log(info("request: {}", refreshUrl.toString()));			
			String body = HttpClient.post(refreshUrl.toString(), null);
			JsonObject json = JsonObject.parse(body);
			accessToken = json.getString("access_token");
			logger().log(info("retrived new access token: {}", accessToken));
			// Save new access token:
			account.accessToken(accessToken);
			account.accessTokenTime(Timestamp.from(Instant.now()));
			account.accessToken(accessToken);
			update(account, c);
		}
		return account.accessToken();
	}
	
	public QueryResult<Account> listFor(User u, Connection c) {
		Checker.nil(c);
		if(_listFor == null) {
			_listFor = query().where(user.equalTo());
		}
		return c.run(_listFor, u);
	}
}
