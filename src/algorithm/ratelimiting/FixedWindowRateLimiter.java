package algorithm.ratelimiting;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class FixedWindowRateLimiter {

    private final int windowSizeInSeconds; // e.g., 60 for 1 minute
    private final int capacity;           // e.g., 100 requests
    private final Map<String, AtomicInteger> userRequestCounts;
    private long windowStartTime;

    public FixedWindowRateLimiter(int windowSizeInSeconds, int capacity) {
        this.windowSizeInSeconds = windowSizeInSeconds;
        this.capacity = capacity;
        this.userRequestCounts = new HashMap<>();
        this.windowStartTime = System.currentTimeMillis() / 1000;
    }

    public synchronized boolean allowRequest(String userId) {
        long currentTime = System.currentTimeMillis() / 1000;

        // Check if the window has expired and reset
        if (currentTime - windowStartTime >= windowSizeInSeconds) {
            System.out.println("--- Window Reset ---");
            userRequestCounts.clear(); // Reset all counters
            windowStartTime = currentTime;
        }

        // Get or create a counter for the user
        AtomicInteger counter = userRequestCounts.computeIfAbsent(userId, k -> new AtomicInteger(0));

        // Check if the user has exceeded the limit
        if (counter.get() >= capacity) {
            return false; // Request rejected
        }

        // Increment the counter and allow the request
        counter.incrementAndGet();
        return true;
    }

    public static void main(String[] args) throws InterruptedException {
        FixedWindowRateLimiter limiter = new FixedWindowRateLimiter(10, 5); // 5 requests per 10 seconds
        String userId = "user1";

        for (int i = 0; i < 8; i++) {
            System.out.println("Request " + (i + 1) + ": " + (limiter.allowRequest(userId) ? "Allowed" : "Blocked"));
            if (i == 3) {
                System.out.println("Waiting for 12 seconds to reset window...");
                Thread.sleep(12000);
            }
        }
    }
}
