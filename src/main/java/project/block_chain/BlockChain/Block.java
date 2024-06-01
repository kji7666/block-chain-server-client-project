package project.block_chain.BlockChain;

/** Block defines what info to be stored in a block
 * To label the block on the chain for fast searching
 * Merkle tree's root hash to ensure the file is on the path when searching
 * transaction on this block(1~4), block created time, block size in byte
 * 
 * @author Harris
 */
public class Block {
    public String previousHash;
    public String blockHash;
   
    public String rootHash;
    public String height;

    public String transactionCounts;
    public String age;
    public String size;
    public long nonce;


    public String[] transaction;
    //what if we cannot find a nonce to match the comparator?->new run

    public Block(String previousHash, String[] transaction){
        this.previousHash = previousHash;
        this.transaction = transaction;
        this.nonce = 0;

        String[] content = new String[3];
        content[0] = SHA256.generateSHA256(transaction);
        content[1] = previousHash;
        String strNonce = null;
        
        while(true){
            // the number of 0 is more than that of the previous one's
            if(this.blockHash!=null && (SHA256.countLeadingZeros(this.blockHash)>SHA256.countLeadingZeros(previousHash))){
                break;
            }
            else{
                strNonce = String.valueOf(nonce);
                content[2] = strNonce;
                this.blockHash = SHA256.generateSHA256(content);
                nonce++;
            }
        }
        this.blockHash = SHA256.generateSHA256(content);
    }

    public String getPreviousHash() {
        return previousHash;
    }
    public String[] getTransaction() {
        return transaction;
    }
    public String getBlockHash() {
        return blockHash;
    }
    public void showAllInfo(){
        StringBuilder outputs = new StringBuilder();
        for(String s : this.transaction){
            outputs.append(s);
        }
        System.out.println("Hash: "+this.blockHash+"\n"+"Nonce: "+this.nonce+"\n"+"Transaction: "+outputs.toString()+"\n");
    }
}








