import java.util.*;
import java.util.Map.Entry;

/**
 * This class represents a probability distribution over a type T as a map from
 * T values to integers. We can think of this map as a "histogram" of the
 * frequency of occurrences observed for T values.
 * 
 * We can build a probability distribution by "recording" new occurrences of a
 * value. For instance, if we want to build a probability distribution over
 * String values, we might record the following occurrences: record("a");
 * record("b"); record("a"); record("a"); record("c")
 * 
 * The resulting distribution would map "a" to a frequency count of 3, and "b"
 * and "c" both to 1, since we recorded only one of each.
 * 
 * We can sample (a.k.a. "pick") an element from the probability distribution at
 * random, based on the frequency information in the records.
 * 
 * For instance, given the distribution above, we would pick "a" with 3/5
 * probability and each of "b" and "c" with 1/5 probability. Of the 5 total
 * observations, 3 were "a". (Assuming that the number generator is truly random
 * -- a non-random number generator, which is useful for testing, might yield
 * different outcomes.)
 */
class ProbabilityDistribution<T extends Comparable<T>> {

	private final Map<T, Integer> records;
	private Integer total = 0;

	public ProbabilityDistribution() {
		this.records = new HashMap<T, Integer>();
	}

	/**
	 * Total number of instances that have been added via record().
	 * 
	 * @return an int representing the number of records in the
	 *         ProbabilityDistribution.
	 */
	public int getTotal() {
		return total;
	}

	/**
	 * @return an EntrySet representation of the internal Map.
	 */
	public Set<Entry<T, Integer>> getEntrySet() {
		// Copy constructor so records cannot be modified externally.
		return new HashSet<Entry<T, Integer>>(records.entrySet());
	}

	/**
	 * @return a copy of the ProbabilityDistribution's internal Map
	 */
	public Map<T, Integer> getRecords() {
		// Copy constructor so records cannot be modified externally.
		return new HashMap<T, Integer>(records);
	}

	/**
	 * Picks an instance of the ProbabilityDistribution according to the provided
	 * NumberGenerator. In the provided NumberGenerator is random, then the
	 * resulting T values are chosen with probability proportional to the frequency
	 * that they have been recorded.
	 * 
	 * @param generator - uses the generator to pick a particular element in the
	 *                  ProbabilityDistribution.
	 * @return the chosen element of the ProbabilityDistribution
	 * 
	 * @throws IllegalArgumentException if a number recieved from the generator is less than zero
	 *                                  or greater than the total number of records in the PD
	 */
	public T pick(NumberGenerator generator) {
		return this.pick(generator.next(total));
	}

	/**
	 * Picks an instance of the ProbabilityDistribution non-randomly according to
	 * the provided index.
	 * 
	 * @param index - use this to pick a particular element in the
	 *              ProbabilityDistribution. Must not be more than the number of
	 *              elements in the ProbabilityDistribution.
	 * @return the chosen element of the ProbabilityDistribution
	 * @throws IllegalArgumentException if index is less than zero or greater than
	 *                                  the total number of records in the PD
	 */
	public T pick(int index) {
		if (index >= total || index < 0)
			throw new IllegalArgumentException(
					"Index has to be less than or equal to the total " + "number of records in the PD");
		int currentIndex = 0;
		List<T> rs = new ArrayList<T>(total);
		rs.addAll(records.keySet());
		Collections.sort(rs, new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {
				return o1 == null && o2 == null ? 0 : o1 == null ? -1 : o2 == null ? 1 : o1.compareTo(o2);
			}
		});
		for (T key : rs) {
			int currentCount = records.get(key);
			if (currentIndex + currentCount > index) {
				return key;
			}
			currentIndex += currentCount;
		}
		throw new RuntimeException(
				"Error in ProbabilityDistribution. Make sure to only add new records through record().");
	}
	/**
	 * Add an instance to the ProbabilityDistribution. If the element already exists
	 * in the ProbabilityDistribution, it will increment the number of occurrences
	 * of that element.
	 * 
	 * @param word
	 */
	public void record(T t) {
		records.putIfAbsent(t, 0);
		records.put(t, records.get(t) + 1);
		total++;
	}

	/**
	 * Counts the number of occurrences of an element in the ProbabilityDistribution
	 * 
	 * @param t - the element you want to get the count of
	 * @return the number of occurences of the provided element in the
	 *         ProbabilityDistribution
	 */
	public int count(T t) {
		Integer count = records.get(t);
		return count != null ? count.intValue() : 0;
	}

	/**
	 * @return a set containing all of the elements in the ProbabilityDistribution
	 */
	public Set<T> keySet() {
		return records.keySet();
	}
}
