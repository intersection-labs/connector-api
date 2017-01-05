package im.connector.test.data;
import java.util.List;
import java.util.ArrayList;
import static java.lang.System.*;
import static org.junit.Assert.*;
import io.unequal.reuse.data.*;
import io.unequal.reuse.http.Env;
import io.unequal.reuse.util.Checker;
import static io.unequal.reuse.util.Util.*;
import im.connector.api.data.*;
import im.connector.api.rest.App;


public class UnitTester {

	// TYPE:
	public static void main(String[] args) {
		UnitTester tester = new UnitTester();
		tester.test();
		out.println("All done!");
	}

	// INSTANCE:
	private final Database _db;
	private final List<UserEntry> _users;
	
	public UnitTester() {
		// Load config:
		App.loadConfig();
		// Start database:
		boolean local = App.env() == Env.DEV;
		_db = new Database(App.databaseUrl(), local);
		// Users:
		_users = new ArrayList<>();
		_users.add(new UserEntry("Ellen", "Ripley", "Weyland-Yutani Corporation", "ripley@alien.mv", null));
		_users.add(new UserEntry("Tyler", "Durden", "Federated Motor Corporation", "tyler@fightclub.mv", null));
		_users.add(new UserEntry("Graham", "Waters", "LAPD", "graham@crash.mv", null));
		_users.add(new UserEntry("Alan", "Schaefer", "US Army", "alan@predator.mv", null));
		_users.add(new UserEntry("Sarah", "Connor", "Random Diner", "sarah@terminator.mv", null));
	}

	public void test() {
		final Connection c = _db.connect();
		// Loading:
		out.print("Attempting to use an entity before it is loaded... ");
		try {
			ContactFields.get().find(1L, c);
			fail();
		}
		catch(IllegalArgumentException iae) {
			out.println(iae.getMessage());
		}
		_db.load(new ConnectorModel());
		out.print("Attempting to load the entities again... ");
		try {
			_db.load(new ConnectorModel());
		}
		catch(IllegalArgumentException iae) {
			out.println(iae.getMessage());
		}
		// Delete existing data:
		for(UserEntry entry : _users) {
			out.println(x("Deleting {}... ", entry.personalEmail));
			UserField uf = UserFields.get().with(entry.personalEmail, FieldType.EMAIL, c);
			if(uf != null) {
				Users.get().delete(uf.user(c), c);
				// Ensure that delete constraints are applied:
				uf = UserFields.get().find(uf.id(), c);
				assertNull(uf);
			}
			out.println("Done.");
		}
		out.print("Testing mandatory contraints... ");
		try {
			_users.get(0).user().status(null);
			fail();
		}
		catch(MandatoryConstraintException mce) {
			out.println(mce);
		}
		// Insert users again:
		for(UserEntry entry : _users) {
			out.println(x("Inserting {}... ", entry.user.fullName()));
			entry.insert(c);
			out.println("Done.");
		}
		
		
		
		c.close();
		
		// TODO attempt to do ops after the connection is closed
		
		//User lisa = _addUser("Lisa", "Smith", "lisa.connector@gmail.com", "lisa.smith@innoventive.com", false);
		
		/*
		boolean recreate = false;
		User lisa = _addUser("Lisa", "Smith", "lisa.connector@gmail.com", "lisa.smith@innoventive.com", recreate);
		User carlos = _addUser("Carlos", "Silva", "hiCarlosSilva@gmail.com", "carlos@connector.im", recreate);
		_addUser("Jon", "Turnbull", "jon.turnbul@gmail.com", "jon@connector.im", recreate);
		_addUser("Michael", "Kramskoy", "selflogs@gmail.com", null, recreate);
		_addUser("Tanya", "Karagodova", "tati.karagodova@gmail.com", null, recreate);
		_addUser("Bogdan", "Geleta", "bogdan.geleta@gmail.com", null, recreate);
		_addUser("Filipa", "Fernandes", "pipa.fernandes@gmail.com", null, recreate);
		_addUser("Connor", "McFadden", "connormcfadden7@gmail.com", null, recreate);
		*/
	}

	private User _addUser(String firstName, String lastName, String pEmail, String wEmail, boolean recreate) {
		Checker.empty(firstName);
		Checker.empty(lastName);
		Checker.empty(pEmail);
		try(Connection c = _db.connect()) {
			User user = Users.get().with(pEmail, FieldType.EMAIL, c);
			if(recreate) {
				if(user != null) {
					//Users.get().delete(user);
					out.println("Deleted existing "+firstName);
				}
				user = null;
			}
			if(user == null) {
				out.print(recreate ? "Re-creating "+firstName+"... " : "Creating "+firstName+"... ");
				user = new User().firstName(firstName).lastName(lastName).status(Users.Status.REGISTERED);
				Users.get().insert(user, c);
				UserField pEmailField = new UserField(user, FieldType.EMAIL, "Personal email", pEmail);
				UserFields.get().insert(pEmailField, c);
				if(wEmail != null) {
					UserField wEmailField = new UserField(user, FieldType.EMAIL, "Work email", wEmail);
					UserFields.get().insert(wEmailField, c);
				}
				Account account = new Account(Accounts.Type.GOOGLE, user, pEmailField);
				Accounts.get().insert(account, c);
				out.println("done.");
			}
			return user;
		}
	}
}
