/**
 * Represents one node in the Binary Search Tree.
 * Each node stores a k-mer, its occurrence count, and links to its children.
 */
public class Node {
    private final String kmer;
    private int count;
    private Node left;
    private Node right;

    public Node(String kmer) {
        if (kmer == null || kmer.isEmpty()) {
            throw new IllegalArgumentException("A k-mer cannot be null or empty.");
        }
        this.kmer = kmer;
        this.count = 1;
        this.left = null;
        this.right = null;
    }

    public String getKmer() {
        return kmer;
    }

    public int getCount() {
        return count;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    void increaseCount() {
        count++;
    }

    void setLeft(Node left) {
        this.left = left;
    }

    void setRight(Node right) {
        this.right = right;
    }
}
