package project.block_chain.Main;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // 使用 invokeLater 確保 UI 在排程執行緒內
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //new BlockChainGUI();
            }
        });
    }
}
