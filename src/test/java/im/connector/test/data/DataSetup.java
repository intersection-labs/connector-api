package im.connector.test.data;
import static java.lang.System.*;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import im.connector.api.data.*;
import im.connector.api.rest.App;
import io.unequal.reuse.data.Connection;
import io.unequal.reuse.data.Database;
import io.unequal.reuse.http.Env;
import io.unequal.reuse.util.Checker;
import io.unequal.reuse.util.Config;


public class DataSetup {

	// TYPE:
	public static void main(String[] args) throws IOException {
		new DataSetup(true).setUp();
	}
	
	// INSTANCE:
	private final boolean _recreate; 
	private final Connection _c;

	public DataSetup(boolean recreate) throws IOException {
		Properties props = new Properties();
		props.load(getClass().getResourceAsStream("dev.properties"));
		out.println(props.getProperty("DATABASE_URL"));
		Config.source(props);
		_recreate = recreate;
		App.loadConfig();
		out.println(App.databaseUrl());
		final Database db = new Database(App.databaseUrl(), App.env() == Env.DEV);
		db.load(new ConnectorModel());
		_c = db.connect();
	}

	public void setUp() {
		// Initialize:
		// Add users:
		UserEntry lisa = _add(new UserEntry("Lisa", "Smith", "Innoventive", "lisa.connector@gmail.com", "lisa.smith@innoventive.com"));
		UserEntry carlos = _add(new UserEntry("Carlos", "Silva", "Connector", "hiCarlosSilva@gmail.com", "carlos@connector.im"));
		_add(new UserEntry("Jon", "Turnbull", "Accenture", "jon.turnbul@gmail.com", "jonathan.turnbull@accenture.com"));
		_add(new UserEntry("Michael", "Kramskoy", "Hard Theme", "selflogs@gmail.com", null));
		_add(new UserEntry("Tanya", "Karagodova", "Connector", "tati.karagodova@gmail.com", null));
		_add(new UserEntry("Filipa", "Fernandes", "ECTP", "pipa.fernandes@gmail.com", "filipa.fenandes@ectp.com"));
		_add(new UserEntry("Connor", "McFadden", "Jinn", "connormcfadden7@gmail.com", "conno@jinnapp.com"));
		// Connect two users:
		if(_recreate) {
			// Contacts on each side:
			Contact cCarlos = new Contact(lisa.user);
			cCarlos.copy(carlos.user);
			cCarlos.status(Contacts.Status.CONNECTED);
			Contacts.get().insert(cCarlos, _c);
			ContactFields.get().insert(new ContactField(cCarlos, FieldType.EMAIL, "An awkward label", "carlos@connector.im"), _c);
			Contact cLisa = new Contact(carlos.user);
			cLisa.copy(lisa.user);
			cLisa.status(Contacts.Status.CONNECTED);
			Contacts.get().insert(cLisa, _c);
			ContactFields.get().insert(new ContactField(cLisa, FieldType.EMAIL, "A strange label indeed", "lisa.connector@gmail.com"), _c);
			// Lisa connects with Carlos:
			UserField toShare = UserFields.get().with("lisa.connector@gmail.com", FieldType.EMAIL, _c);
			List<UserField> toShareList = new ArrayList<>();
			toShareList.add(toShare);
			Invitation i = lisa.user.connectWith(carlos.user, cCarlos, toShareList, _c);
			// Carlos accepts Lisa's invitation:
			toShare = UserFields.get().with("carlos@connector.im", FieldType.EMAIL, _c);
			toShareList.clear();
			toShareList.add(toShare);
			carlos.user.accept(i, cLisa, toShareList, _c);
			/*
			out.println("Lisa's connections:");
			for(Contact contact : lisa..findConnections().list()) {
				out.println(c.describe());
			}
			out.println("Carlos's connections:");
			for(Contact c : carlos.findConnections().list()) {
				out.println(c.describe());
			}
			*/
		}
		_c.close();
		out.println("All done.");
	}

	private UserEntry _add(UserEntry entry) {
		Checker.nil(entry);
		User user = Users.get().with(entry.personalEmail, FieldType.EMAIL, _c);
		if(_recreate) {
			if(user != null) {
				Users.get().delete(user, _c);
				out.println("Deleted existing "+user.firstName());
			}
			user = null;
		}
		if(user == null) {
			out.println(_recreate ? "Re-creating "+entry.firstName+"... " : "Creating "+entry.firstName+"... ");
			entry.insert(_c);
			out.println("Done.");
		}
		return entry;
	}
}
