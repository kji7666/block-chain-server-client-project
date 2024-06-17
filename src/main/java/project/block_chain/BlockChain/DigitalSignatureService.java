package project.block_chain.BlockChain;
import java.security.PrivateKey;
import java.security.PublicKey;

public interface DigitalSignatureService {

    public void generateKeyPair()throws Exception;
    public byte[] signData(byte[] data)throws Exception;
    public boolean verifySignature(byte[] data, byte[] signatureBytes, PublicKey publicKey) throws Exception;
    
    public String exportPrivateKey();
    public String exportPublicKey();
    public void getKeyPairFile();//get the base64RSAKey String output file

    public void setPublicKey(String base64PublicKey) throws Exception;
    //covert given String to Key
    // public PrivateKey convertToPrivateKey(String base64PrivateKey) throws Exception;
    // public PublicKey convertToPublicKey(String base64PublicKey) throws Exception;

    public String getBackupDirectory();
}