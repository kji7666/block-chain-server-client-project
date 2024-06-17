package project.block_chain.FTP;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.logging.Logger;

import project.block_chain.GUI.*;

/**
 * Responsible for outgoing requests and processing responses
 * As an FTPClient variable, a client determines that a non-blocking ClientCommandHandler is available.
 * @author KJI
 */
public class ClientCommandHandler {
    private final Logger logger = Logger.getLogger(ClientCommandHandler.class.getName());
    private static CommandFormat commandFormat = new CommandFormat();
    private final Object lock = new Object();
    private GUI gui;

    public ClientCommandHandler(GUI gui){
        this.gui = gui;
    }

    /**
     * Executes the command by sending it and then receiving the response.
     * @param out The PrintWriter to send the command.
     * @param in The BufferedReader to receive the response.
     * @param type The type of command.
     * @param user The user sending the command.
     * @param command The command to be executed.
     */
    public synchronized void commandExecution(PrintWriter out, BufferedReader in, int type, String user, String request) {
        clearInputBuffer(in);
        commandSending(out, type, user, request);
        commandReceiving(in, user, type, request);
    }

    /**
     * Sends the command to the server.
     * @param out The PrintWriter to send the command.
     * @param type The type of command.
     * @param user The user sending the command.
     * @param command The command to be sent.
     */
    public synchronized void commandSending(PrintWriter out, int type, String user, String request) {
        //System.out.println(user + " try to send request" + commandFormat.commandSplicing(type, user, "Server", request));
        out.println(commandFormat.commandSplicing(type, user, "Server", request));
    }

    /**
     * Receives the response from the server and handles it based on the command type.
     * @param in The BufferedReader to receive the response.
     * @param sendingType The type of the original command sent.
     */
    public synchronized void commandReceiving(BufferedReader in, String user, int sendingType, String request) {
        try {
            //System.out.println(user + " wait for response");
            while (true) {
                String response = in.readLine();
                System.out.println("any response");
                if (response == null || response.equals("")) {
                    continue;
                }
                System.out.println(user + " receive response : " + response);
                int header = Integer.parseInt(commandFormat.commandParsing(response)[0]);
                if (header == 333) {
                    if (sendingType == 000) {
                        connectSuccess();
                    } else if (sendingType == 111) {
                        uploadSuccess(request, response, user);
                    } else if (sendingType == 222) {
                        querySuccess(response, user);
                    }
                    break;
                } else if (header == 444) {
                    responseError(commandFormat.commandParsing(response)[3]);
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            responseError("Error: Response not completed");
        }
    }

    private void clearInputBuffer(BufferedReader in) {
        try {
            while (in.ready()) {
                in.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Handles a successful connection response.
     */
    private void connectSuccess() {
        //System.out.println("connection success");
    }

    /**
     * Handles a successful upload response.
     * @param command The command string received.
     */
    private void uploadSuccess(String request, String response, String username) {
        // command: 4 transaction info, split by "\n"
        String[] dataArray = commandFormat.commandParsing(response);

        String[] commands = commandFormat.uploadMultiCommandsParsing(dataArray[3]);
        simulateGuiUploadOutput(commands, username);
    }

    /**
     * Handles a successful query response.
     * @param command The command string received.
     */
    private void querySuccess(String command, String username) {
        //System.out.println("return command : " + command);
        System.out.println("display result on gui");
        try{
            String[] result = commandFormat.queryResponseParsing(command);
            simulateGuiQueryOutput(result, username);
        } catch (Exception e){
            System.out.println("this is a error");
        }
        // [0]=transactionId, [1]=user, [2]=time, [3]=handlingfee, [4]=height, [5]=transactionText
    }

    /**
     * Handles an error response.
     */
    private void responseError(String message) {
        System.out.println(message);
    }

    private void simulateGuiUploadOutput(String[] commands, String username){
        StringBuilder sb = new StringBuilder("");
        sb.append("\n").append("New Block\n");
        sb.append("=================================\n");
        for(int i=0; i<commands.length; i++){
            String[] result = commandFormat.uploadCommandParsing(commands[i]);
            sb.append(" transaction ID : ").append(result[0]).append("\n")
                .append(" user : ").append(result[1]).append("\n")
                .append(" time : ").append(result[2]).append("\n")
                .append(" handling fee : ").append(result[3]).append("\n")
                .append(" height : ").append(result[4]).append("\n");
            // [0]=transactionId, [1]=user, [2]=time, [3]=handlingfee, [4]=height
                sb.append("=================================\n");
        }
        gui.showUploadResult(sb.toString());
        System.out.println("FINNISH");
    }

    private void simulateGuiQueryOutput(String[] result, String username){
        StringBuilder sb = new StringBuilder();
        try{
            sb.append("======================================\n");
            sb.append(username).append(" receive query response :\n");
                    sb.append(" transaction ID : ").append(result[0]).append("\n")
                    .append(" user : ").append(result[1]).append("\n")
                    .append(" time : ").append(result[2]).append("\n")
                    .append(" handling fee : ").append(result[3]).append("\n")
                    .append(" height : ").append(result[4]).append("\n")
                    .append(" transaction : ").append(result[5]).append("\n");
        }catch(Exception e){
            System.out.println("error not in gui");
        }
        gui.showQueryResult(sb.toString());
        System.out.println("FINNISH");
    }
}
