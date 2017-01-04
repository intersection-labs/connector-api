// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package im.connector.api.rest;
import java.io.IOException;
import io.unequal.reuse.http.Request;
import io.unequal.reuse.http.Response;
import im.connector.api.data.Sessions;
import im.connector.api.data.Session;


public class SessionsRefreshEndpointV2 extends UserEndpoint {

	public void post(Request req, Session s, Response resp) throws IOException {
		// No need to update cookie; it's done by UserServlet.
		// Update database:
		Sessions.get().update(s.accessed(), resp.connection());
		resp.sendOk();
	}
}
