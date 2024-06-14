package project.block_chain.Test;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The FTPServerQueue class represents an FTP server that uses a queue to manage client connections and transactions.
 *
 * @param FTPServerSoc         The server socket for accepting client connections.
 * @param clientsInLine        The queue to store usernames of clients waiting in line for transaction processing.
 * @param transactionInLine    The queue to store transactions waiting to be processed(add to chain).
 * @param blockChain           The blockchain instance to store transaction blocks.
 * @param blockProcessingQueue The queue to store new blocks info waiting to be processed.
 * @param connectedClients     The list of connected client handlers.
 */
public class FTPServer {
    private ServerSocket FTPServerSoc;
    private DatabaseOperator databaseOperator;
    private static final int PORT = 9090;
    private static CommandFormat commandFormat = new CommandFormat();
    private static ExecutorService pool = Executors.newFixedThreadPool(23);

    private final LinkedBlockingQueue<String> clientsInLine = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<String> transactionInLine = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<Block> blockProcessingQueue = new LinkedBlockingQueue<>();
    private final List<ClientHandler> connectedClients = new ArrayList<>();

    private static Chain blockChain;
    private final int NUMBER_OF_TRANSACTION_IN_BLOCK = 4;
    private final Object transactionLock = new Object();

    private static final Logger logger = Logger.getLogger(FTPServer.class.getName());

    /**
     * Constructor for FTPServerQueue.
     * @throws IOException If an I/O error occurs when creating the ServerSocket.
     */
    public FTPServer() throws IOException {
        blockChain = Chain.getInstance();
        this.FTPServerSoc = new ServerSocket(PORT);
        this.databaseOperator = new DatabaseOperator();
        logger.info("FTP Server started on port " + PORT);

        pool.submit(this::processTransactionQueue);
        pool.submit(this::processReturnBlock);
        pool.submit(this::processBlockInfo);
        //pool.submit(()->blockChain.queueSizeChecking());
        pool.submit(this::detectTransaction);
        pool.submit(this::startListening);
        pool.submit(this::startListening);
        pool.submit(this::startListening);
        pool.submit(this::startListening);
    }

    /**
     * Starts listening for client connections.
     */
    public void startListening() {
        logger.info("FTP server listening");
        while (true) {
            try {
                Socket clientSoc = FTPServerSoc.accept();
                logger.info("Client connected to server");
                ClientHandler clientThread = new ClientHandler(clientSoc);
                connectedClients.add(clientThread);
                pool.submit(clientThread);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error accepting client connection", e);
            }
        }
    }

    /**
     * Detects transactions from connected clients.
     * If a transaction is uploaded, add transactionInLine to queue into the Chain. 
     * At the same time, add the client who uploaded the transaction to ClientInLine to compare the generated block information.
     */
    public void detectTransaction() {
        while (true) {
            for (ClientHandler client : connectedClients) {
                if (client.hasTransaction() && !client.isTransactionHandling()) {
                    client.handleTransaction();
                    logger.info("Transaction found from client: " + client.getUsername());
                    synchronized (transactionLock) {
                        transactionInLine.add(client.getTransaction());
                        clientsInLine.add(client.getUsername());
                    }
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.log(Level.SEVERE, "Transaction detection interrupted", e);
                break;
            }
        }
    }

    /**
     * Queue the transactions to be processed and send them to the blockchain
     */
    public void processTransactionQueue() {
        while (true) {
            try {
                String transaction = transactionInLine.take();
                logger.info("Processing transaction");
                pool.submit(() -> blockChain.addTransaction(transaction));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.log(Level.SEVERE, "Transaction queue processing interrupted", e);
                break;
            }
        }
    }

    /**
     * By detecting the height changes of the block chain, 
     * the latest block information is captured and sent to blockProcessingQueue to prepare for broadcast.
     */
    public void processReturnBlock() {
        int height = blockChain.getChainLatestHeight();
        while (true) {
            synchronized (blockChain) {
                int currentHeight = blockChain.getChainLatestHeight();
                if (currentHeight > height) {
                    logger.info("Detected new block ");
                    try {
                        blockProcessingQueue.put(blockChain.getHead());
                        height = currentHeight;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        logger.log(Level.SEVERE, "Error processing new block", e);
                        break;
                    }
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.log(Level.SEVERE, "Block detection interrupted", e);
                break;
            }
        }
    }

    /**
     * Return the block data on the queue to all clients (broadcast)
     */
    public void processBlockInfo() {
        while (true) {
            try {
                //[upload]transaction_id,user_name,time,handling_fee,height
                Block block = blockProcessingQueue.take();
                String[] transactionIDs = block.getTransactionIds();
                String[] commands = new String[4];

                for (int i = 0; i < NUMBER_OF_TRANSACTION_IN_BLOCK; i++) {
                    String username = clientsInLine.take();
                    String[] dataArray = new String[]{transactionIDs[i], username, block.getTimestamp(), "0.1 USD", String.valueOf(block.getHeight())};
                    pool.submit(() -> databaseOperator.insert(dataArray));
                    commands[i] = commandFormat.responseDataSplicing(dataArray);
                    logger.info("Processed transaction for client: " + username + "---" + commands[i]);
                }
                for (ClientHandler client : connectedClients) {
                    pool.submit(() -> client.returnInfo(commandFormat.uploadResponseSplicing(333, "server", client.getUsername(), commands)));
                }
                logger.info("Server returned result");

            } catch (InterruptedException e) {
                logger.info(e.getMessage());
                logger.log(Level.SEVERE, "Block info processing interrupted", e);
                //@Error
                break;
            }
        }
    }

    /**
     * Starts the FTP server.
     */
    public void start() {
        throw new UnsupportedOperationException("Unimplemented method 'start'");
    }
}