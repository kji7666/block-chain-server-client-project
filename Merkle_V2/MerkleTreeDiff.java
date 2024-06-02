import java.io.BufferedReader;
import java.io.FileReader;

public class MerkleTreeDiff {
    public static void main(String[] args) {
        diff(args[0], args[1]);
    }

    private static void diff(String path1, String path2){
        try(BufferedReader br1 = new BufferedReader(new FileReader(path1));
        BufferedReader br2 = new BufferedReader(new FileReader(path2))){
            String line1 = null;
            String line2 = null;
            while(((line1 = br1.readLine()) != null) && ((line2 = br2.readLine()) != null)){
                if(line1.equals(line2)){
                    System.out.println("--------------------");
                    System.out.println(line1);
                    System.out.println(line2);
                    System.out.println("FOUND");
                    System.out.println("--------------------");
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}


