package io.unequal.reuse.data;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import io.unequal.reuse.util.Checker;
import io.unequal.reuse.util.IllegalUsageException;
import static io.unequal.reuse.util.Util.*;


public class Query<I extends Instance<?>> {

	// TYPE:
	public static enum Direction { ASC, DESC }

	// INSTANCE:
	private final Entity<I> _entity;
	private final List<Predicate> _predicates;
	private final List<Property<?>> _params;
	private final List<_Sort> _sort;
	private Integer _limit;
	private Integer _offset;
	private String _sql;

	public Query(Entity<I> entity) {
		Checker.checkNull(entity);
		_entity = entity;
		_predicates = new ArrayList<>();
		_params = new ArrayList<>();
		_sort = new ArrayList<>();
		_limit = -1;
		_offset = -1;
		_sql = null;
	}
	
	public Query<I> where(Predicate ... where) {
		_checkLocked();
		for(Predicate p : where) {
			// TODO check for repeated
			if(p.getProperty().getEntity() != _entity) {
				throw new IllegalArgumentException(x("property '{}' cannot be used to query entity '{}'", p.getProperty().getFullName(), _entity.getName()));
			}
			_predicates.add(p);
			if(p.isParameter()) {
				_params.add(p.getProperty());
			}
		}
		return this;
	}

	public Query<I> orderByAsc(Property<?> p) {
		return _orderBy(p, Direction.ASC);
	}

	public Query<I> orderByDesc(Property<?> p) {
		return _orderBy(p, Direction.DESC);
	}

	private Query<I> _orderBy(Property<?> p, Direction direction) {
		Checker.checkNull(p);
		if(p.getEntity() != _entity) {
			throw new IllegalArgumentException(x("property {} cannot be used to sort entity {}", p.getFullName(), _entity.getName()));
		}
		_sort.add(new _Sort(p, direction));
		return this;
	}

	public Query<I> limit(Integer limit) {
		if(limit == null) {
			_limit = null;
		}
		else {
			Checker.checkMinValue(limit, 1);
			_limit = limit;
		}
		return this;
	}

	public Integer limit() {
		return _limit;
	}

	public Query<I> offset(Integer offset) {
		if(offset == null) {
			_offset = null;
		}
		else {
			Checker.checkMinValue(offset, 1);
			_offset = offset;
		}
		return this;
	}
	
	public Integer offset() {
		return _offset;
	}

	// TODO return an immutable list instead
	public Property<?>[] params() {
		return _params.toArray(new Property<?>[0]);
	}

	public String sql() {
		if(_sql == null) {
			StringBuilder sb = new StringBuilder();
			sb.append("SELECT ");
			Iterator<Property<?>> itProps = _entity.propertyList().iterator();
			while(itProps.hasNext()) {
				sb.append(itProps.next().getColumnName());
				if(itProps.hasNext()) {
					sb.append(",");
				}
			}
			sb.append(" FROM ").append(_entity.getTableName()).append(" WHERE ");
			Iterator<Predicate> itArgs = _predicates.iterator();
			while(itArgs.hasNext()) {
				sb.append(itArgs.next().sql());
				if(itArgs.hasNext()) {
					sb.append(" AND ");
				}
			}
			// TODO order by
			// TODO limit and offset
			_sql = sb.toString();
		}
		return _sql;
	}
	
	public String toString() {
		return sql();
	}

	// For Connection:
	Entity<I> entity() {
		return _entity;
	}
	
	// For Connection:
	Class<I> type() {
		return _entity.getInstanceClass();
	}

	private void _checkLocked() {
		if(_sql != null) {
			throw new IllegalUsageException("query cannot be modified after being used");
		}
	}

	private class _Sort {
		public final Property<?> property;
		public final Direction direction;
		public _Sort(Property<?> p, Direction d) {
			property = p;
			direction = d;
		}
	}
}
