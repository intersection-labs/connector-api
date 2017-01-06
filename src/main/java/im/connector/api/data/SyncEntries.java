package im.connector.api.data;
import java.util.Date;
import io.unequal.reuse.data.Entity;
import io.unequal.reuse.data.Connection;
import io.unequal.reuse.data.Query;
import io.unequal.reuse.data.QueryResult;
import io.unequal.reuse.util.Checker;
import io.unequal.reuse.data.Property;
import io.unequal.reuse.data.Property.Flag;
import io.unequal.reuse.data.Property.OnDelete;


public class SyncEntries extends Entity<SyncEntry> {

	// TYPE:
	private final static int _IPP = 20;

	private final static class SingletonHolder {
		private final static SyncEntries instance = new SyncEntries();
	}

	public static SyncEntries get() {
		return SingletonHolder.instance;
	}

	// INSTANCE:
	// Properties:
	public final Property<Account> account;
	public final Property<Date> started;
	public final Property<Date> completed;
	public final Property<Integer> updateCount;
	// Queries:
	private Query<SyncEntry> _pageFor = null;
	
	public SyncEntries() {
		super("sync_entries");
		account = property(Account.class, "account", "account_id", OnDelete.CASCADE, Flag.MANDATORY, Flag.READ_ONLY);
		started = property(Date.class, "started", "time_started", Flag.MANDATORY, Flag.READ_ONLY);
		completed = property(Date.class, "completed", "time_completed");
		updateCount = property(Integer.class, "updateCount", "update_count");
	}

	public Property<?>[] naturalKey() {
		return new Property<?>[] { account, started };
	}
	
	public QueryResult<SyncEntry> pageFor(Account aArg, int page, Connection c) {
		Checker.nil(account);
		if(_pageFor == null) {
			_pageFor = query().where(account.equalTo()).where(completed.notEqualTo(null)).orderByDesc(completed).limit(_IPP).offset(null);
		}		
		return c.run(_pageFor, aArg, (page-1) * _IPP);
	}
}
