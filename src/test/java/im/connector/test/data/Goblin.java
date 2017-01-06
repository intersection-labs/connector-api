// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package im.connector.test.data;
import java.util.Date;
import io.unequal.reuse.data.ActiveInstance;
import io.unequal.reuse.data.Connection;


public class Goblin extends ActiveInstance<Goblins> {

	public Goblin() {
	}

	// Impl:
	public Goblins entity() { return Goblins.get(); }
	public String describe(Connection c) { return name(); }

	// Getters and setters:
	public Goblin id(Long value) { set(entity().id, value); return this; }
	public Goblin timeCreated(Date value) { set(entity().timeCreated, value); return this; }
	public Goblin timeUpdated(Date value) { set(entity().timeUpdated, value); return this; }
	public String name() { return get(entity().name); }
	public Goblin name(String value) { set(entity().name, value); return this; }
	public Goblins.Temperament temperament() { return get(entity().temperament); }
	public Goblin temperament(Goblins.Temperament value) { set(entity().temperament, value); return this; }
	public Boolean wonkyEyes() { return get(entity().wonkyEyes); }
	public Goblin wokkyEyes(Boolean value) { set(entity().wonkyEyes, value); return this; }
}
