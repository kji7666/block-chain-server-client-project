
package project.block_chain.Test;
public class TestMain {

    public static void main(String[] args){
        FTPClient ftpClient1 = new FTPClient("USER1");
        FTPClient ftpClient2 = new FTPClient("USER2");
        FTPClient ftpClient3 = new FTPClient("USER3");
        FTPClient ftpClient4 = new FTPClient("USER4");
        System.out.println("GUI upload->");
        
        ftpClient1.setUploadRequest("user1 first transaction");
        ftpClient2.setUploadRequest("user2 first transaction");
        ftpClient3.setUploadRequest("user3 first transaction");
        ftpClient4.setUploadRequest("user4 first transaction");
        
        try{
            Thread.sleep(15000);
        } catch (Exception e){
            System.out.println("stop");
        }
        ftpClient1.setQueryRequest("89a37c8edd100a61c6703a0edc663e15015cfbafd5e2973ca7b979fe55c89a08");
        
        /*try{
            Thread.sleep(15000);
        } catch (Exception e){
            System.out.println("stop");
        }
        System.out.println("Second Upload");
        ftpClient1.setUploadRequest("user1 second transaction");
        ftpClient2.setUploadRequest("user2 second transaction");
        ftpClient3.setUploadRequest("user3 second transaction");
        ftpClient4.setUploadRequest("user4 second transaction");
        
        try{
            Thread.sleep(10000);
        } catch (Exception e){
            System.out.println("stop");
        }
        ftpClient1.setQueryRequest("8a37c8edd100a61c6703a0edc663e15015cfbafd5e2973ca7b979fe55c89a08");
     */   
    }
}
