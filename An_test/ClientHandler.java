import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The ClientHandler class manages the communication with a single client.
 * 兩種情況:
 * 如果只是查詢就到資料庫找
 * 如果是上傳就等人來把你的資料紀錄, 記錄完會通知你結果
 * 
 * 一次只能做一次request
 */
public class ClientHandler implements Runnable {
    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());
    private final Socket clientSoc;
    private BufferedReader in;
    private PrintWriter out;
    private String username;
    private String transaction;
    private boolean hasTransaction, isTransactionHandling;
    private final Object transactionLock = new Object();

    /**
     * Constructor for ClientHandler.
     * @param clientSocket The client socket
     */
    public ClientHandler(Socket clientSocket) {
        hasTransaction = false;
        isTransactionHandling = false;
        this.clientSoc = clientSocket;
        try {
            in = new BufferedReader(new InputStreamReader(clientSoc.getInputStream()));
            out = new PrintWriter(clientSoc.getOutputStream(), true);
            logger.info("Client handler created for socket: " + clientSoc);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error creating client reader/writer", e);
        }
    }

    /**
     * Run method to handle client requests.
     */
    @Override
    public void run() {
        try {
            username = in.readLine(); // First message must be the username
            logger.info("User connected: " + username);

            while (true) {
                String request = in.readLine();
                if (request == null) { // Client disconnected, readLine() returns null
                    logger.info("Client " + username + " disconnected.");
                    break;
                } else if(request.startsWith("[upload]")){
                    upload(request);
                } else if(request.startsWith("[query]")){ // 一次傳一個請求, 卡舊卡阿
                    query(request);
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IOException occurred in ClientHandler", e);
        } finally {
            try {
                logger.info("Client " + username + " disconnects.");
                clientSoc.close();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error closing client socket for " + username, e);
            }
        }
    }

    public void upload(String request){
        transaction = request.substring(request.indexOf("]")+1);
        hasTransaction = true;
        logger.info("Server received transaction from " + username + ": " + transaction);
        synchronized (transactionLock) {
            while (hasTransaction) {
                try {
                    transactionLock.wait(); // Wait until hasTransaction is set to false
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore interrupted status
                    logger.log(Level.SEVERE, "Interrupted while waiting for transaction to complete", e);
                    break;
                }
            }
        }
    }

    public void query(String request){
        String transactionID = request.substring(request.indexOf("]")+1);
        // [query]transaction_id,user_name,time,handling_fee,height
        returnInfo("[query]" + DatabaseOperator.query(transactionID)); // need pool?
        logger.info("Server received transaction from " + username + ": " + transaction);
    }

    /**
     * Update client with information.
     * @param info The information to send to the client
     */
    public void returnInfo(String info) {
        logger.info("Updating client " + username + " with info: " + info);
        out.println(info);
        finishTransaction();
    }

    /**
     * Get the username.
     * @return The username of the client
     */
    public synchronized String getUsername() {
        return this.username;
    }

    /**
     * Get the transaction.
     * @return The transaction received from the client
     */
    public synchronized String getTransaction() {
        return this.transaction;
    }

    // 以下都是標記用
    /**
     * Check if transaction is being handled.
     * @return True if transaction is being handled, false otherwise
     */
    public synchronized boolean isTransactionHandling() {
        return isTransactionHandling;
    }

    /**
     * Handle transaction.
     */
    public synchronized void handleTransaction() {
        logger.info("Client " + username + " is handling transaction.");
        synchronized (transactionLock) {
            isTransactionHandling = true;
        }
    }

    /**
     * Finish transaction.
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
     * Check if there is a transaction.
     * @return True if there is a transaction, false otherwise
     */
    public synchronized boolean hasTransaction() {
        return hasTransaction;
    }
}
