package im.connector.api.rest;
import io.unequal.reuse.data.Database;
import io.unequal.reuse.http.Env;
import io.unequal.reuse.http.RestServer;
import io.unequal.reuse.http.Settings;
import im.connector.api.data.ConnectorModel;


public class Main {
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception {
		// Load config:
		App.loadConfig();
		// Load database:
		boolean local = App.env() == Env.DEV;
		Database db = new Database(App.databaseUrl(), local);
		db.load(new ConnectorModel());
		// Configure server:
		Settings settings = new Settings();
		settings.port(App.port());
		settings.staticFiles("/public");
		settings.database(db);
		RestServer server = new RestServer(settings);
		// Prepare endpoints:
		server.endpoint(new ConfigCheckVersionEndpointV1(), "/config/check-version/v1");
		server.endpoint(new ConfigGetLinkEndpointV1(), "/config/get-link/v1");
		server.endpoint(new SessionsNewEndpointV1(), "/sessions/new/v1");
		server.endpoint(new SessionsRefreshEndpointV1(), "/sessions/refresh/v1");
		server.endpoint(new SessionsRefreshEndpointV2(), "/sessions/refresh/v2");
		server.endpoint(new SessionsSignInEndpointV1(), "/sessions/sign-in/v1");
		server.endpoint(new SessionsSignOutEndpointV1(), "/sessions/sign-out/v1");
		server.endpoint(new GoogleAuthStartEndpointV1(), "/google/oauth/start/v1");
		server.endpoint(new GoogleAuthConfirmEndpointV1(), "/google/oauth/confirm/v1");
		server.endpoint(new UsersCurrentEndpointV1(), "/users/current/v1");
		server.endpoint(new ContactsListEndpointV1(), "/contacts/list/v1");
		server.endpoint(new ContactsViewEndpointV1(), "/contacts/view/v1");
		server.endpoint(new ContactsInviteEndpointV1(), "/contacts/invite/v1");
		server.endpoint(new ContactsModifySharingEndpointV1(), "/contacts/modify-sharing/v1");
		server.endpoint(new ContactsSyncEndpointV1(), "/contacts/sync/v1");
		server.endpoint(new SyncListEndpointV1(), "/sync/list/v1");
		//server.endpoint(new SyncViewEndpointV1(), "/sync/view/v1");
		server.endpoint(new ToolsSendEmailEndpointV1(), "/tools/send-email/v1");
		server.run();
	}
}
