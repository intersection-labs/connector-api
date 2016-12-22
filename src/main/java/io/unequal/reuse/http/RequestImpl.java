package io.unequal.reuse.http;
import java.net.URLDecoder;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import io.unequal.reuse.util.Arrays;
import io.unequal.reuse.util.Checker;
import io.unequal.reuse.util.IntegrityException;
import io.unequal.reuse.util.Strings;


public class RequestImpl extends HttpServletRequestWrapper implements Request {

	public RequestImpl(HttpServletRequest request) {
		super(request);
	}

	public Cookie getCookie(String name) {
		Checker.checkEmpty(name);
		Cookie[] cookies = getCookies();
		if(cookies == null) {
			return null;
		}
		for(int i=0; i<cookies.length; i++) {
			if(cookies[i].getName().equals(name)) {
				return cookies[i];					
			}
		}
		return null;
	}

	public String getOrigin() {
		String origin = getHeader("origin");
		if(origin == null) {
			return null;
		}
		// TODO do we really need this?
		// TODO where is actually the Origin header used?
		if(origin.startsWith("http://") || origin.startsWith("https://")) {
			return origin;
		}
		else {
			return "http://"+origin;
		}
	}

	public String getServerPath() {
		StringBuilder sb = new StringBuilder(getScheme());
		sb.append("://");
		sb.append(getServerName());
		int port = getServerPort();
		if(port != 80) {
			sb.append(":");
			sb.append(port);			
		}
		return sb.toString();
	}

	public String getRequestPath() {
		return getRequestURI().substring(getContextPath().length()+1);
	}

	public String getApplicationName() {
		String s = getContextPath();
		if(Strings.isEmpty(s)) {
			return s;
		}
		else {
			return getContextPath().substring(1);
		}
	}

	public String getQueryString(boolean decode) {
		if(decode) {
			String qs = super.getQueryString();
			if(qs == null) {
				return null;
			}
			else {
				try {
					return URLDecoder.decode(qs, Constants.CHARSET);
				}
				catch(UnsupportedEncodingException uee) {
					throw new IntegrityException(uee);
				}
			}
		}
		else {
			return super.getQueryString();
		}
	}

	public String getCompleteRequestURL(boolean decode) {
		String query = getQueryString(decode);
		return getRequestURL().append(query==null?"":"?"+query).toString();
	}

	public String getCompleteRequestURL() {
		return getCompleteRequestURL(false);
	}

	public String getParameter(String name, boolean required) {
		Checker.checkEmpty(name);
		String param = super.getParameter(name);
		param = Strings.isEmpty(param) ? null : param;
		if(required && param==null) {
			throw new ParameterValidationException(name);
		}
		return param;
	}
	
	public String getParameter(String name) {
		return getParameter(name, false);
	}

	public String getParameter(String name, String def, String ... allowed) {
		Checker.checkIllegalValue(def, (Object[])allowed);
		final String param = getParameter(name);
		if(param == null) {
			return def;
		}
		if(!Arrays.contains(param, allowed)) {
			throw new ParameterValidationException(name, param);
		}
		return param;
	}

	public Integer getIntegerParameter(String name, boolean required) {
		String param = getParameter(name, required);
		return param == null ? null : Parameter.toInteger(name, param);
	}

	public Integer getIntegerParameter(String name) {
		return getIntegerParameter(name, false);
	}

	public Integer getIntegerParameter(String name, Integer def) {
		final Integer value = getIntegerParameter(name);
		return value == null ? def : value;
	}

	public Long getLongParameter(String name, boolean required) {
		String param = getParameter(name, required);
		return param == null ? null : Parameter.toLong(name, param);
	}

	public Long getLongParameter(String name) {
		return getLongParameter(name, false);
	}

	public Long getLongParameter(String name, Long def) {
		final Long value = getLongParameter(name);
		return value == null ? def : value;
	}

	public Float getFloatParameter(String name, boolean required) {
		String param = getParameter(name, required);
		return param == null ? null : Parameter.toFloat(name, param);
	}

	public Float getFloatParameter(String name) {
		return getFloatParameter(name, false);
	}

	public Float getFloatParameter(String name, Float def) {
		final Float value = getFloatParameter(name);
		return value == null ? def : value;
	}

	public Double getDoubleParameter(String name, boolean required) {
		String param = getParameter(name, required);
		return param == null ? null : Parameter.toDouble(name, param);
	}

	public Double getDoubleParameter(String name) {
		return getDoubleParameter(name, false);
	}
	
	public Double getDoubleParameter(String name, Double def) {
		final Double value = getDoubleParameter(name);
		return value == null ? def : value;
	}

	public Boolean getBooleanParameter(String name, boolean required) {
		String param = getParameter(name, required);
		return param == null ? null : Parameter.toBoolean(name, param);
	}

	public Boolean getBooleanParameter(String name) {
		return getBooleanParameter(name, false);
	}

	public JsonObject readDataAsJson(boolean required) throws IOException {
		StringBuilder sb = new StringBuilder();
		String line = null;
	    BufferedReader reader = getReader();
	    while((line = reader.readLine()) != null) {
		      sb.append(line);
	    }
		if(sb.length() == 0) {
			if(required) {
				throw new ParameterValidationException("data");
			}
			return null;
		}
		return JsonObject.parse(URLDecoder.decode(sb.toString(), Constants.CHARSET));
	}

	public JsonObject readDataAsJson() throws IOException {
		return readDataAsJson(false);
	}
}
