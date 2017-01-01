package io.unequal.reuse.data;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import io.unequal.reuse.util.Checker;
import io.unequal.reuse.util.Util;
import io.unequal.reuse.util.IntegrityException;
import static io.unequal.reuse.util.Util.info;

public class Connection implements AutoCloseable {

	private final Database _db;
	private final java.sql.Connection _c;
	private final Logger _logger;
	
	// For Database:
	Connection(Database db, java.sql.Connection c) {
		_db = db;
		_c = c;
		_logger = Logger.getLogger(getClass().getName());
	}

	public Database database() {
		return _db;
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

	long insert(String sql, Object[] params) {
		try {
			PreparedStatement ps = _c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			for(int i=0; i<params.length; i++) {
				ps.setObject(i+1, params[i]);
			}
			_logger.info(Util.x("Executing query: {}", ps));
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			rs.next();
			long id = rs.getLong(1);
			rs.close();
			ps.close();
			return id;
		}
		catch(SQLException sqle) {
			throw new DatabaseException(sqle);
		}
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
