package bcProject.BlockChain;

import java.util.ArrayList;
import java.util.List;

/**Priority queue for transaction 
 * 
 *     //wait for 4 transactions A,B,C,D
    //timeout may copy the last one and make all to 4::
    //special case 1 transaction: direcly stored into the block
    //special case 2and4 transactions: no duplicate
    //special case 3 transactions: duplicate the third
*/
public class TransactionHandler {

    public void Prioritize(){

    }

    public Transaction createTransaction(StringBuilder transactionData) {
        return new Transaction(transactionData);
    }

    public String getTransactionContent(Transaction transaction) {
        return transaction.getTransactionContent();
    }

    public String getTransactionID(Transaction transaction) {
        return transaction.getTransactionID();
    }
}

/** So all the method and the constructor in this class is 
 * package-private access level bcProject.BlockChain 
 * the only way to create it is via the tool-- TransactionHandler
*/
class Transaction{
    private String transactionID;
    private StringBuilder transactionData;

    Transaction(StringBuilder transactionData){
        this.transactionData = transactionData;
        this.transactionID = SHA256.calculateTXID(this.transactionData);
    }
   
    String getTransactionContent() {
        return this.transactionData.toString();
    }

    String getTransactionID() {
        return this.transactionID;
    }
}











