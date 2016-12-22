package io.unequal.reuse.http;
import java.io.IOException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;


public interface Request extends HttpServletRequest {

	public static enum HttpMethod {GET, POST, PUT, DELETE, HEAD, OPTIONS, TRACE};

	// TODO make a note in the documentation that cookies sent by the browser only contain name=value
	// in the header, since the browser only returns cookies visible to the requesting server resource.
	// So a method getCookie(name, path) does not make sense for example.
	public Cookie getCookie(String name);

	public String		getParameter(String name);
	public String		getParameter(String name, boolean required);
	public String		getParameter(String name, String def, String ... allowed);
	public Integer		getIntegerParameter(String name);
	public Integer		getIntegerParameter(String name, boolean required);
	public Integer		getIntegerParameter(String name, Integer def);
	public Long			getLongParameter(String name);
	public Long			getLongParameter(String name, boolean required);
	public Long			getLongParameter(String name, Long def);
	public Float		getFloatParameter(String name);
	public Float		getFloatParameter(String name, boolean required);
	public Float		getFloatParameter(String name, Float def);
	public Double		getDoubleParameter(String name);
	public Double		getDoubleParameter(String name, boolean required);
	public Double		getDoubleParameter(String name, Double def);
	public Boolean		getBooleanParameter(String name);
	public Boolean		getBooleanParameter(String name, boolean required);
	public JsonObject	readDataAsJson() throws IOException;
	public JsonObject	readDataAsJson(boolean required) throws IOException;
	
	public String getOrigin();
	public String getServerPath();
	public String getRequestPath();
	public String getApplicationName();
	public String getQueryString(boolean decode);
	public String getCompleteRequestURL();
	public String getCompleteRequestURL(boolean decode);
}
