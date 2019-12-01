/**
 * This simple interface produces numbers. 
 * 
 * It is used in MarkovChain and ProbabilityDistribution with just
 * two implementations: RandomNumberGenerator and ListNumberGenerator,
 * the first of which is useful for producing random numbers and thus
 * is good for the bot in practice, the latter of which can be passed
 * a list for deterministic testing of the bot.
 *
 */
public interface NumberGenerator {
	public int next(int bound);
}