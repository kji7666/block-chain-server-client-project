package project.block_chain.BlockChain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class DifficultyHandler{ // 單元測試
    public static void main(String[] args){
        DifficultyHandler difficultyHandler = new DifficultyHandler();
        //difficultyHandler.init();
        //difficultyHandler.testTime(); // use for test
    }

    private static RequestHandler firstHandler;
    private static DifficultyHandler instance;

    public static DifficultyHandler getInstance(){
        return instance;
    }
    private DifficultyHandler(){}

    public int adjustDifficulty(long timeInterval, int currentDifficulty){
        setHandler();
        return getDifficulty(timeInterval, currentDifficulty);
    }

    private void setHandler(){
        timeIntervalSafe h1 = new timeIntervalSafe();
        timeIntervalOver h2 = new timeIntervalOver();
        h1.setSuccessor(h2);
        firstHandler = h1;
    }

    private int getDifficulty(long timeInterval, int currentDifficulty){
        Request request = new Request(timeInterval); // input time
        Response response = firstHandler.process(request);
        if(response.isDifficultChange()){
            return response.getDifficulty();
        } else {
            return currentDifficulty;
        }
    }
}


class DifficultyList {
    // 如果時間間隔超過 [key]，則映射到 [value]（難度）
    // 定義難度為leadingZero
    private List<Pair> difficultyList;
    private static DifficultyList instance;

    public static DifficultyList getInstance(){
        if(instance != null){
            return instance;   
        }
        return new DifficultyList();
    }

    private DifficultyList() {
        difficultyList = new ArrayList<>();
        difficultyList.add(new Pair(1, 120));
        difficultyList.add(new Pair(2, 240));
        difficultyList.add(new Pair(3, 360));
        difficultyList.add(new Pair(4, 480));
        difficultyList.add(new Pair(5, 600));
        difficultyList.sort(new Comparator<Pair>() {
            @Override
            public int compare(Pair pair1, Pair pair2) {
                // 比較 Pair 的時間值
                return Long.compare(pair1.getTime(), pair2.getTime());
            }
        });
    }

    public int getDifficulty(long time) {
        for (Pair pair : difficultyList) {
            if (time <= pair.getTime()) {
                return pair.getDifficulty();
            }
        }
        // 如果時間超出了最大時間，返回最大難度
        return difficultyList.get(difficultyList.size() - 1).getDifficulty();
    }

    public long getTime(int difficulty) {
        for (Pair pair : difficultyList) {
            if (pair.getDifficulty() == difficulty) {
                return pair.getTime();
            }
        }
        throw new IllegalArgumentException("difficulty does not exist");
        // 如果難度不存在，throw
    }


}

class Pair {
    private int difficulty;
    private long time;

    public Pair(int difficulty, long time) {
        this.difficulty = difficulty;
        this.time = time;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public long getTime() {
        return time;
    }
}

class Request{
    private long timeInterval;
    // add condition

    public Request(long timeInterval){
        this.timeInterval = timeInterval;
    }
    public long getTimeInterval() {
        return timeInterval;
    }

    // override equals and hashCode
    @Override
    public boolean equals(Object o){
        if(this == o)return true;
        if(o == null || o.getClass() != getClass())return false;
        Request that = (Request) o;
        return timeInterval == that.timeInterval;
    }
    @Override
    public int hashCode(){
        return Objects.hash(timeInterval);
    }
}

class Response{
    private boolean isDifficultChange;
    private int difficulty;
    // response its adjected difficult
    public Response(boolean isDifficultChange, int difficulty){
        this.difficulty = difficulty;
        this.isDifficultChange = isDifficultChange;
    }
    public Response(boolean isDifficultChange){
        this(isDifficultChange, 0);
    }
    public boolean isDifficultChange() {
        return isDifficultChange;
    }
    public int getDifficulty() {
        return difficulty;
    }

    @Override
    public boolean equals(Object o){
        if(this == o)return true;
        if(o == null || o.getClass() != getClass())return false;
        Response that = (Response) o;
        return isDifficultChange == that.isDifficultChange && difficulty == that.difficulty;
    }
}

abstract class RequestHandler{
    private RequestHandler successor;
    protected abstract boolean canHandle(Request resquest);
    protected abstract Response handle(Request request);

    public Response process(Request request){
        if(canHandle(request)) return handle(request);
        if(successor != null) return successor.process(request); 
        throw new IllegalStateException("No handler can handle this request");
    }
    public RequestHandler getSuccessor() {
        return successor;
    }
    public void setSuccessor(RequestHandler successor) {
        this.successor = successor;
    }
}

class timeIntervalOver extends RequestHandler{
    DifficultyList difficultyList = DifficultyList.getInstance();
    @Override
    public boolean canHandle(Request request){
        return request.getTimeInterval() > difficultyList.getTime(5); // max difficulty
    }
    @Override
    public Response handle(Request request){
        Random random = new Random();
        int randomDifficulty = random.nextInt(5) + 1; // number 1~5 random
        return new Response(true, randomDifficulty);
    }
}

class timeIntervalSafe extends RequestHandler{
    DifficultyList difficultyList = DifficultyList.getInstance();
    @Override
    public boolean canHandle(Request request){
        return request.getTimeInterval() <= difficultyList.getTime(5); // max difficulty
    }
    @Override
    public Response handle(Request request){
        return new Response(false, -1);
    }
}
