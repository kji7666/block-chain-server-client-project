package project.block_chain.Test;
import java.io.BufferedReader;
import java.io.FileReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256 {
    //if the input is a string
    public static String generateSHA256(String[] inputs){

        try{
            //Create an instance for sha256 
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            //Calculate the hash value of the input string
            int index = 0;
            try {
                
                for(; index<inputs.length; index++){
                    md.update(inputs[index].getBytes());
                }

                // for(String input : inputs){
                //     md.update(input.getBytes());
                // }
            } catch (Exception e) {
                System.err.println("Input String array index:"+index+" is "+inputs[index]);
                e.printStackTrace();
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

    /**Calculate SHA256 for a given input filepath
     * The main difference is that this method check client's transfer task content
     * cannot accept an empty file
     * @param filePath 
     *        read the input file path from the client
     */
    public static String generateFileSha256(String inputData){
        if(inputData.isEmpty()){
            System.err.println("The content in the client's file is empty.");
            return null;
        }
        //We see a whole txt file as a large string
        return generateSHA256(inputData);
    }


    /** This is a  algorithm for hash comparator
     * 
     * @param  hash 
     *         a given sha256 hash 
     * @return count
     *         how many zero the is in a string's head
     */
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

    /**This is for client to read the file from its disk->the client programm
     * 
     * @param  filePath
     *         A given path in the client's path
     * @return the content in client's file.txt uploaded
     */
    public static String readFile(String filePath){
        StringBuilder sb = new StringBuilder();
        String line = null;
        try(BufferedReader bf = new BufferedReader(new FileReader(filePath))){
            while((line = bf.readLine()) != null){
                bf.readLine(); 
                sb.append(line);         
            }
        } catch (Exception e) {
            System.err.println("Error in reading file");
            e.printStackTrace();
        }
        return sb.toString();
    }

}