package im.connector.test.data;
import io.unequal.reuse.data.Constant;
import io.unequal.reuse.data.ActiveEntity;
import io.unequal.reuse.data.Property;
import io.unequal.reuse.data.Property.Flag;



public class Goblins extends ActiveEntity<Goblin> {

	// TYPE:
	private final static class SingletonHolder {
		private final static Goblins instance = new Goblins();
	}

	public static Goblins get() {
		return SingletonHolder.instance;
	}
	
	public static class Temperament extends Constant {
		public static Temperament AGGRESSIVE = new Temperament(100, "Aggressive");
		public static Temperament HORRIBLE = new Temperament(200, "Horrible");
		public static Temperament GREEDY = new Temperament(300, "Greedy");
		public static Temperament MISCHIEVOUS = new Temperament(400, "Mischievous");
		
		private Temperament(int code, String description) {
			super(code, description);
		}		
	}

	// INSTANCE:
	public final Property<String> name;
	public final Property<Temperament> temperament;
	public final Property<Boolean> wonkyEyes;
	
	private Goblins() {
		super("goblins");
		name = property(String.class, "name", "name", Flag.MANDATORY, Flag.UNIQUE);
		temperament = property(Temperament.class, "temperament", "temperament", Flag.MANDATORY, Flag.READ_ONLY);
		wonkyEyes = property(Boolean.class, "wonkyEyes", "wonky_eyes", Flag.MANDATORY);
	}

	public Property<?>[] naturalKey() { return new Property<?>[] { name }; }
}
