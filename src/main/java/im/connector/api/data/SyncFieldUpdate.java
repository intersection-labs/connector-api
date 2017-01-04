package im.connector.api.data;
import io.unequal.reuse.data.Instance;
import io.unequal.reuse.data.Connection;
import im.connector.api.data.SyncFieldUpdates.Piece;
import im.connector.api.data.SyncContactUpdates.Operation;


public class SyncFieldUpdate extends Instance<SyncFieldUpdates> {

	public SyncFieldUpdate() {}
	
	public SyncFieldUpdate(SyncContactUpdate parent, Operation op, Piece p, ContactField f, String before, String after) {
		set(entity().parent, parent);
		set(entity().operation, op);
		set(entity().piece, p);
		set(entity().field, f);
		set(entity().before, before);
		set(entity().after, after);
	}

	// Impl:
	public SyncFieldUpdates entity() { return SyncFieldUpdates.get(); }
	public String describe(Connection c) {
		StringBuilder sb = new StringBuilder();
		sb.append(parent(c).contact(c).describe(c));
		sb.append(": ");
		sb.append(operation().description());
		sb.append(" ");
		sb.append(piece().description());
		return sb.toString();
	}

	// Getters and setters:
	public SyncContactUpdate parent(Connection c) { return get(entity().parent, c); }
	public Operation operation() { return get(entity().operation); }
	public Piece piece() { return get(entity().piece); }
	public ContactField field(Connection c) { return get(entity().field, c); }
	public String before() { return get(entity().before); }
	public String after() { return get(entity().after); }
}
