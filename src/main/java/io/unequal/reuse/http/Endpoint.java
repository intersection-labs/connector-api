package io.unequal.reuse.http;


public abstract class Endpoint {
	
	protected Endpoint() {
	}

	public void get(Request req, Context ctx, Response resp) throws Exception {
		throw new MethodNotAllowedException(Request.HttpMethod.GET);
	}

	public void post(Request req, Context ctx, Response resp) throws Exception {
		throw new MethodNotAllowedException(Request.HttpMethod.POST);
	}

	public void delete(Request req, Context ctx, Response resp) throws Exception {
		throw new MethodNotAllowedException(Request.HttpMethod.DELETE);
	}

	public void put(Request req, Context ctx, Response resp) throws Exception {
		throw new MethodNotAllowedException(Request.HttpMethod.PUT);
	}

	public void trace(Request req, Context ctx, Response resp) throws Exception {
		throw new MethodNotAllowedException(Request.HttpMethod.TRACE);
	}
}
