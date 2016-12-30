// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package im.connector.api.rest;
import javax.servlet.http.HttpServletResponse;
import io.unequal.reuse.http.StatusCode;


public class StatusCodes extends io.unequal.reuse.http.StatusCodes {

	public static final StatusCode NOT_AUTHENTICATED = add(1001, "user is not authenticated", HttpServletResponse.SC_UNAUTHORIZED);
	// TODO code available
	public static final StatusCode EMAIL_NOT_FOUND = add(1003, "the email address was not found", HttpServletResponse.SC_FORBIDDEN);
	public static final StatusCode NO_PHOTO = add(1004, "{} does not have a photo", HttpServletResponse.SC_NOT_FOUND);
	public static final StatusCode ALREADY_CONNECTED = add(1005, "Already connected with this contact", HttpServletResponse.SC_FORBIDDEN);
}
