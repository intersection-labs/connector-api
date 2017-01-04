package im.connector.api.data;
import java.io.IOException;
import io.unequal.reuse.data.Instance;
import io.unequal.reuse.data.Connection;
import static io.unequal.reuse.util.Util.x;




public class ContactMapping extends Instance<ContactMappings> {

	public ContactMapping() {
	}
	
	public ContactMapping(Contact contact, Account account, String sourceId) {
		this.set(entity().contact, contact);
		this.set(entity().account, account);
		this.set(entity().sourceId, sourceId);
	}

	// Impl:
	public ContactMappings entity() { return ContactMappings.get(); }
	public String describe(Connection c) { return contact(c).describe(c) + " @ " + account(c).type(); }

	// Getters / setters:
	public Contact contact(Connection c) { return get(entity().contact, c); }
	public Account account(Connection c) { return get(entity().account, c); }
	public String sourceId() { return get(entity().sourceId); }
	public String photoUrl() { return get(entity().photoUrl); }
	public ContactMapping photoUrl(String value) { set(entity().photoUrl, value); return this; }
	
	// Custom methods:
	public String authorizedPhotoUrl(Connection c) throws IOException {
		String photoUrl = photoUrl();
		if(photoUrl == null) {
			return null;
		}
		return x("{}?access_token={}", photoUrl, Accounts.get().googleAccessToken(account(c), c));
	}
}
