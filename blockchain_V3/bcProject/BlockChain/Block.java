package bcProject.BlockChain;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;
import java.util.Date;

/** Block on BlockChain
 * 
 * 
 * Block defines what info to be stored in a block
 * To label the block on the chain for fast searching
 * Merkle tree's root hash to ensure the file is on the path when searching
 * transaction on this block(1~4), block created time, block size in byte
 * 
 * @author Harris
 */
public class Block {
    //Test for block nonce
    private boolean is_Test = true;

    public static boolean EMERGENCY_LEVEL5 = true;
    private String previousBlockHash;
    private String BlockHash;
   
    private String rootHash;
    private int height;

    private String transactionCounts;
    private Date timestamp;
    private long nonce;


    private String[] transactions;
    private String[] transactionIDs;
    private MerkleTree merkleRoot;
    private int difficulty;
    private Block nextBlock;
    private long blockProcessTime;
    /** Create Genesis Block when newing a Chain object
     *  the genesisNonce : 1~1000
     * 
     *  @author Harris
     *  @since  2024/06/03
     *  @param  size
     *          genesisTransactionSize: 1~4 strings
     *  @param  length
     *          genesisEachTransactionContent: 1~1000 charactoers
     *  @param  nonce 
     *          genesisNonce: 1~1000
     */
    public Block(){
        //making random transaction
        //count : random transaction number, length: random transaction input length
        this.timestamp = new Date(); // Set the creation time to the current date
        //this.size = String.valueOf(calculateSize());
        
        
        Random random = new Random();
        this.nonce = Long.valueOf(random.nextInt(1000));
        int size = random.nextInt(4)+1;
        int length = random.nextInt(1000)+1;
        
        this.transactions = new String[size];
        this.transactionCounts = String.valueOf(size);
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for(int i=0; i<size; i++){
            StringBuilder genesisTransaction = new StringBuilder();
            
            for (int j = 0; j < length; j++) {
                genesisTransaction.append(characters.charAt(random.nextInt(characters.length())));
            }
            this.transactions[i] = genesisTransaction.toString();
        }

        this.height = 0;
        this.merkleRoot = new  MerkleTree(Arrays.asList(this.transactions));
        this.rootHash = this.merkleRoot.getMerkleRoot();
        //this.blockProcessTime = 0;


        this.BlockHash = SHA256.generateSHA256(new String[]{this.rootHash,
        String.valueOf(this.height), this.transactionCounts, String.valueOf(this.nonce)});
        this.previousBlockHash = this.BlockHash;
    }

    /** This constructor creats blocks except for genesisBlock
     * 
     * @param  previousBlock the older head of node ready to be replace by this one
     * @param  transactions  given files 1~4 stored as a string respectively
     *  
     * @author Harris
     * 
     *  by counting the blockProcessTime and then comparing the time and previousBlockDifficulty
     * in the  DiffcultyHandler to adjust the difficulty of this block and then "may"
     * reset the current block's difficulty under certain condition in DifficultyHandler           
     * @since  2024/06/04
     */
    public Block(Block previousBlock, String[] transactions){ 
        long startTime = System.nanoTime();
        /** Handle timeStamp and other info */
        this.height = previousBlock.height + 1;
        this.timestamp = new Date();

        /** Handle Hash Info */
        this.previousBlockHash = previousBlock.BlockHash;
        this.merkleRoot = new MerkleTree(Arrays.asList(transactions));
        this.rootHash = this.merkleRoot.getMerkleRoot();

    
        /** Handle Transaction's info */
        // Deep copy to ensure immutability
        // see Chain make block
        this.transactions = transactions.clone();
        this.transactionCounts = String.valueOf(transactions.length);
        int count = Integer.valueOf(this.transactionCounts);
        this.transactionIDs = new String[count];
        for(int i=0; i<count; i++){
            transactionIDs[i] = SHA256.generateFileSha256(this.transactions[i]);
        }

        
        // Temporarily set current block's difficulty same as previous one
        //since we cannot know the difficulty of uncreated block(this block) in advance
        this.difficulty = previousBlock.difficulty;
        
        /** Handle nonce value and create a current blockHash */
        this.nonce = 0;
        //Preprocess for leading zero counting
        String[] preProcessDataForLeadingZero = new String[6];
        preProcessDataForLeadingZero[0] = previousBlock.BlockHash;
        preProcessDataForLeadingZero[1] = this.rootHash;
        preProcessDataForLeadingZero[2] = SHA256.generateSHA256(transactions);
        preProcessDataForLeadingZero[3] = this.transactionCounts;
        preProcessDataForLeadingZero[4] = String.valueOf(this.height);
        //we initialize the current block's nonce as zero
        preProcessDataForLeadingZero[5] = "0";
        //previous difficulty may get change so it is not necssary same as leadingzeros of previousBlock Hash
        int leadingZeros = difficulty < SHA256.countLeadingZeros(previousBlock.BlockHash) ? difficulty : SHA256.countLeadingZeros(previousBlock.BlockHash);
        leadingZeros = SHA256.countLeadingZeros(previousBlock.BlockHash);
        setCurrentBlockHash(leadingZeros, preProcessDataForLeadingZero);
        long endTime = System.nanoTime();
        this.blockProcessTime = endTime - startTime;
        

        DifficultyHandler difficultyHandler = DifficultyHandler.getInstance();
        //If the time for creating this current block is to long, then we reset this difficulty for this block 
        this.difficulty = difficultyHandler.adjustDifficulty(blockProcessTime, difficulty); 
    }


    private void setCurrentBlockHash(int leadingZeros, String[] preProcessDataForLeadingZero){
        String base64Nonce;
        while (true) {
            preProcessDataForLeadingZero[5] = String.valueOf(this.nonce);
            this.BlockHash = SHA256.generateSHA256(preProcessDataForLeadingZero);

            int currentLeadingZeros = SHA256.countLeadingZeros(this.BlockHash);
            if (currentLeadingZeros > leadingZeros) {
                //If Valid block found, exit the loop, and set the final nonces
                break; 
            }
            //For testing
            base64Nonce = Base64NoPadding.toBase64(new BigInteger(String.valueOf(this.nonce)));
            System.out.print(base64Nonce);
            //System.out.println(nonce);
            //System.out.println(Base64.decodeBase64WithoutPadding(base64Nonce));
        
            this.nonce++;
        } 
    }
    ////turn protected!!!!
    /**
     * Date.toString can show the information
     * @return Date Object
     */
    public Date getTimestamp() {
        return new Date(timestamp.getTime()); // Return a defensive copy
    }

    //set nextBlock's reference
    public void setNextBlock(Block nextBlock){
        this.nextBlock = nextBlock;
    }

    //get nextBlock's reference
    public Block getNextBlock(){
        return nextBlock;
    }

    // public static void main(String[] args) throws InterruptedException {
    //     Block genesisBlock = new Block();
    //     genesisBlock.showInfo(genesisBlock);
    //     Block height1Block = new Block(genesisBlock, new String[]{"IAM", "Harris"});
    //     height1Block.showInfo(height1Block);
    // }

    ///
    //turn protected after!!!
    ///
    public String getPreviousBlockHash() {
        return this.previousBlockHash;
    }
    public String getBlockHash() {
        return this.BlockHash;
    }
    public int getHeight() {
        return height;
    }
    public MerkleTree getMerkleRoot() {
        return merkleRoot;
    }
    public long getNonce() {
        return nonce;
    }
    public String getRootHash() {
        return rootHash;
    }
    public String getTransactionCounts() {
        return transactionCounts;
    }
    public String[] getTransactions() {
        return transactions;
    }
    public String[] getTransactionIds(){
        return this.transactionIDs;
    }
    public int getDifficulty() {
        return difficulty;
    }
    public long getBlockProcessTime(){
        return blockProcessTime;
    }


    /**Input one spcific block, and show that block's info*/
    protected void showInfo(Block block){
        System.out.println("------------------------------------------------------");
        System.out.println("Height:             "+block.height);
        System.out.println("BlockHash:          "+block.BlockHash);
        System.out.println("PreviousBlcokHash:  "+block.previousBlockHash);
        System.out.println("MerkleRoot:         "+block.rootHash);
        System.out.println("TransactionCounts:  "+block.transactionCounts);
        System.out.println("Transaction: ");
        for(int i=0; i<block.transactions.length;i++){
            System.out.println(block.transactions[i]+"\n");
        }
        System.out.println("processTime:        "+block.blockProcessTime); 
        System.out.println("Difficulty:         "+String.valueOf(block.getDifficulty()));
        System.out.println("nonce:              "+String.valueOf(block.nonce));
        System.out.println("------------------------------------------------------");
        //System.out.println("NextBlockReference: " +block.nextBlock);
    }

    /**Input rootBlock will show all the blocks info */
    protected void showAll(Block rootBlock){
        Block currentBlock = rootBlock;
        while(currentBlock != null){
            System.out.println("------------------------------------------------------");
            System.out.println("Height:             "+currentBlock.height);
            System.out.println("BlockHash:          "+currentBlock.BlockHash);
            System.out.println("PreviousBlcokHash:  "+currentBlock.previousBlockHash);
            System.out.println("MerkleRoot:         "+currentBlock.rootHash);
            System.out.println("TransactionCounts:  "+currentBlock.transactionCounts);
            System.out.println("Transaction: ");
            for(int i=0; i<currentBlock.transactions.length;i++){
                System.out.println(currentBlock.transactions[i]+"\n");
            }
            System.out.println("processTime:        "+currentBlock.getBlockProcessTime()); 
            System.out.println("Difficulty:         "+String.valueOf(currentBlock.getDifficulty()));
            System.out.println("nonce:              "+String.valueOf(currentBlock.nonce));
            //System.out.println("NextBlockReference: " +currentBlock.nextBlock);
            System.out.println("------------------------------------------------------");
            currentBlock = currentBlock.nextBlock;
        }
    }
}
