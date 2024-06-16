package bcProject.BlockBackupAuthority;
import java.security.PublicKey;

import bcProject.BlockChain.BlockImpl;

public interface BackupService{
    public void setBackupDirectory(String backupDirectoryName);
    public void setRestoreDirectory(String restoreDirectoryPath);
    public String getBackupDirectory();
    public String getRestoreDirectory();
    public void backupBlock(BlockImpl block) throws Exception;
    public BlockImpl restoreBlock(PublicKey key, int blockHeight) throws Exception;
}
    
