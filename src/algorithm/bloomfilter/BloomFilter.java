package algorithm.bloomfilter;

import java.util.BitSet;

/**
 * Bloom Filter Implementation
 *
 * A Bloom filter is a space-efficient probabilistic data structure that tests
 * whether an element is a member of a set. It can produce false positives
 * (saying an element exists when it doesn't) but NEVER false negatives
 * (if it says an element doesn't exist, it truly doesn't).
 *
 * Use Cases:
 * - Database lookups: check if a key might exist before hitting disk (e.g., LSM trees)
 * - Web caching: avoid caching one-hit-wonders
 * - Spam detection: quickly check if a URL is known malicious
 * - Network routers: packet routing and duplicate detection
 *
 * Time Complexity:  O(k) for add and contains, where k = number of hash functions
 * Space Complexity: O(m) bits, where m = size of the bit array
 */
public class BloomFilter {

    private final BitSet bitSet;
    private final int bitSetSize;       // m — total bits in the filter
    private final int numHashFunctions; // k — number of hash functions
    private int itemCount;              // number of items added

    /**
     * Creates a Bloom filter tuned for the expected number of items
     * and desired false positive probability.
     *
     * @param expectedItems expected number of items (n)
     * @param falsePositiveRate desired false positive probability (e.g., 0.01 for 1%)
     */
    public BloomFilter(int expectedItems, double falsePositiveRate) {
        // Optimal bit array size: m = -(n * ln(p)) / (ln(2)^2)
        this.bitSetSize = optimalBitSize(expectedItems, falsePositiveRate);

        // Optimal number of hash functions: k = (m / n) * ln(2)
        this.numHashFunctions = optimalHashCount(bitSetSize, expectedItems);

        this.bitSet = new BitSet(bitSetSize);
        this.itemCount = 0;

        System.out.println("Bloom Filter created:");
        System.out.println("  Bit array size (m): " + bitSetSize + " bits");
        System.out.println("  Hash functions (k): " + numHashFunctions);
        System.out.println("  Expected items (n): " + expectedItems);
        System.out.printf("  Target FP rate:     %.4f%%%n", falsePositiveRate * 100);
    }

    /**
     * Adds an element to the Bloom filter.
     * Hashes the element k times and sets the corresponding bits.
     */
    public void add(String element) {
        int[] hashes = getHashes(element);
        for (int hash : hashes) {
            bitSet.set(hash);
        }
        itemCount++;
    }

    /**
     * Checks if an element MIGHT be in the set.
     *
     * @return true  → element is POSSIBLY in the set (could be a false positive)
     *         false → element is DEFINITELY NOT in the set
     */
    public boolean mightContain(String element) {
        int[] hashes = getHashes(element);
        for (int hash : hashes) {
            if (!bitSet.get(hash)) {
                return false; // Definitely not in the set
            }
        }
        return true; // Possibly in the set
    }

    /**
     * Returns the current estimated false positive rate based on items added.
     * Formula: (1 - e^(-kn/m))^k
     */
    public double estimatedFalsePositiveRate() {
        double exponent = -((double) numHashFunctions * itemCount) / bitSetSize;
        return Math.pow(1 - Math.exp(exponent), numHashFunctions);
    }

    /**
     * Returns the number of items added to the filter.
     */
    public int getItemCount() {
        return itemCount;
    }

    // ─────────────────── Internal Helpers ───────────────────

    /**
     * Generates k hash positions using double hashing technique.
     * h_i(x) = (h1(x) + i * h2(x)) mod m
     *
     * This avoids needing k independent hash functions.
     */
    private int[] getHashes(String element) {
        int[] result = new int[numHashFunctions];

        int hash1 = element.hashCode();
        int hash2 = fnvHash(element);

        for (int i = 0; i < numHashFunctions; i++) {
            int combinedHash = hash1 + (i * hash2);

            // Ensure non-negative
            combinedHash = combinedHash & Integer.MAX_VALUE;

            result[i] = combinedHash % bitSetSize;
        }
        return result;
    }

    /**
     * FNV-1a hash function — used as the second independent hash.
     */
    private int fnvHash(String element) {
        int hash = 0x811c9dc5; // FNV offset basis
        for (int i = 0; i < element.length(); i++) {
            hash ^= element.charAt(i);
            hash *= 0x01000193; // FNV prime
        }
        return hash;
    }

    /**
     * Calculates optimal bit array size.
     * m = -(n * ln(p)) / (ln(2)^2)
     */
    private static int optimalBitSize(int n, double p) {
        return (int) Math.ceil(-(n * Math.log(p)) / (Math.log(2) * Math.log(2)));
    }

    /**
     * Calculates optimal number of hash functions.
     * k = (m / n) * ln(2)
     */
    private static int optimalHashCount(int m, int n) {
        return Math.max(1, (int) Math.round((double) m / n * Math.log(2)));
    }

    // ─────────────────────── Demo ───────────────────────

    public static void main(String[] args) {
        // Create a Bloom filter expecting 1000 items with 1% false positive rate
        BloomFilter filter = new BloomFilter(1000, 0.01);

        System.out.println();

        // Add some known URLs
        String[] knownMalicious = {
                "http://malware.example.com",
                "http://phishing.badsite.org",
                "http://virus.evil.net",
                "http://trojan.danger.io",
                "http://ransomware.hack.xyz"
        };

        System.out.println("── Adding known malicious URLs ──");
        for (String url : knownMalicious) {
            filter.add(url);
            System.out.println("  Added: " + url);
        }

        System.out.println();

        // Check membership
        String[] testUrls = {
                "http://malware.example.com",   // should be found
                "http://google.com",             // should NOT be found
                "http://phishing.badsite.org",   // should be found
                "http://stackoverflow.com",      // should NOT be found
                "http://safe-website.com"        // should NOT be found
        };

        System.out.println("── Membership Checks ──");
        for (String url : testUrls) {
            boolean result = filter.mightContain(url);
            System.out.println("  " + url);
            System.out.println("    → " + (result ? "POSSIBLY malicious ⚠" : "DEFINITELY safe ✓"));
        }

        System.out.println();
        System.out.printf("Items in filter: %d%n", filter.getItemCount());
        System.out.printf("Estimated FP rate: %.6f%%%n", filter.estimatedFalsePositiveRate() * 100);
    }
}
