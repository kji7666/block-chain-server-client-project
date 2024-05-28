import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import bcProject.BlockChain.SHA256;

public class MerkleTree {
    private List<String> transactions;
    private Map<Integer, List<String>> treeLevels;

    public MerkleTree(List<String> transactions) {
        this.transactions = new ArrayList<>(transactions);
        this.treeLevels = new HashMap<>();
        buildTree(this.transactions);
    }

    private void buildTree(List<String> transactions) {
        if (transactions.size() % 2 != 0) {
            transactions.add(transactions.get(transactions.size() - 1));
        }

        treeLevels.put(0, new ArrayList<>(transactions));

        int level = 0;

        //combined broken
        while (treeLevels.get(level).size() > 1) {
            List<String> currentLevel = treeLevels.get(level);
            List<String> newLevel = new ArrayList<>();
            for (int i = 0; i < currentLevel.size(); i += 2) {
                String combined = currentLevel.get(i) + currentLevel.get(i + 1);
                String hash = SHA256.generateSHA256(combined);
                System.out.println("combined:" +combined);
                newLevel.add(hash);
            }
            level++;
            treeLevels.put(level, newLevel);
        }
    }

    public List<String> getMerkleProof(String transactionHash) {
        List<String> proof = new ArrayList<>();
        int index = transactions.indexOf(transactionHash);
        if (index == -1) {
            throw new IllegalArgumentException("Transaction hash not found in the tree");
        }

        for (int level = 0; level < treeLevels.size() - 1; level++) {
            List<String> currentLevel = treeLevels.get(level);
            int siblingIndex = (index % 2 == 0) ? index + 1 : index - 1;
            if (siblingIndex < currentLevel.size()) {
                proof.add(currentLevel.get(siblingIndex));
            }
            index /= 2;
        }
        return proof;
    }

    //broken
    public boolean verifyTransaction(String transactionHash, List<String> proof) {
        String computedHash = transactionHash;
        for (String proofHash : proof) {
            if (computedHash.compareTo(proofHash) < 0) {
                computedHash = SHA256.generateSHA256(computedHash + proofHash);
            } else {
                computedHash = SHA256.generateSHA256(proofHash + computedHash);
            }
        }
        String rootHash = treeLevels.get(treeLevels.size() - 1).get(0);
        return computedHash.equals(rootHash);
    }

    public static void main(String[] args) {
        List<String> transactions = new ArrayList<>();
        transactions.add("HI I am HArris");
        transactions.add("HI");
        transactions.add("tc");
        transactions.add("td");

        //System.out.println(SHA256.generateSHA256("tctd"));
        MerkleTree merkleTree = new MerkleTree(transactions);

        //String transactionToVerify = "20ba6478af43bb4ff0cb3d72291d300dd57c420ebd06137da932405c6f011cb9";
        String transactionToVerify = "HI";

        List<String> proof = merkleTree.getMerkleProof(transactionToVerify);
        System.out.println("Merkle Proof for " + transactionToVerify + ": " + proof);

        boolean isOnTree = merkleTree.verifyTransaction(transactionToVerify, proof);
        System.out.println("Is " + transactionToVerify + " on the Merkle Tree: " + isOnTree);
    }
}
