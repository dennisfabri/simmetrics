/*
 * SimMetrics - SimMetrics is a java library of Similarity or Distance Metrics,
 * e.g. Levenshtein Distance, that provide float based similarity measures
 * between String Data. All metrics return consistent measures rather than
 * unbounded similarity scores.
 * 
 * Copyright (C) 2014 SimMetrics authors
 * 
 * This file is part of SimMetrics. This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * SimMetrics. If not, see <http://www.gnu.org/licenses/>.
 */
package org.simmetrics.metrics;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Math.max;
import static org.simmetrics.utils.Math.max3;
import static org.simmetrics.utils.Math.min3;
import static org.simmetrics.utils.Math.min4;

import java.util.Objects;

import org.simmetrics.StringMetric;

/**
 * Damerau-Levenshtein algorithm providing a similarity measure between two
 * strings.
 * <p>
 * Insert/delete, substitute and transpose operations can be weighted.
 * <p>
 * This class is immutable and thread-safe.
 * 
 * @see <a
 *      href="https://en.wikipedia.org/wiki/Damerau%E2%80%93Levenshtein_distance">Wikipedia
 *      - Damerau-Levenshtein distance</a>
 * @see Levenshtein
 * 
 */
public class DamerauLevenshtein implements StringMetric {

	private final float maxCost;
	private final float insertDelete;
	private final float substitute;
	private final float transpose;

	/**
	 * Constructs a new Damerau-Levenshtein metric.
	 */
	public DamerauLevenshtein() {
		this(1.0f, 1.0f, 1.0f);
	}

	/**
	 * Constructs a new weighted Damerau-Levenshtein metric.
	 * 
	 * 
	 * @param insertDelete
	 *            positive non-zero cost of an insert or deletion operation
	 * @param substitute
	 *            positive non-zero cost of a substitute operation
	 * @param transpose
	 *            positive cost of a transpose operation
	 */
	public DamerauLevenshtein(float insertDelete, float substitute,
			float transpose) {
		checkArgument(insertDelete > 0);
		checkArgument(substitute > 0);
		checkArgument(transpose >= 0);

		this.maxCost = max3(insertDelete, substitute, transpose);
		this.insertDelete = insertDelete;
		this.substitute = substitute;
		this.transpose = transpose;
	}

	@Override
	public float compare(final String a, final String b) {
		if (a.isEmpty() && b.isEmpty()) {
			return 1.0f;
		}

		if (a.isEmpty() || b.isEmpty()) {
			return 0.0f;
		}

		return 1.0f - (distance(a, b) / (maxCost * max(a.length(), b.length())));
	}


	private float distance(final String s, final String t) {

		if (Objects.equals(s, t))
			return 0;
		if (s.isEmpty())
			return t.length() * insertDelete;
		if (t.isEmpty())
			return s.length() * insertDelete;

		final int tLength = t.length();
		final int sLength = s.length();

		float[] swap;
		float[] v0 = new float[tLength + 1];
		float[] v1 = new float[tLength + 1];
		float[] v2 = new float[tLength + 1];

		// initialize v1 (the previous row of distances)
		// this row is A[0][i]: edit distance for an empty s
		// the distance is just the number of characters to delete from t
		for (int i = 0; i < v1.length; i++) {
			v1[i] = i * insertDelete;
		}

		for (int i = 0; i < sLength; i++) {

			// first element of v2 is A[i+1][0]
			// edit distance is delete (i+1) chars from s to match empty t
			v2[0] = (i + 1) * insertDelete;

			for (int j = 0; j < tLength; j++) {
				if (j > 0 && i > 0 && s.charAt(i - 1) == t.charAt(j)
						&& s.charAt(i) == t.charAt(j - 1)) {
					v2[j + 1] = min4(v2[j] + insertDelete, v1[j + 1]
							+ insertDelete, v1[j]
							+ (s.charAt(i) == t.charAt(j) ? 0.0f : substitute),
							v0[j - 1] + transpose);
				} else {
					v2[j + 1] = min3(v2[j] + insertDelete, v1[j + 1]
							+ insertDelete, v1[j]
							+ (s.charAt(i) == t.charAt(j) ? 0.0f : substitute));
				}
			}

			swap = v0;
			v0 = v1;
			v1 = v2;
			v2 = swap;
		}

		// latest results was in v2 which was swapped to v1
		return v1[tLength];
	}

	@Override
	public String toString() {
		return "DamerauLevenshtein [insertDelete=" + insertDelete + ", substitute="
				+ substitute + "]";
	}
}