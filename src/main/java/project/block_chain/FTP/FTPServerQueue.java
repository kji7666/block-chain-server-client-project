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

public class FTPServerQueue {
/*
 * 添加同步連線後的queue功能
 */
}

class FTPServer {
    private ServerSocket FTPServerSoc;
    private static final int PORT = 9090;
    private static ExecutorService pool = Executors.newFixedThreadPool(4 + 1);
    private final LinkedBlockingQueue<ClientHandler> requestQueue = new LinkedBlockingQueue<>();

    public FTPServer() throws IOException {
        this.FTPServerSoc = new ServerSocket(PORT);
        pool.submit(this::processQueue); // 提交隊列處理任務給線程池
        pool.submit(this::startListening); // 提交啟動監聽任務給線程池
        pool.submit(this::startListening); // 啟動監聽任務
        pool.submit(this::startListening); // 啟動監聽任務
        pool.submit(this::startListening); // 啟動監聽任務
        // 關閉線程池，不再接受新的任務
        pool.shutdown();
    }

    // 啟動監聽客戶端連接的方法
    public void startListening() {
        System.out.println("FTP Server started."); // 打印FTP服務器已啟動消息
        while (true) {
            try {
                Socket clientSoc = FTPServerSoc.accept(); // 接受客戶端連接
                System.out.println("A client is connected"); // 打印有客戶端連接消息
                ClientHandler clientThread = new ClientHandler(clientSoc); // 創建客戶端處理程序
                requestQueue.put(clientThread); // 將客戶端處理程序放入請求隊列
            } catch (IOException | InterruptedException e) {
                e.printStackTrace(); // 打印異常堆棧信息
            }
        }
    }

    // 處理隊列中的客戶端連接的方法
    public void processQueue() {
        while (true) {
            try {
                ClientHandler clientHandler = requestQueue.take(); // 從隊列中取出客戶端處理程序
                pool.submit(() -> handleClientSocket(clientHandler)); // 提交客戶端處理任務給線程池
            } catch (InterruptedException e) {
                e.printStackTrace(); // 打印異常堆棧信息
            }
        }
    }

    // 處理客戶端連接的方法
    public void handleClientSocket(ClientHandler clientThread) {
        try {
            clientThread.run(); // 執行客戶端處理程序
        } catch (Exception e) {
            e.printStackTrace(); // 打印異常堆棧信息
        }
    }

}

