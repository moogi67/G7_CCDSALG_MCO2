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
        clearSubtree(root);
        root = null;
        distinctKmerCount = 0;
    }

    /** Clears the nodes recursively. */
    private void clearSubtree(Node node) {
        if (node == null) {
            return;
        }

        clearSubtree(node.getLeft());
        clearSubtree(node.getRight());

        node.setLeft(null);
        node.setRight(null);
    }

    public int size() {
        return distinctKmerCount;
    }

    public boolean isEmpty() {
        return root == null;
    }

    /** Prints the k-mer distribution in alphabetical order. */
    public void printDistribution() {
        System.out.println("K-mer    Occurrences");
        printInOrder(root);
    }

    /** Prints the BST using in-order traversal. */
    private void printInOrder(Node node) {
        if (node == null) {
            return;
        }

        printInOrder(node.getLeft());

        System.out.println(
            node.getKmer() + "        " + node.getCount()
        );

        printInOrder(node.getRight());
    }

    /** Checks whether the k-mer is valid. */
    private void validateKmer(String kmer) {
        if (kmer == null || kmer.isEmpty()) {
            throw new IllegalArgumentException("A k-mer cannot be null or empty.");
        }
    }
}
