// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package im.connector.api.rest;
import java.io.IOException;
import java.util.List;
import java.util.Iterator;
import io.unequal.reuse.data.Connection;
import io.unequal.reuse.http.Request;
import io.unequal.reuse.http.Response;
import io.unequal.reuse.http.JsonObject;
import im.connector.api.data.*;


public class SyncListEndpointV1 extends UserEndpoint {

	// INSTANCE:
	public void get(Request req, Session s, Response resp) throws IOException {
		int page = req.getIntegerParameter("page");
		final Connection c = resp.connection();
		final User user = s.user(c);
		Iterator<SyncEntry> it = SyncEntries.get().pageFor(user.accounts(c).list().get(0), page, c).iterate();
		JsonObject jContent = new JsonObject();
		List<JsonObject> jEntries = jContent.addChildListOf("sync", JsonObject.class);
		while(it.hasNext()) {
			SyncEntry entry = it.next();
			JsonObject jEntry = new JsonObject();
			jEntry.put("id", entry.id());
			jEntry.put("started", entry.started().getTime());
			jEntry.put("completed", entry.completed().getTime());
			jEntry.put("updates", entry.updateCount());
			jEntries.add(jEntry);
		}
		resp.sendOk(jContent);
	}
}
