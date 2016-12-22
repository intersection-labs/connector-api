package io.unequal.reuse.http;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import io.unequal.reuse.util.IntegrityException;
import io.unequal.reuse.http.Request.HttpMethod;


class EndpointServlet extends HttpServlet {

	private final Endpoint _e;
	
	public EndpointServlet(Endpoint e) {
		_e = e;
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		_handle(HttpMethod.GET, req, resp);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		_handle(HttpMethod.POST, req, resp);
	}

	private void _handle(HttpMethod method, HttpServletRequest httpReq, HttpServletResponse httpResp) throws IOException {
		Request req = new RequestImpl(httpReq);
		Response resp = new ResponseImpl(httpResp, Response.JSON);
		try {
			if(method == HttpMethod.GET) {
				_e.get(req, resp);
			}
			else if(method == HttpMethod.POST) {
				_e.post(req, resp);
			}
			else {
				throw new IntegrityException(method);
			}
		}
		catch(EndpointException ee) {
			resp.sendError(ee);
		}
		catch(Exception e) {
			// TODO impl
			throw new RuntimeException(e);
		}
	}

	private static final long serialVersionUID = 1L;
}
