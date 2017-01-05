package im.connector.api.data;
import java.util.Date;
import io.unequal.reuse.data.Instance;
import io.unequal.reuse.data.Connection;


public class Invitation extends Instance<Invitations> {

	public Invitation() {
	}
	
	public Invitation(User from, User to) {
		this.set(entity().from, from);
		this.set(entity().to, to);
	}

	// Impl:
	public Invitations entity() { return Invitations.get(); }
	public String describe(Connection c) { return from(c).describe(c) + " > " + to(c).describe(c); }

	// Getters and setters:
	public User from(Connection c) { return get(entity().from, c); }
	public User to(Connection c) { return get(entity().to, c); }
	public Date timeAccepted() { return get(entity().timeAccepted); }
	public Invitation timeAccepted(Date value) { set(entity().timeAccepted, value); return this; }
}
