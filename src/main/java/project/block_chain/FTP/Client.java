package project.block_chain.FTP;

public interface Client {
    // upload transaction data(String) to Server
    public void setUploadRequest(String transactionInfo);
    // send query request to Server
    public void setQueryRequest(String transactionID);
}
