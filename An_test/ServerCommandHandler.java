

import java.io.BufferedReader;
import java.io.PrintWriter;

//Handle the input text that the client has transfered to the server
//Specified in response to whatever the client wants


public class ServerCommandHandler {
    public static String[] commandhandle(PrintWriter out, BufferedReader in, String username){
        return null;
    }


    /* 
    public void handleCommand(String username, String command, PrintWriter out){
        if (command.toUpperCase().startsWith("[CONNECT]")) {
            handleConnect(username, command, out);
        } else if (command.toUpperCase().startsWith("[UPLOAD]")) {
            handleUpload(username, command, out);
        } else if (command.toUpperCase().startsWith("[QUERY]")) {
            handleQuery(username, command, out);
        } else {
            out.println("[ERROR]The request was unsuccessful.");
        }
        */
        /* 
        if (command.toUpperCase().startsWith("LIST")) {
            handleList(out);
        } else if (command.toUpperCase().startsWith("RETR")) {
            handleRetr(command, out);
        } else if (command.toUpperCase().startsWith("STOR")) {
            handleStor(command, out);
        } else if (command.toUpperCase().startsWith("PASV")) {
            handlePasv(out);
        } else {
            out.println("502 Command not implemented.");
        }*/
    
/* 
    private void handleConnect(String username, String command, PrintWriter out) {
        // Implementation for LIST command
        String response = "[Success]establish a connection with " + username;
        out.println(response);
        // out.println("Finished"); // Indicate end of response
    }

    private void handleUpload(String username, String command, PrintWriter out) {
        // Implementation for RETR command
        String response = "[Success]upload transaction from " + username;
        out.println(response);
        //out.println("Finished"); // Indicate end of response
    }

    private void handleQuery(String username, String command, PrintWriter out) {
        // Implementation for STOR command
        String response = "[Success]query request from " + username;
        out.println(response);
        //out.println("Finished"); // Indicate end of response
    }*/
    /* 
    private void handlePasv(PrintWriter out) {
        // Implementation for PASV command
        out.println("227 Entering Passive Mode.");
        out.println("Finished"); // Indicate end of response
    }
    */
}
