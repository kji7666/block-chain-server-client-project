package bcProject.BlockChain;
import java.security.PublicKey;

public interface DigitalSignatureService {

    public void generateKeyPair()throws Exception;
    public byte[] signData(byte[] data)throws Exception;
    public boolean verifySignature(byte[] data, byte[] signatureBytes, PublicKey publicKey) throws Exception;
    

    public String exportPrivateKey();
    public String exportPublicKey();
    public void getKeyPairFile(String KeyDirectoryName);
}