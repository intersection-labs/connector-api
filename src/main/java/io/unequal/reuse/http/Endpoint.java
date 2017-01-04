package io.unequal.reuse.http;
import java.util.logging.Logger;


public abstract class Endpoint {

	private final Logger _logger;

	protected Endpoint() {
		_logger = Logger.getLogger(getClass().getName());
	}
	
	protected Logger logger() {
		return _logger;
	}

	public void get(Request req, Response resp) throws Exception {
		throw new MethodNotAllowedException(Request.HttpMethod.GET);
	}

	public void post(Request req, Response resp) throws Exception {
		throw new MethodNotAllowedException(Request.HttpMethod.POST);
	}

	public void delete(Request req, Response resp) throws Exception {
		throw new MethodNotAllowedException(Request.HttpMethod.DELETE);
	}

	public void put(Request req, Response resp) throws Exception {
		throw new MethodNotAllowedException(Request.HttpMethod.PUT);
	}
}
