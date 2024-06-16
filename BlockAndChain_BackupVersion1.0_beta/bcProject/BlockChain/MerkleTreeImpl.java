package bcProject.BlockChain;
import java.util.logging.Logger;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Merkle Tree for data integrity verification in a Blockchain project.
 * The main purpose of the tree is to authenticate user data against the data stored in the blockchain.
 * 
 * @author Harris
 * @since June 02, 2024
 */

public class MerkleTreeImpl implements MerkleTree, Serializable{
    private String[] dataBlocks;
    private String merkleRoot;
    private final Logger logger = Logger.getLogger(MerkleTreeImpl.class.getName());
    
    /**
     * Constructs a MerkleTreeImpl with the specified data blocks.
     *
     * @param dataBlocks the array of data blocks (strings) to build the tree.
     * @throws IllegalArgumentException if dataBlocks is null or empty.
     */
    public MerkleTreeImpl(String[] dataBlocks){
        //MerkleTreeImpl instance is always in a valid state when it is created
        if(dataBlocks == null || dataBlocks.length == 0){
            logger.severe("Failed to create MerkleTree");
            throw new IllegalArgumentException("The Arguments of MerkleTreeImpl cannot be null or empty.");
        }
        //we just assigned the dataBlock's reference
        //potential danger is that the integrity of data, any change here affects the orignal trasaction[] on the block
        this.dataBlocks = dataBlocks;
        this.merkleRoot = buildMerkleTree(dataBlocks);
    }

    /**
     * Builds the Merkle Tree and returns the root hash.
     * 
     * @param dataBlocks the input data blocks.
     * @return the hash value of the Merkle Tree root.
     */
    private String buildMerkleTree(String[] dataBlocks){
        List<String> currentLevel = new ArrayList<>();


        //The initial level is hashing each data's hash
        //each data is a content(String) of a txt file recieve from client
        for(int i=0; i<dataBlocks.length; i++){
            currentLevel.add(SHA256.generateSHA256(dataBlocks[i]));
        }

        //The size of currentLevel would shrink each times of loop
        //until the size is one which means only root hash is on the list
        while(currentLevel.size() > 1){
            List<String> nextLevel = new ArrayList<>();

            // each node "in pairs" counting the combined hash
            // if the size is odd number, we clone the last one making them in pair
            for(int i=0; i<currentLevel.size(); i+=2){
                if (i + 1 < currentLevel.size()) {
                    nextLevel.add(SHA256.generateSHA256(currentLevel.get(i) + currentLevel.get(i + 1)));
                } 
                else {
                    // handling the last, clone to pair
                    nextLevel.add(SHA256.generateSHA256(currentLevel.get(i) + currentLevel.get(i)));
                }
            }
            //renew the currentLevel (shrink it) 
            currentLevel = nextLevel;
        }
        return currentLevel.get(0); 
    }

    /**
     * Searches for the input data in the Merkle Tree.
     * 
     * @param inputData the data to search for.
     * @return true if the data is in the tree, false otherwise.
     */
    public boolean search(String inputData){
        //if input is null or empty means it cannot be on the tree
        if(inputData == null || inputData.isEmpty()){
            return false;
        }

        String targetHash = SHA256.generateSHA256(inputData);
        List<String> currentLevel = new ArrayList<>();
        //Initialize currentLevel by adding all data stored on "Block" before into the list
        for(int i=0; i<this.dataBlocks.length; i++){
            currentLevel.add(SHA256.generateFileSha256(this.dataBlocks[i]));
        }

        
        while(currentLevel.size() > 1){
            List<String> nextLevel = new ArrayList<>();  
            for(int i=0; i<currentLevel.size(); i+=2){
                String left = currentLevel.get(i);
                //if 
                String right = ( i + 1 < currentLevel.size()) ? currentLevel.get(i+1) : left;
                String parentHash = SHA256.generateSHA256(left+right);
                nextLevel.add(parentHash);  

                // If either the left or right hash matches the target hash,
                // update the target hash to be the parent hash
                if(left.equals(targetHash)|| right.equals(targetHash)){
                    targetHash = parentHash;
                }
            }
            currentLevel = nextLevel;
        }
        return targetHash.equals(merkleRoot);
    }

    /**
     * Gets the Merkle root.
     * 
     * @return the Merkle root.
     */
    public String getMerkleRoot(){
        return merkleRoot;
    }
}