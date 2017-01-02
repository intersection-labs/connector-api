package io.unequal.reuse.data;
import io.unequal.reuse.util.IntegrityException;


public class Predicate {

	// TYPE:
	public static enum Operator { EQUAL, NOT_EQUAL, GREATER_THAN, LESS_THAN };
	public static final Object NULL = new Object();
	
	// INSTANCE:
	private final Property<?> _property;
	private final Object _value;
	private final Operator _op;
	private final String _sql;
	
	// For Property:
	<T> Predicate(Property<T> property, Operator op, Object value) {
		_property = property;
		_value = value;
		_op = op;
		StringBuilder sb = new StringBuilder();
		sb.append(_property.getColumnName());
		if(value == NULL) {
			if(_op == Operator.EQUAL) {
				sb.append("IS NULL");
			}
			else if(_op == Operator.NOT_EQUAL) {
				sb.append("IS NOT NULL");
			}
			else {
				throw new IntegrityException(_op);
			}
		}
		else {
			if(_op == Operator.EQUAL) {
				sb.append("=");
			}
			else if(_op == Operator.NOT_EQUAL) {
				sb.append("!=");
			}
			else if(_op == Operator.GREATER_THAN) {
				sb.append(">");
			}
			else if(_op == Operator.LESS_THAN) {
				sb.append("<");
			}
			else {
				throw new IntegrityException(_op);
			}
			if(value == null) {
				sb.append("?");
			}
			else {
				sb.append("'").append(_property.toPrimitive(value)).append("'");
			}
		}
		_sql = sb.toString();
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
	
	public boolean isParameter() {
		return _value == null;
	}
	
	public String sql() {
		return _sql;
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
}
