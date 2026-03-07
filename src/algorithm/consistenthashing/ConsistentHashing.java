package algorithm.consistenthashing;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Consistent Hashing Algorithm Implementation
 *
 * Consistent hashing distributes keys across nodes in a way that minimizes
 * remapping when nodes are added or removed. Each node is placed at multiple
 * points (virtual nodes) on a hash ring, and keys are assigned to the nearest
 * node in the clockwise direction.
 *
 * Use Cases:
 * - Distributed caching (e.g., Memcached, Redis Cluster)
 * - Load balancing across servers
 * - Database sharding / partitioning
 */
public class ConsistentHashing {

    // The hash ring — maps hash values to node names
    private final TreeMap<Long, String> ring;

    // Number of virtual nodes per physical node (improves distribution)
    private final int virtualNodes;

    // Track which physical nodes are currently in the ring
    private final Set<String> physicalNodes;

    public ConsistentHashing(int virtualNodes) {
        this.ring = new TreeMap<>();
        this.virtualNodes = virtualNodes;
        this.physicalNodes = new HashSet<>();
    }

    /**
     * Adds a node to the hash ring with its virtual replicas.
     * Each virtual node is hashed to a different position on the ring.
     */
    public void addNode(String node) {
        physicalNodes.add(node);
        for (int i = 0; i < virtualNodes; i++) {
            long hash = generateHash(node + "-vn-" + i);
            ring.put(hash, node);
        }
        System.out.println("Added node: " + node + " (" + virtualNodes + " virtual nodes)");
    }

    /**
     * Removes a node and all its virtual replicas from the hash ring.
     */
    public void removeNode(String node) {
        physicalNodes.remove(node);
        for (int i = 0; i < virtualNodes; i++) {
            long hash = generateHash(node + "-vn-" + i);
            ring.remove(hash);
        }
        System.out.println("Removed node: " + node);
    }

    /**
     * Finds which node a given key should be routed to.
     * Walks clockwise on the ring from the key's hash to find the nearest node.
     */
    public String getNode(String key) {
        if (ring.isEmpty()) {
            return null;
        }

        long hash = generateHash(key);

        // ceilingEntry returns the entry with the smallest key >= hash
        Map.Entry<Long, String> entry = ring.ceilingEntry(hash);

        // If no entry found (we're past the last node), wrap around to the first
        if (entry == null) {
            entry = ring.firstEntry();
        }

        return entry.getValue();
    }

    /**
     * Returns all physical nodes currently in the ring.
     */
    public Set<String> getNodes() {
        return Collections.unmodifiableSet(physicalNodes);
    }

    /**
     * Generates a hash value using MD5.
     * Takes the first 8 bytes of the MD5 digest to produce a long.
     */
    private long generateHash(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(key.getBytes());
            long hash = 0;
            for (int i = 0; i < 8; i++) {
                hash = (hash << 8) | (digest[i] & 0xFF);
            }
            return hash;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not available", e);
        }
    }

    // ─────────────────────── Demo ───────────────────────

    public static void main(String[] args) {
        ConsistentHashing ch = new ConsistentHashing(150); // 150 virtual nodes per server

        // Add servers to the ring
        ch.addNode("Server-A");
        ch.addNode("Server-B");
        ch.addNode("Server-C");

        System.out.println();

        // Route some keys
        String[] keys = {"user:1001", "user:1002", "session:abc", "order:5050", "cache:homepage"};
        System.out.println("── Key Routing ──");
        for (String key : keys) {
            System.out.println("  " + key + "  →  " + ch.getNode(key));
        }

        // Remove a server and observe minimal disruption
        System.out.println();
        ch.removeNode("Server-B");
        System.out.println();

        System.out.println("── Key Routing (after removing Server-B) ──");
        for (String key : keys) {
            System.out.println("  " + key + "  →  " + ch.getNode(key));
        }

        // Add a new server
        System.out.println();
        ch.addNode("Server-D");
        System.out.println();

        System.out.println("── Key Routing (after adding Server-D) ──");
        for (String key : keys) {
            System.out.println("  " + key + "  →  " + ch.getNode(key));
        }
    }
}
