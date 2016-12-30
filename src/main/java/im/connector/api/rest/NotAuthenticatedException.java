// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact listening@connector.im.
package im.connector.api.rest;
import io.unequal.reuse.http.EndpointException;
import io.unequal.reuse.http.JsonObject;


public class NotAuthenticatedException extends EndpointException {

	public NotAuthenticatedException(JsonObject content) {
		super(StatusCodes.NOT_AUTHENTICATED, content);
	}

	private static final long serialVersionUID = 1L;
}
