import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

class Chain {

    private Block headBlock;
    private LinkedBlockingQueue<String> transactionQueue;
    private static ExecutorService pool = Executors.newFixedThreadPool(2);

    public Chain() {
        headBlock = new Block();
        transactionQueue = new LinkedBlockingQueue<>();
        pool.submit(this::makeBlock);
    }


    /**
     * This ensures that only one thread can modify the headBlock reference at a time, 
     * preventing race conditions. 
     * @param newBlock the block that should be add on chain, replacking the head
     */
    public synchronized void addBlock(Block newBlock) {
        if (headBlock == null) {
            headBlock = newBlock;
        } else {
            newBlock.setNextBlock(headBlock);
            headBlock = newBlock;
        }
    }

    public synchronized void addTransaction(String transaction) {

        transactionQueue.add(transaction);
        System.out.println("Transaction queue size: "+ this.transactionQueue.size());
        if (transactionQueue.size() >= 4) {
            // Notify the waiting thread (makeBlock) that there are transactions available
            notify(); 
        }
    }

    public synchronized void makeBlock() {
        try {
            String[] transactions = new String[4];
            while (true) {
                // Wait until the queue size reaches 4
                waitUntilQueueSizeReached(4); 
                System.out.println("Add Block when queue size: "+this.transactionQueue.size());
                for (int i = 0; i < 4; i++) {
                    // Take transactions from the queue
                    transactions[i] = transactionQueue.take(); 
                }
                // Process the transactions and add block to the chain
                addBlock(new Block(headBlock, transactions));
                 
                // Check if the queue is empty before proceeding
                if (transactionQueue.isEmpty()) {
                    // If the queue is empty, no need to add another block immediately
                    wait();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Block creation thread interrupted.");
        }
    }

    private synchronized void waitUntilQueueSizeReached(int size) throws InterruptedException {
        // Wait until the queue size reaches the desired size
        while (transactionQueue.size() < size) {
            wait(); 
        }
    }

    public void stop() {
        pool.shutdown();
        Thread.currentThread().interrupt();
    }

    

    public Block search(String targetHash) {
        Block currentBlock = headBlock;
        while (currentBlock != null) {
            if (currentBlock.getBlockHash().equals(targetHash)){
                return currentBlock;
            }
            currentBlock = currentBlock.getNextBlock();
        }
        System.err.println("Not found");
        return null; 
    }

    public Block searchTransaction(String targetHash) {
        Block currentBlock = headBlock;
        while (currentBlock != null) {

            for(int i=0; i<currentBlock.getTransactions().length; i++)
            if (currentBlock.getTransactions()[i].equals(targetHash)){
                return currentBlock;
            }
            currentBlock = currentBlock.getNextBlock();
        }
        return null; 
    }

    public void showTransactionOnTransactionQueue(){
        for(String tr: this.transactionQueue){
            System.out.println(tr);
        }
    }

    public void showTransactionOnBlock(){
        Block currentBlock = this.headBlock;
        while(currentBlock!=null){
            System.out.println("------------------------------");
            for(String s : currentBlock.getTransaction()){
                System.out.println(s);
            }
            System.out.println("------------------------------");
            currentBlock = currentBlock.getNextBlock();
        }
    }

    public void showChainInfo(){
        Block root = headBlock;
        int i = 0;
        while(root != null){

            System.out.println("Index: "+ i + ":"+root.getBlockHash()+" transactionSize: "+ root.getTransactionCounts());
            root = root.getNextBlock();
            i++;
        }
    }

    public Block getHead(){
        return this.headBlock;
    }

    public LinkedBlockingQueue<String> getTransactionQueue() {
        return transactionQueue;
    }

    public static ExecutorService getPool() {
        return pool;
    }
}

class TransactionHandler implements Runnable {
    private final Chain chain;
    private String transaction;

    public TransactionHandler(Chain chain, String transaction) {
        this.chain = chain;
        this.transaction = transaction;
    }

    @Override
    public void run() {
        chain.addTransaction(transaction);
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }
}

public class ChainMain {
    public static void main(String[] args) {
        Chain chain = new Chain();

        // Submit transactions to the Chain's queue
        for (int i = 0; i < 12; i++) {
            System.out.println("loop count: "+ i);
            String transaction = "Transaction " + i;
            //chain.showTransactionOnTransactionQueue();
            Chain.getPool().submit(new TransactionHandler(chain, transaction));
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                Thread.interrupted();
               e.printStackTrace();
            }
        }

        chain.showChainInfo();

       
        System.out.println("\n\n\n");
        chain.showTransactionOnBlock();
        


        chain.stop();
        
        
    }
}
