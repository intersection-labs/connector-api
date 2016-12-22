package io.unequal.reuse.http;
import java.io.IOException;
import java.io.PrintWriter;
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
	private final Settings _settings;
	private final ServletContextHandler _root;

	public RestServer(Settings settings) {
		Checker.checkNull(settings);
		_settings = settings;
		_root = new ServletContextHandler(ServletContextHandler.NO_SECURITY | ServletContextHandler.NO_SESSIONS);
	}

	public void endpoint(Endpoint endpoint, String ... routes) {
		Checker.checkNull(endpoint);
		Checker.checkEmpty(routes);
		Checker.checkNullElements(routes);
		//TODO  Check is server is running
		
		ServletHolder holder = new ServletHolder(new EndpointServlet(endpoint));
		for(String route : routes) {
			_root.addServlet(holder, route);
		}
		/*
		_root.addServlet(new ServletHolder(new HttpServlet() {
			public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
				System.out.println("endpoint called");
				resp.setContentType("text/plain; charset=UTF-8");
				resp.setStatus(HttpServletResponse.SC_OK);
				PrintWriter out = resp.getWriter();
				out.println("Hello, World!");
				out.close();
			}
		}), routes[0]);
		*/
	}

	public void run() throws Exception {
		Server server = new Server(_settings.port());
		_root.setContextPath("/");
		_root.setErrorHandler(new ErrorHandler() {
			protected void generateAcceptableResponse(Request base, HttpServletRequest req, HttpServletResponse resp, int code, String message) throws IOException {
				if(code == 404) {
					base.setHandled(true);
					resp.setContentType("text/plain; charset=UTF-8");
					PrintWriter out = resp.getWriter();
					out.println("Not found");
					out.close();
				}
			}
		});
		server.setHandler(_root);
		server.start();
		server.join();
	}
}
