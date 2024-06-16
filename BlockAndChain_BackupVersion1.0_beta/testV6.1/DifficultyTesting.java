import java.util.Random;

import bcProject.BlockChain.DifficultyHandler;

class DifficultyTesting{
    boolean TEST_DIFFICULTY5 = false;
    boolean TEST_DIFFICULTY1to5 = true;
    boolean TEST_RUN = true;
    public static void main(String[] args) {
        DifficultyHandler dh =  DifficultyHandler.getInstance();
        DifficultyTesting dt = new DifficultyTesting();
        int difficulty = 0;
        long timeInterval = 0;
        Random random = new Random();

        if(dt.TEST_RUN){
            long[] time = {0,120,240,360,480,600,999999999};

            for(int i=0; i<1000; i++){
                // difficulty = random.nextInt(6);
                difficulty = 1;
                
                //timeInterval = random.nextLong(time[difficulty], time[difficulty+1]);
                timeInterval = 601;
                if(dt.TEST_DIFFICULTY5){
                    if(difficulty == 5){
                        int adjustedDifficulty =dh.adjustDifficulty(timeInterval, difficulty);
                        System.out.println("------------------------------------");
                        System.out.println("previousDifficulty: "+ difficulty);
                        System.out.println("timeInterval: "+ timeInterval);
                        System.out.println("adjustedDifficulty: "+ adjustedDifficulty);
                        System.out.println("------------------------------------");
                    }
                }
                
                if(dt.TEST_DIFFICULTY1to5){
                    int adjustedDifficulty =dh.adjustDifficulty(timeInterval, difficulty);
                    System.out.println("------------------------------------");
                    System.out.println("previousDifficulty: "+ difficulty);
                    System.out.println("timeInterval: "+ timeInterval);
                    System.out.println("adjustedDifficulty: "+ adjustedDifficulty);
                    System.out.println("------------------------------------");
                } 
            }
        }
    }
}