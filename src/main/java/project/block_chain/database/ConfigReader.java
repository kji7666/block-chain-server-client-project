package project.block_chain.database;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConfigReader {
    private JsonNode rootNode;
    private JsonNode databaseNode;

    public ConfigReader(String fileName) throws IOException{
        // Read JSON file
        ObjectMapper mapper = new ObjectMapper();
        rootNode = mapper.readTree(new File(fileName));
        databaseNode = rootNode.get("Database");
    }

    public String getURL(){
        return databaseNode.get("url").asText();
    }

    public String getUserName(){
        return databaseNode.get("username").asText();
    }

    public String getPassWord(){
        return databaseNode.get("password").asText();
    }

    public String getCachePreStmts(){
        return databaseNode.get("cachePrepStmts").asText();
    }

    public String getPrepStmtCacheSize(){
        return databaseNode.get("prepStmtCacheSize").asText();
    }

    public String getPrepStmtCacheSqlLimit(){
        return databaseNode.get("prepStmtCacheSqlLimit").asText();
    }

    public String getUseServerPrepStmts(){
        return databaseNode.get("useServerPrepStmts").asText();
    }

}
