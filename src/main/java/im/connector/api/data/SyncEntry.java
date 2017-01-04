package im.connector.api.data;
import java.sql.Timestamp;
import io.unequal.reuse.data.Instance;
import io.unequal.reuse.data.Connection;


public class SyncEntry extends Instance<SyncEntries> {

	public SyncEntry() {}
	
	public SyncEntry(Account account, Timestamp started) {
		set(entity().account, account);
		set(entity().started, started);
	}

	// Impl:
	public SyncEntries entity() { return SyncEntries.get(); }
	public String describe(Connection c) { return account(c).describe(c)+" @ "+started(); }

	// Getters and setters:
	public Account account(Connection c) { return get(entity().account, c); }
	public Timestamp started() { return get(entity().started); }
	public Timestamp completed() { return get(entity().completed); }
	public SyncEntry completed(Timestamp value) { set(entity().completed, value); return this; }
	public Integer updateCount() { return get(entity().updateCount); }
	public SyncEntry updateCount(Integer value) { set(entity().updateCount, value); return this; }
}
