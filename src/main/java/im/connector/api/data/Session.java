// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package im.connector.api.data;
import java.sql.Timestamp;
import java.time.Instant;
import io.unequal.reuse.data.Instance;
import io.unequal.reuse.data.Connection;


public class Session extends Instance<Sessions> {

	public Session() {
	}
	
	public Session(String uuid) {
		this.setValue(getEntity().uuid, uuid);
	}

	// Impl:
	public Sessions getEntity() { return Sessions.get(); }
	public String describe() { return uuid().toString(); }

	// Getters and setters:
	public String uuid() { return getValue(getEntity().uuid); }
	public User user(Connection c) { return getValue(getEntity().user, c); }
	public Session user(User value) { setValue(getEntity().user, value); return this; }
	public Timestamp timeLastAccessed() { return getValue(getEntity().timeLastAccessed); }
	public Timestamp timeClosed() { return getValue(getEntity().timeClosed); }
	public String closeReason() { return getValue(getEntity().closeReason); }

	// Custom methods:
	public Session accessed() {
		setValue(getEntity().timeLastAccessed, Timestamp.from(Instant.now()));
		return this;
	}

	public Session close(String closeReason) {
		setValue(getEntity().closeReason, closeReason);
		Timestamp now = Timestamp.from(Instant.now());
		setValue(getEntity().timeClosed, now);
		setValue(getEntity().timeLastAccessed, now);
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
