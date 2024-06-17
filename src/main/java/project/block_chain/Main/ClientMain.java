
package project.block_chain.Main;

import project.block_chain.FTP.*;

public class ClientMain {

    public static void main(String[] args){
        ClientMain clientMain = new ClientMain();
        //clientMain.uploadRequest();
        clientMain.queryRequest();
    }

    public void uploadRequest(){
        FTPClient ftpClient1 = new FTPClient("USER1");
        FTPClient ftpClient2 = new FTPClient("USER2");
        FTPClient ftpClient3 = new FTPClient("USER3");
        FTPClient ftpClient4 = new FTPClient("USER4");

        for(int i=0; i<5; i++){
            ftpClient1.setUploadRequest("Clinet1 transaction content : make block " + String.valueOf(i));
            ftpClient2.setUploadRequest("Clinet2 transaction content : make block  " + String.valueOf(i));
            ftpClient3.setUploadRequest("Clinet3 transaction content : make block  " + String.valueOf(i));
            ftpClient4.setUploadRequest("Clinet4 transaction content : make block  " + String.valueOf(i));
            try{
                Thread.sleep(10000);
            } catch (Exception e){
                System.out.println("stop");
            }
        } 
    }

    public void queryRequest(){
        FTPClient ftpClient1 = new FTPClient("USER1");
        ftpClient1.setQueryRequest("6116f296ef2a4618ec03e38193390dbc5a623134ad7b6ab73d4236de3590bd7");
    }
}