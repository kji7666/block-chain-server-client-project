package bcProject.BlockChain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;


/**
 * The BlockchainBackupManager class provides functionality to backup and restore blockchain blocks.
 * It includes methods for generating cryptographic keys, signing data, and verifying signatures
 * to ensure data integrity during backup and restoration.
 * 
 * @Author Harris
 */
public class BlockchainBackupHandler  {
    private static final String SIGNING_ALGORITHM = "SHA256withRSA";
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public BlockchainBackupHandler() {
        try {
            generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            System.out.println(" The algorithm for digital signature is invalid");
        }
      
    }


    /**
     * Constructs a BlockchainBackupManager and generates a key pair for signing and verifying data.
     * 
     * @throws NoSuchAlgorithmException if the specified algorithm is not available.
     */
    private void generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
    }


    /**
     * Generates an RSA key pair for signing and verifying data.
     * 
     * @throws NoSuchAlgorithmException if the specified algorithm is not available.
     */
    protected void backupBlock(BlockImpl block, String filePath) throws Exception {
        byte[] data;
        byte[] signature;
    
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(block);
            data = baos.toByteArray();
            signature = signData(data);
        }
    
        try (FileOutputStream fos = new FileOutputStream(filePath);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(data);
            oos.writeObject(signature);
        }
    }
    

     /**
     * Backs up a BlockImpl object by serializing it, signing the serialized data, and writing
     * both the data and the signature to the specified file.
     * 
     * @param block the BlockImpl object to be backed up.
     * @param filePath the file path where the backup will be stored.
     * @throws Exception if an error occurs during the backup process.
     */
    protected BlockImpl restoreBlock(String filePath) throws Exception{
        try(FileInputStream fis = new FileInputStream(filePath);
            ObjectInputStream ois = new ObjectInputStream(fis)){
           
            byte[] data = (byte[]) ois.readObject();
            byte[] signature = (byte[]) ois.readObject();

            if (!verifySignature(data, signature)) {
                throw new SecurityException("Data integrity check failed.");
            }
        
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            try(ObjectInputStream oisData = new ObjectInputStream(bais)){
                return (BlockImpl) oisData.readObject();
            }
        }
    }

    /**
     * Restores a Block object from the specified file by reading the serialized data and signature,
     * verifying the signature, and deserializing the data back into a Block object.
     * 
     * @param filePath the file path where the backup is stored.
     * @return the restored Block object.
     * @throws Exception if an error occurs during the restoration process or if data integrity check fails.
     */
    private byte[] signData(byte[] data) throws Exception{
        Signature signature = Signature.getInstance(SIGNING_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }


    /**
     * Verifies the given data and its signature using the public key and the specified signature algorithm.
     * 
     * @param data the data whose signature needs to be verified.
     * @param signatureBytes the digital signature to be verified.
     * @return true if the signature is valid; false otherwise.
     * @throws Exception if an error occurs during the verification process.
     */
    private boolean verifySignature(byte[] data, byte[] signatureBytes) throws Exception{
        Signature signature = Signature.getInstance(SIGNING_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(signatureBytes);
    }
}
