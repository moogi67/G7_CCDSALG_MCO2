/**
 * Processes a DNA sequence and uses BinarySearchTree to compute its k-mer
 * distribution. DNA validation and substring extraction are kept separate
 * from the BST's storage operations.
 */
public class KmerAnalyzer {

    /** Builds and returns a BST containing the frequencies of all k-mers. */
    public BinarySearchTree analyze(String dna, int k) {
        validateInput(dna, k);

        BinarySearchTree result = BinarySearchTree.create();
        for (int i = 0; i <= dna.length() - k; i++) {
            String kmer = dna.substring(i, i + k);
            result.insert(kmer);
        }
        return result;
    }

    private void validateInput(String dna, int k) {
        if (dna == null || dna.isEmpty()) {
            throw new IllegalArgumentException("The DNA sequence cannot be empty.");
        }
        if (k < 1 || k > dna.length()) {
            throw new IllegalArgumentException("k must be between 1 and the DNA sequence length.");
        }
        for (int i = 0; i < dna.length(); i++) {
            char base = dna.charAt(i);
            if (base != 'A' && base != 'C' && base != 'G' && base != 'T') {
                throw new IllegalArgumentException("Use only uppercase A, C, G and T in the DNA sequence.");
            }
        }
    }
}
