// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package im.connector.api.rest;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import io.unequal.reuse.data.Connection;
import io.unequal.reuse.http.JsonObject;
import io.unequal.reuse.http.Request;
import io.unequal.reuse.http.Response;
import io.unequal.reuse.http.ParameterValidationException;
import io.unequal.reuse.util.IntegrityException;
import im.connector.api.data.*;



public class ContactsModifySharingEndpointV1 extends UserEndpoint {

	public void post(Request req, Session s, Response resp) throws Exception {
		// Fetch necessary parameters:
		JsonObject json = req.readDataAsJson(true);
		Long contactId = json.getLong("contact");
		List<Long> fields = json.getListOf("fields", Long.class);
		if(fields.isEmpty()) {
			throw new ParameterValidationException("fields");
		}
		List<UserField> toShare = new ArrayList<UserField>();
		Connection c = resp.connection();
		// TODO use an "in" query
		for(Long fieldId : fields) {
			UserField uf = UserFields.get().find(fieldId, c);
			if(uf == null) {
				resp.sendError(StatusCodes.RESOURCE_NOT_FOUND, null, "field", "id");
				return;
			}
			toShare.add(uf);
		}
		// Retrieve contact entry:
		User connection = Contacts.get().find(contactId, c).connection(c);
		if(connection == null) {
			// TODO some other status code
			throw new IntegrityException();
		}
		// Process removed fields:
		User me = s.user(c);
		Iterator<SharedField> itCurrent = SharedFields.get().listFor(me, connection, c).iterate();
		while(itCurrent.hasNext()) {
			SharedField sf = itCurrent.next();
			UserField uf = sf.field(c);
			if(!fields.contains(uf.id())) {
				SharedFields.get().delete(sf, c);
				// TODO need to replace with saved fields on the connection side (if connected)
			}
			else {
				fields.remove(uf.id());
			}
		}
		// Add remaining fields:
		Iterator<Long> itToAdd = fields.iterator();
		while(itToAdd.hasNext()) {
			SharedFields.get().insert(new SharedField(UserFields.get().find(itToAdd.next(), c), me, connection), c);
		}
		// Send response:
		resp.setDisableCache();
		resp.sendOk();
	}
}
