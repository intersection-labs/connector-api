package io.unequal.reuse.http;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;


public interface Response extends HttpServletResponse {

	// TODO move somewhere else
	public static final String JSON = "application/json";		

	public boolean hasCookie(String name, String path);
	public void removeCookie(String name, String path, String domain);
	public void setContentType(String contentType);
	public void setLastModified(long timestamp);
	public void setDisableCache();
	public void setEnableCache();
	public void sendOk(JsonObject content) throws IOException;
	public void sendOk() throws IOException;
	public void sendError(StatusCode status, JsonObject content, Object ... params) throws IOException;
	public void sendError(StatusCode status) throws IOException;
	public void sendError(EndpointException e) throws IOException;
	public void setLenient(boolean lenient);
}
