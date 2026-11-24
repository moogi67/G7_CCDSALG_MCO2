import java.util.ArrayList;

public class HashTable {

    /** A hashing function that can be selected when a table is created. */
    public interface HashFunction {
        int hash(String kmer, int tableSize);
    }

    /** Stores one distinct k-mer and its occurrence count. */
    private static class Entry {
        String kmer;
        int count;

        Entry(String kmer) {
            this.kmer = kmer;
            this.count = 1;
        }
    }

    private final ArrayList<ArrayList<Entry>> table;
    private final HashFunction hashFunction;
    private int collisionCount;

    /** Creates an empty table with the specified number of buckets. */
    public HashTable(int tableSize, HashFunction hashFunction) {
        if (tableSize <= 0 || hashFunction == null) {
            throw new IllegalArgumentException(
                "A positive table size and hash function are required."
            );
        }

        this.table = new ArrayList<>(tableSize);

        for (int i = 0; i < tableSize; i++) {
            table.add(new ArrayList<>());
        }

        this.hashFunction = hashFunction;
        this.collisionCount = 0;
    }

    /** First hash function: polynomial hashing using a base of 31. */
    public static int polynomialHash(String kmer, int tableSize) {
        long hash = 0;

        for (int i = 0; i < kmer.length(); i++) {
            hash = (hash * 31 + kmer.charAt(i)) % tableSize;
        }

        return (int) hash;
    }

    /** Gets a valid index even if the hash function returns a negative value. */
    private int indexFor(String kmer) {
        return Math.floorMod(
            hashFunction.hash(kmer, table.size()),
            table.size()
        );
    }

    /** Adds a k-mer or increases its count if it is already in the table. */
    public void add(String kmer) {
        if (kmer == null || kmer.isEmpty()) {
            throw new IllegalArgumentException(
                "A k-mer cannot be null or empty."
            );
        }

        ArrayList<Entry> bucket = table.get(indexFor(kmer));

        for (Entry entry : bucket) {
            if (entry.kmer.equals(kmer)) {
                entry.count++;
                return;
            }
        }

        // Count one collision when a new, distinct k-mer enters an occupied bucket.
        if (!bucket.isEmpty()) {
            collisionCount++;
        }

        bucket.add(new Entry(kmer));
    }

    /** Returns the count for a k-mer, or zero if it is not stored. */
    public int getCount(String kmer) {
        if (kmer == null || kmer.isEmpty()) {
            throw new IllegalArgumentException(
                "A k-mer cannot be null or empty."
            );
        }

        ArrayList<Entry> bucket = table.get(indexFor(kmer));

        for (Entry entry : bucket) {
            if (entry.kmer.equals(kmer)) {
                return entry.count;
            }
        }

        return 0;
    }

    /** Returns the number of collisions recorded for new, distinct k-mers. */
    public int getCollisionCount() {
        return collisionCount;
    }

    /** Displays each stored k-mer and its occurrence count. */
    public void printDistribution() {
        System.out.println("K-mer\tOccurrences");

        for (ArrayList<Entry> bucket : table) {
            for (Entry entry : bucket) {
                System.out.println(entry.kmer + "\t" + entry.count);
            }
        }
    }

    /** Builds a table from all overlapping k-mers in a DNA sequence. */
    public static HashTable computeDistribution(
        String dna,
        int k,
        HashFunction hashFunction
    ) {
        if (dna == null || dna.isEmpty()) {
            throw new IllegalArgumentException(
                "The DNA sequence cannot be empty."
            );
        }

        if (k < 1 || k > dna.length()) {
            throw new IllegalArgumentException(
                "k must be between 1 and the DNA sequence length."
            );
        }

        for (int i = 0; i < dna.length(); i++) {
            char base = dna.charAt(i);

            if (base != 'A' && base != 'C' && base != 'G' && base != 'T') {
                throw new IllegalArgumentException(
                    "Use only uppercase A, C, G and T in the DNA sequence."
                );
            }
        }

        HashTable result = new HashTable(dna.length(), hashFunction);

        for (int i = 0; i <= dna.length() - k; i++) {
            result.add(dna.substring(i, i + k));
        }

        return result;
    }

    /** Runs the first hash function using command-line inputs. */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java HashTable <DNA sequence> <k>");
            System.out.println("Example: java HashTable ATATA 3");
            return;
        }

        try {
            int k = Integer.parseInt(args[1]);

            HashTable result = computeDistribution(
                args[0],
                k,
                HashTable::polynomialHash
            );

            result.printDistribution();
            System.out.println("Collisions: " + result.getCollisionCount());

        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
