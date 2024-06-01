package project.block_chain.FTP;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FTPServer {
    private ServerSocket FTPServerSoc;

    /* for testing
    private static String[] names = new String[]{"Trevor","Harris","Mike"};
    private static String[] adjs = new String[]{"Clever","Diligent","Spooky"};
    */
    //the list store each client's socket
    private static List<ClientHandler> clients = new ArrayList<>();
    //specify the port the server want to listen on
    private static final int PORT = 9090;
    //spcify the thread pool's maximum number is 4, 4 client can simultaneously connecting to the server
    private static ExecutorService pool = Executors.newFixedThreadPool(4);

    public FTPServer() throws IOException {
        this.FTPServerSoc = new ServerSocket(PORT);
    }

    public void start(){
        System.out.println("FTP Server started.");
        
        while(true){
            try {
                /*
                /*if FTPServerSoc.accept() has listened return true then do the below
                /*the ServerSocket.accept() method is a blocking call. This means that it will wait, or "block," until a client connection is available. 
                */
                //System.out.println("[SERVER] Waiting for client connection...");
                Socket clientSoc = FTPServerSoc.accept();
                System.out.println("A client is connected");
                ClientHandler clientThread = new ClientHandler(clientSoc);
                clients.add(clientThread);
                //execute every thing in the pool in almost same time
                pool.execute(clientThread);
            } catch (Exception e) {
                e.printStackTrace();
                //not yet done
            }
        }
    }

    /* 
    public static String getRandomName(){
        String name = names[(int) (Math.random() * names.length)];
        String adj = adjs[(int) (Math.random() * adjs.length)];
        return name + " " + adj;
    }
    */
}

