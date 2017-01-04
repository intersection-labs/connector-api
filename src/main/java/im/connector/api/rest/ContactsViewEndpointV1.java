// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package im.connector.api.rest;
import java.io.IOException;
import java.util.List;
import java.util.Iterator;
import io.unequal.reuse.data.Connection;
import io.unequal.reuse.http.JsonObject;
import io.unequal.reuse.http.Request;
import io.unequal.reuse.http.Response;
import im.connector.api.data.*;


public class ContactsViewEndpointV1 extends UserEndpoint {

	public void get(Request req, Session s, Response resp) throws IOException {
		final Long id = req.getLongParameter("id", true);
		Connection c = resp.connection();
		Contact contact = Contacts.get().find(id, c);
		if(contact == null) {
			resp.sendError(StatusCodes.RESOURCE_NOT_FOUND, null, "contact", "id");
			return;
		}
		User me = s.user(c);
		if(!me.equals(contact.owner(c))) {
			resp.sendError(StatusCodes.ACCESS_NOT_ALLOWED, null, "contact");
			return;
		}
		JsonObject jContact = new JsonObject();
		jContact.put("id", id);
		jContact.put("firstName", contact.firstName());
		jContact.put("lastName", contact.lastName());
		// Fields saved by me:
		List<JsonObject> jSaved = jContact.addChildListOf("saved", JsonObject.class);
		Iterator<ContactField> itSavedFields = ContactFields.get().allFor(contact, c).sortByAsc(ContactFields.get().type).iterate();
		while(itSavedFields.hasNext()) {
			ContactField f = itSavedFields.next();
			JsonObject jField = new JsonObject();
			jField.put("type", f.type().code());
			jField.put("label", f.calculatedLabel());
			jField.put("value", f.value());
			jSaved.add(jField);
		}
		// Fields shared with me:
		final Contacts.Status status = contact.status();
		final User user = contact.connection(c);
		// TODO we should not send shared fields when just invited
		if(status == Contacts.Status.CONNECTED || status == Contacts.Status.INVITATION_RECEIVED) {
			List<JsonObject> jConnected = jContact.addChildListOf("connected", JsonObject.class);
			Iterator<SharedField> itConnected = SharedFields.get().listFor(user, me, c).iterate();
			while(itConnected.hasNext()) {
				SharedField sf = itConnected.next();
				UserField f = sf.field(c);
				JsonObject jField = new JsonObject();
				jField.put("type", f.type().code());
				jField.put("label", f.calculatedLabel());
				jField.put("value", f.value());
				jConnected.add(jField);
			}
		}
		// Fields that I shared:
		if(status == Contacts.Status.CONNECTED || status == Contacts.Status.INVITATION_SENT) {
			List<JsonObject> jShared = jContact.addChildListOf("shared", JsonObject.class);
			Iterator<SharedField> itSharedFields = SharedFields.get().listFor(me, user, c).iterate();
			while(itSharedFields.hasNext()) {
				SharedField sf = itSharedFields.next();
				UserField f = sf.field(c);
				JsonObject jField = new JsonObject();
				jField.put("id", f.id().toString());
				jShared.add(jField);
			}
		}
		resp.sendOk(jContact);
	}
}
