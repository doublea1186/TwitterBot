/* Tests for TwitterBot class */
import org.junit.*;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TwitterBotTest {

	String simpleData = "files/simple_test_data.csv";
	String testData = "files/test_data.csv";
	String randomData = "files/twitterbot_test_distinct.csv";
	String uncleanData = "files/unclean.csv";
	String emptyData = "files/empty.csv";
	
	// this tests whether your TwitterBot class itself is written correctly
	@Test
	public void simpleTwitterBotTest(){
		
		/* The first sentence starts with the second start word, 
		 * then always choose the first option.
		 * For a better understanding of how these indices work, 
		 * see the provided test in MarkovChainTest.java
		 */
		List<Integer> indices = new ArrayList<Integer>(Collections.nCopies(100, 0));
		indices.set(0, 1);
		
		ListNumberGenerator lng = new ListNumberGenerator(indices);
		TwitterBot t = new TwitterBot(simpleData, 1, lng);
		
		String expected = "this comes from data with no duplicate words. the end should come.";
		String actual = TweetParser.replacePunctuation(t.generateTweet(63));
		assertEquals(expected, actual);
	}

	/* **** ****** **** WRITE YOUR TESTS BELOW THIS LINE **** ****** **** */

	@Test 
	public void testEmpty() {
		List<Integer> indices = new ArrayList<Integer>(Collections.nCopies(100, 0));
		indices.set(0, 1);
		
		ListNumberGenerator lng = new ListNumberGenerator(indices);
		TwitterBot t = new TwitterBot(emptyData, 2, lng);
		assertEquals(t.generateTweet(10), "");
	}
	
	@Test
	public void testGenerateSimpleTweets() {
		List<Integer> indices = new ArrayList<Integer>(Collections.nCopies(100, 0));
		indices.set(0, 1);
		
		ListNumberGenerator lng = new ListNumberGenerator(indices);
		TwitterBot t = new TwitterBot(randomData, 2, lng);
		assertEquals("startone middleone endone; startfour middlefour endfour; startfour;", 
				t.generateTweet(60));
		assertEquals(t.generateTweet(1), "startfour;");
	}
	
	@Test
	public void testGenerateComplexTweets() {
		List<Integer> indices = new ArrayList<Integer>(Collections.nCopies(100, 0));
		indices.set(0, 1);
		
		ListNumberGenerator lng = new ListNumberGenerator(indices);
		TwitterBot t = new TwitterBot(uncleanData, 0, lng);
		t.mc.reset();
		assertEquals(t.generateTweet(10), "hello world;");
	}
	
}
