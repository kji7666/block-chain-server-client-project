package project.block_chain.Test;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;


/**
 * The DifficultyHandler class handles the adjustment of difficulty based on the time interval.
 * @author KJI
 * @since  June/02/2024
 */
public class DifficultyHandler {
    private static RequestHandler firstHandler;
    private static DifficultyHandler instance;

    /**
     * Get the singleton instance
     * @return the singleton instance of DifficultyHandler
     */
    public static DifficultyHandler getInstance() {
        if(instance == null){
            instance = new DifficultyHandler();
        }
        return instance;
    }
    private DifficultyHandler() {}

    /**
     * Get the adjusted difficulty
     * @param timeInterval the time interval
     * @param currentDifficulty the current leadingZero
     * @return the adjusted difficulty
     */
    public int adjustDifficulty(long timeInterval, int currentDifficulty) {
        setHandler();
        return getDifficulty(timeInterval, currentDifficulty);
    }

    /**
     * Set the chain of responsibility handlers
     * if h1 cannot handle, h2 try to handle
     */
    private void setHandler() {
        timeIntervalSafe h1 = new timeIntervalSafe();
        timeIntervalOver h2 = new timeIntervalOver();
        h1.setSuccessor(h2);
        firstHandler = h1;
    }

    private int getDifficulty(long timeInterval, int currentDifficulty) {
        Request request = new Request(timeInterval);
        Response response = firstHandler.process(request);
        if (response.isDifficultChange()) {
            return response.getDifficulty();
        } else {
            return currentDifficulty;
        }
    }
}

/**
 * The DifficultyList class maintains a list of difficulties and their corresponding time intervals.
 */
class DifficultyList {
    private List<Pair> difficultyList;
    private static DifficultyList instance;

    /**
     * Get the singleton instance
     * @return the singleton instance of DifficultyList
     */
    public static DifficultyList getInstance() {
        if (instance != null) {
            return instance;   
        }
        return new DifficultyList();
    }

    /**
     * Constructor, initialize the difficulty list
     */
    private DifficultyList() {
        difficultyList = new ArrayList<>();
        difficultyList.add(new Pair(0, 0));
        difficultyList.add(new Pair(1, 120));
        difficultyList.add(new Pair(2, 240));
        difficultyList.add(new Pair(3, 360));
        difficultyList.add(new Pair(4, 480));
        difficultyList.add(new Pair(5, 600));
        difficultyList.sort(new Comparator<Pair>() {
            @Override
            public int compare(Pair pair1, Pair pair2) {
                // Compare the time values of the pairs
                return Long.compare(pair1.getTime(), pair2.getTime());
            }
        });
    }

    /**
     * Get the difficulty based on the time interval
     * @return the corresponding difficulty
     */
    public int getDifficulty(long time) {
        for (Pair pair : difficultyList) {
            if (time <= pair.getTime()) {
                return pair.getDifficulty();
            }
        }
        // If the time exceeds the maximum time, return the maximum difficulty
        return difficultyList.get(difficultyList.size() - 1).getDifficulty();
    }

    /**
     * Get the time interval based on the difficulty
     * @return the corresponding time interval
     */
    public long getTime(int difficulty) {
        for (Pair pair : difficultyList) {
            if (pair.getDifficulty() == difficulty) {
                return pair.getTime();
            }
        }
        throw new IllegalArgumentException("difficulty does not exist");
        // If the difficulty does not exist, throw an exception
    }
}

/**
 * The Pair class represents a pair of difficulty and time interval.
 */
class Pair {
    private int difficulty;
    private long time;

    /**
     * Constructor
     * @param difficulty the leadingZero
     * @param time the time interval
     */
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

/**
 * The Request class represents a request for difficulty adjustment.
 */
class Request {
    private long timeInterval;
    // add condition

    /**
     * Constructor
     * @param timeInterval the time interval
     */
    public Request(long timeInterval) {
        this.timeInterval = timeInterval;
    }

    /**
     * Get the time interval
     * @return the time interval
     */
    public long getTimeInterval() {
        return timeInterval;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != getClass()) return false;
        Request that = (Request) o;
        return timeInterval == that.timeInterval;
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeInterval);
    }
}

/**
 * The Response class represents a response to a difficulty adjustment request.
 */
class Response {
    private boolean isDifficultChange;
    private int difficulty;

    /**
     * Constructor
     * @param isDifficultChange whether the difficulty has changed
     * @param difficulty the adjusted difficulty
     */
    public Response(boolean isDifficultChange, int difficulty) {
        this.difficulty = difficulty;
        this.isDifficultChange = isDifficultChange;
    }

    /**
     * Constructor
     * @param isDifficultChange whether the difficulty has changed
     */
    public Response(boolean isDifficultChange) {
        this(isDifficultChange, 0);
    }

    /**
     * Check if the difficulty has changed
     * @return true if the difficulty has changed, false otherwise
     */
    public boolean isDifficultChange() {
        return isDifficultChange;
    }

    /**
     * Get the adjusted difficulty
     * @return the adjusted difficulty
     */
    public int getDifficulty() {
        return difficulty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != getClass()) return false;
        Response that = (Response) o;
        return isDifficultChange == that.isDifficultChange && difficulty == that.difficulty;
    }
}

/**
 * Abstract Handler class for processing difficulty adjustment requests
 * @return adjusted difficulty
 */
abstract class RequestHandler {
    private RequestHandler successor;

    /**
     * Check if the request can be handled
     * @param request the request object
     * @return true if the request can be handled, false otherwise
     */
    protected abstract boolean canHandle(Request request);

    /**
     * Handle the request
     * @param request the request object
     * @return the response after handling the request
     */
    protected abstract Response handle(Request request);

    /**
     * Process the request. If it cannot be handled, pass it to the successor
     * h1, h2, h3 is Request Handler
     * if h1 cannot handle, h1's succesor(h2) will try to handle
     * if h2 cannot handle, h2's succesor(h3) will try to handle
     * @param request the request object
     * @return the response after processing the request
     */
    public Response process(Request request) {
        if (canHandle(request)) return handle(request);
        if (successor != null) return successor.process(request); 
        throw new IllegalStateException("No handler can handle this request");
    }

    /**
     * Get the successor handler
     * @return the successor handler
     */
    public RequestHandler getSuccessor() {
        return successor;
    }

    /**
     * Set the successor handler
     * @param successor the successor handler
     */
    public void setSuccessor(RequestHandler successor) {
        this.successor = successor;
    }
}

/**
 * Handler for processing requests with time intervals over the maximum difficulty time
 */
class timeIntervalOver extends RequestHandler {
    DifficultyList difficultyList = DifficultyList.getInstance();
    @Override
    public boolean canHandle(Request request) {
        return request.getTimeInterval() > difficultyList.getTime(5); // max difficulty
    }
    @Override
    public Response handle(Request request) {
        Random random = new Random();
        int randomDifficulty = random.nextInt(5); // number 0~4 random
        return new Response(true, randomDifficulty);
    }
}

/**
 * Handler for processing requests with time intervals within the safe range
 */
class timeIntervalSafe extends RequestHandler {
    DifficultyList difficultyList = DifficultyList.getInstance();
    @Override
    public boolean canHandle(Request request) {
        return request.getTimeInterval() <= difficultyList.getTime(5); // max difficulty
    }
    @Override
    public Response handle(Request request) {
        return new Response(false, -1);
    }
}


class DifficultyTesting{
    boolean TEST_DIFFICULTY5 = true;
    boolean TEST_DIFFICULTY1to5 = true;
    boolean TEST_RUN = false;
    public static void main(String[] args) {
        DifficultyHandler dh =  DifficultyHandler.getInstance();
        DifficultyTesting dt = new DifficultyTesting();
        int difficulty = 0;
        long timeInterval = 0;
        Random random = new Random();

        if(dt.TEST_RUN){
            long[] time = {0,120,240,360,480,600,999999999};

            for(int i=0; i<1000; i++){
                difficulty = random.nextInt(6);
                
                timeInterval = random.nextLong(time[difficulty], time[difficulty+1]);
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