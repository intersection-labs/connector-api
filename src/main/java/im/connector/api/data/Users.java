package im.connector.api.data;
import java.util.List;
import java.util.Iterator;
import io.unequal.reuse.data.Constant;
import io.unequal.reuse.data.ActiveEntity;
import io.unequal.reuse.data.Property;
import io.unequal.reuse.data.Property.Constraint;
import io.unequal.reuse.data.Connection;
import io.unequal.reuse.http.JsonObject;
import io.unequal.reuse.util.Checker;



public class Users extends ActiveEntity<User> {

	// TYPE:
	private final static class SingletonHolder {
		private final static Users instance = new Users();
	}

	public static Users get() {
		return SingletonHolder.instance;
	}
	
	public static class Status extends Constant {
		public static Status INVITED = new Status(100, "Invited");
		public static Status VIEWED = new Status(200, "Viewed invitation");
		public static Status REGISTERED = new Status(300, "Registered");
		
		private Status(int code, String description) {
			super(code, description);
		}		
	}

	// INSTANCE:
	public final Property<String> firstName;
	public final Property<String> lastName;
	public final Property<String> organization;
	public final Property<Status> status;
	
	private Users() {
		super("users");
		firstName = property(String.class, "firstName", "first_name");
		lastName = property(String.class, "lastName", "last_name");
		organization = property(String.class, "organization", "organization");
		status = property(Status.class, "status", "status", Constraint.MANDATORY);
	}

	public Property<?>[] naturalKey() { return new Property<?>[0]; }
	
	public JsonObject json(User user, Connection c) {
		JsonObject jUser = new JsonObject();
		jUser.put("firstName", user.firstName());
		jUser.put("lastName", user.lastName());
		jUser.put("fullName", user.fullName());
		List<JsonObject> jFields = jUser.addChildListOf("fields", JsonObject.class);
		Iterator<UserField> itFields = UserFields.get().listFor(user, c).iterate();
		while(itFields.hasNext()) {
			UserField f = itFields.next();
			JsonObject jField = new JsonObject();
			jField.put("id", f.id());
			jField.put("type", f.type());
			jField.put("label", f.label());
			jField.put("value", f.value());
			jFields.add(jField);
		}
		List<JsonObject> jAccounts = jUser.addChildListOf("accounts", JsonObject.class);
		Iterator<Account> itAccounts = Accounts.get().listFor(user, c).iterate();
		while(itAccounts.hasNext()) {
			Account account = itAccounts.next();
			JsonObject jAccount = new JsonObject();
			jAccount.put("type", account.type());
			jAccount.put("email", account.googleEmail().value());
			jAccount.put("authorized", account.accessToken()==null ? Boolean.FALSE : Boolean.TRUE);
			jAccounts.add(jAccount);
		}
		return jUser;
	}
	
	public User withEmail(String email, Connection c) {
		Checker.empty(email);
		Checker.nil(c);
		UserField uf = UserFields.get().email(email, false, c);
		if(uf == null) {
			return null;
		}
		return uf.user(c);
	}
}
