// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package im.connector.api.rest;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import io.unequal.reuse.http.JsonObject;
import io.unequal.reuse.http.Endpoint;
import io.unequal.reuse.http.Request;
import io.unequal.reuse.http.Response;


public class ToolsSendEmailEndpointV1 extends Endpoint {

	public void post(Request req, Response resp) throws IOException, MessagingException {
		JsonObject json = req.readDataAsJson(true);
		// TODO validate JSON format; validate email format; validate "type" = {"to", "cc", "bcc"}
		JsonObject message = json.getJsonObject("message");
		final String from = message.getString("from");
		final String subject = message.getString("subject");
		final String html = message.getString("html");
		Session session = Session.getDefaultInstance(new Properties(), null);
		Message msg = new MimeMessage(session);
		// TODO accept from name
		msg.setFrom(new InternetAddress(from, null));
		msg.setSubject(subject);
		msg.setText(html);
		List<JsonObject> recipients = message.getListOf("to", JsonObject.class);
		for(JsonObject recipient : recipients) {
			Message.RecipientType type = null;
			String typeString = recipient.getString("type");
			if(typeString.equalsIgnoreCase("to")) {
				type = Message.RecipientType.TO;
			}
			else if(typeString.equalsIgnoreCase("cc")) {
				type = Message.RecipientType.CC;
			}
			else if(typeString.equalsIgnoreCase("bcc")) {
				type = Message.RecipientType.BCC;
			}
			else {
				// TODO validation
				throw new IllegalArgumentException(typeString);
			}
			msg.addRecipient(type, new InternetAddress(recipient.getString("email"), recipient.getString("name")));
		}
		Transport.send(msg);
		resp.setDisableCache();
		resp.sendOk();
	}
}
