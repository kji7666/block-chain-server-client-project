package project.block_chain.FTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable{
    private Socket clientSoc;
    private BufferedReader in;
    private PrintWriter out;
    private CommandHandler commandHandler;//each thread need its own commandhandler?

    public ClientHandler(Socket clientSocket) {
        this.clientSoc = clientSocket;
        this.commandHandler = new CommandHandler();
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSoc.getInputStream()));
            out = new PrintWriter(clientSoc.getOutputStream(), true);
            
            out.println("Welcome to FTP server!");

            String request;

            while(true){
                request = in.readLine();

                System.out.println(request);

                //processing the request from clients
                commandHandler.handleCommand(request,out);
                
                /*if(request.contains("name")){
                    out.println(FTPServer.getRandomName());
                } else {

                    out.println("Type 'tell me a name'");
                }
                */
            }
        } catch (IOException e) {
            System.err.println("IOException in client handler");
            System.err.println(e.getStackTrace());
        } finally {
            out.close();
            try {
                System.out.println("A client has disconnected");
                clientSoc.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }   
        }   
    }
}
