package im.connector.api.data;
import io.unequal.reuse.data.Constant;


public class FieldType extends Constant {
	
	// TYPE:
	public static FieldType EMAIL = new FieldType(100, "Email address");
	public static FieldType PHONE = new FieldType(200, "Phone number");
	public static FieldType ADDRESS = new FieldType(300, "Address");
	public static FieldType BIRTHDAY = new FieldType(400, "Birthday");
	public static FieldType DATE = new FieldType(500, "Date");

	// INSTANCE:
	private FieldType(int code, String description) {
		super(code, description);
	}
}
