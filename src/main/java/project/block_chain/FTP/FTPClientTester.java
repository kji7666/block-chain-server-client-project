package project.block_chain.FTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FTPClientTester {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 9090;
    private volatile String transaction;
    private final ExecutorService executorService;
    private boolean running = true;

    public FTPClientTester() {
        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(this::startConnecting);
    }

    private void startConnecting() {
        try (Socket clientSoc = new Socket(SERVER_IP, SERVER_PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSoc.getInputStream()));
            PrintWriter out = new PrintWriter(clientSoc.getOutputStream(), true)) {

            String serverResponse = null;

            // Initial server response (optional)
            if ((serverResponse = in.readLine()) != null) {
                System.out.println("[Server]: " + serverResponse);
            }
            System.out.println("Connected to the server.");

            while (running) {
                synchronized (this) {
                    if (transaction != null) {
                        System.out.println("Sending: " + transaction);
                        String text = transaction;

                        // Send command to server
                        out.println(text);

                        // Handle the server's message
                        while ((serverResponse = in.readLine()) != null) {
                            System.out.println("[Server]: " + serverResponse);
                            // Assuming "Finished" indicates the end of server response for a command
                            if (serverResponse.startsWith("Finished")) {
                                break;
                            }
                        }
                        transaction = null;
                    }
                }
                // Sleep for a short time to avoid busy-waiting
                Thread.sleep(100);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } finally {
            stop();
        }
    }

    public synchronized void transactionSetter(String content) {
        transaction = content;
    }

    public void stop() {
        running = false;
        executorService.shutdownNow();
    }

}
