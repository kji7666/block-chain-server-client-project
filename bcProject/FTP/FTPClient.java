package bcProject.FTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class FTPClient {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 9090;

    public void startConnecting(){
        try {
            Socket clientSoc = new Socket(SERVER_IP,SERVER_PORT);
            
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSoc.getInputStream()));
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(clientSoc.getOutputStream(), true);

            String serverResponse = null;
            String command;

            // Print initial server response (e.g., welcome message)
            if ((serverResponse = in.readLine()) != null) {
                System.out.println("[Server]: " + serverResponse);
            }

            while(true){
                System.out.println("> ");
                command = keyboard.readLine();
    
                //quit the connection
                if(command == null || command.equalsIgnoreCase("exit")){
                    out.println("Quit");
                    System.out.println("QUIT!");
                    break;
                }

                //send command to server
                out.println(command);

                //handle the server's message
                while((serverResponse = in.readLine()) != null){
                    System.out.println("[Server]: "+ serverResponse);
                    // Assuming "Finished" indicates the end of server response for a command
                    if(serverResponse.startsWith("Finished")){
                        break;
                    }
                    //This allows client to send another message without stucking in the loop, waiting for server's message
                    break;
                }
            }

            in.close();
            out.close();
            clientSoc.close();
        } catch (IOException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
}
