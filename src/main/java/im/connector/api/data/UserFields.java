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
import io.unequal.reuse.data.Property.Flag;
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
	private Query<UserField> _allFor;
	private Query<UserField> _allWithType;
	private Query<UserField> _validated;
	private Query<UserField> _withValue;

	private UserFields() {
		super("user_fields");
		user = property(User.class, "user", "user_id", Property.OnDelete.CASCADE, Flag.MANDATORY, Flag.READ_ONLY);
		type = property(FieldType.class, "type", "type", Flag.MANDATORY, Flag.READ_ONLY);
		value = property(String.class, "value", "value", Flag.MANDATORY, Flag.UNIQUE);
		label = property(String.class, "label", "label");
		validated = property(Boolean.class, "validated", "validated", Boolean.FALSE, Flag.MANDATORY);
	}

	public Property<?>[] naturalKey() {
		return new Property<?>[] { user, value };
	}

	public QueryResult<UserField> allFor(User userArg, Connection c) {
		Checker.nil(userArg);
		Checker.nil(c);
		if(_allFor == null) {
			_allFor = query()
				.where(user.equalTo())
				.where(active.equalTo(true));
		}
		return c.run(_allFor, userArg);
	}

	public QueryResult<UserField> allFor(User userArg, FieldType typeArg, Connection c) {
		Checker.nil(userArg);
		Checker.nil(typeArg);
		Checker.nil(c);
		if(_allWithType == null) {
			_allWithType = query()
				.where(user.equalTo())
				.where(type.equalTo())
				.where(active.equalTo(true));
		}
		return c.run(_allWithType, userArg, typeArg);
	}

	public QueryResult<UserField> validated(User userArg, FieldType typeArg, Connection c) {
		Checker.nil(userArg);
		Checker.nil(typeArg);
		Checker.nil(c);
		if(_validated == null) {
			_validated = query()
				.where(user.equalTo())
				.where(type.equalTo())
				.where(active.equalTo(true))
				.where(validated.equalTo(true));
		}
		return c.run(_validated, userArg, typeArg);
	}
	
	public UserField with(String valueArg, FieldType typeArg, Connection c) {
		Checker.empty(valueArg);
		Checker.nil(typeArg);
		Checker.nil(c);
		if(_withValue == null) {
			_withValue = query()
				.where(value.equalTo())
				.where(type.equalTo())
				.where(active.equalTo(true));
		}
		return c.run(_withValue, valueArg, typeArg).single();
	}

	/*
	public QueryResult<UserField> phoneNumber(String number, boolean val, Connection c) {
		Checker.empty(number);
		Checker.nil(c);
		if(_phoneNumber == null) {
			_phoneNumber = query()
				.where(value.equalTo())
				.where(validated.equalTo())
				.where(type.equalTo(FieldType.PHONE))
				.where(active.equalTo(true));
		}
		return c.run(_phoneNumber, number, val);
	}
	
	public QueryResult<UserField> listFor(User u, Connection c) {
		Checker.nil(u);
		Checker.nil(c);
		if(_listFor == null) {
			_listFor = query().where(user.equalTo()).where(active.equalTo(true));
		}
		return c.run(_listFor, u);
	}
	*/
}
