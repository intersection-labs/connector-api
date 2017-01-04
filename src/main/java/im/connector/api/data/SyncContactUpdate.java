package im.connector.api.data;
import io.unequal.reuse.data.Instance;
import io.unequal.reuse.data.Connection;
import im.connector.api.data.SyncContactUpdates.Operation;
import im.connector.api.data.SyncContactUpdates.Result;
import im.connector.api.data.SyncContactUpdates.Side;


public class SyncContactUpdate extends Instance<SyncContactUpdates> {

	public SyncContactUpdate() {}
	
	public SyncContactUpdate(SyncEntry parent, Side side, Operation op, Result result, Contact c) {
		set(entity().parent, parent);
		set(entity().operation, op);
		set(entity().side, side);
		set(entity().result, result);
		set(entity().contact, c);
	}

	// Impl:
	public SyncContactUpdates entity() { return SyncContactUpdates.get(); }
	public String describe(Connection c) {
		StringBuilder sb = new StringBuilder();
		Result result = result();
		if(result.equals(Result.REVERT)) {
			sb.append(result.description());
			sb.append(": ");
		}
		sb.append(operation().description());
		sb.append(" ");
		sb.append(contact(c).describe(c));
		return sb.toString();
	}

	// Getters and setters:
	public SyncEntry parent(Connection c) { return get(entity().parent, c); }
	public Operation operation() { return get(entity().operation); }
	public Side side() { return get(entity().side); }
	public Result result() { return get(entity().result); }
	public Contact contact(Connection c) { return get(entity().contact, c); }
}
