package project.block_chain.FTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.io.PrintWriter;

/**
 * FTPServerQueue class adds functionality for queuing connections after synchronization.
 */
public class FTPServerQueue {
    // Add queue functionality after synchronized connection
}

/**
 * FTPServer class represents an FTP server that handles client connections.
 */
class FTPServer {
    private ServerSocket FTPServerSoc;
    private static final int PORT = 9090;
    private static ExecutorService pool = Executors.newFixedThreadPool(4 + 1);
    private final LinkedBlockingQueue<ClientHandler> requestQueue = new LinkedBlockingQueue<>();

    /**
     * Constructor for FTPServer. Initializes the server socket and starts listening for client connections.
     * @throws IOException if an I/O error occurs when opening the socket.
     */
    public FTPServer() throws IOException {
        this.FTPServerSoc = new ServerSocket(PORT);
        pool.submit(this::processQueue); // Submit queue processing task to thread pool
        pool.submit(this::startListening); // Submit start listening task to thread pool
        pool.submit(this::startListening); // Start listening task
        pool.submit(this::startListening); // Start listening task
        pool.submit(this::startListening); // Start listening task
        // Shutdown the thread pool, no longer accepting new tasks
        pool.shutdown();
    }

    /**
     * Method to start listening for client connections.
     */
    public void startListening() {
        System.out.println("FTP Server started."); // Print FTP server started message
        while (true) {
            try {
                Socket clientSoc = FTPServerSoc.accept(); // Accept client connection
                System.out.println("A client is connected"); // Print client connected message
                ClientHandler clientThread = new ClientHandler(clientSoc); // Create client handler
                requestQueue.put(clientThread); // Put client handler into request queue
            } catch (IOException | InterruptedException e) {
                e.printStackTrace(); // Print exception stack trace
            }
        }
    }

    /**
     * Method to process client connections in the queue.
     */
    public void processQueue() {
        while (true) {
            try {
                ClientHandler clientHandler = requestQueue.take(); // Take client handler from queue
                pool.submit(() -> handleClientSocket(clientHandler)); // Submit client handling task to thread pool
            } catch (InterruptedException e) {
                e.printStackTrace(); // Print exception stack trace
            }
        }
    }

    /**
     * Method to handle client socket.
     * @param clientThread the client handler thread
     */
    public void handleClientSocket(ClientHandler clientThread) {
        try {
            clientThread.run(); // Execute client handler
        } catch (Exception e) {
            e.printStackTrace(); // Print exception stack trace
        }
    }

    /**
     * Start the FTP server.
     */
    public void start() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'start'");
    }
}
