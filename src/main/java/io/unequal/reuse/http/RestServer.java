package io.unequal.reuse.http;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import io.unequal.reuse.util.Checker;


public class RestServer {

	private final Settings _settings;
	private final ServletContextHandler _root;

	public RestServer(Settings settings) {
		Checker.checkNull(settings);
		_settings = settings;
		_root = new ServletContextHandler(ServletContextHandler.NO_SECURITY | ServletContextHandler.NO_SESSIONS);
	}

	public void endpoint(Object o, String ... routes) {
		System.out.println(routes[0]);
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
	}

	public void run() throws Exception {
		Server server = new Server(_settings.port());
		_root.setContextPath("/");
		_root.setErrorHandler(new ErrorHandler() {
			protected void generateAcceptableResponse(Request base, HttpServletRequest req, HttpServletResponse resp, int code, String message) throws IOException {
				resp.setContentType("text/plain; charset=UTF-8");
				PrintWriter out = resp.getWriter();
				out.println("Not found");
				out.close();
				base.setHandled(true);
			}
		});
		server.setHandler(_root);
		server.start();
		server.join();
	}
}
