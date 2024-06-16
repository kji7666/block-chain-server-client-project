package bcProject.BlockBackupAuthority;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PublicKey;
import bcProject.BlockChain.BlockImpl;


/** Usage:
 * SHA256SignatureHandler restoreSignatureHandler = new SHA256SignatureHandler("base64_private_key", "base64_public_key");
 * BlockchainBackupHandler restoreHandler = new BlockchainBackupHandler(restoreSignatureHandler, true)
 * For a third party use
 * Ensure data integrity during backup and restoration.
 * The BlockchainBackupManager class provides functionality to backup and restore blockchain blocks
 * It includes methods for generating keys, signing data, and verifying signatures, 
 * and output/input signed serialized object 
 * 
 * @Author Harris
 * @Since 15, Jun 2024
 * 
 */
public class BlockchainBackupHandler implements BackupService {
    private File restoreDirectory;
    private String backupDirectory;
    private DigitalSignatureService signatureHandler;

     // Constructor for backup mode
     public BlockchainBackupHandler(DigitalSignatureService signatureHandler) {
        this.signatureHandler = signatureHandler;
        this.backupDirectory = signatureHandler.getBackupDirectory();
        signatureHandler.getKeyPairFile();
    }

     // Constructor for restore mode to restore the old backup block process with Base64 encoded RSAKey
     public BlockchainBackupHandler(DigitalSignatureService signatureHandler, boolean restoreMode) {
        if (restoreMode) {
            this.signatureHandler = signatureHandler;
        } else {
            throw new IllegalArgumentException("Invalid constructor usage. Use restore mode constructor with restoreMode = true.");
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
    @Override
    public void backupBlock(BlockImpl block) throws Exception {
        byte[] data;
        byte[] signature;
        String eachFilePath = this.backupDirectory + File.separator + block.getHeight();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(block);
            data = baos.toByteArray();
            signature = this.signatureHandler.signData(data);
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
    @Override
    public BlockImpl restoreBlock(PublicKey key, int blockHeight) throws Exception{
        try(FileInputStream fis = new FileInputStream(this.restoreDirectory.toString());
            ObjectInputStream ois = new ObjectInputStream(fis)){
           
            byte[] data = (byte[]) ois.readObject();
            byte[] signature = (byte[]) ois.readObject();

            if (!this.signatureHandler.verifySignature(data, signature, key)) {
                throw new SecurityException("Fail to verify the signature.");
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
    @Override
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
    @Override
    public void setRestoreDirectory(String restoreDirectoryPath) {
        this.restoreDirectory = new File(restoreDirectoryPath);
        if (!this.restoreDirectory.exists()) {
            throw new IllegalArgumentException("Restore directory does not exist.");
        }
        else if (!this.restoreDirectory.isDirectory()) {
            throw new IllegalArgumentException("The specified restore path is not a directory.");
        }
    }

    @Override
    public String getBackupDirectory() {
        return this.backupDirectory;
    }

    @Override
    public String getRestoreDirectory() {
       return this.restoreDirectory.toString();
    }
}