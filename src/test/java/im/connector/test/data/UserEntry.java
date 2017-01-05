package im.connector.test.data;
import io.unequal.reuse.data.*;
import io.unequal.reuse.util.Checker;
import io.unequal.reuse.util.IllegalUsageException;
import im.connector.api.data.*;


public class UserEntry {

	public final String firstName;
	public final String lastName;
	public final String org;
	public final String personalEmail;
	public final String workEmail;
	public final User user;

	public UserEntry(String firstName, String lastName, String org, String pEmail, String wEmail) {
		Checker.empty(firstName);
		Checker.empty(lastName);
		Checker.empty(pEmail);
		this.firstName = firstName;
		this.lastName = lastName;
		this.org = org;
		this.personalEmail = pEmail;
		this.workEmail = wEmail;
		this.user = user();
	}
	
	public User user() {
		User u = new User();
		u.firstName(firstName);
		u.lastName(lastName);
		u.organization(org);
		return u;
	}
	
	public UserField personalEmail() {
		return new UserField(user, FieldType.EMAIL, "Personal email", personalEmail);
	}

	public UserField workEmail() {
		if(workEmail == null) {
			return null;
		}
		return new UserField(user, FieldType.EMAIL, "Work email", workEmail);
	}
	
	public void insert(Connection c) {
		if(user.persisted()) {
			throw new IllegalUsageException("already inserted this user");
		}
		user.status(Users.Status.REGISTERED);
		Users.get().insert(user, c);
		UserField wEmail = workEmail();
		if(wEmail != null) {
			UserFields.get().insert(wEmail, c);
		}
		UserField pEmail = personalEmail();
		UserFields.get().insert(pEmail, c);
		Account account = new Account(Accounts.Type.GOOGLE, user, pEmail);
		Accounts.get().insert(account, c);
	}
}
