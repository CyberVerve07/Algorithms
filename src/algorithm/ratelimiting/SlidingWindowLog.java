package algorithm.ratelimiting;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class SlidingWindowLogRateLimiter {

    private final int windowSizeInSeconds;
    private final int capacity;
    private final Map<String, LinkedList<Long>> userRequestLogs;

    public SlidingWindowLogRateLimiter(int windowSizeInSeconds, int capacity) {
        this.windowSizeInSeconds = windowSizeInSeconds;
        this.capacity = capacity;
        // Thread-safe map for concurrent access
        this.userRequestLogs = new ConcurrentHashMap<>();
    }

    public boolean allowRequest(String userId) {
        long currentTime = System.currentTimeMillis();

        // Get or create a log for the user
        LinkedList<Long> timestamps = userRequestLogs.computeIfAbsent(userId, k -> new LinkedList<>());

        // Remove timestamps older than the window
        long windowStart = currentTime - (windowSizeInSeconds * 1000L);
        while (!timestamps.isEmpty() && timestamps.peekFirst() < windowStart) {
            timestamps.removeFirst();
        }

        // Check capacity
        if (timestamps.size() >= capacity) {
            return false;
        }

        // Add current request timestamp and allow
        timestamps.addLast(currentTime);
        return true;
    }

    public static void main(String[] args) throws InterruptedException {
        SlidingWindowLogRateLimiter limiter = new SlidingWindowLogRateLimiter(10, 5); // 5 requests per 10 seconds
        String userId = "user1";

        for (int i = 0; i < 7; i++) {
            System.out.println("Request " + (i + 1) + ": " + (limiter.allowRequest(userId) ? "Allowed" : "Blocked"));
            Thread.sleep(2000); // Send a request every 2 seconds
        }
    }
}
