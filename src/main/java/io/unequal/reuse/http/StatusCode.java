package io.unequal.reuse.http;
import static io.unequal.reuse.util.Util.*;


public class StatusCode {

	public final int code;
	private final String _message;
	public final int httpCode;
	
	StatusCode(int code, String message, int httpStatus) {
		this.code = code;
		_message = message;
		this.httpCode = httpStatus;
	}
	
	public String expand(Object ... params) {
		if(params == null) {
			// TODO add a check. If message expects params, fail
			return _message;
		}
		return x(_message, params);
	}
	
	public String toString() {
		return Integer.toString(code);
	}
}
