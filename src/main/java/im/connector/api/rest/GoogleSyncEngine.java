package im.connector.api.rest;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import com.google.gdata.client.Query;
import com.google.gdata.data.Link;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.contacts.Birthday;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.data.extensions.FamilyName;
import com.google.gdata.data.extensions.GivenName;
import com.google.gdata.data.extensions.Name;
import com.google.gdata.data.extensions.OrgName;
import com.google.gdata.data.extensions.Organization;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.util.ServiceException;
import io.unequal.reuse.data.Connection;
import io.unequal.reuse.data.QueryResult;
import io.unequal.reuse.util.Path;
import io.unequal.reuse.util.Util;
import static io.unequal.reuse.util.Util.x;
import im.connector.api.data.Accounts;
import im.connector.api.data.Account;
import im.connector.api.data.Contact;
import im.connector.api.data.ContactMapping;
import im.connector.api.data.ContactMappings;
import im.connector.api.data.Contacts;
import im.connector.api.data.ContactField;
import im.connector.api.data.ContactFields;
import im.connector.api.data.User;
import im.connector.api.data.FieldType;
import im.connector.api.data.SyncContactUpdates.Operation;
import im.connector.api.data.SyncContactUpdates.Result;
import im.connector.api.data.SyncContactUpdates.Side;
import im.connector.api.data.SyncFieldUpdates.Piece;



// For SyncEngine:
class GoogleSyncEngine extends SyncEngine {

	private Account _account;
	private User _user;
	private SyncTracker _tracker;
	private PrintWriter _out;
	private Connection _c;

	GoogleSyncEngine() {
	}

	// TODO all rejected updates need to result in the update being reverted in google
	// TODO what if we add a repeated contact in Google?
	// TODO need to process shared fields
	public void runFor(Account account, PrintWriter out, Connection c) throws IOException, ServiceException {
		_out = out;
		_c = c;
		_account = account;
		_user = account.user(c);
		_tracker = new SyncTracker(account, c);
		_tracker.start();
		out.println(x("Processing account '{}'...", account.describe(c)));
		// Prepare Google service:
		ContactsService service = GoogleService.serviceFor(account, c);
		// Prepare contacts for processing:
		Map<String,_ConnectorEntry> cEntries = new HashMap<>();
		Iterator<Contact> it = _user.contacts(c).iterate();
		QueryResult<ContactMapping> qr = ContactMappings.get().listFor(account, c);
		Map<Contact,ContactMapping> mappings = new HashMap<>();
		for(ContactMapping m : qr.list()) {
			mappings.put(m.contact(c), m);
		}
		while(it.hasNext()) {
			Contact contact = it.next();
			ContactMapping m = mappings.get(c);
			if(m == null) {
				// Contacts that have no mapping need to be added to the account:
				out.println(x("Adding new contact '{}'", contact.describe(c)));
				// TODO add contact entry to Google
			}
			else {
				_ConnectorEntry cEntry = new _ConnectorEntry(contact, m);
				cEntries.put(m.sourceId(), cEntry);
			}
		}
		// Retrieve all Google contacts modified after the last sync date:
		Query query = new Query(new URL(GoogleService.GET_ALL_CONTACTS_URL));
		query.setMaxResults(1000);
		Date lastSynced = _account.lastSyncTime();
		if(lastSynced != null) {
			query.setUpdatedMin(new DateTime(lastSynced));
		}
		query.setStringCustomParameter("showdeleted", "true");
		ContactFeed resultFeed = service.getFeed(query, ContactFeed.class);
		// Process Google's contact entries:
		for(ContactEntry gEntry : resultFeed.getEntries()) {
			String sourceId = new Path(gEntry.getId()).last();
			_ConnectorEntry cEntry = cEntries.remove(sourceId);
			out.println(x("Processing entry '{}'...", sourceId));
			if(gEntry.hasDeleted()) {
				if(cEntry != null) {
					if(cEntry.contact.status() != Contacts.Status.NOT_CONNECTED) {
						// Reject this update and add back to Google:
						// TODO add back to Google (by setting the deleted flag back to false, if possible
						out.println(x("Reverted deleting contact '{}'", cEntry.contact.describe(c)));
						_tracker.trackContactUpdate(cEntry.contact, Side.ACCOUNT, Operation.REMOVE, Result.REVERT);
					}
					else {
						cEntry.contact.active(false);
						Contacts.get().update(cEntry.contact, c);
						out.println(x("Deleted contact '{}'", cEntry.contact.describe(c)));
						_tracker.trackContactUpdate(cEntry.contact, Side.ACCOUNT, Operation.REMOVE, Result.ACCEPT);
					}					
					continue;
				}
			}
			if(cEntry == null) {
				// Create a new contact:
				out.print("Found a new contact in Google... ");
				Contact contact = new Contact(_user);
				// First & last name:
				Name gName = gEntry.getName();
				if(gName != null) {
					if(gName.hasGivenName()) {
						contact.firstName(gName.getGivenName().getValue());
					}
					if(gName.hasFamilyName()) {
						contact.lastName(gName.getFamilyName().getValue());
					}
				}
				// Organization:
				contact.organization(_getOrgFrom(gEntry));
				// Save the contact so that we can start adding fields:
				Contacts.get().insert(contact, c);
				// Emails:
				List<GoogleField> emails = GoogleField.fromEmails(gEntry.getEmailAddresses());
				for(GoogleField email : emails) {
					ContactField f = new ContactField(contact, FieldType.EMAIL, email.getLabel(), email.getValue());
					ContactFields.get().insert(f, c);
				}
				// Phone numbers:
				List<GoogleField> numbers = GoogleField.fromPhoneNumbers(gEntry.getPhoneNumbers());
				for(GoogleField number : numbers) {
					ContactField f = new ContactField(contact, FieldType.PHONE, number.getLabel(), number.getValue());
					ContactFields.get().insert(f, c);
				}
				// Addresses:
				List<GoogleField> addresses = GoogleField.fromAddresses(gEntry.getStructuredPostalAddresses());
				for(GoogleField address : addresses) {
					ContactField f = new ContactField(contact, FieldType.ADDRESS, address.getLabel(), address.getValue());
					ContactFields.get().insert(f, c);
				}
				// Birthday:
				Birthday bDay = gEntry.getBirthday();
				if(bDay != null) {
					ContactField f = new ContactField(contact, FieldType.BIRTHDAY, "Birthday", bDay.getWhen());
					ContactFields.get().insert(f, c);
				}
				// Mapping & photo URL:
				ContactMapping mapping = new ContactMapping(contact, _account, sourceId);
				Link photo = gEntry.getContactPhotoLink();
				if(photo.getEtag() != null) {
					mapping.photoUrl(photo.getHref());
				}
				ContactMappings.get().insert(mapping, c);
				out.println(x("created '{}'", contact.describe(c)));
				_tracker.trackContactUpdate(contact, Side.ACCOUNT, Operation.ADD, Result.ACCEPT);
				cEntry = new _ConnectorEntry(contact, mapping);
			}
			else {
				out.println(x("Found existing contact '{}', syncing...", cEntry.contact.describe(c)));
				// Sync contact level info:
				_syncEntries(service, gEntry, cEntry);
				out.println(x("Completed syncing '{}'", cEntry.contact.describe(c)));
			}
			out.println(x("Completed processing entry '{}' / '{}'", sourceId, cEntry.contact.describe(c)));
		}
		// Process Connector entries added or modified after the last sync:
		for(_ConnectorEntry cEntry : cEntries.values()) {
			if(cEntry.contact.timeUpdated().after(lastSynced)) {
				// Fetch contact from google:
				String url = x(GoogleService.GET_SINGLE_CONTACT_URL, _account.email(c).value(), cEntry.mapping.sourceId());
				ContactEntry gEntry = service.getEntry(new URL(url), ContactEntry.class);
				if(gEntry == null) {
					out.println(x("Contact {} does not exist in Google, adding...", cEntry.contact.describe(c)));
					// TODO add contact to Google
					_tracker.trackContactUpdate(cEntry.contact, Side.CONNECTOR, Operation.ADD, Result.ACCEPT);
				}
				else {
					out.println(x("Contact {} exists in Google, syncing...", cEntry.contact.describe(c)));
					_syncEntries(service, gEntry, cEntry);
				}
			}
		}
		// Save last sync date:
		_account.lastSyncTime(new Date());
		Accounts.get().update(account, c);
		_tracker.complete(account.lastSyncTime());
		out.println(x("Completed processing account {}.", _account.describe(c)));		
	}

	private void _syncEntries(ContactsService service, ContactEntry gEntry, _ConnectorEntry cEntry) throws IOException, ServiceException {
		boolean googleNeedsUpdate = false;
		boolean googleWins = _googleWins(gEntry, cEntry.contact.timeUpdated());
		if(googleWins) {
			_out.println("Google entry updated later, updating Connector...");
			// Name:
			Name gName = gEntry.getName();
			String firstAfter = gName == null ? null : (gName.hasGivenName() ? gName.getGivenName().getValue() : null);
			String lastAfter = gName == null ? null : (gName.hasFamilyName() ? gName.getFamilyName().getValue() : null);
			String fullAfter = new Contact().firstName(firstAfter).lastName(lastAfter).fullName();
			String fullBefore = cEntry.contact.fullName();
			if(!Util.equals(fullBefore, fullAfter)) {
				if(cEntry.contact.status() == Contacts.Status.NOT_CONNECTED) {
					cEntry.contact.firstName(firstAfter);
					cEntry.contact.lastName(lastAfter);
					if(fullAfter == null) {
						_tracker.trackFieldUpdate(cEntry.contact, Side.ACCOUNT, Operation.REMOVE, Result.ACCEPT, Piece.NAME, null, fullBefore, fullAfter);
						_out.println("Removed name");
					}
					else {
						_tracker.trackFieldUpdate(cEntry.contact, Side.ACCOUNT, Operation.UPDATE, Result.ACCEPT, Piece.NAME, null, fullBefore, fullAfter);
						_out.println(x("Updated name from '{}' to '{}'", fullBefore, fullAfter));
					}
				}
				else {
					if(fullAfter == null) {
						_tracker.trackFieldUpdate(cEntry.contact, Side.ACCOUNT, Operation.REMOVE, Result.REVERT, Piece.NAME, null, fullBefore, fullAfter);
						_out.println("Name removal reverted");
					}
					else {
						_tracker.trackFieldUpdate(cEntry.contact, Side.ACCOUNT, Operation.UPDATE, Result.REVERT, Piece.NAME, null, fullBefore, fullAfter);
						_out.println(x("Name update from '{}' to '{}' reverted", fullBefore, fullAfter));
					}
				}
			}
			// Organization name:
			String orgBefore = cEntry.contact.organization();
			String orgAfter = _getOrgFrom(gEntry);
			if(!Util.equals(orgBefore, orgAfter)) {
				if(cEntry.contact.status() == Contacts.Status.NOT_CONNECTED) {
					if(orgAfter == null) {
						_tracker.trackFieldUpdate(cEntry.contact, Side.ACCOUNT, Operation.REMOVE, Result.ACCEPT, Piece.ORG, null, orgBefore, orgAfter);
						_out.println("Removed organization");
					}
					else {
						_tracker.trackFieldUpdate(cEntry.contact, Side.ACCOUNT, Operation.UPDATE, Result.ACCEPT, Piece.ORG, null, orgBefore, orgAfter);
						_out.println(x("Updated organization from '{}' to '{}'", orgBefore, orgAfter));
					}
					cEntry.contact.organization(orgAfter);
				}
				else {
					if(orgAfter == null) {
						_tracker.trackFieldUpdate(cEntry.contact, Side.ACCOUNT, Operation.REMOVE, Result.REVERT, Piece.ORG, null, orgBefore, orgAfter);
						_out.println("Organization removal reverted");
					}
					else {
						_tracker.trackFieldUpdate(cEntry.contact, Side.ACCOUNT, Operation.UPDATE, Result.REVERT, Piece.ORG, null, orgBefore, orgAfter);
						_out.println(x("Organization update from '{}' to '{}' reverted", orgBefore, orgAfter));
					}
				}
			}
			// TODO photo URL
		}
		else {
			_out.println("Connector entry updated later, updating Google...");
			// Name:
			String fullAfter = cEntry.contact.fullName();
			Name gName = gEntry.getName();
			String firstBefore = gName == null ? null : (gName.hasGivenName() ? gName.getGivenName().getValue() : null);
			String lastBefore = gName == null ? null : (gName.hasFamilyName() ? gName.getFamilyName().getValue() : null);
			String fullBefore = new Contact().firstName(firstBefore).lastName(lastBefore).fullName();
			if(!Util.equals(fullBefore, fullAfter)) {
				if(fullAfter == null) {
					_tracker.trackFieldUpdate(cEntry.contact, Side.CONNECTOR, Operation.REMOVE, Result.ACCEPT, Piece.NAME, null, fullBefore, null);
					_out.println("Name removed");
				}
				else {
					_tracker.trackFieldUpdate(cEntry.contact, Side.CONNECTOR, Operation.UPDATE, Result.ACCEPT, Piece.NAME, null, fullBefore, fullAfter);
					_out.println(x("Name updated from '{}' to '{}'", fullBefore, fullAfter));
				}
				// Update google entry:
				if(gName == null) {
					gName = new Name();
				}
				GivenName first = new GivenName();
				first.setValue(cEntry.contact.firstName());
				gName.setGivenName(first);
				FamilyName last = new FamilyName();
				last.setValue(cEntry.contact.lastName());
				gName.setFamilyName(last);
				gEntry.setName(gName);
				googleNeedsUpdate = true;
			}
			// Organization name:
			String orgBefore = _getOrgFrom(gEntry);
			String orgAfter = cEntry.contact.organization();
			if(!Util.equals(orgBefore, orgAfter)) {
				if(orgAfter == null) {
					_tracker.trackFieldUpdate(cEntry.contact, Side.CONNECTOR, Operation.REMOVE, Result.ACCEPT, Piece.ORG, null, orgBefore, null);
					_out.println("Name removed");
					// Update Google entry:
					gEntry.getOrganizations().clear();
				}
				else {
					if(orgBefore == null) {
						_tracker.trackFieldUpdate(cEntry.contact, Side.CONNECTOR, Operation.ADD, Result.ACCEPT, Piece.ORG, null, null, orgAfter);
						_out.println(x("Added organization: '{}'", orgAfter));
					}
					else {
						_tracker.trackFieldUpdate(cEntry.contact, Side.CONNECTOR, Operation.UPDATE, Result.ACCEPT, Piece.ORG, null, orgBefore, orgAfter);
						_out.println(x("Organization updated from '{}' to '{}'", orgBefore, orgAfter));
					}
					// Update Google entry:
					Organization org = null;
					if(orgBefore != null) {
						for(Organization tmp : gEntry.getOrganizations()) {
							if(tmp.getOrgName().equals(orgBefore)) {
								org = tmp;
								break;
							}
						}
					}
					if(org == null) {
						org = new Organization();
						gEntry.getOrganizations().add(org);
					}
					OrgName name = new OrgName();
					name.setValue(orgAfter);
					org.setOrgName(name);
					org.setRel(Organization.Rel.WORK);
					org.setPrimary(true);
				}
				googleNeedsUpdate = true;
			}
		}
		// TODO photo
		// Update Connector entry:
		Contacts.get().update(cEntry.contact, _c);
		// Process fields:
		boolean result1 = _syncFields(FieldType.EMAIL, gEntry, GoogleField.fromEmails(gEntry.getEmailAddresses()), cEntry.contact, cEntry.contact.emails(_c));
		boolean result2 = _syncFields(FieldType.PHONE, gEntry, GoogleField.fromPhoneNumbers(gEntry.getPhoneNumbers()), cEntry.contact, cEntry.contact.phoneNumbers(_c));
		boolean result3 = _syncFields(FieldType.ADDRESS, gEntry, GoogleField.fromAddresses(gEntry.getStructuredPostalAddresses()), cEntry.contact, cEntry.contact.addresses(_c));
		if(result1 || result2 || result3) {
			googleNeedsUpdate = true;
		}
		// Process date fields:
		// TODO impl
		// Update the Google entry:
		if(googleNeedsUpdate) {
			_out.println(x("Sending updates to contact '{}' back to Google", cEntry.contact.describe(_c)));
			URL editURL = new URL(gEntry.getEditLink().getHref());
			service.update(editURL, gEntry);
		}
	}

	// TODO reject updates to, or additions of shared fields
	private boolean _syncFields(FieldType type, ContactEntry entry, List<GoogleField> gFields, Contact contact, QueryResult<ContactField> qr) {
		boolean googleNeedsUpdate = false;
		String typeName = type.description().toLowerCase();
		Map<String, ContactField> cFields = new HashMap<>();
		Iterator<ContactField> itFields = qr.iterate();
		while(itFields.hasNext()) {
			ContactField f = itFields.next();
			cFields.put(f.value(), f);
		}
		Iterator<GoogleField> gFieldsIt = gFields.iterator();
		while(gFieldsIt.hasNext()) {
			GoogleField gField = gFieldsIt.next();
			ContactField f = cFields.remove(gField.getValue());
			// Field exists in Google, but not in Connector:
			if(f == null) {
				boolean toDelete = false;
				List<ContactField> deleted = ContactFields.get().deletedWithValue(contact, gField.getValue(), _c).sortByDesc(ContactFields.get().timeUpdated).list();
				if(!deleted.isEmpty()) {
					NeedsDelete:
					for(ContactField df : deleted) {
						if(df.timeUpdated().after(new Date(entry.getUpdated().getValue()))) {
							toDelete = true;
							gFieldsIt.remove();
							googleNeedsUpdate = true;
							_out.println(x("Deleted {} '{}' with label '{}'", typeName, df.value(), df.label()));
							_tracker.trackFieldUpdate(contact, Side.CONNECTOR, Operation.REMOVE, Result.ACCEPT, Piece.FIELD, df, null, df.label());
							break NeedsDelete;
						}
					}
				}
				if(toDelete == false) {
					if(!deleted.isEmpty()) {
						// Reactivate the last instance found (sorted above):
						ContactField df = deleted.get(0);
						df.label(gField.getLabel());
						df.active(true);
						ContactFields.get().update(df, _c);
						_out.println(x("Re-added {} '{}' with label '{}'", typeName, df.value(), df.label()));
						_tracker.trackFieldUpdate(contact, Side.ACCOUNT, Operation.ADD, Result.ACCEPT, Piece.FIELD, df, null, df.label());
					}
					else {
						f = new ContactField(contact, type, gField.getLabel(), gField.getValue());
						ContactFields.get().insert(f, _c);
						_out.println(x("Added {} '{}' with label '{}'", typeName, f.value(), f.label()));
						_tracker.trackFieldUpdate(contact, Side.ACCOUNT, Operation.ADD, Result.ACCEPT, Piece.FIELD, f, null, f.label());
					}
				}
			}
			else {
				if(!Util.equals(f.label(), gField.getLabel())) {
					if(_googleWins(entry, f.timeUpdated())) {
						_out.println(x("Updated label from '{}' to '{}' on {} '{}'", f.label(), gField.getLabel(), typeName, f.value()));
						_tracker.trackFieldUpdate(contact, Side.ACCOUNT, Operation.UPDATE, Result.ACCEPT, Piece.LABEL, f, f.label(), gField.getLabel());
						f.label(gField.getLabel());
						ContactFields.get().update(f, _c);
					}
					else {
						_out.println(x("Updated label from '{}' to '{}' on {} '{}'", gField.getLabel(), f.label(), typeName, f.value()));
						_tracker.trackFieldUpdate(contact, Side.CONNECTOR, Operation.UPDATE, Result.ACCEPT, Piece.LABEL, f, gField.getLabel(), f.label());
						gField.setLabel(f.label());
						googleNeedsUpdate = true;
					}
				}
			}
		}
		// Process remaining fields:
		if(!cFields.isEmpty()) {
			for(ContactField f : cFields.values()) {
				if(_googleWins(entry, f.timeUpdated())) {
					f.active(false);
					ContactFields.get().update(f, _c);
					_out.println(x("Deleted {} '{}' with label '{}'", typeName, f.value(), f.label()));
					_tracker.trackFieldUpdate(contact, Side.ACCOUNT, Operation.REMOVE, Result.ACCEPT, Piece.FIELD, f, null, f.value());
				}
				else {
					GoogleField.add(type, entry, f.label(), f.value());
					_out.println(x("Added {} '{}' with label '{}'", typeName, f.value(), f.label()));							
					_tracker.trackFieldUpdate(contact, Side.CONNECTOR, Operation.ADD, Result.ACCEPT, Piece.FIELD, f, null, f.value());
					googleNeedsUpdate = true;
				}
			}
		}
		return googleNeedsUpdate;
	}
	
	private boolean _googleWins(ContactEntry entry, Date timestamp) {
		return timestamp.before(new Date(entry.getUpdated().getValue()));		
	}

	private String _getOrgFrom(ContactEntry gEntry) {
		List<Organization> orgs = gEntry.getOrganizations();
		// Find primary org with a name:
		for(Organization org : orgs) {
			if(org.getPrimary() && org.hasOrgName()) {
				return org.getOrgName().getValue();
			}
		}
		// Not found, find first org with a name:
		for(Organization org : orgs) {
			if(org.hasOrgName()) {
				return org.getOrgName().getValue();
			}
		}
		return null;
	}

	private class _ConnectorEntry {
		
		public final Contact contact;
		public final ContactMapping mapping;
		
		public _ConnectorEntry(Contact c, ContactMapping m) {
			contact = c;
			mapping = m;
		}
	}
}
