package im.connector.api;
import io.unequal.reuse.http.Endpoint;
import io.unequal.reuse.http.Request;
import io.unequal.reuse.http.Response;
import io.unequal.reuse.http.JsonObject;
import io.unequal.reuse.http.ParameterValidationException;


public class ConfigCheckVersionEndpointV1 extends Endpoint {

	public void get(Request req, Response resp) throws Exception {
		int version = req.getIntegerParameter("version", true);
		if(version <= 0) {
			throw new ParameterValidationException("version", version);
		}
		JsonObject jResp = new JsonObject();
		jResp.put("supported", true);
		resp.setDisableCache();
		resp.sendOk(jResp);
	}
}
