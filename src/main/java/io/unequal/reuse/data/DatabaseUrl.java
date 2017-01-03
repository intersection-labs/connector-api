package io.unequal.reuse.data;
import java.net.URI;
import java.net.URISyntaxException;
import io.unequal.reuse.util.Checker;
import io.unequal.reuse.util.Util;


class DatabaseUrl {

	private final String _jdbcUrl;
	private final String _username;
	private final String _password;	

	public DatabaseUrl(String url, boolean local) {
		Checker.empty(url);
		try {
			URI uri = new URI(url);
			StringBuilder params = new StringBuilder();
			params.append("prepareThreshold=1");
			// This disables SSL for local databases:
			params.append(local ? "" : "&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory");
			_jdbcUrl = Util.x("jdbc:postgresql://{}:{}{}?{}", uri.getHost(), uri.getPort(), uri.getPath(), params.toString());
			String[] parts = uri.getUserInfo().split(":");
			_username = parts[0];
			_password = parts[1];
		}
		catch(URISyntaxException use) {
			throw new IllegalArgumentException(use.getMessage());
		}
	}
	
	public String jdbcUrl() {
		return _jdbcUrl;
	}
	
	public String username() {
		return _username;
	}
	
	public String password() {
		return _password;
	}
}
