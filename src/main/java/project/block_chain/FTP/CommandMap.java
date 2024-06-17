package project.block_chain.FTP;
import java.util.HashMap;
import java.util.Map;

public class CommandMap{
    private final static Map<Integer, String> commandMap;

    static{
        commandMap = new HashMap<>();
        commandMap.put(000, "[CONNECT]");
        commandMap.put(111, "[UPLOAD]");
        commandMap.put(222, "[QUERY]");
        commandMap.put(333, "[SUCCESS]");
        commandMap.put(444, "[ERROR]");
    }

    public static String getType(int type){
        return commandMap.get(type);
    }
}

