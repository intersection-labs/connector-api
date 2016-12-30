package im.connector.api.data;

import io.unequal.reuse.util.Checker;

public class Common {

	public static String getFullName(Person<?> p) {
		if(p.getFirstName() == null) {
			return p.getLastName();
		}
		if(p.getLastName() == null) {
			return p.getFirstName();
		}
		return p.getFirstName() + " " + p.getLastName();
	}
	
	public static String getDescription(Person<?> p) {
		String fullName = p.getFullName();
		if(fullName != null) {
			return fullName;
		}
		return p.getOrganization();
	}
	
	public static void copy(Person<?> from, Person<?> to) {
		Checker.checkNull(from);
		Checker.checkNull(to);
		to.setFirstName(from.getFirstName());
		to.setLastName(from.getLastName());
		to.setOrganization(from.getOrganization());
	}
}
