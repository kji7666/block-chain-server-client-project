package project.block_chain.Test;
import java.util.logging.Logger;
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
        headBlock = new BlockImpl();
        transactionQueue = new LinkedBlockingQueue<>();
    }

    public void queueSizeChecking(){
        if(is_Test){
            System.out.println("checking....");
        }

        boolean timeout = false;
        long startTime = System.currentTimeMillis();

        while(isRunning()){
            //check time in each loop
            // logger.info("transactionQueue : " + transactionQueue.size());
            // logger.info("chain height : " + headBlock.getHeight());
            long currentTime = System.currentTimeMillis();

            if((currentTime - startTime) > TIMEOUT_THRESHOLD_FOR_WAITING_INCOMING_TRANSACTIONS){
                timeout = true;
            }
            //if queue size >= 4 or when timeout and size not 0
            if(this.transactionQueue.size() >= 4 || (this.transactionQueue.size() !=0 && timeout)){
                timeout = false;
                addBlock();
                startTime = System.currentTimeMillis();
            }
            //sleep?? busy-waiting??to prevent busy-waiting, which would consume CPU resources unnecessarily.
            //sleep(1000, "Queue size checking thread interrupted.");
        }
    }

    //what if timeout then we take queue's transaction to make a block but at this time
    //a new transaction is added on queue?

    private void addBlock(){
        if(is_Test){
            System.out.println("add block");
        }

        int size = 4;
        BlockImpl newBlock;
        String[] transactions = new String[size];
        for(int i=0; i<size; i++){
            //already sure the queue size >=4
            transactions[i] = this.transactionQueue.poll();
        }

        newBlock = new BlockImpl(headBlock, transactions);
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
            System.out.println("add transaction. now queue size:" + transactionQueue.size());
        }

        //System.out.println();
        this.transactionQueue.add(transaction);
    }

    @Override
    public void addTransaction(String transaction) {

        // if(is_Test){
        //     logger.info("in add Transaction");
        // }
        
        if(!isRunning()){
            System.out.println("The Chain is not running. Unable to add a new transaction.");
        } 
        else if(transaction == null || transaction.equals("")){
            System.out.println("The transaction cannot be null or empty");
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
            System.out.println("Invalid height : "+ String.valueOf(height));
        }
        else if(TXID == null){
            System.out.println("Transaction ID cannot be null");
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
                        return currentBlock.getTransactions()[i];
                    }
                }
                currentBlock = currentBlock.getNextBlock();
            }
        }
        System.out.println("Transaction not exists");
        return null;
    }


    private static void sleep(long millis, String note){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(note);
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
}