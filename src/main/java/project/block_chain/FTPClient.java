package project.block_chain.Test;
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
 * FTPClient
 * When the user enables the GUI, FTPclient is created as field and connected to the FTPServer, 
 * responsible for outgoing user requests and processing the results returned by the server.
 * Not using threads to ensure one request is processed at a time
 * Disconnect all connections when closing window
 * @author KJI, Harris
 */
public class FTPClient {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 9090;
    private final Socket clientSocket;
    private final BufferedReader input;
    private final PrintWriter output;
    private ExecutorService pool = Executors.newFixedThreadPool(5); // if pool is static, every client will use same pool
    private final ClientCommandHandler clientCommandHandler;
    //private BlockChainGUI gui;

    private volatile String uploadInfo;
    private volatile String queryInfo;

    private boolean running = true;
    private String username;
    private static final Logger logger = Logger.getLogger(FTPClient.class.getName());

    /**
     * Constructs a new FTPClient with the given username.
     * @param username The username of the client
     */
    public FTPClient(String name){//BlockChainGUI gui) {
        // this.gui = gui;
        clientCommandHandler = new ClientCommandHandler();//gui);
        // this.username = gui.getUserName();
        this.username = name;
        Socket tempSocket = null;
        BufferedReader tempInput = null;
        PrintWriter tempOutput = null;
        try {
            tempSocket = new Socket(SERVER_IP, SERVER_PORT);
            tempInput = new BufferedReader(new InputStreamReader(tempSocket.getInputStream()));
            tempOutput = new PrintWriter(tempSocket.getOutputStream(), true);
            //logger.info("Connected to server at " + SERVER_IP + ":" + SERVER_PORT);
        } catch (IOException e) {
            //gui.errorMessage("Error occurred while connecting to the server");
            logger.log(Level.SEVERE, "Error occurred while connecting to the server", e);
        }
        clientSocket = tempSocket;
        input = tempInput;
        output = tempOutput;
        pool.submit(()->startConnecting()); // if not submit, gui will stock in here

    }

    /**
     * Starts connecting to the server.
     */
    public void startConnecting() {
        try {
            logger.info("Client connecting with username: " + username);
            if (clientSocket != null && input != null && output != null) {
                clientCommandHandler.commandExecution(output, input, 000, username, username); // username is command
                logger.info("detect start");
                while (running) {
                    synchronized (this) {
                        if (uploadInfo != null) {
                            logger.info("gui upload info");
                            System.out.println(SHA256.generateSHA256(uploadInfo));
                            clientCommandHandler.commandExecution(output, input, 111, username, uploadInfo);
                            uploadInfo = null;
                        }
                        if(queryInfo != null){
                            logger.info("gui query info");
                            clientCommandHandler.commandExecution(output, input, 222, username, queryInfo);
                            queryInfo = null;
                        }
                    }
                    Thread.sleep(1000);
                }
            }
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Error occurred while connecting to the server", e);
            //gui.errorMessage("Error occurred while connecting to the server");
        } finally {
            logger.info("Client connection finished");
        }
    }
    
    /**
     * Stops the client.
     */
    public synchronized void stop() {
        running = false;
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
            }
            logger.info("Client resources closed successfully");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error occurred while closing resources", e);
            //gui.errorMessage("Error occurred while closing resources");
        }
    }

    /**
     * Turn off the connection.
     */
    public void connectionOff() {
        running = false;
    }

    /**
     * Sets the upload request information.
     * @param transactionInfo The transaction information to set
     */
    public synchronized void setUploadRequest(String transactionInfo) {
        logger.info("Setting upload transaction text : " + transactionInfo);
        this.uploadInfo = transactionInfo;
    }

    public synchronized void setQueryRequest(String transactionID) {
        logger.info("Setting query transaction ID : " + transactionID);
        this.queryInfo = transactionID;
    }

    public void setUsername(String username){
        this.username = username;
    }
}
