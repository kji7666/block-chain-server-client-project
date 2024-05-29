
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
/*
 * 以4檔案同時讀取並輸出為例
 */

public class DataQueue {
    // 建立一個線程安全的請求隊列
    private final LinkedBlockingQueue<String> requestQueue = new LinkedBlockingQueue<>();
    // 紀錄處理過的文件數量
    private final int numberOfFiles = 4;
    private int filesProcessed = 0;
    private int queueOutCount = 0; // 確認所有字都被輸出

    public static void main(String[] args) {
        DataQueue queue = new DataQueue();
        String client1 = "data1.txt";  
        String client2 = "data2.txt";
        String client3 = "data3.txt";
        String client4 = "data4.txt";

        // 創建一個固定大小為5的線程池
        ExecutorService executor = Executors.newFixedThreadPool(4 + 1);

        // 提交隊列解析任務給線程池
        executor.submit(queue::dequeParser);
        // 提交文件讀取任務給線程池
        executor.submit(() -> queue.requestReader(client1));
        executor.submit(() -> queue.requestReader(client2));
        executor.submit(() -> queue.requestReader(client3));
        executor.submit(() -> queue.requestReader(client4));


        // 關閉線程池，不再接受新的任務
        executor.shutdown();
        try {
            // 等待所有任務完成，最多等待1小時
            executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    public synchronized void requestReader(String fileName) {
        // 使用BufferedReader來讀取文件
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            // 逐行讀取文件內容
            while ((line = reader.readLine()) != null) {
                // 將讀取的行放入請求隊列
                requestQueue.put(line);  // 使用 put 方法保證線程安全
                System.out.println(line); // 確認線程執行順序
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            // 同步控制對filesProcessed計數器的訪問
            synchronized (this) {
                filesProcessed++;
                // 如果所有文件都處理完畢，插入EOF標誌到隊列中
                if (filesProcessed == numberOfFiles) {
                    try {
                        requestQueue.put("EOF");
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.out.println(e.getMessage());
                    }
                }
            }
        }
    }

    public void dequeParser() {
        while (true) {
            try {
                // 從請求隊列中取出一行，使用 take 方法保證線程安全
                String line = requestQueue.take();  
                // 如果讀到EOF標誌，退出循環
                if ("EOF".equals(line)) {
                    break;
                }
                System.out.println("Queue out");
                queueOutCount++;
                // 解析行內容
                blockChainProgram(line);
            } catch (InterruptedException e) {
                // 如果線程被中斷，設置中斷標誌並打印錯誤信息
                Thread.currentThread().interrupt();
                System.out.println(e.getMessage());
            }
        }
    }

    public void blockChainProgram(String line) {
        // 傳入
    }
}
