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
 */
public class FTPServer {
    private ServerSocket FTPServerSoc;
    private static final int PORT = 9090;
    private static ExecutorService pool = Executors.newFixedThreadPool(20);
    private final LinkedBlockingQueue<String> clientsInLine = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<String> transactionInLine = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<Block> blockProcessingQueue = new LinkedBlockingQueue<>();
    private final List<ClientHandler> connectedClients = new ArrayList<>();
    private static Chain blockChain = new Chain();
    private final int NUMBER_OF_TRANSACTION_IN_BLOCK = 1;
    private final Object transactionLock = new Object();

    private static final Logger logger = Logger.getLogger(FTPServer.class.getName());

    /**
     * Constructor for FTPServerQueue.
     * @throws IOException If an I/O error occurs when creating the ServerSocket.
     */
    public FTPServer() throws IOException {
        this.FTPServerSoc = new ServerSocket(PORT);
        logger.info("FTP Server started on port " + PORT);

        pool.submit(this::processTransactionQueue);
        pool.submit(this::processReturnBlock);
        pool.submit(this::processBlockInfo);
        pool.submit(blockChain::makeBlock);
        pool.submit(this::detectTransaction);
        pool.submit(this::startListening);
        pool.submit(this::startListening);
        pool.submit(this::startListening);
        pool.submit(this::startListening);
    }

    /**
     * 2.if有交易被上傳, 加入queue
     * Detects transactions from connected clients.
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
     * 1.連線並等待request
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
     * 3.把要處理的交易排隊, 送入區塊鏈
     * Processes the transaction queue.
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
     * 4.做成新區塊後, 立即get
     * Processes the return block.
     */
    public void processReturnBlock() {
        int height = blockChain.getHead().getHeight();
        while (true) {
            synchronized (blockChain) {
                int currentHeight = blockChain.getHead().getHeight();
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
     * 5. 把get的block資料向所有clientHandler廣播, 使其回傳
     * Processes block information.
     */
    public void processBlockInfo() {
        while (true) {
            try {
                //[upload]transaction_id,user_name,time,handling_fee,height
                Block block = blockProcessingQueue.take();
                StringBuilder sb = new StringBuilder("");
                String[] transactionIDs = block.getTransactions();

                for (int i = 0; i < NUMBER_OF_TRANSACTION_IN_BLOCK; i++) {
                    String username = clientsInLine.take();
                    String[] dataArray = new String[]{transactionIDs[i], username, block.getTimestamp().toString(), block.getHandlingFee(), String.valueOf(blockChain.getHead().getHeight())};
                    pool.submit(() -> DatabaseOperator.insert(dataArray));
                    sb.append("[upload]")
                    .append(dataArray[0]).append(",")
                    .append(dataArray[1]).append(",")
                    .append(dataArray[2]).append(",")
                    .append(dataArray[3]).append(",")
                    .append(dataArray[4]).append(",").append("\n");
                    logger.info("Processed transaction for client: " + username);
                }
                String blockInfo = sb.toString();
                for (ClientHandler client : connectedClients) {
                    pool.submit(() -> client.returnInfo(blockInfo));
                }
                logger.info("Server returned result");

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.log(Level.SEVERE, "Block info processing interrupted", e);
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
