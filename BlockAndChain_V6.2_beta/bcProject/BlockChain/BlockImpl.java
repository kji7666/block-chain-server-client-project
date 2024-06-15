package bcProject.BlockChain;
import java.util.logging.Logger;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Random;
import java.util.Date;

/**
 * Represents a block in the blockchain.
 * Each block contains transactions, a Merkle tree root hash, a timestamp, and other metadata.
 * This class also manages the creation of the genesis block and subsequent blocks.
 * 
 * The block is identified by its current hash and linked to the previous block via its hash.
 * It includes mechanisms for validating transactions and adjusting the mining difficulty.
 * 
 * @author Harris
 * @since June 03, 2024
 */
public class BlockImpl implements Block, Serializable {
    //is_Test for testing usage 
    transient private static boolean NONCE_TEST = false;
    transient private static boolean TIMEOUT_TEST = true;
    transient private Logger logger = Logger.getLogger(BlockImpl.class.getName());
    transient private static final long TIMEOUT_THRESHOLD_FOR_MAKING_ONE_BLOCK = 600000; //10 minutes

    private String previousBlockHash;
    private String currentBlockHash;
    private String merkleRootHash;

    private MerkleTree merkleRoot;
    private BlockImpl nextBlock;
    private Date timestamp;

    private int transactionCount;
    private String[] transactions;
    private String[] transactionIDs;
    private int height;
    transient private int difficulty;
    private long nonce;
    transient private long blockProcessTime;
    
    /**
     * Creates the genesis block with random transactions.
     * The genesis block is the first block in the blockchain.
     */
    public BlockImpl(){
        //making random transaction
        //count : random transaction number, length: random transaction input length
        this.timestamp = new Date(); // Set the creation time to the current date
        
        Random random = new Random();
        this.nonce = Long.valueOf(random.nextInt(1000));
        int size = random.nextInt(4)+1;
        int length = random.nextInt(1000)+1;
        
        this.transactions = new String[size];
        this.transactionCount = size;
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for(int i=0; i<size; i++){
            StringBuilder genesisTransaction = new StringBuilder();
            
            for (int j = 0; j < length; j++) {
                genesisTransaction.append(characters.charAt(random.nextInt(characters.length())));
            }
            this.transactions[i] = genesisTransaction.toString();
        }

        this.height = 0;
        this.merkleRoot = new MerkleTreeImpl(this.transactions);
        this.merkleRootHash = this.merkleRoot.getMerkleRoot();
        this.currentBlockHash = generateBlockHash(this.merkleRootHash, this.height, this.transactionCount, this.nonce);
        this.previousBlockHash = this.currentBlockHash;
        this.blockProcessTime = 0;
    }

    /**
     * Creates a new block linked to the previous block.
     * 
     * @param previousBlock the previous block in the blockchain
     * @param transactions  an array of transaction strings
     */
    public BlockImpl(BlockImpl previousBlock, String[] transactions){ 
        /** Handle timeStamp and other info */
        this.height = previousBlock.height + 1;
        this.timestamp = new Date();

        /** Handle Hash Info */
        this.previousBlockHash = previousBlock.currentBlockHash;
        this.merkleRoot = new MerkleTreeImpl(transactions);
        this.merkleRootHash = this.merkleRoot.getMerkleRoot();

    
        /** Handle Transaction's info */
        // Deep copy(shallow but new object for String[]) to ensure immutability @see Chain make block
        // We ensure the data is corresponding in the same index of transactions[] and trasactionsID[]
        this.transactionCount = transactions.length;
        this.transactions = transactions.clone();//new object 
        int count = this.transactionCount;
        this.transactionIDs = new String[count];
        for(int i=0; i<count; i++){
            //Considering the uniqueness of TransactionID we plus the index
            //when using merkletree search please be catious here
            this.transactionIDs[i] = SHA256.generateFileSha256(this.transactions[i]+String.valueOf(i));
        }

        
        // Temporarily set current block's difficulty same as previous one
        //since we cannot know the difficulty of uncreated block(this block) in advance
        this.difficulty = previousBlock.getDifficulty();
        
        /** Handle nonce value and create a current blockHash */
        this.nonce = 0;
        //Preprocess for leading zero counting
        String[] preProcessDataForLeadingZero = new String[]{
            this.previousBlockHash,
            this.merkleRootHash,
            SHA256.generateSHA256(transactions),
            String.valueOf(this.transactionCount),
            String.valueOf(this.height),
            "0"//we initialize the current block's nonce as zero
        };
        //previous difficulty may get change so it is not necssary same as leadingzeros of previousBlock Hash
        //need revising here
        int leadingZeros = difficulty < SHA256.countLeadingZeros(previousBlock.currentBlockHash) ? difficulty : SHA256.countLeadingZeros(previousBlock.currentBlockHash);
        leadingZeros = SHA256.countLeadingZeros(this.previousBlockHash);
        setCurrentBlockHash(leadingZeros, preProcessDataForLeadingZero);
        System.out.println("Estimated process time: "+ this.blockProcessTime+" seconds");

        DifficultyHandler difficultyHandler = DifficultyHandler.getInstance();
        //If the time for creating this current block is to long, then we reset this difficulty for this block 
        this.difficulty = difficultyHandler.adjustDifficulty(this.blockProcessTime, difficulty); 
    }


    /**
     * Sets the current block hash based on the provided leading zero requirement and preprocessed data.
     * 
     * @param leadingZeros the number of leading zeros required in the hash
     * @param preProcessDataForLeadingZero the data used to generate the hash
     */
    private void setCurrentBlockHash(int leadingZeros, String[] preProcessDataForLeadingZero){
        String base64Nonce;
        int currentLeadingZeros = 0;
        long startTime = System.currentTimeMillis();
        long endTime = 0;
        boolean timeout = false;
        
        while (true) {
            this.nonce++;

            preProcessDataForLeadingZero[5] = String.valueOf(this.nonce);
            this.currentBlockHash = SHA256.generateSHA256(preProcessDataForLeadingZero);
            currentLeadingZeros = SHA256.countLeadingZeros(this.currentBlockHash);
            
            endTime = System.currentTimeMillis();
            if(endTime - startTime > TIMEOUT_THRESHOLD_FOR_MAKING_ONE_BLOCK){
                timeout = true;
            }

            if (currentLeadingZeros > leadingZeros || timeout) {
                // If a valid block is found or timeout occurs, exit the loop
                break; 
            }
            
            if(NONCE_TEST){
                base64Nonce = Base64NoPadding.toBase64(new BigInteger(String.valueOf(this.nonce)));
                System.out.print(base64Nonce);
                //System.out.println(nonce);
                //System.out.println(Base64NoPadding.decodeBase64(base64Nonce)); 
            }
        } 
        //seconds
        this.blockProcessTime = (endTime - startTime)/1000;
        if(TIMEOUT_TEST){
            System.out.println("startTime: "+startTime+" (millis)");
            System.out.println("endTime: "+endTime+" (millis)");
        }


        if(timeout && TIMEOUT_TEST){
            logger.info("[Timeout: 10 minutes] in creating current block. [Hegiht] "+String.valueOf(this.height));
        }
    }
    
    
    /**
     * Generates the hash for the block.
     * 
     * @param merkleRootHash the Merkle root hash
     * @param height the block height
     * @param transactionCount the number of transactions
     * @param nonce the nonce value
     * @return the block hash
     */
    private String generateBlockHash(String merkleRootHash, int height, int transactionCount, long nonce) {
        return SHA256.generateSHA256(new String[]{
            merkleRootHash,
            String.valueOf(height),
            String.valueOf(transactionCount),
            String.valueOf(nonce)
        });
    }
    

    protected long getBlockProcessTime(){
        return this.blockProcessTime;
    }

    //set nextBlock's reference need to change to protected
    protected void setNextBlock(BlockImpl nextBlock){
        this.nextBlock = nextBlock;
    }

    @Override
    public String getTimestamp() {
        return this.timestamp.toString();
    }

    @Override
    public BlockImpl getNextBlock(){
        return nextBlock;
    }
    
    @Override
    public String getPreviousBlockHash() {
        return this.previousBlockHash;
    }

    @Override
    public String getCurrentBlockHash() {
        return this.currentBlockHash;
    }

    @Override
    public String getMerkleRootHash() {
       return this.merkleRootHash;
    }

    @Override
    public MerkleTree getMerkleRoot() {
        return this.merkleRoot;
    }

    @Override
    public int getTransactionCount() {
        return this.transactionCount;
    }

    @Override
    public String[] getTransactions() {
       return this.transactions;
    }

    @Override
    public String[] getTransactionIds() {
        return this.transactionIDs;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public long getNonce() {
        return this.nonce;
    }

    @Override
    public int getDifficulty() {
       return this.difficulty;
    }

    /**
     * Displays the information of a specific block.
     */
     @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("------------------------------------------------------\n");
        sb.append("Height:             ").append(this.height).append("\n");
        sb.append("CurrentBlockHash:   ").append(this.currentBlockHash).append("\n");
        sb.append("PreviousBlockHash:  ").append(this.previousBlockHash).append("\n");
        sb.append("MerkleRootHash:     ").append(this.merkleRootHash).append("\n");
        sb.append("TransactionCount:   ").append(this.transactionCount).append("\n");
        sb.append("Transactions: \n");
        for(int i=0; i<this.transactions.length; i++){
            sb.append(this.transactions[i]).append("\n");
        }
        sb.append("ProcessTime:        ").append(this.blockProcessTime).append("\n");
        sb.append("Difficulty:         ").append(this.getDifficulty()).append("\n");
        sb.append("Nonce:              ").append(this.nonce).append("\n");
        sb.append("------------------------------------------------------\n");
        return sb.toString();
    }

    /**
     * Displays the information of all blocks starting from the given root block.
     * 
     * @param rootBlock the root block from which to start displaying information
     */
    protected void showAll(BlockImpl rootBlock){
        BlockImpl currentBlock = rootBlock;
        while(currentBlock != null){
            System.out.println(currentBlock.toString());
            currentBlock = currentBlock.nextBlock;
        }
    }
}
