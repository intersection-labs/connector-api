// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package im.connector.api.data;
import java.util.Date;
import javax.servlet.http.Cookie;
import io.unequal.reuse.data.Entity;
import io.unequal.reuse.data.Property;
import io.unequal.reuse.data.Connection;
import io.unequal.reuse.data.Query;
import io.unequal.reuse.data.Property.Flag;
import io.unequal.reuse.http.JsonObject;
import io.unequal.reuse.http.Request;
import io.unequal.reuse.http.Response;
import io.unequal.reuse.util.Checker;
// TODO replace with new java packages
import io.unequal.reuse.util.TimeValue;
import io.unequal.reuse.util.TimeValue.Measure;
import static io.unequal.reuse.util.Util.*;
import im.connector.api.rest.App;
import im.connector.api.rest.NotAuthenticatedException;


public class Sessions extends Entity<Session> {

	// TYPE:
	public static final String COOKIE_NAME = "sid";
	public static final String COOKIE_PATH = "/";
	public static final int COOKIE_DAYS = 365;	

	private final static class SingletonHolder {
		private final static Sessions instance = new Sessions();
	}

	public static Sessions get() {
		return SingletonHolder.instance;
	}
	
	// INSTANCE:
	public final Property<String> uuid;
	public final Property<User> user;
	public final Property<Date> timeLastAccessed;
	public final Property<Date> timeClosed;
	public final Property<String> closeReason;
	// Queries:
	private Query<Session> _uuid;
	
	private Sessions() {
		super("sessions");
		uuid = property(String.class, "uuid", "uuid", Flag.MANDATORY, Flag.UNIQUE, Flag.READ_ONLY);
		user = property(User.class, "user", "user_id", Property.OnDelete.CASCADE, Flag.MANDATORY);
		timeLastAccessed = property(Date.class, "timeLastAccessed", "time_last_accessed");
		timeClosed = property(Date.class, "timeClosed", "time_closed");
		closeReason = property(String.class, "closeReason", "close_reason");
	}

	public Property<?>[] naturalKey() {
		return new Property<?>[] { uuid };
	}

	public Session find(String uuid, Connection c) {
		Checker.nil(uuid);
		Checker.nil(c);
		if(_uuid == null) {
			_uuid = query().where(this.uuid.equalTo());
		}
		return c.run(_uuid, uuid).single();
	}

	public Session find(Request req, boolean fail, Connection c) {
		JsonObject content = new JsonObject();
		// Check if session cookie is set:
		Cookie sid = req.getCookie(COOKIE_NAME);
		if(sid == null) {
			if(fail) {
				content.put("reason", "no session");
				throw new NotAuthenticatedException(content);
			}
			return null;
		}
		// Cookie found, retrieve session from database:
		Session session = find(sid.getValue(), c);
		if(session == null) {
			logger().log(warn("session with UUID {} was not found on the database", sid.getValue()));
			if(fail) {
				content.put("reason", "invalid session id");
				throw new NotAuthenticatedException(content);
			}
			return null;
		}
		// Session is valid, check if closed:
		if(session.closed()) {
			if(fail) {
				content.put("reason", "session closed");
				content.put("closeReason", session.closeReason());
				throw new NotAuthenticatedException(content);
			}
			return null;
		}
		return session;
	}
	
	public void refreshCookie(Request req, Response resp, Session session) {
		Cookie sessionCookie = req.getCookie(COOKIE_NAME);
		if(sessionCookie == null) {
			sessionCookie = new Cookie(COOKIE_NAME, session.uuid().toString());
		}
		sessionCookie.setPath(COOKIE_PATH);
		String domain = App.domain();
		if(domain != null) {
			sessionCookie.setDomain(domain);
		}
    	sessionCookie.setMaxAge((int)TimeValue.convert(COOKIE_DAYS, Measure.DAYS, Measure.SECONDS));
    	resp.addCookie(sessionCookie);
	}
	
	public void removeCookie(Response resp) {
		resp.removeCookie(COOKIE_NAME, COOKIE_PATH, App.domain());
	}
}
