package im.connector.api.rest;
import java.net.URL;
import io.unequal.reuse.util.Config;
import io.unequal.reuse.http.Env;


public class App {
	
	private static final String _PORT = "PORT";
	private static final String _ENV = "ENV";
	private static final String _APP_URL = "APP_URL";
	private static final String _DATABASE_URL = "DATABASE_URL";
	private static final String _GOOGLE_CLIENT_ID = "GOOGLE_CLIENT_ID";
	private static final String _GOOGLE_CLIENT_SECRET = "GOOGLE_CLIENT_SECRET";
	
	public static void loadConfig() {
		Config config = Config.get();
		config.load(_PORT, Integer.class);
		config.load(_ENV, Env.class);
		config.load(_APP_URL, URL.class);
		config.load(_DATABASE_URL, String.class);
		config.load(_GOOGLE_CLIENT_ID, String.class);
		config.load(_GOOGLE_CLIENT_SECRET, String.class);		
	}

	public static Integer port() {
		return (Integer)Config.get().get(_PORT);
	}
	
	public static Env env() {
		return (Env)Config.get().get(_ENV);
	}
	
	public static URL url() {
		return (URL)Config.get().get(_APP_URL);
	}
	
	public static String domain() {
		URL url = url();
		final String host = url.getHost();
		if(host.equals("localhost")) {
			return null;
		}
		else {
			return host.substring(host.indexOf('.')-1);
		}
	}

	public static String databaseUrl() {
		return (String)Config.get().get(_DATABASE_URL);
	}

	public static String googleClientId() {
		return (String)Config.get().get(_GOOGLE_CLIENT_ID);
	}

	public static String googleClientSecret() {
		return (String)Config.get().get(_GOOGLE_CLIENT_SECRET);
	}

}
