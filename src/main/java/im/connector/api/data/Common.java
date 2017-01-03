package im.connector.api.data;
import io.unequal.reuse.util.Checker;


public class Common {

	public static String fullName(Person<?> p) {
		if(p.firstName() == null) {
			return p.lastName();
		}
		if(p.lastName() == null) {
			return p.firstName();
		}
		return p.firstName() + " " + p.lastName();
	}
	
	public static String description(Person<?> p) {
		String fullName = p.fullName();
		if(fullName != null) {
			return fullName;
		}
		return p.organization();
	}
	
	public static void copy(Person<?> from, Person<?> to) {
		Checker.nil(from);
		Checker.nil(to);
		to.firstName(from.firstName());
		to.lastName(from.lastName());
		to.organization(from.organization());
	}
}
