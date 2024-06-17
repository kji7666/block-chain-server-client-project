package project.block_chain.BlockChain;

public interface MerkleTree{
    public boolean search(String inputData);
    public String getMerkleRoot();
}
