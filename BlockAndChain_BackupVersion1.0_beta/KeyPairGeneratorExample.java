import java.security.*;
import java.util.Base64;

public class KeyPairGeneratorExample {
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public void generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
    }

    public String getPrivateKeyAsString() {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    public String getPublicKeyAsString() {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    public static void main(String[] args) {
        KeyPairGeneratorExample example = new KeyPairGeneratorExample();
        try {
            example.generateKeyPair();
            String privateKeyStr = example.getPrivateKeyAsString();
            String publicKeyStr = example.getPublicKeyAsString();

            System.out.println("Private Key: " + privateKeyStr);
            System.out.println("Public Key: " + publicKeyStr);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    
}
