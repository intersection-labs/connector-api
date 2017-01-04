package im.connector.api.data;
import io.unequal.reuse.data.Constant;
import io.unequal.reuse.data.Entity;
import io.unequal.reuse.data.Property;
import io.unequal.reuse.data.Property.Flag;
import io.unequal.reuse.data.Property.OnDelete;
import im.connector.api.data.SyncContactUpdates.Operation;


public class SyncFieldUpdates extends Entity<SyncFieldUpdate> {

	// TYPE:
	private final static class SingletonHolder {
		private final static SyncFieldUpdates instance = new SyncFieldUpdates();
	}

	public static SyncFieldUpdates get() {
		return SingletonHolder.instance;
	}

	public static class Piece extends Constant {
		public static Piece FIELD = new Piece(100, "FIELD");
		public static Piece LABEL = new Piece(200, "LABEL");
		public static Piece NAME = new Piece(300, "NAME");
		public static Piece ORG = new Piece(400, "ORG");
		public static Piece PHOTO = new Piece(500, "PHOTO");
		public static Piece BIRTHDAY = new Piece(600, "BIRTHDAY");
		
		private Piece(int code, String description) {
			super(code, description);
		}		
	}
	
	// INSTANCE:
	public final Property<SyncContactUpdate> parent;
	public final Property<Operation> operation;
	public final Property<Piece> piece;
	public final Property<ContactField> field;
	public final Property<String> before;
	public final Property<String> after;
	
	
	public SyncFieldUpdates() {
		super("sync_field_updates");
		parent = property(SyncContactUpdate.class, "parent", "parent_id", OnDelete.CASCADE, Flag.MANDATORY, Flag.READ_ONLY);
		operation = property(Operation.class, "op", "operation", Flag.MANDATORY, Flag.READ_ONLY);
		piece = property(Piece.class, "piece", "piece", Flag.MANDATORY, Flag.READ_ONLY);
		field = property(ContactField.class, "field", "field_id", OnDelete.RESTRICT, Flag.READ_ONLY);
		before = property(String.class, "before", "before", Flag.READ_ONLY);
		after = property(String.class, "after", "after", Flag.READ_ONLY);
	}

	public Property<?>[] naturalKey() {
		return new Property<?>[] { parent, operation, piece, field };
	}
}
