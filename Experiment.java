
import java.util.Random;

public class Experiment {

    private static final String BASES = "ACGT";

    /**
     * Generates a random DNA sequence.
     */
    public static String generateDNA(int length, long seed) {

        if (length <= 0) {
            throw new IllegalArgumentException(
                "Length must be positive."
            );
        }

        Random random = new Random(seed);
        StringBuilder dna = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            dna.append(BASES.charAt(random.nextInt(4)));
        }

        return dna.toString();
    }

    /**
     * Measures the average execution time of
     * a hash table implementation.
     */
    public static double measureTime(
            String dna,
            int k,
            HashTable.HashFunction function,
            int repetitions) {

        if (repetitions <= 0) {
            throw new IllegalArgumentException(
                "Repetitions must be positive."
            );
        }

        // Warm-up run
        HashTable.computeDistribution(dna, k, function);

        long start = System.nanoTime();

        HashTable result = null;

        for (int i = 0; i < repetitions; i++) {
            result = HashTable.computeDistribution(
                dna, k, function
            );
        }

        long end = System.nanoTime();

        return (end - start) / (double) repetitions;
    }

    /**
     * Runs the comparison between two hash functions.
     */
    public static void main(String[] args) {

        int[] sequenceLengths = {1000, 5000, 10000};
        int k = 5;
        int repetitions = 10;

        System.out.println("===== HASH FUNCTION EXPERIMENT =====");

        System.out.printf(
            "%-12s %-20s %-20s %-15s%n",
            "DNA Length",
            "Hash Function",
            "Average Time (ns)",
            "Collisions"
        );

        for (int length : sequenceLengths) {

            String dna = generateDNA(length, 12345L);

            // First hash function: Polynomial
            HashTable polynomialTable =
                HashTable.computeDistribution(
                    dna, k, HashTable::polynomialHash
                );

            double polynomialTime = measureTime(
                dna, k, HashTable::polynomialHash,
                repetitions
            );

            // Second hash function: FNV-1a
            HashTable fnvTable =
                HashTable.computeDistribution(
                    dna, k, HashFunctions::fnv1aHash
                );

            double fnvTime = measureTime(
                dna, k, HashFunctions::fnv1aHash,
                repetitions
            );

            System.out.printf(
                "%-12d %-20s %-20.2f %-15d%n",
                length,
                "Polynomial",
                polynomialTime,
                polynomialTable.getCollisionCount()
            );

            System.out.printf(
                "%-12d %-20s %-20.2f %-15d%n",
                length,
                "FNV-1a",
                fnvTime,
                fnvTable.getCollisionCount()
            );

            System.out.println("-----------------------------------------------");
        }
    }
}

