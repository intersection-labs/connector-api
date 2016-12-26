package im.connector.api;
import java.sql.*;
import io.unequal.reuse.data.Database;
import io.unequal.reuse.http.Env;
import io.unequal.reuse.http.RestServer;
import io.unequal.reuse.http.Settings;



public class Main {
	
	public static void main(String[] args) throws Exception {
		// Load config:
		Config config = new Config();
		config.load("PORT", Integer.class);
		config.load("APP_URL", String.class);
		config.load("ENV", Env.class);
		config.load("DATABASE_URL", String.class);
		// Load database:
		boolean local = config.get("ENV") == Env.DEV;
		Database db = new Database((String)config.get("DATABASE_URL"), local);
		// Configure server:
		Settings settings = new Settings();
		settings.port((Integer)config.get("port"));
		settings.staticFiles("/public");
		settings.database(db);
		RestServer server = new RestServer(settings);
		server.endpoint(new ConfigCheckVersionEndpointV1(), "/config/check-version/v1");
		server.run();
	}
}
