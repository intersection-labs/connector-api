package im.connector.api.data;
import io.unequal.reuse.data.Entity;
import io.unequal.reuse.data.Query;
import io.unequal.reuse.data.Connection;
import io.unequal.reuse.data.Property;
import io.unequal.reuse.data.Property.Flag;


public class Links extends Entity<Link> {

	// TYPE:
	private final static class SingletonHolder {
		private final static Links instance = new Links();
	}

	public static Links get() {
		return SingletonHolder.instance;
	}

	// INSTANCE:
	// Properties:
	public final Property<String> linkId;
	public final Property<String> value;
	// Queries:
	private Query<Link> _withId = null;

	public Links() {
		super("links");
		linkId = property(String.class, "linkId", "link_id", Flag.MANDATORY, Flag.UNIQUE, Flag.READ_ONLY);
		value = property(String.class, "value", "value", Flag.MANDATORY);
	}

	public Property<?>[] naturalKey() {
		return new Property<?>[] { linkId };
	}

	public String withId(String id, Connection c) {
		if(_withId == null) {
			_withId = query().where(linkId.equalTo());
		}
		Link link = c.run(_withId, id).single();
		if(link == null) {
			return null;
		}
		return link.value();
	}	
}
