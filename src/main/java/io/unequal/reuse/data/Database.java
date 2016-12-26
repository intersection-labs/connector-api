package io.unequal.reuse.data;
import java.sql.SQLException;
import java.sql.Connection;
import java.beans.PropertyVetoException;
import com.mchange.v2.c3p0.*;
import io.unequal.reuse.util.IntegrityException;


public class Database {

	private final ComboPooledDataSource _pool;
	
	public Database(String url, boolean local) {
		try {
			DatabaseUrl dbUrl = new DatabaseUrl(url, local);
			_pool = new ComboPooledDataSource();
			_pool.setDriverClass("org.postgresql.Driver");            
			_pool.setJdbcUrl(dbUrl.jdbcUrl());
			_pool.setUser(dbUrl.username());                                  
			_pool.setPassword(dbUrl.password());
		}
		catch(PropertyVetoException pve) {
			throw new IntegrityException(pve);
		}
	}
	
	public Connection connect() throws SQLException {
		return _pool.getConnection();
	}
	
	public void close() {
		_pool.close();
	}
}
