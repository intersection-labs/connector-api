package im.connector.api.data;
import io.unequal.reuse.data.Constant;
import io.unequal.reuse.data.Entity;
import io.unequal.reuse.data.Property;
import io.unequal.reuse.data.Property.Flag;
import io.unequal.reuse.data.Property.OnDelete;


public class SyncContactUpdates extends Entity<SyncContactUpdate> {

	// TYPE:
	private final static class SingletonHolder {
		private final static SyncContactUpdates instance = new SyncContactUpdates();
	}

	public static SyncContactUpdates get() {
		return SingletonHolder.instance;
	}

	public static class Side extends Constant {
		public static Side ACCOUNT = new Side(100, "ACCOUNT");
		public static Side CONNECTOR = new Side(200, "CONNECTOR");
		
		private Side(int code, String description) {
			super(code, description);
		}		
	}

	public static class Operation extends Constant {
		public static Operation ADD = new Operation(100, "ADD");
		public static Operation REMOVE = new Operation(200, "REMOVE");
		public static Operation UPDATE = new Operation(300, "UPDATE");
		
		private Operation(int code, String description) {
			super(code, description);
		}		
	}

	public static class Result extends Constant {
		public static Result ACCEPT = new Result(100, "ACCEPT");
		public static Result REVERT = new Result(200, "REVERT");
		
		private Result(int code, String description) {
			super(code, description);
		}		
	}

	// INSTANCE:
	public final Property<SyncEntry> parent;
	public final Property<Side> side;
	public final Property<Operation> operation;
	public final Property<Result> result;
	public final Property<Contact> contact;
	
	
	public SyncContactUpdates() {
		super("sync_contact_updates");
		parent = property(SyncEntry.class, "parent", "parent_id", OnDelete.CASCADE, Flag.MANDATORY, Flag.READ_ONLY);
		side = property(Side.class, "to", "to", Flag.MANDATORY, Flag.READ_ONLY);
		operation = property(Operation.class, "operation", "operation", Flag.MANDATORY, Flag.READ_ONLY);
		result = property(Result.class, "result", "result", Flag.MANDATORY, Flag.READ_ONLY);
		contact = property(Contact.class, "contact", "contact_id", OnDelete.RESTRICT, Flag.MANDATORY, Flag.READ_ONLY);
	}

	public Property<?>[] naturalKey() {
		return new Property<?>[] { parent, contact, operation, side };
	}
}
