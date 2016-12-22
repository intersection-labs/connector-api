package io.unequal.reuse.http;

public abstract class Endpoint {
	
	public void get(Request req, Response resp) throws Exception {
		throw new MethodNotAllowedException(Request.HttpMethod.GET);
	}

	public void post(Request req, Response resp) throws Exception {
		throw new MethodNotAllowedException(Request.HttpMethod.POST);
	}
}
