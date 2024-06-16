package project.block_chain.Test;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The ConfigReader class reads database configuration from a JSON file.
 * @author KJI
 * @since  May/25/2024
 */
public class ConfigReader {
    private JsonNode rootNode;
    private JsonNode databaseNode;

    /**
     * Constructor for ConfigReader.
     * Reads database configuration from the specified JSON file.
     * @param fileName the name of the JSON file containing the database configuration
     * @throws IOException if an I/O error occurs when reading the file
     */
    public ConfigReader(String fileName) throws IOException{
        // Read JSON file
        ObjectMapper mapper = new ObjectMapper();
        rootNode = mapper.readTree(new File(fileName));
        databaseNode = rootNode.get("Database");
    }

    /**
     * Get the JDBC URL from the database configuration.
     * @return the JDBC URL
     */
    public String getURL(){
        return databaseNode.get("url").asText();
    }

    /**
     * Get the username from the database configuration.
     * @return the username
     */
    public String getUserName(){
        return databaseNode.get("username").asText();
    }

    /**
     * Get the password from the database configuration.
     * @return the password
     */
    public String getPassWord(){
        return databaseNode.get("password").asText();
    }

    /**
     * Get the value of cachePrepStmts from the database configuration.
     * @return the value of cachePrepStmts
     */
    public String getCachePreStmts(){
        return databaseNode.get("cachePrepStmts").asText();
    }

    /**
     * Get the value of prepStmtCacheSize from the database configuration.
     * @return the value of prepStmtCacheSize
     */
    public String getPrepStmtCacheSize(){
        return databaseNode.get("prepStmtCacheSize").asText();
    }

    /**
     * Get the value of prepStmtCacheSqlLimit from the database configuration.
     * @return the value of prepStmtCacheSqlLimit
     */
    public String getPrepStmtCacheSqlLimit(){
        return databaseNode.get("prepStmtCacheSqlLimit").asText();
    }

    /**
     * Get the value of useServerPrepStmts from the database configuration.
     * @return the value of useServerPrepStmts
     */
    public String getUseServerPrepStmts(){
        return databaseNode.get("useServerPrepStmts").asText();
    }
}
