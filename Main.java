import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import bcProject.Block;

public class Main{
    // each block would have list of transaction
        //previous hash
        // hash
    ArrayList<Block> blockChains = new ArrayList<>();
    public static void main(String[] args){
        String[] genesisTransaction = {"Hi I am"};
        Block genesisBlock = new Block("0", genesisTransaction);
       
        String[] Transaction1 = {"Harris"};
        Block Block1 = new Block(genesisBlock.getBlockHash(), Transaction1);

        String[] Transaction2 = {"Today I ate a Croissant"};
        Block Block2 = new Block(Block1.getBlockHash(), Transaction2);

        String[] Transaction3 = {"I'm not hungry "};
        Block Block3 = new Block(Block2.getBlockHash(), Transaction3);

        genesisBlock.showAllInfo();
        Block1.showAllInfo();
        Block2.showAllInfo();
        Block3.showAllInfo();
    }
}