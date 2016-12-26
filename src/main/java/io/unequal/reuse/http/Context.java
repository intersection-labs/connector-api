package io.unequal.reuse.http;
import java.sql.SQLException;
import java.sql.Connection;
import io.unequal.reuse.data.Database;


public class Context {

	private final Database _db;
	private Connection _c;

	// For EndpointServlet:
	Context(Database db) {
		_db = db;
	}

	public Connection connection() throws SQLException {
		if(_c == null) {
			_c = _db.connect();
		}
		return _c;
	}
	
	// For EndpointServlet:
	void end() {
		// TODO impl
	}
}
