/**
 * A thread-safe implementation of the Token Bucket algorithm for rate limiting.
 */
public class TokenBucket {
    private final long capacity;
    private final double refillRate; // tokens per second
    private double currentTokens;
    private long lastRefillTimestamp;

    /**
     * @param capacity   The maximum number of tokens the bucket can hold.
     * @param refillRate The rate at which tokens are added to the bucket (tokens per second).
     */
    public TokenBucket(long capacity, double refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
        this.currentTokens = capacity;
        this.lastRefillTimestamp = System.nanoTime();
    }

    /**
     * Attempts to consume the specified number of tokens from the bucket.
     *
     * @param tokens The number of tokens to consume.
     * @return true if tokens were successfully consumed, false otherwise.
     */
    public synchronized boolean allowRequest(int tokens) {
        refill();

        if (currentTokens >= tokens) {
            currentTokens -= tokens;
            return true;
        }

        return false;
    }

    /**
     * Refills the bucket with tokens based on the time elapsed since the last refill.
     */
    private void refill() {
        long now = System.nanoTime();
        double elapsedSeconds = (now - lastRefillTimestamp) / 1_000_000_000.0;
        
        double tokensToAdd = elapsedSeconds * refillRate;
        currentTokens = Math.min(capacity, currentTokens + tokensToAdd);
        lastRefillTimestamp = now;
    }

    public synchronized double getCurrentTokens() {
        refill();
        return currentTokens;
    }
}
