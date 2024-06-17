package project.block_chain.Test;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Ensure data integrity during backup and restoration.
 * The BlockchainBackupManager class provides functionality to backup and restore blockchain blocks
 * It includes methods for generating keys, signing data, and verifying signatures, 
 * and output/input signed serialized object 
 * 
 * @Author Harris
 * @Since 15, Jun 2024
 * 
 */
public class SHA256SignatureHandler implements DigitalSignatureService {
    private static final String SIGNING_ALGORITHM = "SHA256withRSA";
    private PrivateKey privateKey;
    private PublicKey publicKey;
    protected String backupDirectory;


     /**
     * Constructs a SHA256SignatureHandler for backup mode and generates a key pair
     *
     * @param backupDirectory the directory to store backup files
     */
    public SHA256SignatureHandler(String backupDirectory) {
        this.backupDirectory = backupDirectory;
        try {
            generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            System.out.println(" The algorithm for digital signature is invalid");
        }
    }
    

    /**
     * Constructs a SHA256SignatureHandler for "restore mode" using the provided public key
     *
     * @param base64PublicKey the base64 encoded public key
     * @param restoreMode     true to initialize for restore mode, false otherwise
     * @throws Exception if the provided key is invalid
     */
    public SHA256SignatureHandler(String base64PublicKey, boolean restoreMode) throws Exception {
        if(restoreMode && base64PublicKey != null && is_RSAKey(base64PublicKey)){
            this.publicKey = convertToPublicKey(base64PublicKey);
        } else {
            throw new IllegalArgumentException("Invalid RSA keys");
        }
    }

    /**
     * Constructs a BlockchainBackupManager and generates a key pair for signing and verifying data
     * 
     * @throws NoSuchAlgorithmException if the specified algorithm is not available
     */
    @Override
    public void generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
    }


    /**
     * Exports the base64-encoded private key 
     * 
     * @return String of the "private key"
     */
    @Override
    public String exportPrivateKey() {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec spec = keyFactory.getKeySpec(this.privateKey, PKCS8EncodedKeySpec.class);
            byte[] privateKeyBytes = spec.getEncoded();
            return Base64.getEncoder().encodeToString(privateKeyBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to export private key", e);
        }
    }


    /**
     * Exports the base64-encoded public key
     * 
     * @return String of the "public key"
     */
    @Override
    public String exportPublicKey() {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec spec = keyFactory.getKeySpec(this.publicKey, X509EncodedKeySpec.class);
            byte[] publicKeyBytes = spec.getEncoded();
            return Base64.getEncoder().encodeToString(publicKeyBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to export public key", e);
        }
    }


    /** 
    * Signs the given data using the private key and the specified signature algorithm.
    * This ensures data integrity and authenticity by creating a digital signature.
    * 
    * @param data the byte array containing the data to be signed.
    * @return a byte array containing the digital signature of the data.
    * @throws Exception if an error occurs during the signing process.
    */
    @Override
    public byte[] signData(byte[] data) throws Exception{
        // Get a Signature instance configured with the specified algorithm (SHA256withRSA)
        Signature signature = Signature.getInstance(SIGNING_ALGORITHM);
        // Initialize the signature object with the "private key"
        signature.initSign(this.privateKey);
        // Update the signature object with the data to be signed
        signature.update(data);
        // Sign the data and return the resulting digital signature
        return signature.sign();
    }

    
    /**
     * Verifies the given data and its signature using the public key and the specified signature algorithm.
     * This ensures that the data has not been altered and that it was signed by the holder of the private key.
     *  
     * @param data the byte array containing the original data.
     * @param signatureBytes the byte array containing the digital signature to be verified.
     * @return true if the signature is valid and the data integrity is intact; false otherwise.
     * @throws Exception if an error occurs during the verification process.
     */
    @Override
    public boolean verifySignature(byte[] data, byte[] signatureBytes, PublicKey publicKey) throws Exception{
        //Get a Signature instance (SHA256withRSA)
        Signature signature = Signature.getInstance(SIGNING_ALGORITHM);
        //Initialize the signature with the "public key" 
        signature.initVerify(publicKey);
        //Update the signature object with the original data
        signature.update(data);
        //Verify the provided digital signature against the original data
        return signature.verify(signatureBytes);
    }


    /**
    * Write out the base64-encoded private and public keys to files in the backup directory
    */
    @Override
    public void getKeyPairFile(){
        fileWriter(this.backupDirectory+"privatekey", exportPrivateKey());
        fileWriter(this.backupDirectory+"publickey", exportPublicKey());
    }


    /**
    * Checks if the given base64 encoded key is a valid RSA key
    *
    * @param base64Key the base64 encoded key to check
    * @return true if the key is a valid RSA key, false otherwise
    */
    public boolean is_RSAKey(String base64Key) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64Key);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            kf.generatePrivate(new PKCS8EncodedKeySpec(keyBytes)); // Try to generate, throws exception if invalid
            return true; // If no exception, it's a valid RSA private key
        } catch (Exception e) {
            // If any exception occurs during decoding or key generation, it's not a valid RSA key
            return false;
        }
    }


    /**
     * Converts the base64-encoded private key to a PrivateKey object
     *
     * @param base64PrivateKey the base64 encoded private key
     * @return the PrivateKey object
     * @throws Exception fail to create key
     */
    public static PrivateKey convertToPrivateKey(String base64PrivateKey) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64PrivateKey);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }



    /**
     * Converts the base64-encoded public key to a PublicKey object
     *
     * @param base64PublicKey the base64 encoded public key
     * @return the PublicKey object
     * @throws Exception fail to create key
     */
    public static PublicKey convertToPublicKey(String base64PublicKey) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64PublicKey);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    private void fileWriter(String keyPath, String key){
        try(BufferedWriter bf = new BufferedWriter(new FileWriter(keyPath))){
            bf.write(key);
        }
        catch(IOException e){
            System.out.println("fail to output the key file");
            e.printStackTrace();
        }
    }

     @Override
    public String getBackupDirectory() {
        return this.backupDirectory;
    }

    /**
     * Sets the public key from its base64 representation.
     *
     * @param base64PublicKey the base64 encoded public key
     * @throws Exception fail to set key because of problem when converting the key
     */
    @Override
    public void setPublicKey(String base64PublicKey) throws Exception {
        this.publicKey = convertToPublicKey(base64PublicKey);
    }
}