// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package im.connector.api.data;
import io.unequal.reuse.data.Query;
import io.unequal.reuse.data.QueryResult;
import io.unequal.reuse.util.Checker;
import io.unequal.reuse.data.ActiveEntity;
import io.unequal.reuse.data.Property;
import io.unequal.reuse.data.Property.Constraint;
import io.unequal.reuse.data.Connection;


public class UserFields extends ActiveEntity<UserField> {

	// TYPE:
	private final static class SingletonHolder {
		private final static UserFields instance = new UserFields();
	}

	public static UserFields get() {
		return SingletonHolder.instance;
	}
	
	// INSTANCE:
	public final Property<User> user;
	public final Property<FieldType> type;
	public final Property<String> value;
	public final Property<String> label;
	public final Property<Boolean> validated;
	// Queries:
	private Query<UserField> _email;
	private Query<UserField> _phoneNumber;
	private Query<UserField> _listFor;

	private UserFields() {
		super("user_fields");
		user = addProperty(User.class, "user", "user_id", Property.OnDelete.CASCADE, Constraint.MANDATORY, Constraint.READ_ONLY);
		type = addProperty(FieldType.class, "type", "type", Constraint.MANDATORY, Constraint.READ_ONLY);
		value = addProperty(String.class, "value", "value", Constraint.MANDATORY, Constraint.UNIQUE);
		label = addProperty(String.class, "label", "label");
		validated = addProperty(Boolean.class, "validated", "validated", Boolean.FALSE, Constraint.MANDATORY);
	}

	public Property<?>[] getNaturalKeyProperties() {
		return new Property<?>[] { user, value };
	}
	
	public UserField email(String email, boolean val, Connection c) {
		Checker.checkEmpty(email);
		Checker.checkNull(c);
		if(_email == null) {
			_email = query()
				.where(value.isEqualTo())
				.where(validated.isEqualTo())
				.where(type.isEqualTo(FieldType.EMAIL))
				.where(active.isEqualTo(true));
		}
		return c.run(_email, email, val).single();
	}

	public QueryResult<UserField> phoneNumber(String number, boolean val, Connection c) {
		Checker.checkEmpty(number);
		Checker.checkNull(c);
		if(_phoneNumber == null) {
			_phoneNumber = query()
				.where(value.isEqualTo())
				.where(validated.isEqualTo())
				.where(type.isEqualTo(FieldType.PHONE))
				.where(active.isEqualTo(true));
		}
		return c.run(_phoneNumber, number, val);
	}
	
	public QueryResult<UserField> listFor(User u, Connection c) {
		Checker.checkNull(u);
		Checker.checkNull(c);
		if(_listFor == null) {
			_listFor = query().where(user.isEqualTo()).where(active.isEqualTo(true));
		}
		return c.run(_listFor, u);
	}
}
