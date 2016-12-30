package io.unequal.reuse.data;

import java.sql.SQLException;

public class Connection implements AutoCloseable {

	private final java.sql.Connection _c;
	
	// For Database:
	Connection(java.sql.Connection c) {
		_c = c;
	}

	public void close() {
		try {
			_c.close();
		}
		catch(SQLException sqle) {
			// TODO log instead
			sqle.printStackTrace(System.err);
		}
	}
	
	public void insert(Instance<?> i) {
		// TODO impl
	}

	public void update(Instance<?> i) {
		// TODO impl
	}

	public <T extends Instance<?>> T find(Entity<T> e, Long id) {
		return null;
	}

	public <T extends Instance<?>> QueryResult<T> run(Query<T> query, Object ... params) {
		// TODO impl
		return null;
	}
}
