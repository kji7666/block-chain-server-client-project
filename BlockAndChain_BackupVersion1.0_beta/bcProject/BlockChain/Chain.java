package bcProject.BlockChain;
import java.util.logging.Logger;
import bcProject.BlockBackupAuthority.BackupService;
import bcProject.BlockBackupAuthority.BlockchainBackupHandler;
import bcProject.BlockBackupAuthority.DigitalSignatureService;
import bcProject.BlockBackupAuthority.SHA256SignatureHandler;
import java.util.concurrent.LinkedBlockingQueue;

/** Use static way to get the Singleton of Chain
 * Chain chain = Chain.getInstance();
 * 
 * @author harris
 * 
 */
public class Chain implements BlockChain{
    private static final boolean is_Test = true;
    private static final boolean TIMEOUT_VERSION = false;
    private static final long TIMEOUT_THRESHOLD_FOR_WAITING_INCOMING_TRANSACTIONS = 60000;// 1 min
    private static final String BACKUP_DIRECTORY_NAME = "blockBackup";

    private static Chain instance;
    //private static ExecutorService pool = Executors.newFixedThreadPool(2);
    private static final Logger logger = Logger.getLogger((Chain.class.getName()));
    // private static final String REBUILD_DIRECTORY_NAME = "blockBackup";

    private BackupService backupHandler;
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
        //pool.submit(this::queueSizeChecking);
        ////backupusage
        backupHandler = new BlockchainBackupHandler(new SHA256SignatureHandler(BACKUP_DIRECTORY_NAME));
        //SHA256withRSA digital signature restore the backuped file  
        headBlock = new BlockImpl();
        transactionQueue = new LinkedBlockingQueue<>();
    }

    public void queueSizeChecking(){
        if(is_Test){
            logger.info("checking....");
        }

        boolean timeout = false;
        long startTime = System.currentTimeMillis();

        while(isRunning()){
            if(this.transactionQueue.size()>=4){
                addBlock();
            }

            if(TIMEOUT_VERSION){
                //check time in each loop
                // logger.info("transactionQueue : " + transactionQueue.size());
                // logger.info("chain height : " + headBlock.getHeight());
                long currentTime = System.currentTimeMillis();

                if((currentTime - startTime) > TIMEOUT_THRESHOLD_FOR_WAITING_INCOMING_TRANSACTIONS){
                    timeout = true;
                }
                //if queue size >= 4 or when timeout and size not 0
                int queueSize = this.transactionQueue.size();
                if(queueSize >= 4 || (queueSize !=0 && timeout)){
                    timeout = false;
                    //addBlock(queueSize);
                    startTime = System.currentTimeMillis();
                }
                //sleep?? busy-waiting??to prevent busy-waiting, which would consume CPU resources unnecessarily.
                //sleep(1000, "Queue size checking thread interrupted.");
            }
            
            
        }
    }

    //what if timeout then we take queue's transaction to make a block but at this time
    //a new transaction is added on queue?

    private void addBlock(){
        if(is_Test){
            logger.info("add block");
        }

        //int queueSize;
        ////timeout usage
        // Determine the number of transactions to include in the block.
        // If there is a timeout, the queue size may range from 1 to 3.
        // If there is no timeout, the queue size is at least 4.
        // In either case, we only include a maximum of 4 transactions in one block.
        // int transactionsSize = 0;
        // if (queueSize < 4) {
        //     transactionsSize = queueSize;
        // } else {
        //     transactionsSize = 4;
        // }
        int transactionsSize = 4;
        BlockImpl newBlock;
        String[] transactions = new String[transactionsSize];
        for(int i=0; i<transactionsSize; i++){
            //already sure the queue size >=4
            transactions[i] = this.transactionQueue.poll();
        }

        newBlock = new BlockImpl(headBlock, transactions);
        //backup the block here
        backupBlock(newBlock);

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

        this.transactionQueue.add(transaction);
    }

    @Override
    public void addTransaction(String transaction) {

        // if(is_Test){
        //     logger.info("in add Transaction");
        // }
        
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
                    if(currentBlock.getTransactionIds()[i].equals(TXID)){
                        //found the exist id, and then grab the data from the corresponding tr array
                        //and use merkletree search the data
                        String queryingTransaction = currentBlock.getTransactions()[i];
                        if(currentBlock.getMerkleRoot().search(queryingTransaction)){
                            return queryingTransaction;
                        }
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

    private void backupBlock(BlockImpl block){
        try{
            backupHandler.backupBlock(block);
            logger.info("Block backed up to: " + BACKUP_DIRECTORY_NAME + "/Block"+String.valueOf(block.getHeight())+".ser");
        }
        catch(Exception e){
            logger.severe("Failed to backup block.");
            e.printStackTrace();
        } 
    }

    private BlockchainBackupHandler createRestoreTool(String base64PublicKey){
        DigitalSignatureService signatureHandler = null;
        try {
            signatureHandler = new SHA256SignatureHandler(base64PublicKey, true);
            signatureHandler.setPublicKey(base64PublicKey);
            return new BlockchainBackupHandler(signatureHandler, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void rebuildChain(int previousBlockchainLen, String base64PublicKey){
        this.backupHandler = createRestoreTool(base64PublicKey);
        int processingSeq = 0;
        try {
            //Restore the genesis block (file with sequence 0)
            this.headBlock = backupHandler.restoreBlock(SHA256SignatureHandler.convertToPublicKey(base64PublicKey), 0);

            // Restore subsequent blocks (files with sequence from 1 to blockchainLength - 1)
            for (int i = 1; i < previousBlockchainLen; i++) {
                backupHandler.restoreBlock(SHA256SignatureHandler.convertToPublicKey(base64PublicKey), i);
                processingSeq = i;
        }
        } catch (Exception e) {
            logger.severe("Fail to restore previous block Height: " + String.valueOf(processingSeq));
            return;
        }
        logger.info("Complete in rebuilding the previous Blockchain");
        return;
    }
}