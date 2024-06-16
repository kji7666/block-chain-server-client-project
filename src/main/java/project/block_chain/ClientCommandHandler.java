package project.block_chain.Test;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;


/**
 * Responsible for outgoing requests and processing responses
 * As an FTPClient variable, a client determines that a non-blocking ClientCommandHandler is available.
 * @author KJI
 */
public class ClientCommandHandler {
    private final Logger logger = Logger.getLogger(ClientCommandHandler.class.getName());
    private static CommandFormat commandFormat = new CommandFormat();
    private final Object lock = new Object();
    //private BlockChainGUI gui;

    public ClientCommandHandler(){//BlockChainGUI gui){
         //this.gui = gui;
    }

    /**
     * Executes the command by sending it and then receiving the response.
     * @param out The PrintWriter to send the command.
     * @param in The BufferedReader to receive the response.
     * @param type The type of command.
     * @param user The user sending the command.
     * @param command The command to be executed.
     */
    public synchronized void commandExecution(PrintWriter out, BufferedReader in, int type, String user, String command) {
        clearInputBuffer(in);
        commandSending(out, type, user, command);
        commandReceiving(in, user, type);
    }

    /**
     * Sends the command to the server.
     * @param out The PrintWriter to send the command.
     * @param type The type of command.
     * @param user The user sending the command.
     * @param command The command to be sent.
     */
    private synchronized void commandSending(PrintWriter out, int type, String user, String command) {
        logger.info(user + " try to send request" + commandFormat.commandSplicing(type, user, "Server", command));
        out.println(commandFormat.commandSplicing(type, user, "Server", command));
    }

    /**
     * Receives the response from the server and handles it based on the command type.
     * @param in The BufferedReader to receive the response.
     * @param sendingType The type of the original command sent.
     */
    private synchronized void commandReceiving(BufferedReader in, String user, int sendingType) {
        //logger.log(Level.INFO, "Receiving response for command type: {0}", sendingType);
        try {
            logger.info(user + " wait for response");
            while (true) {
                String response = in.readLine();
                if (response == null || response.equals("")) {
                    continue;
                }
                logger.info(user + " receive RESPONSE : " + response);
                int header = Integer.parseInt(commandFormat.commandParsing(response)[0]);
                if (header == 333) {
                    if (sendingType == 000) {
                        connectSuccess();
                    } else if (sendingType == 111) {
                        uploadSuccess(response);
                    } else if (sendingType == 222) {
                        querySuccess(response);
                    }
                    break;
                } else if (header == 444) {
                    responseError(commandFormat.commandParsing(response)[3]);
                    break;
                }
            }
        } catch (Exception e) {
            //logger.log(Level.SEVERE, "Exception occurred while receiving response", e);
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
        logger.info("connection success");
    }

    /**
     * Handles a successful upload response.
     * @param command The command string received.
     */
    private void uploadSuccess(String command) {
        // command: 4 transaction info, split by "\n"
        String[] dataArray = commandFormat.commandParsing(command);
        String[] commands = commandFormat.uploadMultiCommandsParsing(dataArray[3]);
        synchronized(lock){
            // System.out.println("===============================");
            // for(int i=0; i<commands.length; i++){
            //     logger.info("display result on gui");
            //     String[] result = commandFormat.uploadCommandParsing(commands[i]);
            //     // [0]=transactionId, [1]=user, [2]=time, [3]=handlingfee, [4]=height
            //     for (String data : result) {
            //         System.out.println(data);
            //     }
            //     System.out.println("----------------------------");
            // }
            // System.out.println("===============================");
        }
    }

    /**
     * Handles a successful query response.
     * @param command The command string received.
     */
    private void querySuccess(String command) {
        System.out.println("return command : " + command);
        logger.info("display result on gui");
        try{
            String[] result = commandFormat.queryResponseParsing(command);
            // System.out.println("===============================");
            // for (String data : result) {
            //     System.out.println(data);
            // }
            // System.out.println("===============================");
            //gui.setReturnInfo(result);
        } catch (Exception e){
            System.out.println("this is a error");
        }
        // [0]=transactionId, [1]=user, [2]=time, [3]=handlingfee, [4]=height, [5]=transactionText
        
    }

    /**
     * Handles an error response.
     */
    private void responseError(String message) {
        logger.info(message);
        //gui.errorMessage(message);
    }

}
