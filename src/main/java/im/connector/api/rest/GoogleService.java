package im.connector.api.rest;
import java.io.IOException;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.client.authn.oauth.GoogleOAuthParameters;
import com.google.gdata.client.authn.oauth.OAuthHmacSha1Signer;
import com.google.gdata.client.authn.oauth.OAuthException;
import io.unequal.reuse.data.Connection;
import io.unequal.reuse.util.IntegrityException;
import im.connector.api.data.*;



public class GoogleService {

	public static final String CONNECTOR_APP_NAME = "Intersection-Connector/1.0";
	public static final String OAUTH_START_URL = "/google/oauth/start/v1";
	public static final String OAUTH_CONFIRM_URL = "/google/oauth/confirm/v1";
	public static final String OAUTH_COMPLETE_URL = "/google/get-contacts/v1";
	public static final String GET_ALL_CONTACTS_URL = "https://www.google.com/m8/feeds/contacts/default/full";
	public static final String GET_SINGLE_CONTACT_URL = "https://www.google.com/m8/feeds/contacts/{}/full/{}";

	public static ContactsService serviceFor(Account account, Connection c) throws IOException {
		// Initialize service:
		ContactsService service = new ContactsService(CONNECTOR_APP_NAME);
		GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();
		oauthParameters.setOAuthType(GoogleOAuthParameters.OAuthType.TWO_LEGGED_OAUTH);
		oauthParameters.setOAuthConsumerKey(App.googleClientId());		
		oauthParameters.setOAuthConsumerSecret(App.googleClientSecret());		
		oauthParameters.setOAuthToken(Accounts.get().googleAccessToken(account, c));
		try {
			service.setOAuthCredentials(oauthParameters, new OAuthHmacSha1Signer());
		}
		catch(OAuthException oae) {
			throw new IntegrityException(oae);
		}
		service.useSsl();
		service.setConnectTimeout(60000);
		return service;
	}
}
