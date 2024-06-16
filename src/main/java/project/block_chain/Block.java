package project.block_chain.Test;

public interface Block{
    public String getPreviousBlockHash();
    public String getCurrentBlockHash();
    public String getMerkleRootHash();

    public MerkleTree getMerkleRoot();
    public Block getNextBlock();

    public int getTransactionCount();
    public String[] getTransactions();
    public String[] getTransactionIds();
    public String getTimestamp();

    public int getHeight();
    public long getNonce();
    public int getDifficulty();
}
