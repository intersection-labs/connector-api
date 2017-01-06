package im.connector.api.rest;
import java.util.List;
import java.util.Iterator;
import io.unequal.reuse.http.JsonObject;
import io.unequal.reuse.http.Request;
import io.unequal.reuse.http.Response;
import io.unequal.reuse.data.Connection;
import im.connector.api.data.*;


public class ContactsListEndpointV1 extends UserEndpoint {

	public void get(Request req, Session s, Response resp) throws Exception {
		int page = req.getIntegerParameter("page", true);
		Connection c = resp.connection();
		Iterator<Contact> it = Contacts.get().pageActiveFor(s.user(c), page, c).iterate();
		JsonObject jContent = new JsonObject();
		List<JsonObject> jContacts = jContent.addChildListOf("contacts", JsonObject.class);
		while(it.hasNext()) {
			Contact contact = it.next();
			if(contact.fullName() == null && contact.organization() == null) {
				continue;
			}
			JsonObject jContact = new JsonObject();
			jContact.put("id", contact.id());
			jContact.put("fullName", contact.fullName());
			jContact.put("organization", contact.organization());
			jContact.put("status", contact.status().code());
			String photoURL = contact.authorizedPhotoUrl(c);
			if(photoURL != null) {
				jContact.put("photoURL", photoURL);
			}
			jContacts.add(jContact);
		}
		resp.sendOk(jContent);
	}
}
