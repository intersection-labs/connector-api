package io.unequal.reuse.data;
import java.sql.SQLException;


public class DatabaseException extends RuntimeException {

	public DatabaseException(SQLException sqle) {
		super(sqle);
	}

	private static final long serialVersionUID = 1L;
}
