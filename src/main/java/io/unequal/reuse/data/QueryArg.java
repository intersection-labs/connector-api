package io.unequal.reuse.data;
import io.unequal.reuse.util.IntegrityException;
import static io.unequal.reuse.util.Util.*;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;


public class QueryArg {

	// TYPE:
	public static enum Operator { EQUAL, NOT_EQUAL, GREATER_THAN, LESS_THAN };
	
	// INSTANCE:
	private final Property<?> _property;
	private final Object _value;
	private final Operator _op;
	
	// For Property:
	<T> QueryArg(Property<T> property, Object value, Operator op) {
		_property = property;
		_value = value;
		_op = op;
	}

	public Property<?> getProperty() {
		return _property;
	}
	
	public Object getValue() {
		return _value;
	}
	
	public Operator getOperator() {
		return _op;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(_property);
		sb.append(" ");
		sb.append(_op);
		sb.append(" ");
		sb.append(_value);
		sb.append("]");
		return sb.toString();
	}

	// For Facade:
	FilterPredicate toFilter() {
		return new FilterPredicate(_property.getName(), _getOperator(), _property.toPrimitive(_value));
	}
	
	private FilterOperator _getOperator() {
		if(_op == Operator.EQUAL) {
			return FilterOperator.EQUAL;
		}
		if(_op == Operator.NOT_EQUAL) {
			return FilterOperator.NOT_EQUAL;
		}
		if(_op == Operator.GREATER_THAN) {
			return FilterOperator.GREATER_THAN;
		}
		if(_op == Operator.LESS_THAN) {
			return FilterOperator.LESS_THAN;
		}
		throw new IntegrityException(_op);
	}
}
