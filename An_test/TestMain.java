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
            Thread.sleep(5000);
        } catch (Exception e){
            System.out.println("stop");
        }
        //ftpClient1.setUploadRequest("user1 second transaction");
        //ftpClient2.setUploadRequest("user2 second transaction");
        //ftpClient3.setUploadRequest("user3 second transaction");
        //ftpClient4.setUploadRequest("user4 second transaction");
        /* 

        ftpClient1.setTransactionInfo("[upload]" + "user1 second transaction");

        try{
        Thread.sleep(5000);
        } catch (Exception e){
            System.out.println("stop");
        }
        ftpClient1.setTransactionInfo("[upload]" + "user1 third transaction");

        try{
        Thread.sleep(5000);
        } catch (Exception e){
            System.out.println("stop");
        }
        ftpClient1.setTransactionInfo("[upload]" + "user1 fifth transaction");
        */
    }
}
