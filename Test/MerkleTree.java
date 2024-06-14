package project.block_chain.Test;

import java.util.List;

public interface MerkleTree{
    public String buildMerkleTree(List<String> dataBlocks);
    public boolean search(String inputData);
    public String getMerkleRoot();
}