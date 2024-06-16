
package project.block_chain.Test;
import java.awt.GridBagLayout;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class TestMain {

    public static void main(String[] args){
        FTPClient ftpClient1 = new FTPClient("USER1");
        FTPClient ftpClient2 = new FTPClient("USER2");
        FTPClient ftpClient3 = new FTPClient("USER3");
        FTPClient ftpClient4 = new FTPClient("USER4");

        for(int i=0; i<5; i++){
            ftpClient1.setUploadRequest("transaction content " + String.valueOf(i));
            ftpClient2.setUploadRequest("transaction content " + String.valueOf(i));
            ftpClient3.setUploadRequest("transaction content " + String.valueOf(i));
            ftpClient4.setUploadRequest("transaction content " + String.valueOf(i));
            try{
                Thread.sleep(10000);
            } catch (Exception e){
                System.out.println("stop");
            }
        } 
        //ftpClient1.setQueryRequest("03ea543fe3e1bcc88ee2781bc25a9356fa0b2074851106de93aad4c5604b9e1c");
    }
}