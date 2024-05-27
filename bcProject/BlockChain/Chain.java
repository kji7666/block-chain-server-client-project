package bcProject.BlockChain;

import java.util.ArrayList;
import java.util.List;
/**
 * Use Singleton design pattern to make sure all the chain that is new is 
 * all point to the same instance
 * 
 * @author Harris
 */
public final class Chain{
    private List<Block> BlockChain;

    public Chain(Block gensisBlock){
        BlockChain = new ArrayList<>();
    /**Input inital variable to make a genesis block and add it to the chain */
        BlockChain.add(gensisBlock);
    }

    public void addBlock(Block newBlock){
       BlockChain.add(newBlock);
    }

    public Block searchBlock(String index){
        return BlockChain.get(Integer.valueOf(index)); 
    } 
}

/**
 * The MerkleTree is the hash of Tree, it ensures the authetication 
 * and the intact integrity of the data.
 * 
 * @author Harris
 */
class MerkleTree {
    private ArrayList<String> transactions;
    private ArrayList<String> merkleTree;

    public MerkleTree(ArrayList<String> transactions) {
        this.transactions = transactions;
        this.merkleTree = new ArrayList<>();
        buildTree(transactions);
    }

    /** 
     * First, we can the transaction numberIf the number of the transaction is 
     * odd, duplicate the last to make it even.
     * A for loop iterates
     * 
     * 
     * 
     * 
     * @param transactions transactionID and transactionData
     * 
    */
    private void buildTree(ArrayList<String> transactions) {
        if (transactions.size() % 2 != 0) {
            transactions.add(transactions.get(transactions.size() - 1));
        }

        for (String tx : transactions) {
            merkleTree.add(tx);
        }
    
        while (merkleTree.size() > 1) {
            ArrayList<String> newLevel = new ArrayList<>();
            for (int i = 0; i < merkleTree.size(); i += 2) {
                String combined = merkleTree.get(i) + merkleTree.get(i + 1);
                String hash = SHA256.generateSHA256(combined);
                newLevel.add(hash);
            }
            //merkleTree list reduce to small the tree list, until the size of the list
            //is one, we get the merkleTree root hash, and the while loop stops
            merkleTree = newLevel;
        }
    }


     /**
     * Verifies if a transaction is part of the Merkle tree by constructing a
     * Merkle proof.
     *
     * @param transaction the transaction hash to verify
     * @return true if the transaction is part of the Merkle tree, false otherwise
     */
    public boolean verifyTransaction(String transactionID) {
        return merkleTree.contains(transactionID);
    }
}


