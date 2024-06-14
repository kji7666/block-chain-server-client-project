package bcProject.BlockChain;

public interface BlockChain{
    void addTransaction(String transaction);
    String getTransactionData(int height, String TXID);
    int getHeight();
    int getTransactionCount();
    boolean isRunning();
    void stopChain();
}