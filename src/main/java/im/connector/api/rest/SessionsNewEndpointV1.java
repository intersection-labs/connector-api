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
public class SessionsNewEndpointV1 extends Endpoint {

	public static final String USER_SESSION_COOKIE_NAME = "usid";
	public static final String USER_SESSION_COOKIE_PATH = "/";
	public static final int USER_SESSION_COOKIE_DAYS = 365;	

	public void post(Request req, Response resp) throws IOException {
		logger().warning("called deprecated endpoint");
		req.getParameter("device_type", true);
		req.getParameter("device_id", true);
		String sessionId = UUID.randomUUID().toString();
		// Note: we don't actually store a session since this is a deprecated call.
		Cookie cookie = new Cookie(USER_SESSION_COOKIE_NAME, sessionId);
		cookie.setPath(USER_SESSION_COOKIE_PATH);
		cookie.setMaxAge((int)TimeValue.convert(USER_SESSION_COOKIE_DAYS, Measure.DAYS, Measure.SECONDS));
		resp.addCookie(cookie);
		resp.setDisableCache();
		resp.sendOk();
	}
}
