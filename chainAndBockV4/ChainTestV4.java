import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import bcProject.BlockChain.Chain;

public class ChainTestV4 implements Runnable {
        Chain chain = Chain.getInstance();
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        public ChainTestV4(){
            executorService.submit(this::run);
            executorService.submit(this::add);
        }
    public static void main(String[] args) {
        ChainTestV4 tv = new ChainTestV4();

        
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

    @Override
    public void run() {
        try {
            while(true){
                chain.showTransactionQueueSize();
                Thread.sleep(10);
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
