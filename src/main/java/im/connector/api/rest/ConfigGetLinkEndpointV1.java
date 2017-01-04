// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package im.connector.api.rest;
import java.io.IOException;
import io.unequal.reuse.data.Connection;
import io.unequal.reuse.http.Request;
import io.unequal.reuse.http.Response;
import io.unequal.reuse.http.Endpoint;
import io.unequal.reuse.http.ParameterValidationException;
import im.connector.api.data.*;



public class ConfigGetLinkEndpointV1 extends Endpoint {

	public void get(Request req, Response resp) throws IOException {
		try {
			final String linkId = req.getParameter("id", true);
			Connection c = resp.connection();
			final String link = Links.get().withId(linkId, c);
			if(link != null) {
				resp.setStatus(Response.SC_SEE_OTHER);
				resp.addHeader("Location", link);
			}
			else {
				resp.setStatus(Response.SC_NOT_FOUND);				
			}
		}
		catch(ParameterValidationException pve) {
			resp.setStatus(Response.SC_NOT_FOUND);
		}
	}
}
