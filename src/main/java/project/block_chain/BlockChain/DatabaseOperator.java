package project.block_chain.BlockChain;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import project.block_chain.Database.*;

/**
 * The DatabaseOperator class handles operations related to interacting with a database,
 * including inserting and querying transaction data.
 * @author KJI
 * @since  May/25/2024
 */
public class DatabaseOperator {
    public static void main(String[] args) {

        LocalTime currentTime = LocalTime.now();
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        System.out.println("Formatted current date and time: " + formattedDateTime);
        
        DatabaseOperator unit = DatabaseOperator.getInstance();
        // use
        unit.insert("0X0000", "name", formattedDateTime, "150NT", "1");
        List<String> result = unit.query("0X0000");
        for (String data : result) {
            System.out.println(data);
        }
    }

    private DataBaseConnector dataBaseConnector;
    private static DatabaseOperator instance;

    /**
     * Get the singleton instance of DatabaseOperator
     * @return the singleton instance of DatabaseOperator
     */
    public static DatabaseOperator getInstance() {
        if (instance == null) {
            instance = new DatabaseOperator();
        }
        return instance;
    }

    /**
     * Constructor for DatabaseOperator. Initializes the database connector.
     */
    public DatabaseOperator() {
        try {
            dataBaseConnector = DataBaseConnector.getInstance(); // Instantiate the database connector
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
            System.out.println(e.getMessage());
        }
    }

    /**
     * Insert a transaction record into the database
     * @param transactionID the transaction ID
     * @param userName the user name
     * @param time the time of the transaction
     * @param handlingFee the handling fee of the transaction
     * @param height block height
     */
    public void insert(String transactionID, String userName, String time, String handlingFee, String height) {
        String sql = "INSERT INTO transaction_info (transaction_id, user_name, time, handling_fee, height) VALUES (?, ?, ?, ?, ?);";
        String[] dataArray = new String[]{transactionID, userName, time, handlingFee, height};
        try {
            dataBaseConnector.insert(sql, dataArray);
            System.out.println("Success");
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    /**
     * Query a transaction record from the database based on the transaction ID
     * @param transactionID the transaction ID
     * @return a list of strings containing the transaction details
     */
    public List<String> query(String transactionID) {
        String sql = "SELECT * FROM transaction_info WHERE transaction_iD = ?";  // SQL query to search for a transactionID in the specified table
        String[] dataArray = new String[]{transactionID};
        List<String> resultList = new ArrayList<>();
        try {
            ResultSet resultSet = dataBaseConnector.query(sql, dataArray);
            int columnCount = resultSet.getMetaData().getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) { // The JDBC specification states that column indexes start at 1
                    resultList.add(resultSet.getString(i));
                }
            }
            return resultList;
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
        return null;
    }
}
