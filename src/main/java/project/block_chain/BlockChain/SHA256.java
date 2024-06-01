package project.block_chain.BlockChain;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256 {
    //if the input is a string
    public static String generateSHA256(String[] inputs){

        try{
            //Create an instance for sha256 
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            //Calculate the hash value of the input string
            for(String input : inputs){
                md.update(input.getBytes());
            }

            byte[] hashBytes = md.digest();
            //Convert the byte array to a hexademical string
            StringBuilder hexString = new StringBuilder();
            for(byte hashByte : hashBytes){
                String hex = Integer.toHexString(0xff & hashByte);
                if(hex.length() == 1){
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();    
            return null;
        }
    }
    //Hash only one string
    public static String generateSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    //hashcomparator 
    public static int countLeadingZeros(String hash){
        int count = 0;
        for(char c : hash.toCharArray()){
            if(c =='0'){
                count++;
            } else{
                break;
            }
        }
        return count;
    }

    
    /**Calculate Transaction ID */
    public static String calculateTXID(StringBuilder sb){
        return SHA256.generateSHA256(sb.toString());
    }
}

    

