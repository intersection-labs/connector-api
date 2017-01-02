// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package im.connector.api.rest;
import java.util.UUID;
import im.connector.api.data.*;
import io.unequal.reuse.http.Endpoint;
import io.unequal.reuse.http.Request;
import io.unequal.reuse.http.Response;
import io.unequal.reuse.http.JsonObject;
import io.unequal.reuse.data.Connection;


public class SessionsSignInEndpointV1 extends Endpoint {

	public void post(Request req, Response resp) throws Exception {
		// TODO parse email address
		String email = req.getParameter("email", true);
		Connection c = resp.connection();
		User user = Users.get().byEmail(email, c);
		if(user == null) {
			resp.sendError(StatusCodes.EMAIL_NOT_FOUND);
			return;
		}
		Session s = Sessions.get().find(req, false, c);
		if(s == null) {
			s = _insertSessionFor(user, c);
		}
		else {
			if(user.equals(s.user(c))) {
				Sessions.get().update(s.accessed(), c);
			}
			else {
				Sessions.get().update(s.close("signed out"), c);
				s = _insertSessionFor(user, c);
			}
		}
		Sessions.get().refreshCookie(req, resp, s);
		resp.setDisableCache();
		JsonObject jContent = new JsonObject();
		jContent.put("user", Users.get().json(user, c));
		resp.sendOk(jContent);
	}

	private Session _insertSessionFor(User user, Connection c) {
		Session s = new Session(UUID.randomUUID().toString());
		Sessions.get().insert(s.user(user).accessed(), c);
		return s;
	}
}
