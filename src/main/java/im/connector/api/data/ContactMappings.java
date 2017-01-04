// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package im.connector.api.data;
import io.unequal.reuse.data.Entity;
import io.unequal.reuse.data.Property;
import io.unequal.reuse.data.Query;
import io.unequal.reuse.data.QueryResult;
import io.unequal.reuse.data.Connection;
import io.unequal.reuse.data.Property.Flag;
import io.unequal.reuse.data.Property.OnDelete;
import io.unequal.reuse.util.Checker;


public class ContactMappings extends Entity<ContactMapping> {

	// TYPE:
	private final static class SingletonHolder {
		private final static ContactMappings instance = new ContactMappings();
	}

	public static ContactMappings get() {
		return SingletonHolder.instance;
	}
	
	// INSTANCE:
	public final Property<Contact> contact;
	public final Property<Account> account;
	public final Property<String> sourceId;
	public final Property<String> photoUrl;
	// Queries:
	private Query<ContactMapping> _listForAccount = null;
	private Query<ContactMapping> _listForContact = null;

	public ContactMappings() {
		super("contact_mappings");
		contact = property(Contact.class, "contact", "contact_id", OnDelete.CASCADE, Flag.MANDATORY, Flag.READ_ONLY);
		account = property(Account.class, "account", "account_id", OnDelete.CASCADE, Flag.MANDATORY, Flag.READ_ONLY);
		sourceId = property(String.class, "sourceId", "source_id", Flag.MANDATORY, Flag.READ_ONLY);
		photoUrl = property(String.class, "photoURL", "photo_url");
	}

	public Property<?>[] naturalKey() { return new Property<?>[] { contact, account }; }
	
	public QueryResult<ContactMapping> listFor(Account aArg, Connection c) {
		Checker.nil(aArg);
		Checker.nil(c);
		if(_listForAccount == null) {
			_listForAccount = query().where(account.equalTo());
		}
		return c.run(_listForAccount, aArg);
	}

	public QueryResult<ContactMapping> listFor(Contact cArg, Connection c) {
		Checker.nil(cArg);
		Checker.nil(c);
		if(_listForContact == null) {
			_listForContact = query().where(contact.equalTo());
		}
		return c.run(_listForContact, cArg);
	}
}
