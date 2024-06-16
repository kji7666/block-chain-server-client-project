package project.block_chain.Test;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The ClientHandler class manages the communication with a single client.
 * It handles three types of requests:
 * 1. Connect: Confirms client has connected with server
 * 2. Upload: Waits for block chain to record the data, then notifies the client of the result.
 * 3. Query: Fetches data from the database.
 * Each client can only make one request at a time.
 * @param databaseOperator      The database operator to perform database operations.
 * @param transaction           transaction is uploaded by the client.
 * @param hasTransaction        Flag indicating if transaction uploaded.
 * @param isTransactionHandling Flag indicating if transaction is currently being handled.
 */
public class ClientHandler implements Runnable {
    private static DatabaseOperator databaseOperator = new DatabaseOperator();
    private static CommandFormat commandFormat = new CommandFormat();
    private final Logger logger = Logger.getLogger(ClientHandler.class.getName());
    private final Socket clientSoc;
    private BufferedReader in;
    private PrintWriter out;
    private String username = "unknown"; // Default username
    private String transaction;
    private boolean hasTransaction;
    private boolean isTransactionHandling;
    private final Object transactionLock = new Object();

    /**
     * Constructor for ClientHandler.
     * @param clientSocket The client socket
     */
    public ClientHandler(Socket clientSocket) {
        this.clientSoc = clientSocket;
        hasTransaction = false;
        isTransactionHandling = false;
        try {
            in = new BufferedReader(new InputStreamReader(clientSoc.getInputStream()));
            out = new PrintWriter(clientSoc.getOutputStream(), true);
            logger.info("Client handler created for socket: " + clientSoc);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error creating client reader/writer", e);
        }
    }

    /**
     * Confirms connection and sets the username.
     * @throws IOException If an input or output exception occurred
     */
    private void connectConfirm() throws IOException {
        username = commandFormat.commandParsing(in.readLine())[1]; // First message must be the username
        logger.info("User connected: " + username);
        out.println(commandFormat.commandSplicing(333, "Server", username, "connection succeeded"));
    }

    /**
     * Run method to handle client requests.
     */
    @Override
    public void run() {
        try {
            connectConfirm();
            while (true) {
                String request = in.readLine();
                logger.info("server receive request from " + username);
                int type = Integer.parseInt(commandFormat.commandParsing(request)[0]);
                logger.info("request type is " + type);
                String command = commandFormat.commandParsing(request)[3];
                if (request == null) {
                    logger.info("Client " + username + " disconnected.");
                    break;
                }

                if (type == 111) { // Upload
                    logger.info("handling upload request");
                    upload(command);
                } else if (type == 222) { // Query
                    logger.info("handling query request");
                    query(command);
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IOException occurred in ClientHandler", e);
            returnInfo(commandFormat.commandSplicing(444, "Server", username, "Request processing error"));
        } finally {
            try {
                logger.info("Client " + username + " disconnects.");
                if (clientSoc != null && !clientSoc.isClosed()) {
                    clientSoc.close();
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error occurred while closing client socket for " + username, e);
            }
        }
    }

    /**
     * Handles the upload request from the client.
     * @param transaction The transaction data received from the client
     */
    public void upload(String transaction) {
        this.transaction = transaction;
        hasTransaction = true;
        logger.info("Server received transaction from " + username + ": " + transaction);
        synchronized (transactionLock) {
            while (hasTransaction) {
                try {
                    transactionLock.wait(); // Wait until hasTransaction is set to false
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore interrupted status
                    logger.log(Level.SEVERE, "Interrupted while waiting for transaction to complete", e);
                    returnInfo(commandFormat.commandSplicing(444, "Server", username, "Request upload processing error"));
                    break;
                }
            }
        }
    }

    /**
     * Handles the query request from the client.
     * @param transactionID The transaction ID to query
     */
    public void query(String transactionID) {
        // transaction_id,user_name,time,handling_fee,height
        String[] dataArray = databaseOperator.query(transactionID);
        logger.info("GET SIZE" + dataArray.length);
        if(dataArray == null || dataArray.length == 0){
            returnInfo(commandFormat.queryResponseSplicing(444, "Server", username, "This transaction ID was not found"));
        } else {
            logger.info("Query response for " + username + ": " + String.join(", ", dataArray));
            returnInfo(commandFormat.queryResponseSplicing(333, "Server", username, commandFormat.responseDataSplicing(dataArray)));
        }
    }

    /**
     * Updates the client with information.
     * @param info The information to send to the client
     */
    public void returnInfo(String response) {
        out.println(response);
        out.flush();
        finishTransaction();
    }

    /**
     * Gets the username.
     * @return The username of the client
     */
    public synchronized String getUsername() {
        return this.username;
    }

    /**
     * Gets the transaction.
     * @return The transaction received from the client
     */
    public synchronized String getTransaction() {
        return this.transaction;
    }

    /**
     * Checks if a transaction is being handled.
     * @return True if a transaction is being handled, false otherwise
     */
    public synchronized boolean isTransactionHandling() {
        return isTransactionHandling;
    }

    /**
     * Handles the transaction.
     */
    public synchronized void handleTransaction() {
        logger.info("Client " + username + " is handling transaction.");
        synchronized (transactionLock) {
            isTransactionHandling = true;
        }
    }

    /**
     * Finishes the transaction.
     */
    public synchronized void finishTransaction() {
        synchronized (transactionLock) {
            hasTransaction = false;
            isTransactionHandling = false;
            transactionLock.notifyAll(); // Notify all waiting threads
            logger.info("Client " + username + " finished handling transaction.");
        }
    }

    /**
     * Checks if there is a transaction.
     * @return True if there is a transaction, false otherwise
     */
    public synchronized boolean hasTransaction() {
        return hasTransaction;
    }
}
