// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package im.connector.api.rest;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import io.unequal.reuse.data.Connection;
import io.unequal.reuse.http.JsonObject;
import io.unequal.reuse.http.Request;
import io.unequal.reuse.http.Response;
import io.unequal.reuse.http.ParameterValidationException;
import io.unequal.reuse.util.Util;
import static io.unequal.reuse.util.Util.*;
import im.connector.api.data.*;
import im.connector.api.data.Contacts.Status;



public class ContactsInviteEndpointV1 extends UserEndpoint {

	public void post(Request req, Session s, Response resp) throws Exception {
		// Fetch necessary parameters:
		JsonObject json = req.readDataAsJson(true);
		Long contactId = json.getLong("contact");
		List<Long> fields = json.getListOf("fields", Long.class);
		if(fields.isEmpty()) {
			throw new ParameterValidationException("fields");
		}
		List<UserField> toShare = new ArrayList<UserField>();		
		Connection c = resp.connection();
		for(Long fieldId : fields) {
			UserField uf = UserFields.get().find(fieldId, c);
			if(uf == null) {
				resp.sendError(StatusCodes.RESOURCE_NOT_FOUND, null, "field", "id");
				return;
			}
			toShare.add(uf);
		}
		// Retrieve contact entry:
		Contact contact = Contacts.get().find(contactId, c);
		// TODO embed these next calls into Contacts.get().findAndCheck(user, id), which
		// throws exceptions. Use the same method on /contacts/view/v1 as well.
		if(contact == null) {
			resp.sendError(StatusCodes.RESOURCE_NOT_FOUND, null, "contact", "id");
			return;
		}
		User me = s.user(c);
		if(!me.equals(contact.owner(c))) {
			resp.sendError(StatusCodes.ACCESS_NOT_ALLOWED, null, "contact");
			return;
		}
		// Find or create the corresponding user:
		boolean existingUser = false;
		User toInvite = _userFor(contact, c);
		if(toInvite == null) {
			// Create a new user account:
			toInvite = new User();
			toInvite.copyFrom(contact);
			toInvite.status(Users.Status.INVITED);
			Users.get().insert(toInvite, c);
		}
		else {
			existingUser = true;
			// Check that we are not already connected with this user:
			Contact connection = Contacts.get().connection(me, toInvite, c);
			if(connection != null) {
				JsonObject jContent = new JsonObject();
				jContent.put("status", connection.status().code());
				resp.sendError(StatusCodes.ALREADY_CONNECTED, jContent);
				return;
			}
		}
		// Associate contact with user:
		// TODO transaction
		contact.connection(toInvite);
		contact.copy(toInvite);
		contact.status(Status.INVITATION_SENT);
		Contacts.get().update(contact, c);
		// Share selected contacts with user:
		for(UserField uf : toShare) {
			SharedField sf = new SharedField(uf, me, toInvite);
			SharedFields.get().insert(sf, c);
		}
		// Create invitation:
		Invitation i = new Invitation(me, toInvite);
		Invitations.get().insert(i, c);
		if(existingUser) {
			// Send email message:
			final javax.mail.Session session = javax.mail.Session.getDefaultInstance(new Properties(), null);
			final Message msg = new MimeMessage(session);
			// TODO have a preferred email address
			final String myEmail = me.emails(c).list().get(0).value();
			msg.setFrom(new InternetAddress("listening@connector.im", me.fullName()+" via Connector"));
			final InternetAddress[] replyTo = new InternetAddress[] {new InternetAddress(myEmail, me.fullName())};
			msg.setReplyTo(replyTo);
			final String contactEmail =  contact.emails(c).list().get(0).value();			
			msg.setRecipient(Message.RecipientType.TO, new InternetAddress(contactEmail, contact.fullName()));
			msg.setSubject(x("{} shared contact details with you", me.firstName()));
			StringBuilder text = new StringBuilder();
			text.append(x("Hi {},\n", toInvite.firstName()));
			text.append(x("{} shared contact details with you via Connector.\n\n", me.fullName()));
			text.append(x("See {}'s contacts details here.\n\n", me.firstName()));
			text.append("Thanks,\n");
			text.append("Connector Team");
			msg.setText(text.toString());
			Transport.send(msg);
		}
		// Response:
		resp.setDisableCache();
		resp.sendOk();
	}
	
	private User _userFor(Contact contact, Connection c) {
		// Search by email address:
		Iterator<ContactField> emails = contact.emails(c).iterate();
		while(emails.hasNext()) {
			ContactField email = emails.next();
			User u = Users.get().with(email.value(), FieldType.EMAIL, c);
			if(u != null) {
				return u;
			}
		}
		// Search by phone number:
		Iterator<ContactField> phoneNumbers = contact.phoneNumbers(c).iterate();
		while(phoneNumbers.hasNext()) {
			ContactField number = phoneNumbers.next();
			User u = Users.get().with(number.value(), FieldType.PHONE, c);
			if(u != null) {
				if(Util.equals(contact.firstName(), u.firstName())) {
					return u;
				}
			}
		}
		return null;
	}
}
