package im.connector.api.data;
import io.unequal.reuse.data.Model;

public class ConnectorModel extends Model {

	public ConnectorModel() {
		add(Links.get());
		add(Users.get());
		add(UserFields.get());
		add(Accounts.get());
		add(Sessions.get());
		add(Contacts.get());
		add(ContactFields.get());
		add(ContactMappings.get());
		add(SharedFields.get());
		add(Invitations.get());
		add(SyncEntries.get());
		add(SyncContactUpdates.get());
		add(SyncFieldUpdates.get());
	}
}
