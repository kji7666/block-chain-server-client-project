package project.block_chain.FTP;

import java.io.PrintWriter;

//Handle the input text that the client has transfered to the server
//Specified in response to whatever the client wants


public class CommandHandler {
    public void handleCommand(String command, PrintWriter out){
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
        }
    }

    private void handleList(PrintWriter out) {
        // Implementation for LIST command
        out.println("150 Here comes the directory listing.");
        out.println("Finished"); // Indicate end of response
    }

    private void handleRetr(String command, PrintWriter out) {
        // Implementation for RETR command
        out.println("150 Opening data connection for file transfer.");
        out.println("Finished"); // Indicate end of response
    }

    private void handleStor(String command, PrintWriter out) {
        // Implementation for STOR command
        out.println("150 Opening data connection for file upload.");
        out.println("Finished"); // Indicate end of response
    }

    private void handlePasv(PrintWriter out) {
        // Implementation for PASV command
        out.println("227 Entering Passive Mode.");
        out.println("Finished"); // Indicate end of response
    }

    
}
