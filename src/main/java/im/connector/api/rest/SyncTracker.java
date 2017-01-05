package im.connector.api.rest;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import io.unequal.reuse.data.Connection;
import io.unequal.reuse.util.HashKey;
import im.connector.api.data.Account;
import im.connector.api.data.SyncEntry;
import im.connector.api.data.Contact;
import im.connector.api.data.ContactField;
import im.connector.api.data.SyncEntries;
import im.connector.api.data.SyncContactUpdates;
import im.connector.api.data.SyncContactUpdates.Operation;
import im.connector.api.data.SyncContactUpdates.Side;
import im.connector.api.data.SyncContactUpdates.Result;
import im.connector.api.data.SyncContactUpdate;
import im.connector.api.data.SyncFieldUpdates;
import im.connector.api.data.SyncFieldUpdates.Piece;
import im.connector.api.data.SyncFieldUpdate;


public class SyncTracker {

	private final Account _account;
	private final Connection _c;
	private Date _started;
	private int _updateCount;
	private SyncEntry _entry;
	private final Map<HashKey,SyncContactUpdate> _cache;

	public SyncTracker(Account account, Connection c) {
		_account = account;
		_c = c;
		_started = null;
		_updateCount = 0;
		_cache = new HashMap<>();
	}

	public void start() {
		_started = new Date();
	}

	public void complete(Date completed) {
		if(_entry != null) {
			_entry.completed(completed);
			_entry.updateCount(_updateCount);
			SyncEntries.get().update(_entry, _c);
		}
	}

	public SyncContactUpdate trackContactUpdate(Contact c, Side side, Operation op, Result result) {
		SyncEntry e = _getEntry();
		SyncContactUpdate update = new SyncContactUpdate(e, side, op, result, c);
		SyncContactUpdates.get().insert(update, _c);
		_updateCount++;
		// Cache the last update:
		if(op.equals(Operation.UPDATE)) {
			_cache.put(new HashKey(c.id(), side, result), update);
		}
		return update;
	}

	public void trackFieldUpdate(Contact c, Side side, Operation op, Result result, Piece p, ContactField f, String before, String after) {
		SyncFieldUpdate update = new SyncFieldUpdate(_getUpdateFor(c, side, result), op, p, f, before, after);
		SyncFieldUpdates.get().insert(update, _c);
	}
	
	private SyncEntry _getEntry() {
		if(_entry == null) {
			_entry = new SyncEntry(_account, _started);
			SyncEntries.get().insert(_entry, _c);
		}
		return _entry;
	}
	
	private SyncContactUpdate _getUpdateFor(Contact c, Side side, Result result) {
		SyncContactUpdate update = _cache.get(new HashKey(c.id(), side, result));
		if(update != null) {
			return update;
		}
		return trackContactUpdate(c, side, Operation.UPDATE, result);
	}
}
