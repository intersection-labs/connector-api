package im.connector.api.rest;
import java.util.List;
import java.util.ArrayList;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.extensions.Email;
import com.google.gdata.data.extensions.PhoneNumber;
import com.google.gdata.data.extensions.StructuredPostalAddress;
import com.google.gdata.data.extensions.FormattedAddress;
import io.unequal.reuse.util.Checker;
import io.unequal.reuse.util.IntegrityException;
import im.connector.api.data.FieldType;


// For ContactsSyncServlet:
class GoogleField {

	// TYPE:
	public static final String REL_HOME = "http://schemas.google.com/g/2005#home";
	public static final String REL_WORK = "http://schemas.google.com/g/2005#work";
	public static final String REL_OTHER = "http://schemas.google.com/g/2005#other";
	public static final String REL_MOBILE = "http://schemas.google.com/g/2005#mobile";
	public static final String REL_MAIN = "http://schemas.google.com/g/2005#main";
	public static final String REL_PAGER = "http://schemas.google.com/g/2005#pager";
	
	public static List<GoogleField> fromEmails(List<Email> source) {
		List<GoogleField> list = new ArrayList<>(source.size());
		for(Email email : source) {
			list.add(new GoogleField(email));
		}
		return list;
	}
	
	public static List<GoogleField> fromPhoneNumbers(List<PhoneNumber> source) {
		List<GoogleField> list = new ArrayList<>(source.size());
		for(PhoneNumber number : source) {
			list.add(new GoogleField(number));
		}
		return list;
	}

	public static List<GoogleField> fromAddresses(List<StructuredPostalAddress> source) {
		List<GoogleField> list = new ArrayList<>(source.size());
		for(StructuredPostalAddress address : source) {
			if(!address.hasFormattedAddress()) {
				// TODO log a warning
				System.out.println("Found an address without a formatted address: "+address);
			}
			else {
				list.add(new GoogleField(address));
			}
		}
		return list;
	}

	public static void add(FieldType type, ContactEntry entry, String label, String value) {
		if(type == FieldType.EMAIL) {
			Email email = new Email();
			email.setLabel(label);
			email.setAddress(value);
			entry.addEmailAddress(email);
		}
		else if(type == FieldType.PHONE) {
			PhoneNumber number = new PhoneNumber();
			number.setLabel(label);
			number.setPhoneNumber(value);
			entry.addPhoneNumber(number);
		}
		else if(type == FieldType.ADDRESS) {
			StructuredPostalAddress address = new StructuredPostalAddress();
			address.setLabel(label);
			address.setFormattedAddress(new FormattedAddress(value));
		}
		else {
			throw new IntegrityException(type);
		}
	}

	// INSTANCE:
	private final Email _email;
	private final PhoneNumber _number;
	private final StructuredPostalAddress _address;
	
	public GoogleField(Email email) {
		Checker.nil(email);
		_email = email;
		_number = null;
		_address = null;
	}

	public GoogleField(PhoneNumber number) {
		Checker.nil(number);
		_email = null;
		_number = number;
		_address = null;
	}
	
	public GoogleField(StructuredPostalAddress address) {
		Checker.nil(address);
		_email = null;
		_number = null;
		_address = address;
	}
	
	public String getValue() {
		if(_email != null) {
			return _email.getAddress();
		}
		if(_number != null) {
			return _number.getPhoneNumber();
		}
		if(_address != null) {
			return _address.getFormattedAddress().getValue();
		}
		throw new IntegrityException();
	}

	public String getLabel() {
		if(_email != null) {
			String label = _email.getLabel();
			if(label != null) {
				return label;
			}
			return _translate(_email.getRel());
		}
		if(_number != null) {
			String label = _number.getLabel();
			if(label != null) {
				return label;
			}
			return _translate(_number.getRel());
		}
		if(_address != null) {
			String label = _address.getLabel();
			if(label != null) {
				return label;
			}
			return _translate(_address.getRel());
		}
		throw new IntegrityException();
	}
	
	public void setLabel(String label) {
		if(_email != null) {
			_email.setLabel(label);
		}
		else if(_number != null) {
			_number.setLabel(label);
		}
		else if(_address != null) {
			_address.setLabel(label);
		}
		else {
			throw new IntegrityException();
		}
	}
	
	private String _translate(String rel) {
		if(rel == null) {
			return null;
		}
		if(rel.equals(REL_HOME)) {
			return "Home";
		}
		if(rel.equals(REL_WORK)) {
			return "Work";
		}
		if(rel.equals(REL_OTHER)) {
			return "Other";
		}
		if(rel.equals(REL_MOBILE)) {
			return "Mobile";
		}
		if(rel.equals(REL_MAIN)) {
			return "Main";
		}
		if(rel.equals(REL_PAGER)) {
			return "Pager";
		}
		return null;
	}
}
