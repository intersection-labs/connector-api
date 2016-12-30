package im.connector.api.rest;
import java.util.List;
import java.util.Iterator;
import io.unequal.reuse.http.JsonObject;
import io.unequal.reuse.http.Request;
import io.unequal.reuse.http.Response;
import io.unequal.reuse.data.Connection;
import io.unequal.reuse.data.Query;
import io.unequal.reuse.data.QueryResult;
import im.connector.api.data.*;


public class ContactsListEndpointV1 extends UserEndpoint {

	public void get(Request req, Session s, Response resp) throws Exception {
		int page = req.getIntegerParameter("page");
		Connection c = resp.connection();
		QueryResult<Contact> result = Contacts.get().listFor(s.user(c), page, c);
		Iterator<Contact> it = result.iterate();
		JsonObject jContent = new JsonObject();
		List<JsonObject> jContacts = jContent.addChildListOf("contacts", JsonObject.class);
		while(it.hasNext()) {
			Contact contact = it.next();
			if(contact.getFullName() == null && contact.getOrganization() == null) {
				continue;
			}
			JsonObject jContact = new JsonObject();
			jContact.put("id", contact.getId());
			jContact.put("fullName", contact.getFullName());
			jContact.put("organization", contact.getOrganization());
			jContact.put("status", contact.getStatus().getCode());
			String photoURL = contact.getPhotoURL();
			if(photoURL != null) {
				jContact.put("photoURL", photoURL);
			}
			jContacts.add(jContact);
		}
		resp.sendOk(jContent);
	}
}
