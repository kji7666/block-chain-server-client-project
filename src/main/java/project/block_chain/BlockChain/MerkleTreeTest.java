package project.block_chain.BlockChain;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MerkleTreeTest {

    public static void main(String[] args) {
        // Step 1: Generate a large dataset
        List<String> constructiveDataBlocks = generateLargeDataset(1000); // Adjust the number for larger dataset
        writeConstuctiveDataFile(constructiveDataBlocks);
        // Step 2: Construct the Merkle Tree

        MerkleTree merkleTree = new MerkleTree(constructiveDataBlocks);

        // Step 3: Test the search functionality
        List<String> queryDataBlocks = generateLargeDataset(1000);
        writeQueryResultFile(queryDataBlocks);
        testSearch(merkleTree, queryDataBlocks);
    }

    private static List<String> generateLargeDataset(int size) {
        List<String> dataBlocks = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            dataBlocks.add(generateRandomString(random, 100)); // Generate random strings of length 100
        }
        return dataBlocks;
    }

    private static String generateRandomString(Random random, int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    private static void testSearch(MerkleTree merkleTree, List<String> dataBlocks) {
        // Test searching for a few random blocks in the dataset
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < dataBlocks.size(); i++) { // Test with 10 random blocks
            String dataBlock = dataBlocks.get(i);
            boolean result = merkleTree.search(dataBlock);

            sb.append(dataBlock +" | Found: " + result+"\n");
        }       

        try(BufferedWriter bf = new BufferedWriter(new FileWriter("searchResult.txt"))){
            bf.write(sb.toString());

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static void writeConstuctiveDataFile(List<String> data){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter("constuctiveDataForTree.txt"))){
            
            for(String s : data){
                bw.write(s);
                bw.newLine();
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static void writeQueryResultFile(List<String> data){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter("queryData.txt"))){
            
            for(String s : data){
                bw.write(s);
                bw.newLine();
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
