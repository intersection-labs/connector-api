// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package im.connector.api.data;
import io.unequal.reuse.data.ActiveEntity;
import io.unequal.reuse.data.Constant;
import io.unequal.reuse.data.Property;
import io.unequal.reuse.data.Property.Flag;
import io.unequal.reuse.data.Property.OnDelete;
import io.unequal.reuse.data.Connection;
import io.unequal.reuse.data.Query;
import io.unequal.reuse.data.QueryResult;
import io.unequal.reuse.util.Checker;


public class Contacts extends ActiveEntity<Contact> {

	// TYPE:
	private final static int _IPP = 20;

	private final static class SingletonHolder {
		private final static Contacts instance = new Contacts();
	}

	public static Contacts get() {
		return SingletonHolder.instance;
	}
	
	public static class Status extends Constant {
		public static Status NOT_CONNECTED = new Status(100, "Not Connected");
		public static Status INVITATION_SENT = new Status(200, "Invitation Sent");
		public static Status INVITATION_RECEIVED = new Status(300, "Invitation Received");
		public static Status CONNECTED = new Status(400, "Connected");
		
		private Status(int code, String description) {
			super(code, description);
		}		
	}
	
	// INSTANCE:
	public final Property<User> owner;
	public final Property<Boolean> me;
	public final Property<String> firstName;
	public final Property<String> lastName;
	public final Property<String> organization;
	public final Property<User> connection;
	public final Property<Status> status;
	// TODO unique restriction: only one me=true for a particular user id
	// Queries:
	private Query<Contact> _pageActiveFor;
	private Query<Contact> _listActiveFor;
	private Query<Contact> _connection;

	private Contacts() {
		super("contacts");
		owner = property(User.class, "owner", "owner_id", OnDelete.CASCADE, Flag.MANDATORY, Flag.READ_ONLY);
		me = property(Boolean.class, "me", "me", Boolean.FALSE, Flag.MANDATORY, Flag.READ_ONLY);
		firstName = property(String.class, "firstName", "first_name");
		lastName = property(String.class, "lastName", "last_name");
		organization = property(String.class, "organization", "organization");
		connection = property(User.class, "connection", "connection_id", OnDelete.SET_NULL);
		status = property(Status.class, "status", "status", Status.NOT_CONNECTED, Flag.MANDATORY);
	}

	public Property<?>[] naturalKey() { return null; }
	
	// TODO change this to sort by contact id, and then sort in the client app
	public QueryResult<Contact> pageActiveFor(User user, int page, Connection c) {
		Checker.nil(user);
		Checker.min(page, 1);
		Checker.nil(c);
		if(_pageActiveFor == null) {
			_pageActiveFor = query()
				.where(owner.equalTo())
				.where(active.equalTo(true))
				.orderByAsc(firstName)
				.orderByAsc(lastName)
				.orderByAsc(organization)
				.limit(_IPP)
				.offset(null);
		}
		return c.run(_pageActiveFor, user, (page-1) * _IPP);
	}
	
	public QueryResult<Contact> listActiveFor(User user, Connection c) {
		Checker.nil(user);
		Checker.nil(c);
		if(_listActiveFor == null) {
			_listActiveFor = query().where(owner.equalTo()).where(active.equalTo(true));
		}
		return c.run(_listActiveFor, user);
	}

	public Contact connection(User a, User b, Connection c) {
		if(_connection == null) {
			_connection = query().where(owner.equalTo(), connection.equalTo(), active.equalTo(true));
		}
		return c.run(_connection, a, b).single();
	}
}
