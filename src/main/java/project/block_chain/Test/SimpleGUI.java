package project.block_chain.Test;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class SimpleGUI implements GUI{
    JFrame newUploadFrame;
    JTextArea newJTextArea;
    JScrollPane newsScrollPane;

    public static void main(String[] args){
        new SimpleGUI("user");
    }
    public SimpleGUI(String name) {
        initializeNewWindow(name); // new
        initializeNewUploadComponent(); // new
        addNewComponent();// new
        newUploadFrame.revalidate(); // 强制重新布局
        newUploadFrame.repaint(); // 强制重绘窗口
    }
    
    private void initializeNewWindow(String name) {
        newUploadFrame = new JFrame(name);
        newUploadFrame.setSize(1000, 800);
        newUploadFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        newUploadFrame.setLocationRelativeTo(null);
        newUploadFrame.setLayout(new GridBagLayout());
        newUploadFrame.setVisible(true);
    }
    
    private void initializeNewUploadComponent() {
        newJTextArea = new JTextArea();
        newsScrollPane = new JScrollPane(newJTextArea);
    }
    
    private void addNewComponent() {
        addComponent(newUploadFrame, newsScrollPane, 0, 4, 5, 1, 0, 0, 1, 1);
    }

    private void addComponent(Container container, Component c, int x, int y, int width, int height, int ipadx,
            int ipady, int weightx, int weighty) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        gbc.ipadx = ipadx;
        gbc.ipady = ipady;
        container.add(c, gbc);
    }

    public void showUploadResult(String content){
        newJTextArea.append(content);
    }

    public void showQueryResult(String content){
        newJTextArea.append(content);
    }
}
