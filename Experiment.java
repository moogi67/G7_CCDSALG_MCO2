import java.util.Random;

public class Experiment {

    private static final String BASES = "ACGT";

    // Keeps the computed table or tree available while measuring time.
    private static volatile Object lastResult;

    /** Generates a random DNA sequence using a fixed seed. */
    public static String generateDNA(int length, long seed) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be positive.");
        }

        Random random = new Random(seed);
        StringBuilder dna = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            dna.append(BASES.charAt(random.nextInt(4)));
        }

        return dna.toString();
    }

    /** Measures the average time for one hash table implementation. */
    public static double measureTime(
        String dna,
        int k,
        HashTable.HashFunction function,
        int repetitions
    ) {
        if (repetitions <= 0) {
            throw new IllegalArgumentException(
                "Repetitions must be positive."
            );
        }

        // Warm up before measuring.
        for (int i = 0; i < 3; i++) {
            lastResult = HashTable.computeDistribution(dna, k, function);
        }

        long start = System.nanoTime();

        for (int i = 0; i < repetitions; i++) {
            lastResult = HashTable.computeDistribution(dna, k, function);
        }

        long end = System.nanoTime();

        lastResult = null;
        return (end - start) / (double) repetitions;
    }

    /** Measures the average time for the BST implementation. */
    public static double measureBSTTime(
        String dna,
        int k,
        int repetitions
    ) {
        if (repetitions <= 0) {
            throw new IllegalArgumentException(
                "Repetitions must be positive."
            );
        }

        KmerAnalyzer analyzer = new KmerAnalyzer();

        // Use the same number of warm-up runs as the hash table.
        for (int i = 0; i < 3; i++) {
            lastResult = analyzer.analyze(dna, k);
        }

        long start = System.nanoTime();

        for (int i = 0; i < repetitions; i++) {
            lastResult = analyzer.analyze(dna, k);
        }

        long end = System.nanoTime();

        lastResult = null;
        return (end - start) / (double) repetitions;
    }

    /** Compares both hash functions and the BST on the same DNA sequences. */
    public static void main(String[] args) {
        // These are the values in the original file. Check them against Task 5.
        int[] sequenceLengths = {1000, 5000, 10000};
        int[] kValues = {5};
        int repetitions = 10;

        System.out.println("===== K-MER PERFORMANCE COMPARISON =====");

        System.out.printf(
            "%-12s %-6s %-18s %-20s %-12s%n",
            "DNA Length",
            "k",
            "Method",
            "Average Time (ns)",
            "Collisions"
        );

        for (int length : sequenceLengths) {
            String dna = generateDNA(length, 12345L);

            for (int k : kValues) {
                HashTable polynomialTable = HashTable.computeDistribution(
                    dna,
                    k,
                    HashTable::polynomialHash
                );

                HashTable fnvTable = HashTable.computeDistribution(
                    dna,
                    k,
                    HashFunctions::fnv1aHash
                );

                double polynomialTime = measureTime(
                    dna,
                    k,
                    HashTable::polynomialHash,
                    repetitions
                );

                double fnvTime = measureTime(
                    dna,
                    k,
                    HashFunctions::fnv1aHash,
                    repetitions
                );

                double bstTime = measureBSTTime(
                    dna,
                    k,
                    repetitions
                );

                System.out.printf(
                    "%-12d %-6d %-18s %-20.2f %-12d%n",
                    length,
                    k,
                    "Polynomial",
                    polynomialTime,
                    polynomialTable.getCollisionCount()
                );

                System.out.printf(
                    "%-12d %-6d %-18s %-20.2f %-12d%n",
                    length,
                    k,
                    "FNV-1a",
                    fnvTime,
                    fnvTable.getCollisionCount()
                );

                System.out.printf(
                    "%-12d %-6d %-18s %-20.2f %-12s%n",
                    length,
                    k,
                    "BST",
                    bstTime,
                    "N/A"
                );

                System.out.println(
                    "---------------------------------------------------------------"
                );
            }
        }
    }
}
