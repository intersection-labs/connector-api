package io.unequal.reuse.data;
import java.sql.Timestamp;
import java.time.Instant;


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
	public class Now implements Generator<Timestamp> {
		public Timestamp generate() { return Timestamp.from(Instant.now()); }
	}
}
