
import java.util.ArrayList;
import java.util.List;


/**
 * The DatabaseOperator class handles operations related to interacting with a database,
 * including inserting and querying transaction data.
 * @author KJI
 * @since  May/25/2024
 */
public class DatabaseOperator {
    private static DataBaseConnector dataBaseConnector;
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
    private DatabaseOperator() {
        try {
            dataBaseConnector = DataBaseConnector.getInstance(); // Instantiate the database connector
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
            System.out.println(e.getMessage());
        }
    }

    // dataArray = {transactionID, userName, time, handlingFee, height}
    public static void insert(String[] dataArray) {
        String sql = "INSERT INTO transaction_info (transaction_id, user_name, time, handling_fee, height) VALUES (?, ?, ?, ?, ?);";
        try {
            dataBaseConnector.insert(sql, dataArray);
            System.out.println("Success");
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }
    public static String[] query(String transactionID) {
        String sql = "SELECT * FROM transaction_info WHERE transaction_iD = ?";  // SQL query to search for a transactionID in the specified table
        String[] dataArray = new String[]{transactionID};
        List<String> resultList = new ArrayList<>();
        try {
            for (int i = 1; i <= dataArray.length; i++) { // The JDBC specification states that column indexes start at 1
                resultList.add("data");
            }
            /* 
            String[] resultSet = dataBaseConnector.query(sql, dataArray);
            int columnCount = resultSet.getMetaData().getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) { // The JDBC specification states that column indexes start at 1
                    resultList.add(resultSet.getString(i));
                }
            }*/
            return new String[]{"transaction", "user", "time", "handingfee", "1"};
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
        return null;
    }
}

class DataBaseConnector{
    // fake connector
    private static DataBaseConnector instance;
    public static DataBaseConnector getInstance(){
        if(instance == null){
            instance = new DataBaseConnector();
        }
        return instance;
    }
    private DataBaseConnector(){}
    public void insert(String sql, String[] dataArray) {}
    public String[] query(String sql, String[] dataArray) {
        return null;
    }
}