package bcProject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;



interface Operatable {
    public void makeBlock(String previousHash, String[] transaction);//make and insert
    public String searchBlock(String blockHash);//if exist then show
    
}

abstract class transactionBuffer {
    //wait for 4 transactions A,B,C,D
    //timeout may copy the last one and make all to 4::
    //special case 1 transaction: direcly stored into the block
    //special case 2and4 transactions: no duplicate
    //special case 3 transactions: duplicate the third
    public void makeTransactionID(){};
}


//use arraylist to stroe each block 
//the number of the block called height

class Tool{
    public void makeBlock(String previousHash, String[] transaction){
        Block b =new Block(previousHash, transaction);  
    }
}

public class Block {
    public String previousHash;
    public String blockHash;
    public String[] transaction;
    public long nonce;
    //what if we cannot find a nonce to match the comparator?->new run

    public Block(String previousHash, String[] transaction){
        this.previousHash = previousHash;
        this.transaction = transaction;
        this.nonce = 0;

        String[] content = new String[3];
        content[0] = SHA256.calculateSh256(transaction);
        content[1] = previousHash;
        String strNonce = null;
        
        while(true){
            // 0 is more than previous
            if(this.blockHash!=null && (SHA256.countLeadingZeros(this.blockHash)>SHA256.countLeadingZeros(previousHash))){
                break;
            }
            else{
                strNonce = String.valueOf(nonce);
                content[2] = strNonce;
                this.blockHash = SHA256.calculateSh256(content);
                nonce++;
            }
        }
        this.blockHash = SHA256.calculateSh256(content);
    }

    public String getPreviousHash() {
        return previousHash;
    }
    public String[] getTransaction() {
        return transaction;
    }
    public String getBlockHash() {
        return blockHash;
    }
    public void showAllInfo(){
        StringBuilder outputs = new StringBuilder();
        for(String s : this.transaction){
            outputs.append(s);
        }
        System.out.println("Hash: "+this.blockHash+"\n"+"Nonce: "+this.nonce+"\n"+"Transaction: "+outputs.toString()+"\n");
    }
}

class SHA256 {
    //if the input is a string
    public static String calculateSh256(String[] inputs){

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
}

