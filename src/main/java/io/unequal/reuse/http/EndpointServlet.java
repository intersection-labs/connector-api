package io.unequal.reuse.http;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import io.unequal.reuse.util.Reflection;
import io.unequal.reuse.util.IntegrityException;
import io.unequal.reuse.http.Request.HttpMethod;


class EndpointServlet extends HttpServlet {

	private final Endpoint _e;
	private final boolean _doesGet;
	private final boolean _doesPost;
	
	public EndpointServlet(Endpoint e) {
		_e = e;
		_doesGet = Reflection.getDeclaredMethod(true, e.getClass(), "get", Request.class, Response.class) != null;
		_doesPost = Reflection.getDeclaredMethod(true, e.getClass(), "post", Request.class, Response.class) != null;
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
		Request req = new RequestImpl(httpReq);
		Response resp = new ResponseImpl(httpResp, Constants.JSON);
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
				_e.trace(req, resp);
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
