import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * TweetParser.csvFileToTrainingData() takes in a CSV file that contains tweets
 * and iterates through the file, one tweet at a time, removing parts of the
 * tweets that would be bad inputs to MarkovChain (for example, a URL). It then
 * parses tweets into sentences and returns those sentences as lists of
 * cleaned-up words.
 *
 * Note: TweetParser's public methods are csvFileToTrainingData() and
 * getPunctuation(). These are the only methods that other classes should call. All
 * of the other methods provided are helper methods that build up the code
 * you'll need to write those public methods. They have "package" (default, no
 * modifier) visibility, which lets us write test cases for them as long as
 * those test cases are in the same package.
 *
 */
public class TweetParser {

	/**
	 * 
	 * Regular Expressions
	 * 
	 * For the purposes of this project, we consider "word characters" to be
	 * alpha-numeric characters [a-zA-Z0-9] and apostrophes [']. A word is "bad" if
	 * it contains some other character. (In particular, twitter mentions like
	 * "@user" are "bad".)
	 * 
	 * The regular expression BADWORD_REGEX expresses those constraints -- any
	 * String that matches it is considered "bad" and will be removed from the
	 * training data.
	 * 
	 * The regular expression "[\\W&&[^']]" matches non-word characters. The
	 * regular expression ".*" matches _any_ sequence of characters. When
	 * concatenated into the full regular expression, they match any sequence of
	 * characters followed by a non-word character followed again by any sequence of
	 * characters, or, any string containing a non-word character.
	 * 
	 * Similarly, the URL_REGEX matches any substring that starts a word with "http"
	 * and continues until some whitespace occurs. See the removeURLs static method.
	 * 
	 * See https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html
	 * for more details about Java's regular expressions.
	 * 
	 * tldr: use word.matches(BADWORD_REGEX) to determine if word is a bad String.
	 */
	private static final String BADWORD_REGEX = ".*[\\W&&[^']].*";
	private static final String URL_REGEX = "\\bhttp\\S*";

	/**
	 * Do not modify this method
	 * 
	 * Given a String, remove all substrings that look like a URL. Any word that
	 * begins with the character sequence 'http' is simply replaced with the empty
	 * string.
	 * 
	 * @param s - a String from which URL-like words should be removed
	 * @return s where each "URL-like" string has been deleted
	 */
	static String removeURLs(String s) {
		return s.replaceAll(URL_REGEX, "");
	}

	/**
	 * Do not modify this method.
	 * 
	 * Cleans a word by removing leading and trailing whitespace and converting it
	 * to lower case. If the word matches the BADWORD_REGEX or is the empty String,
	 * returns null instead.
	 * 
	 * @param word - a (non-null) String to clean
	 * @return - a trimmed, lowercase version of the word if it contains no illegal
	 *         characters and is not empty, and null otherwise.
	 */
	static String cleanWord(String word) {
		String cleaned = word.trim().toLowerCase();
		if (cleaned.matches(BADWORD_REGEX) || cleaned.isEmpty())
			return null;
		return cleaned;
	}

	/**
	 * Valid punctuation marks.
	 */
	private static final char[] puncs = new char[] { '.', '?', '!', ';' };

	/**
	 * 
	 * @return an array containing the punctuation marks used by the parser.
	 */
	public static char[] getPunctuation() {
		return puncs.clone();
	}

	/**
	 * Do not modify this method.
	 *
	 * Given a string, replaces all of the punctuation with periods.
	 *
	 * @param tweet - a String representing a tweet
	 * @return A String with all of the punctuation replaced with periods
	 */
	static String replacePunctuation(String tweet) {
		for (char c : puncs) {
			tweet = tweet.replace(c, '.');
		}
		return tweet;
	}

	/**
	 * Do not modify this method.
	 *
	 * Given a tweet, splits the tweet into sentences (without end punctuation) and
	 * inserts each sentence into a list.
	 *
	 * Use this as a helper function for parseAndCleanTweet().
	 *
	 * @param tweet - a String representing a tweet
	 * @return A List of Strings where each String is a (non-empty) sentence from
	 *         the tweet
	 */
	static List<String> sentenceSplit(String tweet) {
		List<String> sentences = new LinkedList<String>();
		for (String sentence : replacePunctuation(tweet).split("\\.")) {
			sentence = sentence.trim();
			if (!sentence.equals("")) {
				sentences.add(sentence);
			}
		}
		return sentences;
	}

	/**
	 * Given a String that represents a line extracted from a CSV file and an int
	 * that represents the column of the CSV file that we want to extract from,
	 * return the contents of that column from the String. Columns in the CSV file
	 * are zero indexed.
	 * 
	 * You may find the String.split() method useful here. Your solution should be
	 * relatively short.
	 * 
	 * You may assume that the column contents themselves don't have any commas.
	 *
	 * @param csvLine   - a line extracted from a CSV file
	 * @param csvColumn - the column of the line whose contents ought to be returned
	 * @return the portion of csvLine corresponding to the column of csvColumn. If
	 *         the csvLine is null or has no appropriate csvColumn, return null
	 */
	static String extractColumn(String csvLine, int csvColumn) {
		if (csvLine == null || csvLine.isEmpty()) {
			return null;
		}
		
		String[] words = csvLine.split(","); // TODO: Complete this method.

		try {
			String word = words[csvColumn];
			
			if (word.isEmpty()) {
				return null;
			}
			
			return word;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Splits a String representing a sentence into a sequence of words, filtering
	 * out any "bad" words from the sentence.
	 * 
	 * Hint: use the String split method and the cleanWord helper defined above.
	 * 
	 * @param sentence - a String representing one sentence from a tweet
	 * @return a (non-null) list of clean words in the order they appear in the
	 *         sentence. Any "bad" words are just dropped.
	 */
	static List<String> parseAndCleanSentence(String sentence) {
		String[] uncleanedWords = sentence.split(" ");
		ArrayList<String> cleanWords = new ArrayList<String>();
	
		for (String word: uncleanedWords) {
			String cleanword = cleanWord(word);
			if (cleanword != null && !cleanword.isEmpty()) {
				cleanWords.add(cleanword);
			}
		}
		return cleanWords;
	}

	/**
	 * Processes a tweet in to a list of sentences, where each sentence is itself a
	 * (non-empty) list of cleaned words. Before breaking up the tweet into
	 * sentences, this method uses removeURLs to sanitize the tweet.
	 * 
	 * Hint: use removeURLs followed by sentenceSplit and parseAndCleanSentence
	 * 
	 * @param tweet - a String that will be split into sentences, each of which is
	 *              cleaned as described above (assumed to be non-null)
	 * 
	 * @return a (non-null) list of sentences, each of which is a (non-empty)
	 *         sequence of clean words drawn from the tweet.
	 */
	static List<List<String>> parseAndCleanTweet(String tweet) {
		ArrayList<List<String>> cleanTweet = new ArrayList<List<String>>();
		String urlFreeTweet = removeURLs(tweet);
		List<String> sentences = sentenceSplit(urlFreeTweet);
		
		for (String sentence: sentences) {
			List<String> cleanedSentence = parseAndCleanSentence(sentence);
			if (cleanedSentence.size() > 0 && cleanedSentence != null) {
				cleanTweet.add(cleanedSentence);
			}
		}
		
		return cleanTweet;
	}

	/**
	 * Given the argument pathToFile and the column that the tweets are in, use the
	 * extractColumn and a FileIterator to extract every tweet from the CSV.
	 * (Recall that extractColumn returns null if there is no data at that column.)
	 * 
	 * @param pathToCSVFile - a String representing a path to a CSV file containing
	 *                      tweets
	 * @param tweetColumn   - the number of the column in the CSV file that contains
	 *                      the tweet
	 * @return a List of tweet Strings, none of which are null (but that are not yet
	 *         cleaned)
	 * 
	 * @throws IllegalArgumentException if pathToCSVFile is null or if the file
	 *                                  doesn't exist
	 */
	static List<String> csvFileToTweets(String pathToCSVFile, int tweetColumn) {
		ArrayList<String> tweets = new ArrayList<String>();
		try {
			FileLineIterator file = new FileLineIterator(pathToCSVFile);

			while (file.hasNext()) {
				String word = file.next();
				if (word.length() > 0 && word != null) {
					String extractedWord = extractColumn(word, tweetColumn);
					if (extractedWord.length() > 0 && extractedWord != null) {
						tweets.add(extractedWord);
					}
				}
			}
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("path to csv file is null or the file does not exist");
		}
		
		return tweets;
	}

	/**
	 * Given a path to a CSV file and the column from which to extract the tweet
	 * data, computes a training set. The training set is a list of sentences, each
	 * of which is a list of words. The sentences have been cleaned up by removing
	 * URLs and non-word characters, putting all words into lower case, and
	 * stripping out punctuation.
	 * 
	 * @param pathToCSVFile - a String representing a path to a CSV file containing
	 *                      tweets
	 * @param tweetColumn   - the number of the column in the CSV file that contains
	 *                      the tweet
	 * @return a list of training data examples
	 * 
	 * @throws IllegalArgumentException if pathToCSVFile is null or if the file
	 *                                  doesn't exist
	 */
	public static List<List<String>> csvFileToTrainingData(String pathToCSVFile, int tweetColumn) {
		ArrayList<List<String>> cleanTweets = new ArrayList<List<String>>();
		List<String> uncleanTweets = csvFileToTweets(pathToCSVFile, tweetColumn);
		
		for (String tweet: uncleanTweets) {
			List<List<String>> sentence = parseAndCleanTweet(tweet);
			if (sentence.size() > 0) {
				for (List<String> word: sentence) {
					if (word.size() > 0 && word != null) {
						cleanTweets.add(word);						
					}
				}
			}
		}
		
		return cleanTweets;
 }
	
}
