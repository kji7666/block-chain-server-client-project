public class Chain {
    private Block headBlock;

    public Chain(){
        //create genesis block
        headBlock = new Block();
    }

    public void addBlock(Block newBlock){
        if(headBlock == null){
            this.headBlock = newBlock;
        }
        else{
            newBlock.setNextBlock(headBlock);


            headBlock = newBlock;
        }
    }

    public Block search(String targetHash) {
        Block currentBlock = headBlock;
        while (currentBlock != null) {
            if (currentBlock.getBlockHash().equals(targetHash)){
                return currentBlock;
            }
            currentBlock = currentBlock.getNextBlock();

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

    // public static void main(String[] args){
    //     Chain chain = new Chain();
    //     ChainTest ch = new ChainTest();

    //     //System.out.println(chain.headBlock.currentBlockHash);

        


    //     chain.insert(new Block(chain.headBlock.currentBlockHash, ch.makeRandomData(0)));
        
    //     System.out.println(chain.headBlock.currentBlockHash);
    //     System.out.println(chain.headBlock.nextBlock.currentBlockHash);
    // }
}
