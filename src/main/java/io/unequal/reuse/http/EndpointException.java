package io.unequal.reuse.http;
import io.unequal.reuse.util.Checker;


public abstract class EndpointException extends RuntimeException {

	private final StatusCode _code;
	private final Object[] _parameters;
	private final JsonObject _content;
	
	protected EndpointException(StatusCode code, JsonObject content, Object ... parameters) {
		Checker.checkNull(code);
		if(code == StatusCodes.OK) {
			throw new IllegalArgumentException("status code OK cannot be used in an exception");
		}
		_code = code;
		_parameters = parameters;
		_content = content;
	}

	protected EndpointException(StatusCode code, Object ... parameters) {
		this(code, null, parameters);
	}

	public final String getMessage() {
		return _code.expand(_parameters);
	}
	
	public final StatusCode getErrorCode() {
		return _code;
	}
	
	public final JsonObject getContent() {
		return _content;
	}

	private static final long serialVersionUID = 1L;
}
