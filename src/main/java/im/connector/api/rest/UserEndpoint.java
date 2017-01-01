// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package im.connector.api.rest;
import io.unequal.reuse.http.Endpoint;
import io.unequal.reuse.http.Request.HttpMethod;
import io.unequal.reuse.http.MethodNotAllowedException;
import io.unequal.reuse.http.Request;
import io.unequal.reuse.http.Response;
import io.unequal.reuse.data.Connection;
import im.connector.api.data.Session;
import im.connector.api.data.Sessions;
import im.connector.api.data.User;


public abstract class UserEndpoint extends Endpoint {

	public final void get(Request req, Response resp) throws Exception {
		get(req, _session(req, resp), resp);
	}

	protected void get(Request req, Session s, Response resp) throws Exception {
		super.get(req, resp);
	}
	
	public final void post(Request req, Response resp) throws Exception {
		post(req, _session(req, resp), resp);
	}

	protected void post(Request req, Session s, Response resp) throws Exception {
		super.post(req, resp);
	}

	public final void put(Request req, Response resp) throws Exception {
		put(req, _session(req, resp), resp);
	}

	protected void put(Request req, Session s, Response resp) throws Exception {
		super.put(req, resp);
	}

	public final void delete(Request req, Response resp) throws Exception {
		delete(req, _session(req, resp), resp);
	}

	protected void delete(Request req, Session s, Response resp) throws Exception {
		super.delete(req, resp);
	}

	private Session _session(Request req, Response resp) {
		Connection c = resp.connection();
		Session session = Sessions.get().find(req, true, c);
		// Update cookie:
		Sessions.get().refreshCookie(req, resp, session);
		// Update timestamp:
		Sessions.get().update(session.accessed(), c);
		return session;
	}
}
