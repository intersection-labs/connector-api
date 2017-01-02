package io.unequal.reuse.data;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import io.unequal.reuse.util.IntegrityException;
import static io.unequal.reuse.util.Util.*;



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

	long insert(String sql, int[] types, Object[] args) {
		try {
			PreparedStatement ps = _c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			for(int i=0; i<args.length; i++) {
				ps.setObject(i+1, args[i], types[i]);
			}
			_logger.info(x("Executing query: {}", ps));
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

	public void update(String sql) {
		try {
			_logger.info(x("Executing query: {}", sql));
			Statement stmt = _c.createStatement();
			int count = stmt.executeUpdate(sql);
			stmt.close();
			if(count != 1) {
				throw new IntegrityException(count);
			}
		}
		catch(SQLException sqle) {
			throw new DatabaseException(sqle);
		}
	}

	public <I extends Instance<?>> QueryResult<I> run(Query<I> query, Object ... args) {
		try {
			PreparedStatement ps = _c.prepareStatement(query.sql());
			Property<?>[] params = query.params();
			int length = params.length;
			if(query.limit() == null) {
				length++;
			}
			if(query.offset() == null) {
				length++;
			}
			if(length != args.length) {
				throw new IllegalArgumentException(x("expected {} arguments, found {}", length, args.length));
			}
			int i=0;
			for(; i<params.length; i++) {
				ps.setObject(i+1, params[i].unwrap(args[i]), params[i].typeMapping().sqlType());
			}
			if(query.limit() == null) {
				ps.setInt(++i, query.limit());
			}
			if(query.offset() == null) {
				ps.setInt(++i, query.offset());
			}
			_logger.info(x("Executing query: {}", ps));
			ResultSet rs = ps.executeQuery();
			rs.setFetchDirection(ResultSet.FETCH_FORWARD);
			List<I> results = new ArrayList<>();
			List<Property<?>> propList = query.entity().propertyList();
			while(rs.next()) {
				I instance = Instance.newFrom(query.type());
				for(int j=0; j<propList.size(); j++) {
					Property<?> prop = propList.get(j);
					instance.update(prop, rs.getObject(j+1), true);
				}
				results.add(instance);
			}
			rs.close();
			ps.close();
			return new QueryResult<I>(results);
		}
		catch(SQLException sqle) {
			throw new DatabaseException(sqle);
		}
	}
}
