package im.connector.api.rest;
import java.net.URL;
import io.unequal.reuse.data.Database;
import io.unequal.reuse.http.Env;
import io.unequal.reuse.http.RestServer;
import io.unequal.reuse.http.Settings;
import io.unequal.reuse.util.Config;



public class Main {
	
	public static void main(String[] args) throws Exception {
		// Load config:
		App.loadConfig();
		// Load database:
		boolean local = App.env() == Env.DEV;
		Database db = new Database(App.databaseUrl(), local);
		// Configure server:
		Settings settings = new Settings();
		settings.port(App.port());
		settings.staticFiles("/public");
		settings.database(db);
		RestServer server = new RestServer(settings);
		server.endpoint(new ConfigCheckVersionEndpointV1(), "/config/check-version/v1");
		server.run();
	}
}
