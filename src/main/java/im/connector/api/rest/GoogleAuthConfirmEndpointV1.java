// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package im.connector.api.rest;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import io.unequal.reuse.data.Connection;
import io.unequal.reuse.http.JsonObject;
import io.unequal.reuse.http.Request;
import io.unequal.reuse.http.Response;
import io.unequal.reuse.util.HttpClient;
import im.connector.api.data.*;



public class GoogleAuthConfirmEndpointV1 extends UserEndpoint {

	public void get(Request req, Session s, Response resp) throws IOException {
		// Google redirects with:
		// <endpoint>?state=0&code=4/ygE-kCdJ_pgwb1mKZq3uaTEWLUBd.slJWq1jM9mcUEnp6UAPFm0F2NQjrgwI&authuser=0&prompt=consent&session_state=a3d1eb134189705e9acf2f573325e6f30dd30ee4..d62c
		// If the user denied access, we get back an error, e.g. error=access_denied&state=0
		if (req.getParameter("error") != null) {
			// TODO properly formatted web page
			resp.getWriter().println("Error: "+req.getParameter("error"));
			return;
		}

		// Google returns a code that can be exchanged for a access token:
		String code = req.getParameter("code", true);
		// Exchange code for access tokens:
		Map<String,String> params = new HashMap<>();
		params.put("code", code);
		params.put("client_id", App.googleClientId());
		params.put("client_secret", App.googleClientSecret());
		params.put("redirect_uri", App.url()+":"+App.port()+GoogleService.OAUTH_CONFIRM_URL);
		params.put("grant_type", "authorization_code");
		String body = HttpClient.post("https://accounts.google.com/o/oauth2/token", params);
		// Returns:
		//   {
		//       "access_token": "ya29.AHES6ZQS-BsKiPxdU_iKChTsaGCYZGcuqhm_A5bef8ksNoU",
		//       "token_type": "Bearer",
		//       "expires_in": 3600,
		//       "id_token": "eyJhbGciOiJSUzI1NiIsImtpZCI6IjA5ZmE5NmFjZWNkOGQyZWRjZmFiMjk0NDRhOTgyN2UwZmFiODlhYTYifQ.eyJpc3MiOiJhY2NvdW50cy5nb29nbGUuY29tIiwiZW1haWxfdmVyaWZpZWQiOiJ0cnVlIiwiZW1haWwiOiJhbmRyZXcucmFwcEBnbWFpbC5jb20iLCJhdWQiOiI1MDgxNzA4MjE1MDIuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdF9oYXNoIjoieUpVTFp3UjVDX2ZmWmozWkNublJvZyIsInN1YiI6IjExODM4NTYyMDEzNDczMjQzMTYzOSIsImF6cCI6IjUwODE3MDgyMTUwMi5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsImlhdCI6MTM4Mjc0MjAzNSwiZXhwIjoxMzgyNzQ1OTM1fQ.Va3kePMh1FlhT1QBdLGgjuaiI3pM9xv9zWGMA9cbbzdr6Tkdy9E-8kHqrFg7cRiQkKt4OKp3M9H60Acw_H15sV6MiOah4vhJcxt0l4-08-A84inI4rsnFn5hp8b-dJKVyxw1Dj1tocgwnYI03czUV3cVqt9wptG34vTEcV3dsU8",
		//       "refresh_token": "1/Hc1oTSLuw7NMc3qSQMTNqN6MlmgVafc78IZaGhwYS-o"
		//   }
		// Get the access token:
		JsonObject json = JsonObject.parse(body);
		Connection c = resp.connection();
		User user = s.user(c);
		Account account = user.accounts(c).list().get(0);
		account.accessToken(json.getString("access_token"));
		account.accessTokenTime(new Date());
		// Google access tokens expire after an hour, but since we requested offline access 
		// we can get a new token without user involvement via the refresh token
		account.refreshToken(json.getString("refresh_token"));
		Accounts.get().update(account, c);
		// All done:
		resp.sendRedirect(App.webAppUrl()+"/app");
	}
}
