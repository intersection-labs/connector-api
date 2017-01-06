package im.connector.test.data;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import static java.lang.System.*;
import static org.junit.Assert.*;
import io.unequal.reuse.data.*;
import io.unequal.reuse.http.Env;
import io.unequal.reuse.util.IllegalUsageException;
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
		catch(IllegalUsageException iae) {
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
		// Instance tests:
		Goblin goblin = new Goblin();
		assertFalse(goblin.persisted());
		out.print("Testing id... ");
		try {
			goblin.id(1L);
			fail();
		}
		catch(IllegalArgumentException iae) {
			out.println(iae.getMessage());
		}
		out.print("Testing timeCreated... ");
		try {
			goblin.timeCreated(new Date());
			fail();
		}
		catch(IllegalArgumentException iae) {
			out.println(iae.getMessage());
		}
		out.print("Testing timeUpdated... ");
		try {
			goblin.timeUpdated(new Date());
			fail();
		}
		catch(IllegalArgumentException iae) {
			out.println(iae.getMessage());
		}
		out.print("Testing mandatory contraints... ");
		try {
			goblin.temperament(null);
			fail();
		}
		catch(MandatoryConstraintException mce) {
			out.println(mce.getMessage());
		}
		out.print("Testing loading... ");
		try {
			Goblins.get().insert(goblin, c);
			fail();
		}
		catch(IllegalUsageException iue) {
			out.println(iue.getMessage());
		}		
		_db.load(new GoblinHorde());
		out.print("Testing update before insert... ");
		try {
			Goblins.get().update(goblin, c);
			fail();
		}
		catch(IllegalArgumentException iae) {
			out.println(iae.getMessage());
		}		
		out.print("Testing delete before insert... ");
		try {
			Goblins.get().delete(goblin, c);
			fail();
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
		// Insert users again:
		for(UserEntry entry : _users) {
			assertFalse(entry.user.persisted());
			out.println(x("Inserting {}... ", entry.user.fullName()));
			entry.insert(c);
			assertTrue(entry.user.persisted());
			out.println("Done.");
		}
		// Close connection:
		c.close();
		out.print("Testing connection closure on insert... ");
		try {
			Users.get().insert(new User(), c);
			fail();
		}
		catch(IllegalUsageException iue) {
			out.println(iue.getMessage());
		}
		out.print("Testing connection closure on update... ");
		try {
			Users.get().update(_users.get(0).user, c);
			fail();
		}
		catch(IllegalUsageException iue) {
			out.println(iue.getMessage());
		}
		out.print("Testing connection closure on delete... ");
		try {
			Users.get().delete(new User(), c);
			fail();
		}
		catch(IllegalUsageException iue) {
			out.println(iue.getMessage());
		}
	}
}
