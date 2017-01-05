package im.connector.test.data;
import static java.lang.System.*;
import org.junit.*;
import static org.junit.Assert.*;
import io.unequal.reuse.data.*;
import io.unequal.reuse.http.Env;
import io.unequal.reuse.util.Checker;
import im.connector.api.data.*;
import im.connector.api.rest.App;


public class Main {

	// TYPE:
	public static void main(String[] args) {
		Main tester = new Main();
		tester.test();
		out.println("done.");
	}

	// INSTANCE:
	private final Database _db;
	
	public Main() {
		// Load config:
		App.loadConfig();
		// Load database:
		boolean local = App.env() == Env.DEV;
		_db = new Database(App.databaseUrl(), local);
		_db.load(new ConnectorModel());
	}

	public void test() {
		// TODO delete existing
		
		User ripley = new User().firstName("Ellen").lastName("Ripley").organization("Weyland-Yutani Corporation");
		try {
			out.print("Testing mandatory contraints... ");
			ripley.status(null);
			fail();
		}
		catch(MandatoryConstraintException mce) {
			out.println(mce);
		}
		
		
		
		
		User lisa = _addUser("Lisa", "Smith", "lisa.connector@gmail.com", "lisa.smith@innoventive.com", false);
		
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
