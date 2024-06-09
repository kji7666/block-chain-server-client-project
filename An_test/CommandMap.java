import java.util.HashMap;
import java.util.Map;

public class CommandMap{
    private final static Map<Integer, String> commandMap;

    static{
        commandMap = new HashMap<>();
        commandMap.put(0, "[CONNECT]");
        commandMap.put(1, "[UPLOAD]");
        commandMap.put(2, "[QUERY]");
        commandMap.put(3, "[SUCCESS]");
        commandMap.put(4, "[ERROR]");
    }

    public static String getHeader(int type){
        return commandMap.get(type);
    }
}

