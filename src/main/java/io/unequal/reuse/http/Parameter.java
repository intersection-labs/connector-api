package io.unequal.reuse.http;

class Parameter {

	public static Integer toInteger(String paramName, String value) {
		try {
			return new Integer(value);
		}
		catch(NumberFormatException nfe) {
			throw new ParameterValidationException(paramName, value);
		}
	}

	public static Long toLong(String paramName, String value) {
		try {
			return new Long(value);
		}
		catch(NumberFormatException nfe) {
			throw new ParameterValidationException(paramName, value);
		}
	}

	public static Float toFloat(String paramName, String value) {
		try {
			return new Float(value);
		}
		catch(NumberFormatException nfe) {
			throw new ParameterValidationException(paramName, value);
		}
	}

	public static Double toDouble(String paramName, String value) {
		try {
			return new Double(value);
		}
		catch(NumberFormatException nfe) {
			throw new ParameterValidationException(paramName, value);
		}
	}

	public static Boolean toBoolean(String paramName, String value) {
		if("true".equalsIgnoreCase(value)) {
			return Boolean.TRUE;
		}
		if("false".equalsIgnoreCase(value)) {
			return Boolean.FALSE;
		}
		throw new ParameterValidationException(paramName, value);
	}
}
