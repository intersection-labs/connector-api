package im.connector.api.rest;
import java.net.URL;
import io.unequal.reuse.util.Config;
import io.unequal.reuse.http.Env;


public class App {
	
	private static final String _ENV = "ENV";
	private static final String _APP_URL = "APP_URL";
	private static final String _PORT = "PORT";
	private static final String _WEB_APP_URL = "WEB_APP_URL";
	private static final String _DATABASE_URL = "DATABASE_URL";
	private static final String _GOOGLE_CLIENT_ID = "GOOGLE_CLIENT_ID";
	private static final String _GOOGLE_CLIENT_SECRET = "GOOGLE_CLIENT_SECRET";
	
	public static void loadConfig() {
		Config.load(_PORT, Integer.class);
		Config.load(_ENV, Env.class);
		Config.load(_APP_URL, URL.class);
		Config.load(_WEB_APP_URL, URL.class);
		Config.load(_DATABASE_URL, String.class);
		Config.load(_GOOGLE_CLIENT_ID, String.class);
		Config.load(_GOOGLE_CLIENT_SECRET, String.class);		
	}

	public static Integer port() {
		return (Integer)Config.get(_PORT);
	}

	public static Env env() {
		return (Env)Config.get(_ENV);
	}
	
	public static URL url() {
		return (URL)Config.get(_APP_URL);
	}

	public static URL webAppUrl() {
		return (URL)Config.get(_WEB_APP_URL);
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
		return (String)Config.get(_DATABASE_URL);
	}

	public static String googleClientId() {
		return (String)Config.get(_GOOGLE_CLIENT_ID);
	}

	public static String googleClientSecret() {
		return (String)Config.get(_GOOGLE_CLIENT_SECRET);
	}

}
