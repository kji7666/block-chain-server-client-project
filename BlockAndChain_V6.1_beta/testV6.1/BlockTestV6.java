import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import bcProject.BlockChain.BlockImpl;
import bcProject.BlockChain.Chain;

public class BlockTestV6 extends BlockImpl {

    private static boolean is_BlockTest = true;

    public static void main(String[] args) throws InterruptedException {
        BlockTestV6 bt = new BlockTestV6();
        List<BlockImpl> blockList = new ArrayList<>();
        BlockImpl genesisBlock = new BlockImpl();
        
        String input = args[0];
        // BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        // try {
        //     input = bf.readLine();
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }

        if(input!= null){
            int size = Integer.valueOf(input);

            if(is_BlockTest){
                blockList.add(genesisBlock);
                //bt.showInfo(genesisBlock);
    
                for(int i=1; i<=size; i++){
                    BlockImpl newBlock = new BlockImpl(blockList.get(i-1), bt.getRandomTransaction());
                    blockList.add(newBlock);
                    blockList.get(i-1).setNextBlock(blockList.get(i));
                }
            }
        }
        

        bt.showAll(genesisBlock);
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

