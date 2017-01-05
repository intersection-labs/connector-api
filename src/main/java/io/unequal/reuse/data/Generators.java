package io.unequal.reuse.data;
import java.util.Date;


public interface Generators {

	public class Direct<T> implements Generator<T> {
		
		private final T _value;
		
		public Direct(T value) {
			_value = value;
		}

		public T generate() {
			return _value;
		}
	}

	// TODO substitute for regexp generator
	public class Now implements Generator<Date> {
		public Date generate() { return new Date(); }
	}
}
