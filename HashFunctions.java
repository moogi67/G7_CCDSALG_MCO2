public class HashFunctions {

    /**
     * Second hash function: FNV-1a hashing.
     * Uses XOR and multiplication to calculate
     * the hash value of a k-mer.
     */
    public static int fnv1aHash(String kmer, int tableSize) {

        int hash = 0x811c9dc5;

        for (int i = 0; i < kmer.length(); i++) {
            hash ^= kmer.charAt(i);
            hash *= 0x01000193;
        }

        return Math.floorMod(hash, tableSize);
    }
}

