


import java.util.Arrays;
import java.util.Random;

import bcProject.BlockChain.DifficultyHandler;
import bcProject.BlockChain.MerkleTree;
import bcProject.BlockChain.SHA256;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/** Block defines what info to be stored in a block
 * To label the block on the chain for fast searching
 * Merkle tree's root hash to ensure the file is on the path when searching
 * transaction on this block(1~4), block created time, block size in byte
 * 
 * @author Harris
 */
public class Block {
    public static boolean EMERGENCY_LEVEL5 = true;
    private String previousBlockHash;
    private String BlockHash;
   
    private String rootHash;
    private int height;

    private String transactionCounts;
    private Date timestamp;
    private String size;
    private long nonce;


    private String[] transactions;
    private MerkleTree merkleRoot;
    private int difficulty;
    private Block nextBlock;
    
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


        //leadingzero and make many times


        this.BlockHash = SHA256.generateSHA256(new String[]{this.rootHash,
        String.valueOf(this.height), this.transactionCounts, String.valueOf(this.nonce)});
        this.previousBlockHash = this.BlockHash;
    }

    /** This constructor creats blocks except for genesisBlock
     *  by counting the blockProcessTime and then comparing the time and previousBlockDifficulty
     * in the  DiffcultyHandler to adjust the difficulty of this block and then "may"
     * reset the current block's difficulty under certain condition in DifficultyHandler
     * @author Harris
     * @author KJI
     * @param  previousBlock
     *         the older head of node ready to be replace by this one
     * @param  transactions
     *         given files 1~4 stored as a string respectively
     * @since  2024/06/04
     */
    public Block(Block previousBlock, String[] transactions){ 
        this.previousBlockHash = previousBlock.BlockHash;
        this.height = previousBlock.height + 1;
        this.transactions = transactions;
        this.nonce = 0;
        this.transactionCounts = String.valueOf(transactions.length);
        //we temporary set current block's difficulty same as previous one
        //since we cannot know the difficulty of uncreated block(this block) in advance
        this.difficulty = previousBlock.difficulty;
        this.timestamp = new Date();
        this.merkleRoot = new MerkleTree(Arrays.asList(transactions));
        this.rootHash = this.merkleRoot.getMerkleRoot();

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

        long startTime = System.currentTimeMillis();
        setCurrentBlockHash(leadingZeros, preProcessDataForLeadingZero);
        long endTime = System.currentTimeMillis();
        long blockProcessTime = endTime - startTime;
        
        if(EMERGENCY_LEVEL5){     
            System.out.println("blockProcessTime:     "+blockProcessTime);
            System.out.println("blockDifficulty:      "+difficulty);
        }
        
        DifficultyHandler difficultyHandler = DifficultyHandler.getInstance();
        //If the time for creating this current block is to long, then we reset this difficulty for this block 
        //this.difficulty = difficultyHandler.adjustDifficulty(blockProcessTime, difficulty); 

    }


    public void setCurrentBlockHash(int leadingZeros, String[] preProcessDataForLeadingZero){
        while (true) {
            preProcessDataForLeadingZero[5] = String.valueOf(this.nonce);
            this.BlockHash = SHA256.generateSHA256(preProcessDataForLeadingZero);

            int currentLeadingZeros = SHA256.countLeadingZeros(this.BlockHash);
            if (currentLeadingZeros > leadingZeros) {
                //If Valid block found, exit the loop, and set the final nonces
                break; 
            }
            this.nonce++;
        } 
    }

    /**
     * Caculate all the object's size in a block
     * @author Harris
     * @return size 
     *         a block's size
     */
    // Method to calculate the size of the block
    // public int calculateSize() {
    //     int size = 0;
    //     size += previousBlockHash != null ? previousBlockHash.getBytes(StandardCharsets.UTF_8).length : 0;
    //     size += BlockHash != null ? BlockHash.getBytes(StandardCharsets.UTF_8).length : 0;
    //     size += rootHash != null ? rootHash.getBytes(StandardCharsets.UTF_8).length : 0;
    //     //size += height != null ? height.getBytes(StandardCharsets.UTF_8).length : 0;
    //     size += transactionCounts != null ? transactionCounts.getBytes(StandardCharsets.UTF_8).length : 0;
    //     size += timestamp != null ? Long.BYTES : 0; // Date can be represented by a long
    //     size += Long.BYTES; // For nonce
    //     size += transactions != null ? calculateTransactionsSize(transactions) : 0;
    //     //size += merkleRoot != null ? merkleRoot.calculateSize() : 0; // Assuming MerkleTree has a method to calculate size
    //     return size;
    // }

    // Helper method to calculate the size of the transactions array
    // private int calculateTransactionsSize(String[] transactions) {
    //     int size = 0;
    //     for (String transaction : transactions) {
    //         size += transaction != null ? transaction.getBytes(StandardCharsets.UTF_8).length : 0;
    //     }
    //     return size;
    // }


    //get Block's Size
    //public String getSize() {
    //    return this.size;
    //}

    //get Block's timestamp
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


    public static void main(String[] args) throws InterruptedException {
        Block genesisBlock = new Block();
        genesisBlock.showInfo(genesisBlock);
        Block height1Block = new Block(genesisBlock, new String[]{"IAM", "Harris"});
        height1Block.showInfo(height1Block);
    }

    //     for(int i=0; i<Integer.valueOf(bt.transactionCounts); i++){
    //         System.out.println(bt.merkleRoot.search(bt.transactions[0]));
    //     }

    //     System.out.println(bt.getTimestamp().toString());

    //     //Not count all the object on it, need to revise
    //     System.out.println(bt.getSize());
    // }
    
    public String getPreviousBlockHash() {
        return this.previousBlockHash;
    }
    public String[] getTransaction() {
        return this.transactions;
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
    public int getDifficulty() {
        return difficulty;
    }


    //debug using
    public void showInfo(Block block){
        System.out.println("Height:             "+block.height);
        System.out.println("BlockHash:          "+block.BlockHash);
        System.out.println("PreviousBlcokHash:  "+block.previousBlockHash);
        System.out.println("MerkleRoot:         "+block.rootHash);
        System.out.println("TransactionCounts:  "+block.transactionCounts);
        System.out.println("Transaction: ");
        for(int i=0; i<block.transactions.length;i++){
            System.out.println(block.transactions[i]+"\n");
        }
        System.out.println("nonce:              "+String.valueOf(block.nonce));
        System.out.println("NextBlockReference: " +block.nextBlock);
    }
}
