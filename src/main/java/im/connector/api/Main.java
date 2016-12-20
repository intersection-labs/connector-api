package im.connector.api;
import java.sql.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.net.URI;
import java.net.URISyntaxException;
import static spark.Spark.*;
import spark.template.freemarker.FreeMarkerEngine;
import spark.ModelAndView;
import static spark.Spark.get;
import com.heroku.sdk.jdbc.DatabaseUrl;
import io.unequal.reuse.rest.Config;
import io.unequal.reuse.rest.Env;
import io.unequal.reuse.rest.JsonObject;



public class Main {
	
	public static void main(String[] args) {
		// Load config:
		Config config = new Config();
		config.load("PORT", Integer.class);
		config.load("APP_URL", String.class);
		config.load("ENV", Env.class);
		// Configure server:
		port((Integer)config.get("port"));
		staticFileLocation("/public");
		// Run endpoint:
		get("/config/check-version", (req, res) -> {
			String param = req.queryParams("version");
			if(param == null) {
				throw new RuntimeException("version not found");
			}
			int version = new Integer(param).intValue();
			if(version <= 0) {
				throw new RuntimeException("version <= 0");
			}
			java.io.StringWriter out = new java.io.StringWriter();
			JsonObject jResp = new JsonObject();
			jResp.put("supported", true);
			jResp.write(out);
			return out.toString();
		});
	}
}
