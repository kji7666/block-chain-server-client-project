package project.block_chain.Test;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BlockChainGUI extends JFrame implements GUI{
    public static void main(String[] args){
        new BlockChainGUI(new FTPClient("user1"));
    }

    FTPClient ftpClient;

    JFrame uploadJFrame;//upload frame
    JLabel userNameLabel;
    JTextArea userNameTextArea;
    JButton userNameButton;

    JLabel fileNameLabel;
    JTextArea fileNameTextArea;
    JButton fileOpenButton;
    
    JLabel handlingFeeLabel;
    JTextArea handlingFeeTextArea;

    JLabel filePreviewLabel;
    JTextArea filePreviewTextArea;
    JScrollPane scrollPane;

    JButton uploadButton;
    JButton cancelButton;
    String userName;
    String handlingFee;

    JFrame queryJFrame;
    String transactionID;
    JLabel transactionIDLabelForQuery;
    JTextArea transactionIDTextAreaForQuery;
    JButton transactionIDEnterButtonForQuery;

    JLabel dataLabelForQuery;
    JTextArea dataTextAreaForQuery;

    JLabel handlingFeeLabelForQuery;
    JTextArea handlingFeeTextAreaForQuery;

    JLabel userNameLabelForQuery;
    JTextArea userNameTextAreaForQuery;

    JLabel filePreviewLabelForQuery;
    JTextArea filePreviewTextAreaForQuery;
    JScrollPane scrollPaneForQuery;

    JFrame newUploadFrame;
    JTextArea newJTextArea;
    JScrollBar newsScrollBar;


    public BlockChainGUI(FTPClient ftpClient) {
        //initializeUserName();
        this.ftpClient = ftpClient;
        userName = ftpClient.getUsername();
        //ftpClient.setUsername(userName);
        initializeWindow();
        setUpInitialComponent();
        setVisible(true);
    }
    
    public String getUserName(){
        return userName;
    }

    private void initializeUserName() {
        while (true) {
            userName = JOptionPane.showInputDialog(uploadJFrame, "Enter User Name:", "Input",
                    JOptionPane.PLAIN_MESSAGE);
            if (userName != null && !userName.trim().isEmpty()) {
                return;
            } else {
                errorMessage("please try again");
            }
        }

    }

    private void initializeWindow() {
        setTitle("GridBagLayout Test"); // Set window title
        setSize(200, 200); // Set window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Set window close operation
        setLocationRelativeTo(null); // Center the window
        setLayout(new GridBagLayout()); //Use GridBagLayout
    }

    private void setUpInitialComponent() {
        addComponent(this, createButton("Upload"), 0, 0, 1, 1, 0, 0, 0, 0);
        addComponent(this, createButton("Query"), 0, 1, 1, 1, 0, 0, 0, 0);
        addComponent(this, createButton("Exit"), 0, 2, 1, 1, 0, 0, 0, 0);
    }
    private ButtonClickListener buttonClickListener;
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        buttonClickListener = new ButtonClickListener();
        button.addActionListener(buttonClickListener);
        return button;
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

    public void errorMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if ("Upload".equals(command)) {
                setVisible(false);
                // Open new window
                openNewWindowForUpload();
            } else if ("Query".equals(command)) {
                setVisible(false);
                // Open new window
                openNewWindowForQuery();
            } else if ("Exit".equals(command)) {
                // Close current window
                dispose();
                ftpClient.stop();// @add
            }
        }

        private void openNewWindowForQuery() {
            initailizeQueryWindow();
            initailizeQueryComponent();
            addQueryComponent(); //Add and layout
            queryaddActionListener();
            enterTransactionID();
            queryJFrame.setVisible(true);
        }

        private void initailizeQueryWindow() {
            queryJFrame = new JFrame("Query");
            queryJFrame.setSize(900, 600);
            queryJFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            queryJFrame.setLocationRelativeTo(null);
            queryJFrame.setLayout(new GridBagLayout());
        }

        private void initailizeQueryComponent() {
            transactionIDLabelForQuery = new JLabel("     Transaction ID: ");
            transactionIDTextAreaForQuery = new JTextArea("");
            transactionIDEnterButtonForQuery = new JButton("Enter");
            dataLabelForQuery = new JLabel("     Data: ");
            dataTextAreaForQuery = new JTextArea("");
            userNameLabelForQuery = new JLabel("         Username: ");
            userNameTextAreaForQuery = new JTextArea("");
            filePreviewLabelForQuery = new JLabel("   File Context Preview: ");
            filePreviewTextAreaForQuery = new JTextArea();
            scrollPaneForQuery = new JScrollPane(filePreviewTextAreaForQuery);
            cancelButton = new JButton("Cancel");

            transactionIDTextAreaForQuery.setEditable(false);
            dataTextAreaForQuery.setEditable(false);
            userNameTextAreaForQuery.setEditable(false);
            filePreviewTextAreaForQuery.setEditable(false);
        }

        private void enterTransactionID() {
            while (true) {
                transactionID = JOptionPane.showInputDialog(queryJFrame, "Enter TransactionID:", "Input",
                        JOptionPane.PLAIN_MESSAGE);
                if (transactionID != null && !transactionID.trim().isEmpty()) {
                    // 更新 userNameTextArea 的内容
                    ftpClient.setQueryRequest(transactionID); // @change
                    return;
                } else {
                    errorMessage("please try again");
                }
            }

        }

        private void addQueryComponent() {
            addComponent(queryJFrame, transactionIDLabelForQuery, 1, 0, 1, 1, 0, 0, 0, 0);
            addComponent(queryJFrame, transactionIDTextAreaForQuery, 2, 0, 1, 1, 0, 0, 1, 0);
            addComponent(queryJFrame, transactionIDEnterButtonForQuery, 3, 0, 1, 1, 0, 0, 0, 0);
            addComponent(queryJFrame, dataLabelForQuery, 1, 1, 1, 1, 0, 0, 0, 0);
            addComponent(queryJFrame, dataTextAreaForQuery, 2, 1, 1, 1, 0, 10, 2, 0);
            addComponent(queryJFrame, userNameLabelForQuery, 1, 2, 1, 1, 0, 0, 0, 0);
            addComponent(queryJFrame, userNameTextAreaForQuery, 2, 2, 1, 1, 0, 10, 0, 0);
            addComponent(queryJFrame, filePreviewLabelForQuery, 1, 3, 0, 1, 0, 0, 0, 0);
            addComponent(queryJFrame, scrollPaneForQuery, 0, 4, 5, 1, 0, 0, 1, 1);
            addComponent(queryJFrame, cancelButton, 4, 5, 1, 1, 0, 0, 0, 0);
        }

        private void queryaddActionListener() {
            transactionIDEnterButtonForQuery.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    while (true) { //until the input is correct
                        transactionID = JOptionPane.showInputDialog(queryJFrame, "Enter TransactionID:", "Input",
                                JOptionPane.PLAIN_MESSAGE);
                        if (transactionID != null && !transactionID.trim().isEmpty()) {
                            // Update the contents of TextArea
                            ftpClient.setQueryRequest(transactionID); // @change
                            return;
                        } else {
                            errorMessage("process error");
                        }
                    }
                }
            });

            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    queryJFrame.dispose(); // Close current window
                    setVisible(true); // Show original window
                }
            });

            queryJFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    setVisible(true);
                }
            });
        }

        private void openNewWindowForUpload() {
            initailizeUploadWindow();
            initailizeUploadComponent();
            addUploadComponent();
            uploadaddActionListener();
            //enterUserName();
            userNameTextArea.setText(userName);

            initializeNewWindow(); //new
            initializeNewUploadComponent(); // new
            addNewComponent();// new

            uploadJFrame.setVisible(true);
        }
        private void initializeNewWindow() {
            newUploadFrame = new JFrame("transaction info");
            newUploadFrame.setSize(400, 300);
            newUploadFrame.setDefaultCloseOperation
            (JFrame.DISPOSE_ON_CLOSE);
            newUploadFrame.setLocationRelativeTo
            (fileOpenButton);
            newUploadFrame.setLayout
            (new GridBagLayout());
            uploadJFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    // 关闭 newUploadFrame
                    if (newUploadFrame != null) {
                        newUploadFrame.dispose();
                    }
                }
            });
                // 添加组件监听器，处理 uploadJFrame 移动事件
            uploadJFrame.addComponentListener(new java.awt.event.ComponentAdapter() {
                @Override
                public void componentMoved(java.awt.event.ComponentEvent componentEvent) {
                    // 更新 newUploadFrame 的位置
                    if (newUploadFrame != null && newUploadFrame.isVisible()) {
                        updateNewUploadFrameLocation();
                    }
                }
            });

            createNewUploadFrame();
            newUploadFrame.setVisible(true);
        }

        private void updateNewUploadFrameLocation() {
            // 获取 uploadJFrame 的位置
            int uploadX = uploadJFrame.getX();
            int uploadY = uploadJFrame.getY();
    
            // 设置 newUploadFrame 的位置相对于 uploadJFrame 的左边
            int newUploadX = uploadX - newUploadFrame.getWidth();
            int newUploadY = uploadY;
    
            newUploadFrame.setLocation(newUploadX, newUploadY);
        }

        private void createNewUploadFrame() {
            // 获取 uploadJFrame 的位置
            int uploadX = uploadJFrame.getX();
            int uploadY = uploadJFrame.getY();
    
            // 设置 newUploadFrame 的位置相对于 uploadJFrame 的左边
            int newUploadX = uploadX - newUploadFrame.getWidth();
            int newUploadY = uploadY;
    
            newUploadFrame.setLocation(newUploadX, newUploadY);
    
            // 设置 newUploadFrame 关闭时的操作
            newUploadFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    
            // 显示 newUploadFrame
            newUploadFrame.setVisible(true);
        }
    }

        private void initializeNewUploadComponent() {
            newJTextArea = new JTextArea();
            scrollPane = new JScrollPane(newJTextArea);
        }

        private void addNewComponent() {
            addComponent
            (newUploadFrame, scrollPane, 
            0, 4, 5, 1, 0, 0, 1, 1);
        }

        private void enterUserName() {
            while (true) {
                userName = JOptionPane.showInputDialog(uploadJFrame, "Enter User Name:", "Input",
                        JOptionPane.PLAIN_MESSAGE);
                if (userName != null && !userName.trim().isEmpty()) {
                    // Update the contents of userNameTextArea
                    userNameTextArea.setText(userName);
                    //ftpClient.setUsername(userName); // @add
                    return;
                } else {
                    errorMessage("please try again");
                }
            }

        }

        private void uploadaddActionListener() {
            userNameButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    while (true) {
                        userName = JOptionPane.showInputDialog(uploadJFrame, "Enter User Name:", "Input",
                                JOptionPane.PLAIN_MESSAGE);
                        if (userName != null && !userName.trim().isEmpty()) {
                            // Update the contents of userNameTextArea
                            userNameTextArea.setText(userName);
                            return;
                        } else {
                            errorMessage("please try again");
                        }
                    }
                }
            });
            fileOpenButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    openFileAction();
                }
            });
            uploadButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    uploadFileAction();
                    //uploadJFrame.dispose(); //Close current window
                    //setVisible(true); //Show original window
                }
            });
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    uploadJFrame.dispose(); //Close current window
                    setVisible(true); //Show original window
                }
            });
            uploadJFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    setVisible(true);
                }
            });
        }

        private void addUploadComponent() {
            addComponent(uploadJFrame, userNameLabel, 1, 0, 1, 1, 0, 0, 0, 0);
            addComponent(uploadJFrame, userNameTextArea, 2, 0, 1, 1, 0, 0, 1, 0);
            addComponent(uploadJFrame, userNameButton, 3, 0, 1, 1, 0, 0, 0, 0);
            addComponent(uploadJFrame, fileNameLabel, 1, 1, 1, 1, 0, 0, 0, 0);
            addComponent(uploadJFrame, fileNameTextArea, 2, 1, 1, 1, 0, 0, 2, 0);
            addComponent(uploadJFrame, fileOpenButton, 3, 1, 1, 1, 0, 0, 0, 0);
            addComponent(uploadJFrame, handlingFeeLabel, 1, 2, 1, 1, 0, 0, 0, 0);
            addComponent(uploadJFrame, handlingFeeTextArea, 2, 2, 1, 1, 0, 10, 0, 0);
            addComponent(uploadJFrame, filePreviewLabel, 1, 3, 0, 1, 0, 0, 0, 0);
            addComponent(uploadJFrame, scrollPane, 0, 4, 5, 1, 0, 0, 1, 1);
            addComponent(uploadJFrame, uploadButton, 3, 5, 1, 1, 0, 0, 0, 0);
            addComponent(uploadJFrame, cancelButton, 4, 5, 1, 1, 0, 0, 0, 0);
        }

        private void initailizeUploadComponent() {
            userNameLabel = new JLabel("     User Name: ");
            userNameTextArea = new JTextArea("");
            userNameButton = new JButton("Enter");
            fileNameLabel = new JLabel("     File Name: ");
            fileNameTextArea = new JTextArea("choose file");
            fileOpenButton = new JButton("Open");
            handlingFeeLabel = new JLabel("     Handling Fee: ");
            handlingFeeTextArea = new JTextArea("0.1 USD");
            filePreviewLabel = new JLabel("   File Context Preview: ");
            filePreviewTextArea = new JTextArea();
            scrollPane = new JScrollPane(filePreviewTextArea);
            uploadButton = new JButton("Upload");
            cancelButton = new JButton("Cancel");

            userNameTextArea.setEditable(false);
            fileNameTextArea.setEditable(false);
            handlingFeeTextArea.setEditable(false);
            filePreviewTextArea.setEditable(false);
        }

        private void initailizeUploadWindow() {
            uploadJFrame = new JFrame("Upload");
            uploadJFrame.setSize(900, 300);
            uploadJFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            uploadJFrame.setLocationRelativeTo(null);
            uploadJFrame.setLayout(new GridBagLayout());
        }

        private void uploadFileAction() {
            // Retrieve the content of the text area
            String content = filePreviewTextArea.getText();
            if (content == null || content.trim().isEmpty()) {
                // If command is empty, display a warning message
                errorMessage("please input transaction");
            } else { 
                ftpClient.setUploadRequest(content);
                //JOptionPane.showMessageDialog(null, "Upload successful", "Nice!!", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        private void openFileAction() {
            JFileChooser fileChooser = new JFileChooser() {
                @Override
                public void approveSelection() {
                    // Prevent users from manually entering file names
                    if (getSelectedFile() != null && !getSelectedFile().isDirectory()) {
                        super.approveSelection();
                    } else {
                        JOptionPane.showMessageDialog(this, "Please select a valid file.", "Invalid File",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            fileChooser.setCurrentDirectory(new File(".")); // Set current directory
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt"); // Create file filter
            fileChooser.setFileFilter(filter); // Set file filter

            int response = fileChooser.showOpenDialog(null); // Show open file dialog

            if (response == JFileChooser.APPROVE_OPTION) { // If a file is selected
                File file = new File(fileChooser.getSelectedFile().getAbsolutePath()); // Get the selected file
                fileNameTextArea.setText(file.getName()); // Update the file path area with the selected file name
                Scanner fileIn = null; // Declare file scanner

                try {
                    fileIn = new Scanner(file); // Create file scanner
                    if (file.isFile()) { // If it's a file

                        while (fileIn.hasNextLine()) { // Read file line by line
                            String line = fileIn.nextLine() + "\n"; // Read each line and add newline character
                            filePreviewTextArea.append(line); // Add content to text area
                        }
                    }
                } catch (FileNotFoundException e1) {
                    errorMessage("Failed to open file");
                    uploadJFrame.dispose();
                    setVisible(true);
                    e1.printStackTrace(); // Print exception
                } finally {
                    if (fileIn != null) {
                        fileIn.close(); // Close file scanner
                    }
                }
            }
        }
    
    public void showUploadResult(String content){
        newJTextArea.append(content + "\n");
    }

    public void showQueryResult(String content){
        // transactionIDLabelForQuery = new JLabel("     Transaction ID: ");
        // transactionIDTextAreaForQuery = new JTextArea("");
        // transactionIDEnterButtonForQuery = new JButton("Enter");
        // dataLabelForQuery = new JLabel("     Data: ");
        // dataTextAreaForQuery = new JTextArea("");
        // handlingFeeLabel = new JLabel("         Username: ");
        // handlingFeeTextArea = new JTextArea("");
        // filePreviewLabel = new JLabel("   File Context Preview: ");
        // filePreviewTextArea = new JTextArea();
        // scrollPane = new JScrollPane(filePreviewTextArea);
        // cancelButton = new JButton("Cancel");   addComponent(queryJFrame, cancelButton, 4, 5, 1, 1, 0, 0, 0, 0);
        // sb.append("======================================\n");
        // sb.append(username).append(" receive query response :\n");
        //         sb.append(" transaction ID : ").append(result[0]).append("\n")
        //         .append(" user : ").append(result[1]).append("\n")
        //         .append(" time : ").append(result[2]).append("\n")
        //         .append(" handling fee : ").append(result[3]).append("\n")
        //         .append(" height : ").append(result[4]).append("\n")
        //         .append(" transaction : ").append(result[5]).append("\n");
        if(content == null){
            errorMessage("Transaction not found");
        }
        String[] lines = content.split("\n");
        transactionIDTextAreaForQuery.setText(lines[2]);
        userNameTextAreaForQuery.setText(lines[3]);
        dataTextAreaForQuery.setText(lines[4]);
        filePreviewTextAreaForQuery.setText(lines[7]);
    
        for(String line : lines){
            System.out.println("THIS IS A LINE" + line);
        }
    }
}


