/* Tests for TweetParser */
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class TweetParserTest {

	// A helper function to create a singleton list from a word
	private static List<String> singleton(String word) {
		List<String> l = new LinkedList<String>();
		l.add(word);
		return l;
	}
	
	// A helper function for creating lists of strings
	private static List<String> listOfArray(String[] words) {
		List<String> l = new LinkedList<String>();
		for (String s : words) {
			l.add(s);
		}
		return l;
	}
	
	// Cleaning and filtering tests -------------------------------------------
		@Test
		public void removeURLsTest() {
			assertEquals("abc", TweetParser.removeURLs("abc"));
			assertEquals("abc ", TweetParser.removeURLs("abc http://www.cis.upenn.edu"));
			assertEquals(" abc ", TweetParser.removeURLs("http:// abc http:ala34?#?"));
			assertEquals(" abc  def", TweetParser.removeURLs("http:// abc http:ala34?#? def"));
			assertEquals(" abc  def", TweetParser.removeURLs("https:// abc https``\":ala34?#? def"));
			assertEquals("abchttp", TweetParser.removeURLs("abchttp"));
		}

		@Test
		public void testCleanWord() {
			assertEquals("abc", TweetParser.cleanWord("abc"));
			assertEquals("abc", TweetParser.cleanWord("ABC"));
			assertEquals(null, TweetParser.cleanWord("@abc"));
		}

		@Test
		public void testExtractColumnGetsCorrectColumn() {
			assertEquals(" This is a tweet.",
					TweetParser.extractColumn("wrongColumn, wrong column, wrong column!, This is a tweet.", 3));
		}
		
		@Test
		public void parseAndCleanSentenceNonEmptyFiltered() {
			List<String> sentence = TweetParser.parseAndCleanSentence("abc #@#F");
			List<String> expected = new LinkedList<String>();
			expected.add("abc");
			assertEquals(expected, sentence);
		}
		
		@Test
		public void testParseAndCleanTweetRemovesURLS1() {
			List<List<String>> sentences = TweetParser.parseAndCleanTweet("abc http://www.cis.upenn.edu");
			List<List<String>> expected = new LinkedList<List<String>>();
			expected.add(singleton("abc"));
			assertEquals(expected, sentences);
		}
		
		@Test
		public void testCsvFileToTrainingDataSimpleCSV() {
			List<List<String>> tweets = TweetParser.csvFileToTrainingData("files/simple_test_data.csv", 1);
			List<List<String>> expected = new LinkedList<List<String>>();
			expected.add(listOfArray("the end should come here".split(" ")));
			expected.add(listOfArray("this comes from data with no duplicate words".split(" ")));
			assertEquals(expected, tweets);
		}
		
		@Test
		public void testCsvFileToTweetsSimpleCSV() {
			List<String> tweets = TweetParser.csvFileToTweets("files/simple_test_data.csv", 1);
			List<String> expected = new LinkedList<String>();
			expected.add(" The end should come here.");
			expected.add(" This comes from data with no duplicate words!");		
			assertEquals(expected, tweets);
		}
		
  /* **** ****** **** WRITE YOUR TESTS BELOW THIS LINE **** ****** **** */

		@Test 
		public void extractColumnException() {
			assertEquals(null, TweetParser.extractColumn(",,,", 2));	
			assertEquals(null, TweetParser.extractColumn("", 7));
		}
		
		@Test
		public void parseAndCleanSentenceExceptions() {
			List<String> sentence = TweetParser.parseAndCleanSentence("... #@#F");
			List<String> expected = new ArrayList<String>();
			assertEquals(expected, sentence);
		}
		
		@Test
		public void parseAndCleanURLException() {
			List<List<String>> sentences = TweetParser.parseAndCleanTweet("abc http://www.cis.upenn.edu efg");
			List<List<String>> expected = new LinkedList<List<String>>();
			ArrayList<String> words = new ArrayList<String>();
			words.add("abc");
			words.add("efg");
			expected.add(words);
			assertEquals(expected, sentences);
			
			sentences = TweetParser.parseAndCleanTweet("http://www.cis.upenn.edu");
			expected = new LinkedList<List<String>>();
			
			assertEquals(expected, sentences);
		}
		
		@Test
		public void testCsvFileToTrainingDataException() {
			List<List<String>> tweets = TweetParser.csvFileToTrainingData("files/tweetparser_test.csv", 3);
			assertEquals(tweets.get(0), listOfArray("a tweet lives in this line".split(" ")));
			assertEquals(tweets.get(1), listOfArray("12345678910".split(" ")));
			assertEquals(tweets.get(2), listOfArray("banana".split(" ")));
			assertEquals(tweets.get(3), listOfArray("despicable me 2 coming out in theaters soon".split(" ")));
			assertEquals(tweets.get(4), listOfArray("banana".split(" ")));
			assertEquals(tweets.get(5), listOfArray("i will go to the zoo".split(" ")));
			assertEquals(tweets.get(6), listOfArray("there i will be happy".split(" ")));
		}
		
}
