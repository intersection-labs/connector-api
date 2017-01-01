package io.unequal.reuse.data;
import java.util.Map;
import java.util.HashMap;
import java.sql.SQLException;
import java.beans.PropertyVetoException;
import com.mchange.v2.c3p0.*;
import io.unequal.reuse.util.Checker;
import io.unequal.reuse.util.IntegrityException;
import static io.unequal.reuse.util.Util.*;


public class Database {

	// TYPE:
	private final static Object _LOCK = new Object();

	// INSTANCE:
	private final Map<String,Entity<?>> _byName;
	private final Map<Class<?>,Entity<?>> _byInstance;
	private final ComboPooledDataSource _pool;
	private final Map<java.sql.Connection, Connection> _dbcCache;

	public Database(String url, boolean local) {
		try {
			_byName = new HashMap<>();
			_byInstance = new HashMap<>();
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

	public void load(Model model) {
		Checker.checkNull(model);
		for(Entity<?> e : model.entities()) {
			Entity<?> tmp = _byName.get(e.getClass().getSimpleName());
			if(tmp != null) {
				throw new IllegalArgumentException(x("entity '{}' has already been registered", e.getClass().getSimpleName()));
			}
			_byName.put(e.getName(), e);
			if(_byInstance.containsKey(e.getInstanceClass())) {
				throw new IntegrityException();
			}
			_byInstance.put(e.getInstanceClass(), e);
			e.loadInto(this);
		}
	}

	public Entity<?> getEntity(String name) {
		Checker.checkEmpty(name);
		return _byName.get(name);
	}

	public Entity<?> getEntityForInstance(Class<?> c) {
		Checker.checkNull(c);
		return _byInstance.get(c);
	}

	public Connection connect() {
		try {
			synchronized(_LOCK) {
				java.sql.Connection c = _pool.getConnection();
				Connection dbc = _dbcCache.get(c);
				if(dbc == null) {
					dbc = new Connection(this, c);
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
