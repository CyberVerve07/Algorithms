import java.util.concurrent.TimeUnit;

public class TokenBucketDemo {
    public static void main(String[] args) throws InterruptedException {
        // Capacity of 5 tokens, refill rate of 2 tokens per second
        TokenBucket bucket = new TokenBucket(5, 2.0);

        System.out.println("Starting Token Bucket Demo...");
        System.out.println("Capacity: 5 tokens, Refill Rate: 2.0 tokens/sec\n");

        // Simulate burst requests
        System.out.println("--- Simultaneous Burst of 6 requests ---");
        for (int i = 1; i <= 6; i++) {
            boolean allowed = bucket.allowRequest(1);
            System.out.printf("Request %d: %s (Remaining: %.2f)%n", i,
                    allowed ? "ALLOWED" : "DENIED", bucket.getCurrentTokens());
        }

        // Wait for refill
        System.out.println("\n--- Waiting for 2 seconds (refill) ---");
        TimeUnit.SECONDS.sleep(2);

        // Run more requests
        System.out.println("\n--- 4 more requests after wait ---");
        for (int i = 7; i <= 10; i++) {
            boolean allowed = bucket.allowRequest(1);
            System.out.printf("Request %d: %s (Remaining: %.2f)%n", i,
                    allowed ? "ALLOWED" : "DENIED", bucket.getCurrentTokens());

            // Small delay between requests
            TimeUnit.MILLISECONDS.sleep(400);
        }
    }
}
