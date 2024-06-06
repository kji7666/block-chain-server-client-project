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
 * The FTPClientTester class is used to test the FTP client functionality.
 */
public class FTPClient {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 9090;
    private final Socket clientSoc;
    private final BufferedReader in;
    private final PrintWriter out;
    private volatile String transactionInfo;
    private final ExecutorService executorService;
    private boolean running = true;
    private final String username;
    private static final Logger logger = Logger.getLogger(FTPClient.class.getName());

    /**
     * Constructor for FTPClientTester.
     * @param username The username of the client
     */
    public FTPClient(String username) {
        this.username = username;
        executorService = Executors.newFixedThreadPool(2);
        Socket tempSocket = null;
        BufferedReader tempIn = null;
        PrintWriter tempOut = null;
        try {
            logger.info("Connecting to server...");
            tempSocket = new Socket(SERVER_IP, SERVER_PORT);
            tempIn = new BufferedReader(new InputStreamReader(tempSocket.getInputStream()));
            tempOut = new PrintWriter(tempSocket.getOutputStream(), true);
            logger.info("Connected to server at " + SERVER_IP + ":" + SERVER_PORT);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error occurred while connecting to the server", e);
        }
        clientSoc = tempSocket;
        in = tempIn;
        out = tempOut;
        executorService.submit(this::startConnecting);
    }

    public void newConnection(){
        while(true){
            if(!running){
                startConnecting();
            }
        }
    }

    /**
     * Method to start connecting to the server.
     */
    public void startConnecting() {
        try {
            logger.info("Client connecting with username: " + username);
            if (clientSoc != null && in != null && out != null) {
                out.println(username);
                
                while (running) {
                    synchronized (this) {
                        if (transactionInfo != null && transactionInfo.startsWith("[upload]")) {
                            executorService.submit(this::upload);
                        } else if(transactionInfo != null && transactionInfo.startsWith("[query]")){
                            executorService.submit(this::query);
                        }
                    }
                    Thread.sleep(100);
                }
            }
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Error occurred while connecting to the server", e);
        } finally {
            logger.info("Client connection finished");
        }
    }

    public synchronized void upload(){ //[upload]message
        String serverResponse;
        logger.info("Client sending transaction and waiting for response...");
        out.println(transactionInfo);
        transactionInfo = null;
        // Wait for server response
        try{
            while (running && (serverResponse = in.readLine()) != null) {
                System.out.println("SSSSUCCESS");//(ok);
                //logger.info("Server response received");
                /*
                here
                 */
                // Check server response
                for (String infos : serverResponse.split("\n")) {
                    System.out.println(infos);
                }
                // Check if it needs to exit the loop
                if (!running) {
                    break;
                }
            }
        } catch (Exception e){
            logger.log(Level.SEVERE, "Error occurred while connecting to the server", e);
        }
    }

    private synchronized void query(){ // [query]message
        String serverResponse;
        logger.info("Client sending transaction and waiting for response...");
        out.println(transactionInfo);
        transactionInfo = null;
        // Wait for server response
        try{
            while (running && (serverResponse = in.readLine()) != null) {
                logger.info("Server response received");
                // Check server response
                for (String infos : serverResponse.split("\n")) {
                    System.out.println(infos);
                }
                // Check if it needs to exit the loop
                if (!running) {
                    break;
                }
            }
        } catch (Exception e){
            logger.log(Level.SEVERE, "Error occurred while connecting to the server", e);
        }
    }
    /**
     * Stop the client.
     */
    public synchronized void stop() {
        running = false;
        executorService.shutdownNow();
        try {
            if (clientSoc != null && !clientSoc.isClosed()) {
                clientSoc.close();
            }
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
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
     * Set the transaction information.
     * @param transactionInfo The transaction information to set
     */
    public synchronized void setTransactionInfo(String transactionInfo) {
        logger.info("Setting transaction info: " + transactionInfo);
        this.transactionInfo = transactionInfo;
    }
}
