// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package im.connector.api.rest;
import java.io.IOException;
import io.unequal.reuse.data.Connection;
import io.unequal.reuse.http.Endpoint;
import io.unequal.reuse.http.Request;
import io.unequal.reuse.http.Response;
import im.connector.api.data.*;


public class SessionsSignOutEndpointV1 extends Endpoint {

	public void post(Request req, Response resp) throws IOException {
		Connection c = resp.connection();
		Session s = Sessions.get().find(req, true, c);
		s.signOut();
		Sessions.get().update(s, c);
		// TODO should I remove it here?
		Sessions.get().removeCookie(resp);
		resp.setDisableCache();
		resp.sendOk();
	}
}
