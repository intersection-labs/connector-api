package im.connector.api.data;
import io.unequal.reuse.data.Model;

public class ConnectorModel extends Model {

	public ConnectorModel() {
		add(Users.get());
		add(Sessions.get());
		add(Contacts.get());
		add(UserFields.get());
		add(Accounts.get());
	}
}
