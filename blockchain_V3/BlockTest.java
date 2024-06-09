import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import bcProject.BlockChain.*;

public class BlockTest extends Block {

    boolean is_NonceTest = false;
    boolean is_BlockTest = true;

    public static void main(String[] args) throws InterruptedException {
        BlockTest bt = new BlockTest();
        List<Block> blockList = new ArrayList<>();
        Block genesisBlock = new Block();

        if(bt.is_BlockTest){
            blockList.add(genesisBlock);
            bt.showInfo(genesisBlock);

            for(int i=1; i<7; i++){
                Block newBlock = new Block(blockList.get(i-1), bt.getRandomTransaction());
                blockList.add(newBlock);
                blockList.get(i-1).setNextBlock(blockList.get(i));
            }
        }

        bt.showAll(genesisBlock);

        if(bt.is_NonceTest){
            System.out.println(Base64NoPadding.decodeBase64("HsX8v"));
        }
       
        
    }

    public String[] getRandomTransaction(){
        List<String> randomTransactionList = new ArrayList<>();
        Random random = new Random();
        int wordLen = random.nextInt(100);
        int dataSize = random.nextInt(4)+1;
        String word = "123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

        for(int i=0; i<dataSize; i++){
            StringBuilder sb = new StringBuilder();
            for(int j=0; j<wordLen; j++){
                sb.append(word.charAt(random.nextInt(61)));
            }
            randomTransactionList.add(sb.toString());
        }

        // Convert list to array using Stream API
        // String[] show = randomTransactionList.stream().toArray(String[]::new);
        // for(String str : show){
        //     System.out.println(str);
        // }
        return randomTransactionList.stream().toArray(String[]::new);
    }
}

