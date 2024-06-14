package project.block_chain.Test;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * The DatabaseOperator class handles operations related to interacting with a database,
 * including inserting and querying transaction data.
 * @author KJI
 * @since  May/25/2024
 */
public class DatabaseOperator {
    private static DataBaseConnector dataBaseConnector;
    private static final Logger logger = Logger.getLogger(FTPServer.class.getName());
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

    // dataArray = {transactionID, userName, time, handlingFee, height}
    public void insert(String[] dataArray) {
        String sql = "INSERT INTO transaction_info (transaction_id, user_name, time, handling_fee, height) VALUES (?, ?, ?, ?, ?);";
        try {
            dataBaseConnector.insert(sql, dataArray);
            logger.info("upload to database");
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    public String[] query(String transactionID) {
        String sql = "SELECT * FROM transaction_info WHERE transaction_iD = ?";  // SQL query to search for a transactionID in the specified table
        String[] dataArray = new String[]{transactionID};
        try { //[query]transaction_id,user_name,time,handling_fee,height,transaction
            
            List<String> resultList = Arrays.asList(dataBaseConnector.query(sql, dataArray)); // cannot convert from String[] to ResultSet
            if(resultList == null){
                return null;
            }
            /*
            //List<String> resultList = resultSetToList(resultSet);// transaction_id,user_name,time,handling_fee,height
            int height = Integer.parseInt(resultList.get(4));
            Block targetBlock = Chain.getInstance().getList().get(height);
            String[] transactionIDs = targetBlock.getTransactionID();
            String[] transactions = targetBlock.getTransaction();
            for(int i=0; i<transactionIDs.length; i++){
                if(transactionID.equals(transactionIDs[i])){
                    resultList.add(transactions[i]);
                }
            }
            */
            return resultList.toArray(new String[0]);

        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
        return new String[0];
    }

    /**
     * Converts the given ResultSet to a List of Strings.
     *
     * @param resultSet The ResultSet to convert.
     * @return A List of Strings containing the data from the ResultSet.
     * @throws SQLException If an SQL error occurs.
     */
    private static List<String> resultSetToList(ResultSet resultSet) throws SQLException {
        List<String> resultList = new ArrayList<>();
        while (resultSet.next()) {
            int columnCount = resultSet.getMetaData().getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                resultList.add(resultSet.getString(i));
            }
        }
        return resultList;
    }
}
