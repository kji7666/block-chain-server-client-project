package project.block_chain.FTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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
    private final ClientCommandHandler clientCommandHandler;

    private volatile String uploadInfo;
    private volatile String queryInfo;

    private boolean running = true;
    private final String username;
    private static final Logger logger = Logger.getLogger(FTPClient.class.getName());

    /**
     * Constructs a new FTPClient with the given username.
     * @param username The username of the client
     */
    public FTPClient(String username) {
        clientCommandHandler = new ClientCommandHandler();
        this.username = username;
        Socket tempSocket = null;
        BufferedReader tempInput = null;
        PrintWriter tempOutput = null;
        try {
            tempSocket = new Socket(SERVER_IP, SERVER_PORT);
            tempInput = new BufferedReader(new InputStreamReader(tempSocket.getInputStream()));
            tempOutput = new PrintWriter(tempSocket.getOutputStream(), true);
            logger.info("Connected to server at " + SERVER_IP + ":" + SERVER_PORT);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error occurred while connecting to the server", e);
            SimpleGUI.setMessage("[ERROR]The request was unsuccessful.");
        }
        clientSocket = tempSocket;
        input = tempInput;
        output = tempOutput;
        startConnecting();
    }

    /**
     * Starts connecting to the server.
     */
    public void startConnecting() {
        try {
            logger.info("Client connecting with username: " + username);
            if (clientSocket != null && input != null && output != null) {
                clientCommandHandler.commandExecution(output, input, 0, username, username); // username is command
                logger.info("detect start");
                while (running) {
                    synchronized (this) {
                        if (uploadInfo != null) {
                            logger.info("detect uploadinfo");
                            clientCommandHandler.commandExecution(output, input, 1, username, uploadInfo);
                            uploadInfo = null;
                        }
                        if(queryInfo != null){
                            clientCommandHandler.commandExecution(output, input, 2, username, queryInfo);
                            queryInfo = null;
                        }
                    }
                    Thread.sleep(1000);
                }
            }
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Error occurred while connecting to the server", e);
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
}
