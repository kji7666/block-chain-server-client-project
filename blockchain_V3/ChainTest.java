import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import bcProject.BlockChain.Block;

public class ChainTest {
    List<String[]> trList = new ArrayList<>();
    List<String[]> notOnList = new ArrayList<>();

    public String[] makeRandomData(int size){
        int length = 100;
        Random random = new Random();
        String[] transactions = new String[size];
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        
        for(int i=0; i<size; i++){
            StringBuilder sb = new StringBuilder();
            for(int j=0; j<length; j++){
                sb.append(str.charAt(random.nextInt(str.length())));
            }
            transactions[i] = sb.toString();
        }
        trList.add(transactions);
        return transactions; 
    }

    public void createTestingChain(Chain chain, int times){
        //insert block with trasaction of 1000
        for(int i=0; i<times; i++){
            chain.addBlock(new Block(chain.getHead(), makeRandomData(100)));//difficult=0
        }
    }


    public void testBuildingBlocksOfChain(List<String[]> queryList, Chain chain){
        int blockSeq = 0;
        for(String[] bb : queryList){
            System.out.println("-------------------------------------");
            System.out.println("blockSeq:"+String.valueOf(blockSeq));
            int transactionSeq = 0;
            for(int i=0; i<bb.length; i++){
                Block Block = chain.searchTransaction(bb[i]);
                if(Block == null){
                    System.out.println("transactionSeq: " + transactionSeq + " | NOT FOUND");
                }
                else{
                    System.out.println("transactionSeq: " + transactionSeq + " | FOUND");
                }
                transactionSeq++;
            }
            System.out.println("-------------------------------------");
            blockSeq++;
        }

    }


    public static void main(String[] args) {
        Chain chain = new Chain();
        ChainTest ch = new ChainTest();
        ch.createTestingChain(chain, 100);

        //Chain size = 100 + 1
        System.out.println("Search for ON list");
        ch.testBuildingBlocksOfChain(ch.trList, chain);


        System.out.println("\n\n\n\n\nSearch for NOT on list");
        for(int i=0; i<100; i++){
            ch.notOnList.add(ch.makeRandomData(100));
        }
        ch.testBuildingBlocksOfChain(ch.notOnList, chain);

        //chain.showChainInfo();
    }
}
