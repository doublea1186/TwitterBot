import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

/**
 * A Markov Chain is a data structure that tracks the frequency with which one
 * value follows another value in sequence. This project uses a MarkovChain to
 * model tweets by gathering the frequency information from a Twitter feed. We
 * can use the MarkovChain to generate "plausible" tweets by conducting a random
 * walk through the chain according to the frequencies. Please see the homework
 * instructions for more information on Markov Chains.
 *
 * TRAINING:
 *
 * An example: Suppose we train the MarkovChain on these two Strings that
 * represent tweets: "a table" and "A banana? A banana!"
 * 
 * We first "clean up" the tweets and parse them into individual sentences to
 * use as training data. This process will remove punctuation and put all words
 * into lower case, yielding these three sentences (written using OCaml list
 * notation):
 * 
 * [["a"; "table"]; ["a"; "banana"]; ["a"; "banana"]]
 * 
 * The MarkovChain that results from this training data maps each observed
 * string to a ProbabilityDistribution that is based on the recorded occurrences
 * of bigrams (adjacent words) in the data:
 * 
 * - "a" maps to "table":1, "banana":2
 * 
 * - "table" maps to null:1
 * 
 * - "banana" maps to null:2
 * 
 * "a" is followed by "table" one time and "banana" twice, "table" is the end of
 * a sentence once, and "banana" is the end of a sentence twice. NOTE: we use
 * null to mark the end of a sentence.
 * 
 * The MarkovChain also records a ProbabilityDistribution that contains the
 * frequencies with which words start any sentence. In this case, that
 * startWords data will just say that "a" started 3 sentences.
 * 
 * GENERATING A TWEET:
 * 
 * Once we have trained the Markov model, we can use it to generate a tweet.
 * Given a desired length of tweet (in characters), we repeatedly generate
 * sentences until the tweet is long enough.
 * 
 * To generate a sentence, we treat the MarkovChain as an iterator that
 * maintains state about the current word (i.e. the one that will be generated
 * by next()).
 * 
 * - the reset() method picks (at random) one of the startWords to be the
 * current word. We use reset() to start a new sentence.
 * 
 * - the next() method picks (at random) a successor of the current word
 * according to the current word's probability distribution. That successor will
 * be the new "current" word after the current one is returned by next().
 * 
 * In the example above, reset() sets the current word to "a" (the only choice
 * offered by startWord). Then: next(); // yields "a" (the start word) with
 * probability 3/3 next(); // yields "table" with probability 1/3 and "banana"
 * with probability "2/3" then the iterator is finished (the current word will
 * be null), since both "table" and "banana" appeared only at the end of
 * sentences.
 * 
 * The random choices are determined by a NumberGenerator.
 *
 */
public class MarkovChain implements Iterator<String> {
	final NumberGenerator ng;
	final Map<String, ProbabilityDistribution<String>> chain;
	final ProbabilityDistribution<String> startWords;


	// TODO: add field(s) used in implementing the Iterator functionality
	String next;
	/**
	 * No need to write any constructors. They are provided for you.
	 */
	public MarkovChain() {
		this(new RandomNumberGenerator());
	}

	/**
	 * No need to write any constructors. They are provided for you.
	 *
	 * @param ng - A (non-null) NumberGenerator used to walk through the MarkovChain
	 */
	public MarkovChain(NumberGenerator ng) {
		if (ng == null) {
			throw new IllegalArgumentException("NumberGenerator input cannot be null");
		}
		this.chain = new TreeMap<String, ProbabilityDistribution<String>>();
		this.ng = ng;
		this.startWords = new ProbabilityDistribution<String>();
		this.next = null;
		reset();
	}

	/**
	 * Adds a bigram to the Markov Chain dictionary. Note that the dictionary is a
	 * field called chain of type final Map<String,
	 * ProbabilityDistribution<String>>.
	 *
	 * @param first  - The first word of the Bigram (should not be null)
	 * @param second - The second word of the Bigram
	 * @throws IllegalArgumentException if the first parameter is null.
	 */
	void addBigram(String first, String second) {
		// TODO: Complete this method.
		if (first == null) {
			throw new IllegalArgumentException("First parameter can't be null");
		} else {
			if (chain.containsKey(first)) {
				ProbabilityDistribution<String> x = chain.get(first);
				x.record(second);
				chain.put(first, x);
			}
			else {
				ProbabilityDistribution<String> x = new ProbabilityDistribution<String>();
				x.record(second);
				chain.put(first, x);
			}
		}
	}

	/**
	 * Adds a sentence's training data to the MarkovChain frequency information.
	 *
	 * This method is meant to be called multiple times. Each call to this method
	 * should provide this method with an Iterator that represents one sentence. If
	 * we were to train a Markov Chain with four unique sentences, we would convert
	 * each sentence into an iterator and call train() four times, once on each of
	 * the iterators.
	 *
	 * Look at the homework instructions for more details on bigrams. You should use
	 * the addBigram() method, provided below, in this method.
	 *
	 * Once you reach the last word of a sentence, add a bigram of that word and
	 * null. This will teach the Markov Chain that that word can be used to end a
	 * sentence.
	 *
	 * Do nothing if the sentence is empty.
	 *
	 * @param sentence - an iterator representing one sentence of training data
	 * @throws IllegalArgumentException if the sentence Iterator is null
	 */
	public void train(Iterator<String> sentence) {
		// TODO: Complete this method.
		if (sentence == null) {
			throw new IllegalArgumentException("Iterator is null");
		} else if (sentence.hasNext()) {
			String current = sentence.next();
			
			if (!current.isEmpty()) {
				startWords.record(current);
			}
			
			while(sentence.hasNext()) {
				String word = sentence.next();
				if (!word.isEmpty() && !current.isEmpty()) {
				addBigram(current, word);
				current = word;
				}
			}
				if (!current.isEmpty()) {
				addBigram(current, null);
				}
		}
	}

	/**
	 * Returns the ProbabilityDistribution for a given token. Returns null if none
	 * exists.
	 *
	 * @param token - the token for which the ProbabilityDistribution is sought
	 * @return a ProbabilityDistribution or null
	 */
	ProbabilityDistribution<String> get(String token) {
		return chain.get(token);
	}

	/**
	 * Given a starting String, sets up the Iterator functionality such that: (1)
	 * the Markov Chain will begin a walk at start. (2) the first call to next()
	 * made after calling reset(start) will return start.
	 *
	 * If start is null, then hasNext() should return false. start need not actually
	 * be a part of the chain (but it should have no successor).
	 *
	 * @param start - the element that will be the first word in a walk on the
	 *              Markov Chain.
	 */
	public void reset(String start) {
		// TODO: Complete this method.
		next = start;
	}

	/**
	 * DO NOT EDIT THIS METHOD. WE COMPLETED IT FOR YOU.
	 *
	 * Sets up the Iterator functionality with a random start word such that the
	 * MarkovChain will now move along a walk beginning with that start word.
	 *
	 * The first call to next() after calling reset() will return the random start
	 * word selected by this call to reset().
	 */
	public void reset() {
		if (startWords.getTotal() == 0) {
			reset(null);
		} else {
			reset(startWords.pick(ng));
		}
	}

	/**
	 * This method should check if there is another word to retrieve from the Markov
	 * Chain based on the current word of our walk.
	 *
	 * Your solution should be very short.
	 *
	 * @return true if next() will return a String and false otherwise
	 */
	@Override
	public boolean hasNext() {
		return next != null; // TODO: Complete this method.
	}

	/**
	 * Returns either: (1) the most recent word passed to reset(word), or (2) a
	 * successor picked from the probability distribution associated with the word
	 * most recently returned by next()
	 * 
	 * @return the next word in the MarkovChain (chosen at random via the number
	 *         generator if it is a successor)
	 * @throws NoSuchElementException if there are no more words on the walk through
	 *                                the chain.
	 */
	@Override
	public String next() {
		String current = this.next;

		if (!hasNext()) {
			throw new NoSuchElementException("no more words on walk");
		} else {

			ProbabilityDistribution<String> next = chain.get(current);

			if (next == null) {
				this.next = null;
			} else {
				this.next = next.pick(ng);
			}

		}
		return current; // TODO: Complete this method.
	}
}
