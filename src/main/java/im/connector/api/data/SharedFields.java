package im.connector.api.data;
import io.unequal.reuse.data.Entity;
import io.unequal.reuse.data.Query;
import io.unequal.reuse.data.QueryResult;
import io.unequal.reuse.util.Checker;
import io.unequal.reuse.data.Connection;
import io.unequal.reuse.data.Property;
import io.unequal.reuse.data.Property.Flag;
import io.unequal.reuse.data.Property.OnDelete;


public class SharedFields extends Entity<SharedField> {

	// TYPE:
	private final static class SingletonHolder {
		private final static SharedFields instance = new SharedFields();
	}

	public static SharedFields get() {
		return SingletonHolder.instance;
	}
	
	// INSTANCE:
	// Properties:
	public final Property<UserField> field;
	public final Property<User> sharedBy;
	public final Property<User> sharedWith;
	// Queries:
	private Query<SharedField> _listFor = null;

	public SharedFields() {
		super("shared_fields");
		field = property(UserField.class, "field", "field_id", OnDelete.CASCADE, Flag.MANDATORY, Flag.READ_ONLY);
		sharedBy = property(User.class, "sharedBy", "shared_by_id", OnDelete.CASCADE, Flag.MANDATORY, Flag.READ_ONLY);
		sharedWith = property(User.class, "sharedWith", "shared_with_id", OnDelete.CASCADE, Flag.MANDATORY, Flag.READ_ONLY);
	}

	public Property<?>[] naturalKey() { return new Property<?>[] { field, sharedWith }; }
	
	public QueryResult<SharedField> listFor(User by, User with, Connection c) {
		Checker.nil(by);
		Checker.nil(with);
		Checker.nil(c);
		if(_listFor == null) {
			_listFor = query().where(sharedBy.equalTo()).where(sharedWith.equalTo());
		}
		return c.run(_listFor, by, with);
	}
}
