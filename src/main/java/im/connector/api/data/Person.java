package im.connector.api.data;
import io.unequal.reuse.data.Instance;
import io.unequal.reuse.data.QueryResult;

// TODO make this a superclass and merge with Common
public interface Person<F extends Instance<?>> {

	public String getFirstName();
	public Instance<?> setFirstName(String firstName);
	public String getLastName();
	public Person<?> setLastName(String lastName);
	public String getFullName();
	public String getOrganization();
	public Person<?> setOrganization(String org);
	public QueryResult<F> findFields();
	public QueryResult<F> findEmails();
	public QueryResult<F> findPhoneNumbers();
	public QueryResult<F> findAddresses();
	public F findField(String value);
	public F findDeletedField(String value);
}
