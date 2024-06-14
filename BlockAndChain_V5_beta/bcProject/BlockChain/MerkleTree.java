package bcProject.BlockChain;

import java.util.List;

public interface MerkleTree{
    public String buildMerkleTree(List<String> dataBlocks);
    public boolean search(String inputData);
    public String getMerkleRoot();
}
