package io.unequal.reuse.http;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Set;
import java.util.Calendar;
import java.util.HashSet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import io.unequal.reuse.data.Connection;
import io.unequal.reuse.data.Database;
import io.unequal.reuse.util.Checker;
import io.unequal.reuse.util.Strings;


public class ResponseImpl extends HttpServletResponseWrapper implements Response {

	private final Set<String> _cookieNames;
	private final String _contentType;
	private boolean _lenient;
	private boolean _committed;
	private boolean _contentTypeSet;
	private final Database _db;
	private Connection _c;
	
	// TODO contentType should not be required
	public ResponseImpl(HttpServletResponse response, String contentType, Database db) {
		super(response);
		_cookieNames = new HashSet<String>();
		_contentType = contentType;
		_lenient = _committed = _contentTypeSet = false;
		_db = db;
	}

	public boolean hasCookie(String name, String path) {
		Checker.empty(name);
		if(path != null) {
			Checker.empty(path);
		}
		return _cookieNames.contains(_getCookieFQN(name, path));
	}

	public void addCookie(Cookie c) {
		Checker.nil(c);
		_checkCommitted();
		if(hasCookie(c.getName(), c.getPath())) {
			throw new IllegalStateException("cookie '"+_getCookieFQN(c)+"' has already been added to the response");
		}
		_cookieNames.add(_getCookieFQN(c));
		super.addCookie(c);
	}

	public void removeCookie(String name, String path, String domain) {
		Checker.empty(name);
		_checkCommitted();
		Cookie c = new Cookie(name, "deleted");
		if(path != null) {
			Checker.empty(path);
			c.setPath(path);
		}
		if(domain != null) {
			Checker.empty(domain);
			c.setDomain(domain);
		}
		c.setMaxAge(0);
		addCookie(c);
	}

	public void setContentType(String contentType) {
		Checker.empty(contentType);
		_checkCommitted();
		if(!_lenient) {
			if(_contentTypeSet) {
				throw new IllegalStateException("content type has already been set");
			}
		}
		// TODO check if the Servlet API already sets the correct character set (in which case we don't need to do this)
		if(contentType.startsWith("text")) {
			if(contentType.indexOf("charset") == -1) {
				contentType = contentType+"; charset="+Constants.CHARSET;
			}
		}
		super.setContentType(contentType);
		_contentTypeSet = true;
	}

	public void setLastModified(long timestamp) {
		_checkCommitted();
		setDateHeader("Last-Modified", timestamp);
	}

	public void setDisableCache() {
		_checkCommitted();
		setHeader("Expires", "Mon, 26 Jul 1997 05:00:00 GMT"); // Date in the past
		setDateHeader("Last-Modified", Calendar.getInstance().getTimeInMillis()); // always modified
		setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
		addHeader("Cache-Control", "post-check=0, pre-check=0");
		// Supposedly, the Pragma header is a request header, not a response header (i.e., only HTTP clients
		// should be using it). However, IE seems to use it to avoid caching, so we will keep sending it.
		addHeader("Pragma", "no-cache");
	}

	public void setEnableCache() {
		_checkCommitted();
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, c.get(Calendar.YEAR)+1);
		setDateHeader("Expires", c.getTimeInMillis()); // Date in the future
		setHeader("Last-Modified", "Mon, 26 Jul 1997 05:00:00 GMT"); // Date in the past
	}

	public void sendError(StatusCode status, JsonObject jContent, Object ... params) throws IOException {
		Checker.nil(status);
		_sendContentType(Constants.JSON);
		setStatus(status.httpCode);
		JsonObject jResponse = new JsonObject();
		JsonObject jHeader = jResponse.addChild("header");
		jHeader.put("status", status.code);
		if(status != StatusCodes.OK) {
			String message = status.expand(params);
			jHeader.put("message", message);
		}
		if(jContent != null) {
			jResponse.put("content", jContent);
		}
		PrintWriter out = getWriter();
		jResponse.write(out);
		out.println();
		out.close();
	}
	
	public void sendError(StatusCode status) throws IOException {
		if(status == StatusCodes.OK) {
			throw new IllegalArgumentException("incorrect status code (cannot be OK)");
		}
		sendError(status, null);
	}

	public void sendError(Exception e) throws IOException {
		Checker.nil(e);
		_sendContentType(Constants.JSON);
		JsonObject jResponse = new JsonObject();
		JsonObject jHeader = jResponse.addChild("header");
		StatusCode errorCode = null;
		if(EndpointException.class.isAssignableFrom(e.getClass())) {
			EndpointException ee = ((EndpointException)e);
			errorCode = ee.errorCode();
			jHeader.put("message", e.getMessage());
			if(ee.content() != null) {
				jResponse.put("content", ee.content());
			}
		}
		else {
			errorCode = StatusCodes.UNEXPECTED;
			jHeader.put("message", e.toString());
		}
		setStatus(errorCode.httpCode);
		jHeader.put("status", errorCode.code);
		PrintWriter out = getWriter();
		jResponse.write(out);
		out.println();
		out.close();
	}

	public void sendOk(JsonObject jContent) throws IOException {
		sendError(StatusCodes.OK, jContent);
	}

	public void sendOk() throws IOException {
		sendOk(null);
	}
	
	public void setLenient(boolean lenient) {
		_lenient = lenient;
	}

	private void _checkCommitted() {
		if(!_lenient) {
			if(_committed) {
				throw new IllegalStateException("response has already been committed");
			}
		}
	}
	
	@Override
	public PrintWriter getWriter() throws IOException {
		_sendContentType(_contentType);
		return super.getWriter();
	}

	private String _getCookieFQN(Cookie c) {
		return _getCookieFQN(c.getName(), c.getPath());
	}
	
	public Connection connection() {
		if(_c == null) {
			_c = _db.connect();
		}
		return _c;
	}
	
	// For EndpointServlet:
	void close() {
		if(_c != null) {
			_c.close();
		}
	}

	private String _getCookieFQN(String name, String path) {
		if(path == null) {
			return name;
		}
		else {
			return path + name;
		}
	}
	
	private void _sendContentType(String contentType) {
		if(!_contentTypeSet && !_committed && !Strings.empty(contentType)) {
			setContentType(contentType);
		}
		_committed = true;
	}
}
