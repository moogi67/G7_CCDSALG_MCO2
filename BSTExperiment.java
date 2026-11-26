import java.util.HashSet;
import java.util.Map;

/** Runs correctness tests for the BST and compares its results with the group's hash table. */
public class BSTExperiment {
    private static int passed = 0;
    private static int failed = 0;
    private static final KmerAnalyzer analyzer = new KmerAnalyzer();

    private static void check(String description, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("[PASS] " + description);
        } else {
            failed++;
            System.out.println("[FAIL] " + description);
        }
    }

    private static void expectInvalid(String description, Runnable operation) {
        boolean rejected = false;
        try {
            operation.run();
        } catch (IllegalArgumentException exception) {
            rejected = true;
        }
        check(description, rejected);
    }

    private static boolean matchesHashTable(
        String dna, int k, BinarySearchTree bst, HashTable.HashFunction hashFunction
    ) {
        HashTable hashTable = HashTable.computeDistribution(dna, k, hashFunction);
        Map<String, Integer> bstDistribution = bst.getDistribution();
        Map<String, Integer> hashDistribution = new java.util.TreeMap<>();

        for (int i = 0; i <= dna.length() - k; i++) {
            String kmer = dna.substring(i, i + k);
            if (bst.getCount(kmer) != hashTable.getCount(kmer)) {
                return false;
            }
            hashDistribution.put(kmer, hashTable.getCount(kmer));
        }
        return bstDistribution.equals(hashDistribution);
    }

    private static void testRequiredCases() {
        BinarySearchTree empty = BinarySearchTree.create();
        check("create() produces an empty BST", empty.isEmpty() && empty.size() == 0);
        check("search() returns false for a missing key", !empty.search("AC"));

        BinarySearchTree repeated = analyzer.analyze("AAAAAA", 2);
        check("Repeated k-mer: AA occurs 5 times", repeated.getCount("AA") == 5);
        check("Repeated k-mers create only one distinct node", repeated.size() == 1);
        check("search() finds an existing k-mer", repeated.search("AA"));

        BinarySearchTree kOne = analyzer.analyze("ACGT", 1);
        check("k = 1: four distinct one-character k-mers", kOne.size() == 4);
        check("k = 1: each base occurs once", kOne.getCount("A") == 1
            && kOne.getCount("C") == 1 && kOne.getCount("G") == 1
            && kOne.getCount("T") == 1);

        BinarySearchTree kEqualsN = analyzer.analyze("ACGT", 4);
        check("k = sequence length: one k-mer with count 1",
            kEqualsN.size() == 1 && kEqualsN.getCount("ACGT") == 1);

        BinarySearchTree identical = analyzer.analyze("AAAAAA", 3);
        check("All-identical sequence: AAA occurs 4 times",
            identical.size() == 1 && identical.getCount("AAA") == 4);

        BinarySearchTree overlapping = analyzer.analyze("ATATAT", 2);
        check("Overlapping windows: AT=3 and TA=2",
            overlapping.getCount("AT") == 3 && overlapping.getCount("TA") == 2);
        check("Distribution is in alphabetical order",
            overlapping.getDistribution().keySet().toString().equals("[AT, TA]"));

        expectInvalid("Reject k = 0", () -> analyzer.analyze("ACGT", 0));
        expectInvalid("Reject k greater than sequence length",
            () -> analyzer.analyze("ACGT", 5));
        expectInvalid("Reject empty DNA sequence", () -> analyzer.analyze("", 1));
        expectInvalid("Reject invalid DNA character", () -> analyzer.analyze("ACNX", 2));
        expectInvalid("Reject lowercase DNA sequence", () -> analyzer.analyze("acgt", 2));

        BinarySearchTree destroyed = analyzer.analyze("ACGTAC", 2);
        destroyed.destroy();
        check("destroy() empties the BST", destroyed.isEmpty() && destroyed.size() == 0
            && !destroyed.search("AC"));
    }

    private static void testHashTableAgreement() {
        String[] sequences = {"AAAAAA", "ACGTACGT", "ATATATAT", "AACCGGTTAC"};
        int[] kValues = {1, 2, 3};
        int comparisons = 0;
        boolean allMatch = true;

        for (String dna : sequences) {
            for (int k : kValues) {
                BinarySearchTree bst = analyzer.analyze(dna, k);
                boolean polynomialMatch = matchesHashTable(
                    dna, k, bst, HashTable::polynomialHash
                );
                boolean fnvMatch = matchesHashTable(
                    dna, k, bst, HashFunctions::fnv1aHash
                );
                allMatch = allMatch && polynomialMatch && fnvMatch;
                comparisons += 2;
            }
        }
        check("BST agrees with both hash functions on " + comparisons + " comparisons", allMatch);
    }

    public static void main(String[] args) {
        System.out.println("===== BST CORRECTNESS TESTS =====");
        testRequiredCases();
        testHashTableAgreement();
        System.out.println("---------------------------------");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);

        if (failed > 0) {
            throw new AssertionError("Some BST tests failed.");
        }
    }
}
