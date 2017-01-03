package im.connector.api.data;
import io.unequal.reuse.data.Instance;
import io.unequal.reuse.data.QueryResult;

// TODO make this a superclass and merge with Common
public interface Person<F extends Instance<?>> {

	public String firstName();
	public Instance<?> firstName(String firstName);
	public String lastName();
	public Person<?> lastName(String lastName);
	public String fullName();
	public String organization();
	public Person<?> organization(String org);
	/*
	public QueryResult<F> findFields();
	public QueryResult<F> findEmails();
	public QueryResult<F> findPhoneNumbers();
	public QueryResult<F> findAddresses();
	public F findField(String value);
	public F findDeletedField(String value);
	*/
}
