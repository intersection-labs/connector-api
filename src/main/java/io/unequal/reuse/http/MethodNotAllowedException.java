package io.unequal.reuse.http;


public class MethodNotAllowedException extends EndpointException {

	public MethodNotAllowedException(Request.HttpMethod method) {
		super(StatusCodes.METHOD_NOT_ALLOWED, method);
	}

	private static final long serialVersionUID = 1L;
}
