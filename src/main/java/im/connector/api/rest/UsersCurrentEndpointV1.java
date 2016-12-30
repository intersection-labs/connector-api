// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package im.connector.api.rest;
import im.connector.api.data.*;
import io.unequal.reuse.data.Connection;
import io.unequal.reuse.http.JsonObject;
import io.unequal.reuse.http.Request;
import io.unequal.reuse.http.Response;


public class UsersCurrentEndpointV1 extends UserEndpoint {

	public void get(Request req, Session s, Response resp) throws Exception {
		Connection c = resp.connection();
		User user = s.user(c);
		JsonObject jContent = new JsonObject();
		jContent.put("user", Users.get().json(user, c));
		resp.sendOk(jContent);
	}
}
