package bcProject.BlockChain;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
public class BlockchainBackupHandler implements DigitalSignatureService {
    private static final String SIGNING_ALGORITHM = "SHA256withRSA";
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private File restoreDirectory;
    private String backupDirectory;
    public BlockchainBackupHandler() {
        try {
            generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            System.out.println(" The algorithm for digital signature is invalid");
        }
    }

    /**
     * Backs up a BlockImpl object 
     * 1 by serializing it, 
     * 2 signing the serialized data, 
     * 3 writing both the data and the signature to the specified file
     * 
     * @param block the BlockImpl object to be backed up.
     * @throws Exception if an error occurs during the backup process.
     */
    protected void backupBlock(BlockImpl block) throws Exception {
        byte[] data;
        byte[] signature;
        String eachFilePath = this.backupDirectory + File.separator + block.getHeight();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(block);
            data = baos.toByteArray();
            signature = signData(data);
        }
    
        try (FileOutputStream fos = new FileOutputStream(eachFilePath);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(data);
            oos.writeObject(signature);
        }
    }

    /**
     * Restores a Block object from the specified file and the corresponding public key
     * 1 reading the serialized data and signature,
     * 2 verifying the signature, 
     * 3 deserializing the data back into a Block object
     * 
     * @return the restored Block object.
     * @throws Exception if an error occurs during the restoration process or if data integrity check fails
     */
    protected BlockImpl restoreBlock(PublicKey key) throws Exception{
        try(FileInputStream fis = new FileInputStream(this.restoreDirectory.toString());
            ObjectInputStream ois = new ObjectInputStream(fis)){
           
            byte[] data = (byte[]) ois.readObject();
            byte[] signature = (byte[]) ois.readObject();

            if (!verifySignature(data, signature, key)) {
                throw new SecurityException("Data integrity check failed.");
            }
        
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            try(ObjectInputStream oisData = new ObjectInputStream(bais)){
                return (BlockImpl) oisData.readObject();
            }
        }
    }

     /**
     * Set the backup directory path
     * @param backupDirectoryName
     * @throws IllegalStateException Permission denied  or IO error
     * @throws IllegalArgumentException Wrong attribute of given path name, can only be a directory
     * @throws IOException fails to create directory
     */
    public void setBackupDirectory(String backupDirectoryName) {
        File backupDirectory = new File(backupDirectoryName);
        this.backupDirectory = backupDirectory.getAbsolutePath();
    
        // If the directory does not exist, attempt to create it
        if (!backupDirectory.exists()) {
            try {
                if (!backupDirectory.mkdirs()) {
                    throw new IOException("Failed to create directory: " + backupDirectoryName);
                }
            } catch (SecurityException e) {
                throw new IllegalStateException("Permission denied: Unable to create backup directory: " + backupDirectoryName, e);
            } catch (IOException e) {
                throw new IllegalStateException("IO error: Unable to create backup directory: " + backupDirectoryName, e);
            }
        } else if (!backupDirectory.isDirectory()) {
            // If the path exists but is not a directory
            throw new IllegalArgumentException("The specified backup path is not a directory.");
        }
    }

    /**
     * Set the restore directory path
     * @param restoreDirectoryPath where you want to get the block
     * @throws IllegalArgumentException the directory is not exist or not a directory
     */
    public void setRestoreDirectory(String restoreDirectoryPath) {
        this.restoreDirectory = new File(restoreDirectoryPath);
        if (!this.restoreDirectory.exists()) {
            throw new IllegalArgumentException("Restore directory does not exist.");
        }
        else if (!this.restoreDirectory.isDirectory()) {
            throw new IllegalArgumentException("The specified restore path is not a directory.");
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

    @Override
    public void getKeyPairFile(String KeyDirectoryName){
        setRestoreDirectory(KeyDirectoryName);
        fileWriter(KeyDirectoryName+"privatekey", exportPrivateKey());
        fileWriter(KeyDirectoryName+"publickey", exportPublicKey());
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
}