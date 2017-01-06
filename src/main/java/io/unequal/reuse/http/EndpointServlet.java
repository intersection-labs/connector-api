package io.unequal.reuse.http;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import io.unequal.reuse.data.Database;
import io.unequal.reuse.http.Request.HttpMethod;
import io.unequal.reuse.util.Reflection;
import io.unequal.reuse.util.IntegrityException;
import static io.unequal.reuse.util.Util.x;


// For RestServer:
class EndpointServlet extends HttpServlet {

	private final Endpoint _e;
	private final Database _db;
	private final String _webAppUrl;
	private final boolean _doesGet;
	private final boolean _doesPost;
	private final Logger _logger;
	
	public EndpointServlet(Endpoint e, Database db, String webAppUrl) {
		_e = e;
		_db = db;
		_webAppUrl = webAppUrl;
		_doesGet = Reflection.declaredMethod(true, e.getClass(), "get", Request.class, Response.class) != null;
		_doesPost = Reflection.declaredMethod(true, e.getClass(), "post", Request.class, Response.class) != null;
		_logger = Logger.getLogger(getClass().getName());
	}

	public void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("OPTIONS, HEAD");
		if(_doesGet) {
			sb.append(", GET");
		}
		if(_doesPost) {
			sb.append(", POST");
		}
		resp.setHeader("Allow", sb.toString());
	}

	public void doHead(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		_handle(HttpMethod.GET, req, new HeadResponse(resp));
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		_handle(HttpMethod.GET, req, resp);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		_handle(HttpMethod.POST, req, resp);
	}
	
	public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		_handle(HttpMethod.DELETE, req, resp);
	}
	
	public void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		_handle(HttpMethod.PUT, req, resp);
	}

	public void doTrace(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		_handle(HttpMethod.TRACE, req, resp);
	}

	private void _handle(HttpMethod method, HttpServletRequest httpReq, HttpServletResponse httpResp) throws IOException {
		httpReq.setCharacterEncoding(Constants.CHARSET);
		RequestImpl req = new RequestImpl(httpReq);
		ResponseImpl resp = new ResponseImpl(httpResp, Constants.JSON, _db);
		// Check allowed origins:
		String origin = req.getOrigin();
		if(_webAppUrl.equals(origin)) {
			// Enable Cross-Origin Resource Sharing (see link below for details)
			// http://www.html5rocks.com/en/tutorials/cors/
			resp.setHeader("Access-Control-Allow-Origin", origin);
			resp.setHeader("Access-Control-Allow-Credentials", "true");
			resp.setHeader("Access-Control-Expose-Headers", "Set-Cookie");
		}
		else {
			_logger.info(x("origin '{}' is not allowed", origin));
		}
		// Process request:
		try {
			if(method == HttpMethod.GET) {
				_e.get(req, resp);
			}
			else if(method == HttpMethod.POST) {
				_e.post(req, resp);
			}
			else if(method == HttpMethod.DELETE) {
				_e.delete(req, resp);
			}
			else if(method == HttpMethod.PUT) {
				_e.put(req, resp);
			}
			else if(method == HttpMethod.TRACE) {
				throw new MethodNotAllowedException(HttpMethod.TRACE);
			}
			else {
				throw new IntegrityException(method);
			}
		}
		catch(Exception e) {
			resp.sendError(e);
			if(!EndpointException.class.isAssignableFrom(e.getClass())) {
				_logger.log(Level.SEVERE, "Unknown error", e);
			}
		}
		finally {
			resp.close();
		}
	}

	private static final long serialVersionUID = 1L;
}
