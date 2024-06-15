package bcProject.BlockChain;
import java.util.logging.Logger;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/** Use static way to get the Singleton of Chain
 * Chain chain = Chain.getInstance();
 * 
 * @author harris
 * 
 */
public class Chain implements BlockChain{
    private static Chain instance;
    private static ExecutorService pool = Executors.newFixedThreadPool(2);
    private static final Logger logger = Logger.getLogger((Chain.class.getName()));
    private static final boolean is_Test = true;
    private static final long TIMEOUT_THRESHOLD_FOR_WAITING_INCOMING_TRANSACTIONS = 60000;// 1 min
    private static final String BACKUP_DIRECTORY_NAME = "blockBackup";
    private static final String REBUILD_DIRECTORY_NAME = "blockBackup";

    private BlockchainBackupHandler backupHandler;
    private BlockImpl headBlock;
    private LinkedBlockingQueue<String> transactionQueue;
    private boolean operatingStatus;
    private int chainHeight;
    private int TransactionCount;
    

    public static Chain getInstance(){
        if(instance == null){
            instance = new Chain();
        }
        return instance;
    }

    private Chain(){
        operatingStatus = true;
        pool.submit(this::queueSizeChecking);
        backupHandler = new BlockchainBackupHandler();
        //SHA256withRSA digital signature restore the backuped file  
        backupHandler.setBackupDirectory(BACKUP_DIRECTORY_NAME);
        
        headBlock = new BlockImpl();
        transactionQueue = new LinkedBlockingQueue<>();
    }

    private void queueSizeChecking(){
        if(is_Test){
            logger.info("checking....");
        }

        boolean timeout = false;
        long startTime = System.currentTimeMillis();

        while(isRunning()){
            //check time in each loop
            long currentTime = System.currentTimeMillis();

            if((currentTime - startTime) > TIMEOUT_THRESHOLD_FOR_WAITING_INCOMING_TRANSACTIONS){
                timeout = true;
            }
            //if queue size >= 4 or when timeout and size not 0
            int queueSize = this.transactionQueue.size();
            if(queueSize >= 4 || (queueSize !=0 && timeout)){
                timeout = false;
                addBlock(queueSize);
                startTime = System.currentTimeMillis();
            }
            //sleep?? busy-waiting??to prevent busy-waiting, which would consume CPU resources unnecessarily.
            //sleep(1000, "Queue size checking thread interrupted.");
        }
    }

    //what if timeout then we take queue's transaction to make a block but at this time
    //a new transaction is added on queue?

    private void addBlock(int queueSize){
        if(is_Test){
            logger.info("add block");
        }

        // Determine the number of transactions to include in the block.
        // If there is a timeout, the queue size may range from 1 to 3.
        // If there is no timeout, the queue size is at least 4.
        // In either case, we only include a maximum of 4 transactions in one block.
        int transactionsSize = 0;
        if (queueSize < 4) {
            transactionsSize = queueSize;
        } else {
            transactionsSize = 4;
        }
        
        BlockImpl newBlock;
        String[] transactions = new String[transactionsSize];
        for(int i=0; i<transactionsSize; i++){
            //already sure the queue size >=4
            transactions[i] = this.transactionQueue.poll();
        }

        newBlock = new BlockImpl(headBlock, transactions);
        //backup the block here


        this.chainHeight = newBlock.getHeight();
        if (headBlock == null) {
            headBlock = new BlockImpl(headBlock, transactions);
        } else {
            newBlock.setNextBlock(headBlock);
            headBlock = newBlock;
        }
    }

    private void addTransactionToQueue(String transaction){

        if(is_Test){
            logger.info("now queue size:" + transactionQueue.size());
        }

        System.out.println();
        this.transactionQueue.add(transaction);
    }

    @Override
    public void addTransaction(String transaction) {

        if(is_Test){
            logger.info("in add Transaction");
        }
        
        if(!isRunning()){
            logger.info("The Chain is not running. Unable to add a new transaction.");
        } 
        else if(transaction == null || transaction.equals("")){
            logger.info("The transaction cannot be null or empty");
        } 
        else{
            addTransactionToQueue(transaction);
        }
    }

    /**
     * Get Transaction by given Height of Block and Transaction ID to get the original data
     * @param height The query block height where TXID on
     * @param TXID   The document's SHA256 
     * @return if transaction exists, then return the original data
     *         else return null
     * @note NOT YET Testing!!!!!!!
     */
    @Override
    public String getTransactionData(int height, String TXID) {
        BlockImpl currentBlock = headBlock;
        if(height > getChainLatestHeight() || height < 0){
            logger.info("Invalid height : "+ String.valueOf(height));
        }
        else if(TXID == null){
            logger.info("Transaction ID cannot be null");
        }
        else{
            int estimatedPathLong = currentBlock.getHeight() - height;
            for(int i=0; i<estimatedPathLong; i++){
                currentBlock = currentBlock.getNextBlock();
            }

            while (currentBlock != null) {
                int trLen = currentBlock.getTransactions().length;
                for(int i=0; i<trLen; i++){
                    if(currentBlock.getTransactions()[i].equals(TXID)){
                        return currentBlock.getTransactions()[i];
                    }
                }
                currentBlock = currentBlock.getNextBlock();
            }
        }
        logger.info("Transaction not exists");
        return null;
    }


    private static void sleep(long millis, String note){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.info(note);
            e.printStackTrace();
        }
    }

    @Override
    public boolean isRunning() {
        return this.operatingStatus;
    }

    @Override
    public void stopChain() {
        this.operatingStatus = false;
    }

    @Override
    //get latest block's chain Height
    public int getChainLatestHeight() {
        return this.chainHeight;
    }

    @Override
    public int getTransactionCount() {
        return this.TransactionCount;
    }

    public int showTransactionQueueSize(){
        return this.transactionQueue.size();
    }

    @Override
    public Block getHead() {
        return this.headBlock;
    }

    // private void addBackupedBlock(BlockImpl newBlock){
    //     if(headBlock != null && newBlock != null) {
    //         newBlock.setNextBlock(headBlock);
    //         headBlock = newBlock;
    //     } 
    //     else {
    //         logger.severe("headBlock or backedupBlock cannot be null");
    //     }
    // }

    // @Override
    // public void rebuildChain(int blockchainLength) {
    //     //SHA256withRSA digital signature restore the backuped file   
    //     BlockchainBackupHandler backupHandler = new BlockchainBackupHandler();

    //     backupHandler.setRestoreDirectory(REBUILD_DIRECTORY_NAME);

    //     try {
    //         // Restore the genesis block (file with sequence 0)
    //         this.headBlock = backupHandler.restoreBlock();

    //         // Restore subsequent blocks (files with sequence from 1 to blockchainLength - 1)
    //         for (int i = 1; i < blockchainLength; i++) {
    //             File blockFile = new File(REBUILD_DIRECTORY_NAME + File.separator + i);
    //             if (blockFile.exists()) {
    //                 addBackupedBlock(backupHandler.restoreBlock());
    //             } else {
    //                 // If the block file doesn't exist, stop restoring further blocks
    //                 logger.warning("Block file " + blockFile.getAbsolutePath() + " does not exist.");
    //                 break;
    //             }
    //         }

    //     } catch (Exception e) {
    //         logger.severe("Failed to restore backup. Initializing the chain with a new genesis block.");
    //         this.headBlock = new BlockImpl(); 
    //         e.printStackTrace();
    //     }
    //     finally{
    //         backupHandler = null;
    //     }  
    // }

    // private void backupBlock(BlockImpl block){
    //     try{
    //         backupHandler.backupBlock(block);
    //         logger.info("Block backed up to: " + BACKUP_DIRECTORY_NAME + "/Block"+String.valueOf(block.getHeight())+".ser");
    //     }
    //     catch(Exception e){
    //         logger.severe("Failed to backup block.");
    //         e.printStackTrace();
    //     } 
    //     finally{
    //         backupHandler = null;
    //     }  
    // }
}


