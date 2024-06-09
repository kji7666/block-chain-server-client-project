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
    public static String commandSplicing(int type, String user, String target, String command) {
        StringBuilder sb = new StringBuilder();
        return sb.append(CommandMap.getHeader(type)) // [header] from user to target : command
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
    public static String[] commandParsing(String command) {
        // Define the regex pattern
        String pattern = "(\\[.*?\\]) from (.*?) to (.*?) : (.*)";
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
     * Parses an upload response string.
     * @param command The response string.
     * @return An array containing transactionId, user, time, handling fee, and height.
     */
    public static String[] uploadResponseParsing(String command) {
        return command.trim().split(","); // Parse and split the string
    }

    /**
     * Parses a query response string.
     * @param command The response string.
     * @return An array containing transactionId, user, time, handling fee, height, and transaction text.
     */
    public static String[] queryResponseParsing(String command) {
        return command.trim().split(","); // Parse and split the string
    }
}
