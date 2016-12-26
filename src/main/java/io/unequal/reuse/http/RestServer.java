package io.unequal.reuse.http;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import io.unequal.reuse.util.Checker;


public class RestServer {

	// TYPE:
	
	// Instance:
	private final Server _server;
	private final Settings _settings;
	private final ServletContextHandler _root;

	public RestServer(Settings settings) {
		Checker.checkNull(settings);
		_settings = settings;
		_server = new Server(_settings.port());
		_root = new ServletContextHandler(ServletContextHandler.NO_SECURITY | ServletContextHandler.NO_SESSIONS);
		_root.setContextPath("/");
		_root.setErrorHandler(new ErrorHandler() {
			protected void generateAcceptableResponse(Request base, HttpServletRequest httpReq, HttpServletResponse httpResp, int code, String message) throws IOException {
				if(code == 404) {
					base.setHandled(true);
					Response resp = new ResponseImpl(httpResp, Constants.JSON);
					resp.sendError(StatusCodes.ENDPOINT_NOT_FOUND, null, base.getServletPath());
				}
			}
		});
		_server.setHandler(_root);
	}

	public void endpoint(Endpoint endpoint, String ... routes) {
		Checker.checkNull(endpoint);
		Checker.checkEmpty(routes);
		Checker.checkNullElements(routes);
		if(_server.isRunning()) {
			throw new IllegalStateException("server is already running");
		}
		ServletHolder holder = new ServletHolder(new EndpointServlet(endpoint, _settings.database()));
		for(String route : routes) {
			_root.addServlet(holder, route);
		}
	}

	public void run() throws Exception {
		_server.start();
		_server.join();
	}
}
