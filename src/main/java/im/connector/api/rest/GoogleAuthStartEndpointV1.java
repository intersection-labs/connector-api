// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package im.connector.api.rest;
import io.unequal.reuse.http.Request;
import io.unequal.reuse.http.Response;
import static io.unequal.reuse.util.Util.*;
import im.connector.api.data.*;


public class GoogleAuthStartEndpointV1 extends UserEndpoint {

	public void get(Request req, Session s, Response resp) throws Exception {
		final String account = req.getParameter("account", true);
		// Redirect to google for authorization
		StringBuilder oauthUrl = new StringBuilder().append("https://accounts.google.com/o/oauth2/auth")
			// Client ID from the API console registration:
			.append("?client_id=").append(App.googleClientId())
			.append("&response_type=code")
			// Scope = API permissions that we are requesting:
			.append("&scope=https://www.google.com/m8/feeds/")
			// URL that Google redirects to after authorization:
			.append(x("&redirect_uri={}:{}{}", App.url(), App.port(), GoogleService.OAUTH_CONFIRM_URL))
			// State is a user-defined parameter to help correlate responses with each other (e.g. session ID)
			// In our case, because the session ID is included in the response as a cookie, we won't use it.
			.append("&state=0")
			// Ask to access user's data while they are not signed into Google:
			.append("&access_type=offline")
			// Force approval for a specific account:
			.append("&login_hint=").append(account)
			// This requires users to verify which Google account to use, if they are already signed in
			.append("&approval_prompt=force");
		resp.sendRedirect(oauthUrl.toString());
	}
}
