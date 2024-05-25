import bcProject.FTP.FTPClient;

public class ClientMain {
    public static void main(String[] args){
        FTPClient client = new FTPClient();
        client.startConnecting();
    }
}
