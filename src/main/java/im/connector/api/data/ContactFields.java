package im.connector.api.data;
import io.unequal.reuse.data.ActiveEntity;
import io.unequal.reuse.data.Connection;
import io.unequal.reuse.data.Query;
import io.unequal.reuse.data.QueryResult;
import io.unequal.reuse.util.Checker;
import io.unequal.reuse.data.Property;
import io.unequal.reuse.data.Property.Flag;


public class ContactFields extends ActiveEntity<ContactField> {

	// TYPE:
	private final static class SingletonHolder {
		private final static ContactFields instance = new ContactFields();
	}

	public static ContactFields get() {
		return SingletonHolder.instance;
	}
	
	// INSTANCE:
	// Properties:
	public final Property<Contact> contact;
	public final Property<FieldType> type;
	public final Property<String> value;
	public final Property<String> label;
	// Queries:
	private Query<ContactField> _allFor = null;
	private Query<ContactField> _allForWithType = null;
	private Query<ContactField> _deletedWithValue = null;	

	public ContactFields() {
		super("contact_fields");
		contact = property(Contact.class, "contact", "contact_id", Property.OnDelete.CASCADE, Flag.MANDATORY, Flag.READ_ONLY);
		type = property(FieldType.class, "type", "type", Flag.MANDATORY, Flag.READ_ONLY);
		value = property(String.class, "value", "value", Flag.MANDATORY);
		label = property(String.class, "label", "label");
	}

	public Property<?>[] naturalKey() {
		return new Property<?>[] { contact, value };
	}

	public QueryResult<ContactField> allFor(Contact cArg, Connection c) {
		Checker.nil(cArg);
		if(_allFor == null) {
			_allFor = query().where(contact.equalTo(), active.equalTo(true));
		}
		return c.run(_allFor, cArg);
	}

	public QueryResult<ContactField> allFor(Contact cArg, FieldType tArg, Connection c) {
		Checker.nil(cArg);
		Checker.nil(tArg);
		Checker.nil(c);
		if(_allForWithType == null) {
			_allForWithType = query().where(contact.equalTo(), type.equalTo(), active.equalTo(true));
		}
		return c.run(_allForWithType, cArg, tArg);
	}
	
	public QueryResult<ContactField> deletedWithValue(Contact cArg, String valueArg, Connection c) {
		Checker.nil(cArg);
		Checker.nil(c);
		Checker.empty(valueArg);
		if(_deletedWithValue == null) {
			_deletedWithValue = query().where(contact.equalTo(), value.equalTo(), active.equalTo(false));
		}
		return c.run(_deletedWithValue, cArg, valueArg);
	}
}
