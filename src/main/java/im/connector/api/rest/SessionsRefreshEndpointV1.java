// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package im.connector.api.rest;
import java.io.IOException;
import java.util.UUID;
import javax.servlet.http.Cookie;
import io.unequal.reuse.http.Endpoint;
import io.unequal.reuse.http.Request;
import io.unequal.reuse.http.Response;
import io.unequal.reuse.util.TimeValue;
import io.unequal.reuse.util.TimeValue.Measure;


@Deprecated
public class SessionsRefreshEndpointV1 extends Endpoint {

	public void post(Request req, Response resp) throws IOException {
		Cookie sessionCookie = req.getCookie(SessionsNewEndpointV1.USER_SESSION_COOKIE_NAME);
		if(sessionCookie == null) {
			sessionCookie = new Cookie(SessionsNewEndpointV1.USER_SESSION_COOKIE_NAME, UUID.randomUUID().toString());
		}
		sessionCookie.setPath(SessionsNewEndpointV1.USER_SESSION_COOKIE_PATH);
		String domain = App.domain();
		if(domain != null) {
			sessionCookie.setDomain(domain);
		}		
    	sessionCookie.setMaxAge((int)TimeValue.convert(SessionsNewEndpointV1.USER_SESSION_COOKIE_DAYS, Measure.DAYS, Measure.SECONDS));
    	resp.addCookie(sessionCookie);
		resp.sendOk();
	}
}
