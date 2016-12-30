package io.unequal.reuse.data;
import java.util.Map;
import java.util.HashMap;
import java.sql.SQLException;
import java.beans.PropertyVetoException;
import com.mchange.v2.c3p0.*;
import io.unequal.reuse.util.IntegrityException;


public class Database {

	// TYPE:
	private final static Object _LOCK = new Object();

	// INSTANCE:
	private final ComboPooledDataSource _pool;
	private final Map<java.sql.Connection, Connection> _dbcCache;
	
	public Database(String url, boolean local) {
		try {
			DatabaseUrl dbUrl = new DatabaseUrl(url, local);
			_pool = new ComboPooledDataSource();
			_pool.setDriverClass("org.postgresql.Driver");            
			_pool.setJdbcUrl(dbUrl.jdbcUrl());
			_pool.setUser(dbUrl.username());                                  
			_pool.setPassword(dbUrl.password());
			_pool.setMaxStatementsPerConnection(200);
			_dbcCache = new HashMap<>();
		}
		catch(PropertyVetoException pve) {
			throw new IntegrityException(pve);
		}
	}
	
	public Connection connect() {
		try {
			synchronized(_LOCK) {
				java.sql.Connection c = _pool.getConnection();
				Connection dbc = _dbcCache.get(c);
				if(dbc == null) {
					dbc = new Connection(c);
					_dbcCache.put(c, dbc);
				}
				return dbc;
			}
		}
		catch(SQLException sqle) {
			throw new DatabaseException(sqle);
		}
	}
	
	public void close() {
		_pool.close();
	}
}
