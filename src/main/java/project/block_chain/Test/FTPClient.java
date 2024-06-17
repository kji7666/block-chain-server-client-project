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

    private volatile String uploadInfo;
    private volatile String queryInfo;

    private boolean running = true;
    private String username;
    private static final Logger logger = Logger.getLogger(FTPClient.class.getName());

    /**
     * Constructs a new FTPClient with the given username.
     * @param username The username of the client
     */
    public FTPClient(String name){
        GUI gui = new SimpleGUI(username);

        clientCommandHandler = new ClientCommandHandler(gui);
        this.username = name;
        Socket tempSocket = null;
        BufferedReader tempInput = null;
        PrintWriter tempOutput = null;
        try {
            tempSocket = new Socket(SERVER_IP, SERVER_PORT);
            tempInput = new BufferedReader(new InputStreamReader(tempSocket.getInputStream()));
            tempOutput = new PrintWriter(tempSocket.getOutputStream(), true);
            System.out.println("Connected to server at " + SERVER_IP + ":" + SERVER_PORT);
        } catch (IOException e) {
            System.out.println("Error occurred while connecting to the server");
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
            System.out.println("Client connecting with username: " + username);
            if (clientSocket != null && input != null && output != null) {
                clientCommandHandler.commandExecution(output, input, 000, username, username); // username is command
                System.out.println("detect start");
                while (running) {
                    synchronized (this) {
                        if (uploadInfo != null) {
                            System.out.println("gui upload info");
                            clientCommandHandler.commandExecution(output, input, 111, username, uploadInfo);
                            uploadInfo = null;
                        }
                        if(queryInfo != null){
                            System.out.println("gui query info");
                            clientCommandHandler.commandExecution(output, input, 222, username, queryInfo);
                            queryInfo = null;
                        }
                    }
                    Thread.sleep(1000);
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Error occurred while connecting to the server");
        } finally {
            System.out.println("Client connection finished");
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
            System.out.println("Client resources closed successfully");
        } catch (IOException e) {
            System.out.println("Error occurred while closing resources");
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
        System.out.println("Setting upload transaction text : " + transactionInfo);
        this.uploadInfo = transactionInfo;
    }

    public synchronized void setQueryRequest(String transactionID) {
        System.out.println("Setting query transaction ID : " + transactionID);
        this.queryInfo = transactionID;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getUsername(){
        return username;
    }
}
