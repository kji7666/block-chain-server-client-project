import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.logging.Logger;


/**
 * Responsible for outgoing requests and processing responses
 * As an FTPClient variable, a client determines that a non-blocking ClientCommandHandler is available.
 * @author KJI
 */
public class ClientCommandHandler {
    private static final Logger logger = Logger.getLogger(ClientCommandHandler.class.getName());

    /**
     * Executes the command by sending it and then receiving the response.
     * @param out The PrintWriter to send the command.
     * @param in The BufferedReader to receive the response.
     * @param type The type of command.
     * @param user The user sending the command.
     * @param command The command to be executed.
     */
    public synchronized void commandExecution(PrintWriter out, BufferedReader in, int type, String user, String command) {
        //logger.log(Level.INFO, "Executing command: {0}, User: {1}, Type: {2}", new Object[]{command, user, type});
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
        logger.info(user + " try to send request");
        out.println(CommandFormat.commandSplicing(type, user, "Server", command));
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
                logger.info(user + " receive response");
                String[] result = CommandFormat.commandParsing(response);
                String header = result[0];
                String command = result[3];
                
                if (header.equals(CommandMap.getHeader(3))) {
                    if (sendingType == 0) {
                        connectSuccess();
                    } else if (sendingType == 1) {
                        uploadSuccess(command);
                    } else if (sendingType == 2) {
                        querySuccess(command);
                    }
                    break;
                } else if (header.equals(CommandMap.getHeader(4))) {
                    responseError();
                    break;
                }
            }
        } catch (Exception e) {
            //logger.log(Level.SEVERE, "Exception occurred while receiving response", e);
            responseError();
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
        logger.info("display result on gui");
        String[] result = CommandFormat.uploadResponseParsing(command);
        // [0]=transactionId, [1]=user, [2]=time, [3]=handlingfee, [4]=height
        for (String data : result) {
            System.out.println(data);
        }
    }

    /**
     * Handles a successful query response.
     * @param command The command string received.
     */
    private void querySuccess(String command) {
        logger.info("display result on gui");
        String[] result = CommandFormat.queryResponseParsing(command);
        // [0]=transactionId, [1]=user, [2]=time, [3]=handlingfee, [4]=height, [5]=transactionText
        for (String data : result) {
            System.out.println(data);
        }
    }

    /**
     * Handles an error response.
     */
    private void responseError() {
        logger.info("Error: Request not completed");
        SimpleGUI.printMessage("Error: Request not completed");
    }
}
