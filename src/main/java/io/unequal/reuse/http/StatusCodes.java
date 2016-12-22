package io.unequal.reuse.http;
import java.util.Map;
import java.util.HashMap;
import javax.servlet.http.HttpServletResponse;
import static io.unequal.reuse.util.Util.*;


public class StatusCodes {

	private final static Map<Integer,StatusCode> _statusCodes = new HashMap<Integer,StatusCode>();
	
	protected static StatusCode add(int status, String message, int httpStatus) {
		if(_statusCodes.containsKey(status)) {
			throw new IllegalArgumentException(x("status code {} already exists", status));
		}
		StatusCode code = new StatusCode(status, message, httpStatus);
		_statusCodes.put(status, code);
		return code;
	}

	public static final StatusCode OK = add(0, "OK", HttpServletResponse.SC_OK);
	public static final StatusCode ENDPOINT_NOT_FOUND = add(1, "'{}': endpoint not found", HttpServletResponse.SC_NOT_FOUND);
	public static final StatusCode VERSION_DEPRECATED = add(2, "version {} has been deprecated", HttpServletResponse.SC_GONE);
	public static final StatusCode METHOD_NOT_ALLOWED = add(3, "{}: method not allowed", HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	public static final StatusCode PARAM_NOT_FOUND = add(4, "mandatory parameter '{}' not found", HttpServletResponse.SC_BAD_REQUEST);
	public static final StatusCode INVALID_PARAM = add(5, "unexpected value for parameter '{}': '{}'", HttpServletResponse.SC_BAD_REQUEST);
	public static final StatusCode RESOURCE_NOT_FOUND = add(6, "{} was not found: '{}'", HttpServletResponse.SC_NOT_FOUND);
	public static final StatusCode ACCESS_NOT_ALLOWED = add(7, "access to {} is not allowed", HttpServletResponse.SC_FORBIDDEN);
	public static final StatusCode UNEXPECTED = add(999, "unexpected error occurred: {}", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
}
