package project.block_chain.FTP;

public class ClientMain {
    public static void main(String[] args){
        FTPClient client = new FTPClient();
        client.startConnecting();
    }
}
