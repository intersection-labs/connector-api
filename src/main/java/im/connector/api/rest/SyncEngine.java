package im.connector.api.rest;
import java.io.IOException;
import java.io.PrintWriter;
import com.google.gdata.util.ServiceException;
import io.unequal.reuse.data.Connection;
import io.unequal.reuse.util.Checker;
import io.unequal.reuse.util.IntegrityException;
import im.connector.api.data.Accounts;
import im.connector.api.data.Account;


public abstract class SyncEngine {

	// TYPE:
	public static SyncEngine createFor(Accounts.Type type) {
		Checker.nil(type);
		if(type == Accounts.Type.GOOGLE) {
			return new GoogleSyncEngine();
		}
		throw new IntegrityException(type);
	}

	// INSTANCE:
	public abstract void runFor(Account account, PrintWriter out, Connection c) throws IOException, ServiceException;
}
