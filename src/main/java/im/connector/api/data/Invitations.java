package im.connector.api.data;
import java.sql.Timestamp;
import io.unequal.reuse.data.Entity;
import io.unequal.reuse.data.Property;
import io.unequal.reuse.data.Property.Flag;
import io.unequal.reuse.data.Property.OnDelete;


public class Invitations extends Entity<Invitation> {

	// TYPE:
	private final static class SingletonHolder {
		private final static Invitations instance = new Invitations();
	}

	public static Invitations get() {
		return SingletonHolder.instance;
	}
	
	// INSTANCE:
	public final Property<User> from;
	public final Property<User> to;
	public final Property<Timestamp> timeAccepted;

	public Invitations() {
		super("invitations");
		from = property(User.class, "from", "from_id", OnDelete.CASCADE, Flag.MANDATORY, Flag.READ_ONLY);
		to = property(User.class, "to", "to_id", OnDelete.CASCADE, Flag.MANDATORY, Flag.READ_ONLY);
		timeAccepted = property(Timestamp.class, "timeAccepted", "time_accepted");
	}

	public Property<?>[] naturalKey() { return new Property<?>[] { from, to }; }
}
