package io.unequal.reuse.http;


public class ParameterValidationException extends EndpointException {

	public ParameterValidationException(String parameterName, Object value) {
		super(StatusCodes.INVALID_PARAM, parameterName, value);
	}	

	public ParameterValidationException(String parameterName) {
		super(StatusCodes.PARAM_NOT_FOUND, parameterName);
	}	

	private static final long serialVersionUID = 1L;
}
