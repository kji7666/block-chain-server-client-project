package project.block_chain.BlockChain;
import java.security.PublicKey;


public interface BackupService{
    public void setBackupDirectory(String backupDirectoryName);
    public void setRestoreDirectory(String restoreDirectoryPath);
    public String getBackupDirectory();
    public String getRestoreDirectory();
    public void backupBlock(BlockImpl block) throws Exception;
    public BlockImpl restoreBlock(PublicKey key, int blockHeight) throws Exception;
}
    
