// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package im.connector.api.rest;
import java.io.PrintWriter;
import java.util.List;
import io.unequal.reuse.data.Connection;
import io.unequal.reuse.http.Request;
import io.unequal.reuse.http.Response;
import im.connector.api.data.*;


// TODO this Servlet needs to be protected somehow
public class ContactsSyncEndpointV1 extends UserEndpoint {

	// TODO this needs to be restructured to be done by account id
	public void get(Request req, Session s, Response resp) throws Exception {
		Connection c = resp.connection();
		User user = s.user(c);
		PrintWriter out = resp.getWriter();
		// Check that we have an account to sync from:
		List<Account> accounts = user.accounts(c).list();
		if(accounts.isEmpty()) {
			out.println("User does not have any Google accounts configured.");
			return;
		}
		for(Account account : accounts) {
			if(account.accessToken() == null) {
				out.println("Access token is not available for account "+account.describe(c));
				continue;
			}
			SyncEngine engine = SyncEngine.createFor(account.type());
			engine.runFor(account, out, resp.connection());
		}
		out.println("All contacts synced.");
	}
}
