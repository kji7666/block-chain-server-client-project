import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import bcProject.BlockChain.Chain;

public class ChainTestV5{
    Chain chain = Chain.getInstance();
    private static ExecutorService executorService = Executors.newFixedThreadPool(3);

    ChainTestV5(){
        executorService.submit((this::showSize));
        executorService.submit(this::add);
        executorService.submit(this::showHeight);
    }

    public static void main(String[] args) {
        System.out.println("IN");
        ChainTestV5 tv5= new ChainTestV5();
        
        // for(int i=0; i<20; i++){
        //     tv5.chain.addTransaction(tv5.getRandomTransaction(100));
        // }
        
        // System.out.println(tv5.chain.isRunning());


        System.out.println("OUT");
        //tv5.showSize();
        System.out.println("FINAL");
    }

    private String getRandomTransaction(int wordLen){
        String tr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<wordLen; i++){
            sb.append(tr.charAt(random.nextInt(tr.length()-1)));
        }

        return sb.toString();
    }

    public void showSize() {
        try {
            while(true){
                chain.showTransactionQueueSize();
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void showHeight(){
        try {
            while(true){
                System.out.println("Height "+ String.valueOf(chain.getChainLatestHeight()));
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void add(){
        try {
            while(true){
                chain.addTransaction(getRandomTransaction(100));
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
       
    }

}
