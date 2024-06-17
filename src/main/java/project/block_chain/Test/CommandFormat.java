package project.block_chain.Test;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class CommandFormat {
    /**
     * Constructs a command string.
     * @param type The command type.
     * @param user The sender.
     * @param target The receiver.
     * @param command The command content.
     * @return The constructed command string.
     */
    public String commandSplicing(int type, String user, String target, String command) {
        StringBuilder sb = new StringBuilder();
        return sb.append("[").append(type).append("]")  // [header] from user to target : command
                    .append(" from ").append(user)
                    .append(" to ").append(target)
                    .append(" : ").append(command).toString();
    }

    /**
     * Parses a command string.
     * @param command The command string.
     * @return An array containing header, sender, receiver, and command.
     * @throws IllegalArgumentException If the command format is incorrect.
     */
    public String[] commandParsing(String command) {
        // Define the regex pattern
        String pattern = "\\[(.*?)\\] from (.*?) to (.*?) : (.*)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(command);
        
        // Match and extract results
        if (m.find()) {
            String[] result = new String[4];
            result[0] = m.group(1); // header
            result[1] = m.group(2); // sender
            result[2] = m.group(3); // receiver
            result[3] = m.group(4); // command
            return result;
        } else {
            // Throw an exception if the pattern does not match
            throw new IllegalArgumentException("incomplete message");
        }
    }

    /**
     * This method specifies the format of string when data(commands) is passed.
     * @param dataArray Data obtained from the chain
     * @return Splice the data obtained on the chain in this format
     */
    public String responseDataSplicing(String[] dataArray){
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<dataArray.length; i++){
            sb.append(dataArray[i]);
            if(i != dataArray.length-1){
                sb.append(",");
            }
        }
        return sb.toString();
    }

    /**
     * This method specifies the form when multiple data commands form a String.
     * @param commandArray commands following responseDataSplicing methods
     * @return Splice complete response containing multiple commands in this format
     */
    public String uploadResponseSplicing(int type, String user, String target, String[] commandArray){
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<commandArray.length; i++){
            sb.append(commandArray[i]);
            if(i != commandArray.length-1){
                sb.append("/");
            }
        }
        return commandSplicing(type, user, target, sb.toString());
    }

    /**
     * Parses an upload response string.
     * @param commands The response string.
     * @return split multiple commands and store in array
     */
    public String[] uploadMultiCommandsParsing(String commands) {
        return commands.trim().split("/"); // Parse and split the string
    }

    /**
     * Parses an upload response string.
     * @param command The command string.
     * @return split a commands to get data and store in array
     */
    public String[] uploadCommandParsing(String command) {
        return command.trim().split(","); // Parse and split the string
    }

    /**
     * @return Return according to the specified format
     */
    public String queryResponseSplicing(int type, String user, String target, String command){
        return commandSplicing(type, user, target, command);
    }

    /**
     * Parses a query response string.
     * @param command The response string.
     * @return An array containing transactionId, user, time, handling fee, height, and transaction text.
     */
    public String[] queryResponseParsing(String command) {
        return commandParsing(command)[3].trim().split(","); // Parse and split the string
    }
}
