/**
 * A thread-safe, generic messaging queue implementation using a circular buffer.
 * Provides blocking enqueue and dequeue operations.
 */
public class MessageQueue<T> {
    private final T[] buffer;
    private int head = 0;
    private int tail = 0;
    private int size = 0;
    private final int capacity;

    @SuppressWarnings("unchecked")
    public MessageQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
        this.buffer = (T[]) new Object[capacity];
    }

    /**
     * Adds a message to the queue. Blocks if the queue is full.
     */
    public synchronized void enqueue(T message) throws InterruptedException {
        while (size == capacity) {
            wait(); // Wait until there is space
        }
        buffer[tail] = message;
        tail = (tail + 1) % capacity;
        size++;
        notifyAll(); // Notify consumers that a message is available
    }

    /**
     * Removes and returns a message from the queue. Blocks if the queue is empty.
     */
    public synchronized T dequeue() throws InterruptedException {
        while (size == 0) {
            wait(); // Wait until there is a message
        }
        T message = buffer[head];
        buffer[head] = null; // Clear reference for GC
        head = (head + 1) % capacity;
        size--;
        notifyAll(); // Notify producers that space is available
        return message;
    }

    public synchronized int size() {
        return size;
    }

    public synchronized boolean isEmpty() {
        return size == 0;
    }

    public synchronized boolean isFull() {
        return size == capacity;
    }
}
