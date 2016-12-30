package im.connector.api.data;
import io.unequal.reuse.data.Constant;
import io.unequal.reuse.data.Entity;
import io.unequal.reuse.data.Property;
import io.unequal.reuse.data.Property.Constraint;

import java.util.Iterator;
import java.util.List;

import io.unequal.reuse.data.Connection;
import io.unequal.reuse.http.JsonObject;
import io.unequal.reuse.util.Checker;



public class Users extends Entity<User> {

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
		firstName = addProperty(String.class, "firstName", Constraint.MANDATORY);
		lastName = addProperty(String.class, "lastName");
		organization = addProperty(String.class, "organization");
		status = addProperty(Status.class, "status", Constraint.MANDATORY);
	}

	public Property<?>[] getNaturalKeyProperties() { return new Property<?>[0]; }
	
	public JsonObject json(User user, Connection c) {
		JsonObject jUser = new JsonObject();
		jUser.put("firstName", user.getFirstName());
		jUser.put("lastName", user.getLastName());
		jUser.put("fullName", user.getFullName());
		List<JsonObject> jFields = jUser.addChildListOf("fields", JsonObject.class);
		Iterator<UserField> itFields = UserFields.get().listFor(user, c).iterate();
		while(itFields.hasNext()) {
			UserField f = itFields.next();
			JsonObject jField = new JsonObject();
			jField.put("id", f.getId());
			jField.put("type", f.getType());
			jField.put("label", f.getLabel());
			jField.put("value", f.getValue());
			jFields.add(jField);
		}
		List<JsonObject> jAccounts = jUser.addChildListOf("accounts", JsonObject.class);
		Iterator<Account> itAccounts = Accounts.get().listFor(user, c).iterate();
		while(itAccounts.hasNext()) {
			Account account = itAccounts.next();
			JsonObject jAccount = new JsonObject();
			jAccount.put("type", account.getType());
			jAccount.put("email", account.findGoogleEmail().getValue());
			jAccount.put("authorized", account.getAccessToken()==null ? Boolean.FALSE : Boolean.TRUE);
			jAccounts.add(jAccount);
		}
		return jUser;
	}
	
	public User byEmail(String email, Connection c) {
		Checker.checkEmpty(email);
		Checker.checkNull(c);
		UserField uf = UserFields.get().email(email, false, c);
		if(uf == null) {
			return null;
		}
		return uf.findUser(c);
	}
}
