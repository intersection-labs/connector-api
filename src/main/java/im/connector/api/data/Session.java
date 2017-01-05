// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package im.connector.api.data;
import java.util.Date;
import io.unequal.reuse.data.Instance;
import io.unequal.reuse.data.Connection;


public class Session extends Instance<Sessions> {

	public Session() {
	}
	
	public Session(String uuid) {
		this.set(entity().uuid, uuid);
	}

	// Impl:
	public Sessions entity() { return Sessions.get(); }
	public String describe(Connection c) { return uuid().toString(); }

	// Getters and setters:
	public String uuid() { return get(entity().uuid); }
	public User user(Connection c) { return get(entity().user, c); }
	public Session user(User value) { set(entity().user, value); return this; }
	public Date timeLastAccessed() { return get(entity().timeLastAccessed); }
	public Date timeClosed() { return get(entity().timeClosed); }
	public String closeReason() { return get(entity().closeReason); }

	// Custom methods:
	public Session accessed() {
		set(entity().timeLastAccessed, new Date());
		return this;
	}

	public Session close(String closeReason) {
		set(entity().closeReason, closeReason);
		Date now = new Date();
		set(entity().timeClosed, now);
		set(entity().timeLastAccessed, now);
		return this;
	}
	
	public Session signOut() {
		return close("signed-out");
	}

	public boolean open() {
		return timeClosed() == null;
	}

	public boolean closed() {
		return timeClosed() != null;
	}
}
