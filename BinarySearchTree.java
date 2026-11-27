import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;

public class BinarySearchTree {
    private Node root;
    private int distinctKmerCount;

    /** Creates an empty Binary Search Tree. */
    public BinarySearchTree() {
        root = null;
        distinctKmerCount = 0;
    }

    /** Creates a new empty BST. */
    public static BinarySearchTree create() {
        return new BinarySearchTree();
    }

    /** Returns true if the k-mer exists in the BST. */
    public boolean search(String kmer) {
        validateKmer(kmer);
        return findNode(kmer) != null;
    }

    /** Finds a node containing the given k-mer. */
    private Node findNode(String kmer) {
        Node current = root;

        while (current != null) {
            int comparison = kmer.compareTo(current.getKmer());

            if (comparison == 0) {
                return current;
            } else if (comparison < 0) {
                current = current.getLeft();
            } else {
                current = current.getRight();
            }
        }

        return null;
    }

    /** Returns the occurrence count of a k-mer. */
    public int getCount(String kmer) {
        validateKmer(kmer);

        Node found = findNode(kmer);

        if (found == null) {
            return 0;
        }

        return found.getCount();
    }

    /** Inserts a k-mer or increases its count if it already exists. */
    public void insert(String kmer) {
        validateKmer(kmer);

        if (root == null) {
            root = new Node(kmer);
            distinctKmerCount++;
            return;
        }

        Node current = root;

        while (true) {
            int comparison = kmer.compareTo(current.getKmer());

            if (comparison == 0) {
                current.increaseCount();
                return;
            } else if (comparison < 0) {
                if (current.getLeft() == null) {
                    current.setLeft(new Node(kmer));
                    distinctKmerCount++;
                    return;
                }

                current = current.getLeft();
            } else {
                if (current.getRight() == null) {
                    current.setRight(new Node(kmer));
                    distinctKmerCount++;
                    return;
                }

                current = current.getRight();
            }
        }
    }

    /** Removes all nodes from the BST. */
    public void destroy() {
        root = null;
        distinctKmerCount = 0;
    }

    public int size() {
        return distinctKmerCount;
    }

    public boolean isEmpty() {
        return root == null;
    }

    /** Returns the distribution in alphabetical order using in-order traversal. */
    public Map<String, Integer> getDistribution() {
        Map<String, Integer> distribution = new LinkedHashMap<>();
        Deque<Node> stack = new ArrayDeque<>();
        Node current = root;

        while (current != null || !stack.isEmpty()) {
            while (current != null) {
                stack.push(current);
                current = current.getLeft();
            }

            current = stack.pop();
            distribution.put(current.getKmer(), current.getCount());
            current = current.getRight();
        }

        return distribution;
    }

    /** Prints the k-mer distribution in alphabetical order. */
    public void printDistribution() {
        System.out.println("K-mer    Occurrences");

        for (Map.Entry<String, Integer> entry : getDistribution().entrySet()) {
            System.out.println(entry.getKey() + "        " + entry.getValue());
        }
    }

    /** Checks whether the k-mer is valid. */
    private void validateKmer(String kmer) {
        if (kmer == null || kmer.isEmpty()) {
            throw new IllegalArgumentException(
                "A k-mer cannot be null or empty."
            );
        }
    }
}
