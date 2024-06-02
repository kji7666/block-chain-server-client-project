import java.util.ArrayList;
import java.util.List;
import bcProject.BlockChain.SHA256;;

/** For data integrity usage
 * This code define each Merkle Tree on the Blockchain project's bloc
 *  The main purpose for the tree is to authenticate the user's givne data
 *  If his is the same as that stored in our blockchain
 * 
 * @author Harris
 * @author KJI
 * @since  June/02/2024
 * 
 */

public class MerkleTree{
    private List<String> dataBlocks;
    private String merkleRoot;

    /**
     *
     * @param dataBlocks
     *        it is list of file.txt strings
     */
    public MerkleTree(List<String> dataBlocks){
        if(dataBlocks == null || dataBlocks.isEmpty()){
            throw new IllegalArgumentException("Error in creating MerkleTree");
        }
        this.dataBlocks = dataBlocks;
        this.merkleRoot = buildMerkleTree(dataBlocks);
    }

    /**Use loop to create the root's hash, we classify each data's hash into pair
     * For example: Given a size 4 List 
     * -> we combine the hash: hash[(hash[hashA+hash])+(hash[hashC+hashD])]
     * @note   we will make sure each level has even number nodes
     *         if the number is odd we clone the last transaction's hash to make 
     *         up to an even number nodes
     * @param  dataBlocks
     *         The input data(1~4) store in List<String> dataBlocks
     * @return hash value of Merkle Tree Root
     */
    public String buildMerkleTree(List<String> dataBlocks){
        List<String> currentLevel = new ArrayList<>();


        //The initial level is hashing each data's hash
        //each data is a content(String) of a txt file recieve from client
        for(String block : dataBlocks){
            currentLevel.add(SHA256.generateSHA256(block));
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
     * 
     * @param  inputData
     * @return boolean 
     *         true  : the given data is on the tree
     *         fasle : the given data is not on the tree since it's final construct 
     *                 root hash value is not the same as that stored on the block(root)
     */
    public boolean search(String inputData){
        //if input is null or empty means it cannot be on the tree
        if(inputData == null || inputData.isEmpty()){
            return false;
        }

        String targetHash = SHA256.generateSHA256(inputData);
        List<String> currentLevel = new ArrayList<>();
        //Initialize currentLevel by adding all data stored on "Block" before into the list
        for (String block : dataBlocks){
            currentLevel.add(SHA256.generateSHA256(block));
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

    public String getMerkleRoot(){
        return merkleRoot;
    }
    public static void main(String[] args) {
        List<String> db = new ArrayList<>();
        db.add("HI I AM HArris");
        //db.add("HI ");
        //db.add("AM HArris");
        //db.add("cat 1230");

        MerkleTree mt = new MerkleTree(db);
        String searchBlock = "AM HArris";
        System.out.println(mt.search(searchBlock));
    }
}