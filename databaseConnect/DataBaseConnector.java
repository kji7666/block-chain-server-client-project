package com.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



public class DataBaseConnector {
    private static DataBaseConnector instance;
    private HikariDataSource dataSource;

    public static DataBaseConnector getInstance(){
        if(instance == null) {
            instance = new DataBaseConnector();
        } 
        return instance;
    }

    private DataBaseConnector() {
        try{
            ConfigReader configReader = new ConfigReader("demo\\src\\main\\java\\com\\example\\config.json");
            configProperty(configReader);
        } catch (Exception e) {
            System.out.println("config.json doesn't exist.");
        }
    }

    private void configProperty(ConfigReader configReader){
        // Configuration for HikariCP
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(configReader.getURL()); // JDBC URL for connecting to MySQL database
        config.setUsername(configReader.getUserName()); // Username for database authentication
        config.setPassword(configReader.getPassWord()); // Password for database authentication
        
        // Add additional data source properties for optimization
        config.addDataSourceProperty("cachePrepStmts", configReader.getCachePreStmts()); // Enable caching of prepared statements
        config.addDataSourceProperty("prepStmtCacheSize", configReader.getPrepStmtCacheSize()); // Set the maximum number of prepared statements that can be cached per connection
        config.addDataSourceProperty("prepStmtCacheSqlLimit", configReader.getPrepStmtCacheSqlLimit()); // Set the maximum length of SQL statements that can be cached
        config.addDataSourceProperty("useServerPrepStmts", configReader.getUseServerPrepStmts()); // Enable the use of server-side prepared statements if the database supports them

        dataSource = new HikariDataSource(config);
    }

    // Method to insert data
    public void insert(String sql, String[] dataArray) {
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            for(int i=0; i<dataArray.length; i++){
                preparedStatement.setString(i+1, dataArray[i]);
            }
            // Execute the insert operation
            int rowsInserted = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    // Method to query data
    public ResultSet query(String sql, String[] dataArray) {
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            for(int i=0; i<dataArray.length; i++){
                preparedStatement.setString(i+1, dataArray[i]);
            }
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Close the data source when done
    public void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}