package project.block_chain.Test;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The DataBaseConnector class handles database connection and operations.
 * @author KJI
 * @since  May/25/2024
 */
public class DataBaseConnector {
    private static DataBaseConnector instance;
    private HikariDataSource dataSource;

    /**
     * Get the singleton instance of DataBaseConnector
     * @return the singleton instance of DataBaseConnector
     */
    public static DataBaseConnector getInstance() {
        if (instance == null) {
            instance = new DataBaseConnector();
        }
        return instance;
    }

    /**
     * Constructor for DataBaseConnector. Initializes the database connection pool.
     */
    private DataBaseConnector() {
        try {
            ConfigReader configReader = new ConfigReader("src\\main\\resource\\config.json");
            configProperty(configReader);
        } catch (Exception e) {
            System.out.println("config.json doesn't exist.");
        }
    }

    /**
     * Configure data source properties for HikariCP
     * @param configReader the configuration reader
     */
    private void configProperty(ConfigReader configReader) {
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

    /**
     * Method to insert data into the database
     * @param sql the SQL statement
     * @param dataArray the array of data values to insert
     */
    public void insert(String sql, String[] dataArray) {
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < dataArray.length; i++) {
                preparedStatement.setString(i + 1, dataArray[i]);
            }
            // Execute the insert operation
            int rowsInserted = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    /**
     * Executes the given SQL query with the provided parameters and returns the results as a String array.
     *
     * @param sql The SQL query to execute.
     * @param dataArray The parameters for the SQL query.
     * @return A String array containing the query results.
     */
    public String[] query(String sql, String[] dataArray) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            for (int i = 0; i < dataArray.length; i++) {
                preparedStatement.setString(i + 1, dataArray[i]);
            }

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSetToArray(resultSet);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new String[0];
    }

    /**
     * Converts the given ResultSet to a String array.
     *
     * @param resultSet The ResultSet to convert.
     * @return A String array containing the data from the ResultSet.
     * @throws SQLException If an SQL error occurs.
     */
    private String[] resultSetToArray(ResultSet resultSet) throws SQLException {
        if (!resultSet.next()) {
            return new String[0];
        }

        int columnCount = resultSet.getMetaData().getColumnCount();
        String[] resultArray = new String[columnCount];

        for (int i = 0; i < columnCount; i++) {
            resultArray[i] = resultSet.getString(i + 1);
        }

        return resultArray;
    }

    /**
     * Close the data source when done
     */
    public void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
