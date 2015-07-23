package org.simmetrics.metrics;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Iterator;
import java.util.List;

import org.simmetrics.Distance;

/**
 * Hamming Distance algorithm to calculate distance between lists and strings.
 *
 * <p>
 * This class is immutable and thread-safe.
 * 
 */
public final class HammingDistance {
	/**
	 * Hamming Distance algorithm to calculate distance between strings of equal
	 * length.
	 *
	 * <p>
	 * This class is immutable and thread-safe.
	 * 
	 */
	public static final class HammingStringDistance implements Distance<String> {

		HammingStringDistance() {
			// avoid synthetics
		}

		/**
		 * Measures the distance between strings {@code a} and {@code b} of
		 * equal length. The measurement results in a non-negative value. A
		 * value of {@code 0.0} indicates that {@code a} and {@code b} are
		 * similar.
		 * 
		 * @param a
		 *            string a to compare
		 * @param b
		 *            string b to compare
		 * @return a non-negative value
		 * @throws NullPointerException
		 *             when either a or b is null
		 * @throws IllegalArgumentException
		 *             when a and b differ in length
		 */
		@Override
		public float distance(String a, String b) {
			checkArgument(a.length() == b.length());

			if (a.isEmpty()) {
				return 0;
			}

			int distance = 0;
			for (int i = 0; i < a.length(); i++) {
				if (a.charAt(i) != b.charAt(i)) {
					distance++;
				}
			}
			return distance;
		}
	}

	/**
	 * Hamming Distance algorithm to calculate distance between lists of equal
	 * size.
	 *
	 * <p>
	 * This class is immutable and thread-safe.
	 * 
	 * @param <T>
	 *            type of the token
	 * 
	 */
	public static final class HammingListDistance<T> implements
			Distance<List<T>> {

		HammingListDistance() {
			// avoid synthetics
		}

		/**
		 * Measures the distance between lists {@code a} and {@code b} of equal
		 * size. The measurement results in a non-negative value. A value of
		 * {@code 0.0} indicates that {@code a} and {@code b} are similar.
		 * 
		 * @param a
		 *            list a to compare
		 * @param b
		 *            list b to compare
		 * @return a non-negative value
		 * @throws NullPointerException
		 *             when either a or b is null
		 * @throws IllegalArgumentException
		 *             when a and b differ in size
		 */
		@Override
		public float distance(List<T> a, List<T> b) {
			checkArgument(a.size() == b.size());

			if (a.isEmpty()) {
				return 0;
			}

			int distance = 0;

			Iterator<T> aItt = a.iterator();
			Iterator<T> bItt = b.iterator();

			while (aItt.hasNext() && bItt.hasNext()) {
				if (!aItt.next().equals(bItt.next())) {
					distance++;
				}
			}

			return distance;
		}
	}

	/**
	 * Constructs a new Hamming distance to compare strings.
	 * 
	 * @return a new Hamming distance to compare strings
	 */
	public static Distance<String> forString() {
		return new HammingStringDistance();
	}

	/**
	 * Constructs a new Hamming distance to compare lists.
	 * 
	 * @return a new Hamming distance to compare lists
	 */
	public static <T> Distance<List<T>> forList() {
		return new HammingListDistance<>();
	}
}