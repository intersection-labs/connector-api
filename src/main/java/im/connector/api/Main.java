package im.connector.api;
import java.sql.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.net.URI;
import java.net.URISyntaxException;
import com.heroku.sdk.jdbc.DatabaseUrl;
import io.unequal.reuse.http.Env;
import io.unequal.reuse.http.JsonObject;
import io.unequal.reuse.http.RestServer;
import io.unequal.reuse.http.Settings;



public class Main {
	
	public static void main(String[] args) throws Exception {
		// Load config:
		Config config = new Config();
		config.load("PORT", Integer.class);
		config.load("APP_URL", String.class);
		config.load("ENV", Env.class);
		// Configure server:
		Settings settings = new Settings();
		settings.port((Integer)config.get("port"));
		settings.staticFiles("/public");
		RestServer server = new RestServer(settings);
		server.endpoint(new ConfigCheckVersionEndpointV1(), "/config/check-version/v1");
		server.run();
	}
}
