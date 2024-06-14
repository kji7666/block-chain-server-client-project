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
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BlockChainGUI extends JFrame {
    /* 
    FTPClient ftpClient;
    JFrame uploadJFrame;
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

    JLabel transactionIDLabel;
    JTextArea transactionIDTextArea;
    JButton transactionIDEnterButton;
    JLabel dataLabel;
    JTextArea dataTextArea;

    JFrame newUploadFrame;
    JTextArea newJTextArea;
    JScrollBar newsScrollBar;

    private BlockChainGUI() {
        //initializeUserName();
        userName = "user1";
        ftpClient = new FTPClient(this);
        System.out.println("1");
        initializeWindow();
        System.out.println("2");
        setUpInitialComponent();
        System.out.println("3");
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
                errorMessage();
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

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.addActionListener(new ButtonClickListener());
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

    private void errorMessage() {
        JOptionPane.showMessageDialog(null, "Error, Please try again", "Warning", JOptionPane.WARNING_MESSAGE);
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
            queryJFrame.setSize(900, 500);
            queryJFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            queryJFrame.setLocationRelativeTo(null);
            queryJFrame.setLayout(new GridBagLayout());
        }

        private void initailizeQueryComponent() {
            transactionIDLabel = new JLabel("     Transaction ID: ");
            transactionIDTextArea = new JTextArea("");
            transactionIDEnterButton = new JButton("Enter");
            dataLabel = new JLabel("     Data: ");
            dataTextArea = new JTextArea("");
            handlingFeeLabel = new JLabel("         Username: ");
            handlingFeeTextArea = new JTextArea("");
            filePreviewLabel = new JLabel("   File Context Preview: ");
            filePreviewTextArea = new JTextArea();
            scrollPane = new JScrollPane(filePreviewTextArea);
            cancelButton = new JButton("Cancel");

            transactionIDTextArea.setEditable(false);
            dataTextArea.setEditable(false);
            handlingFeeTextArea.setEditable(false);
            filePreviewTextArea.setEditable(false);
        }

        private void enterTransactionID() {
            while (true) {
                transactionID = JOptionPane.showInputDialog(queryJFrame, "Enter TransactionID:", "Input",
                        JOptionPane.PLAIN_MESSAGE);
                if (transactionID != null && !transactionID.trim().isEmpty()) {
                    // 更新 userNameTextArea 的内容
                    ftpClient.setQueryRequest(transactionID); // @change
                    String[] result = waitForResponse();
                    transactionIDTextArea.setText(transactionID);
                    dataTextArea.setText(result[2]);// result
                    handlingFeeTextArea.setText(result[1]);
                    filePreviewTextArea.setText(result[5]);
                    return;
                } else {
                    errorMessage();
                }
            }

        }

        private void addQueryComponent() {
            addComponent(queryJFrame, transactionIDLabel, 1, 0, 1, 1, 0, 0, 0, 0);
            addComponent(queryJFrame, transactionIDTextArea, 2, 0, 1, 1, 0, 0, 1, 0);
            addComponent(queryJFrame, transactionIDEnterButton, 3, 0, 1, 1, 0, 0, 0, 0);
            addComponent(queryJFrame, dataLabel, 1, 1, 1, 1, 0, 0, 0, 0);
            addComponent(queryJFrame, dataTextArea, 2, 1, 1, 1, 0, 10, 2, 0);
            addComponent(queryJFrame, handlingFeeLabel, 1, 2, 1, 1, 0, 0, 0, 0);
            addComponent(queryJFrame, handlingFeeTextArea, 2, 2, 1, 1, 0, 10, 0, 0);
            addComponent(queryJFrame, filePreviewLabel, 1, 3, 0, 1, 0, 0, 0, 0);
            addComponent(queryJFrame, scrollPane, 0, 4, 5, 1, 0, 0, 1, 1);
            addComponent(queryJFrame, cancelButton, 4, 5, 1, 1, 0, 0, 0, 0);
        }

        private void queryaddActionListener() {
            transactionIDEnterButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    while (true) { //until the input is correct
                        transactionID = JOptionPane.showInputDialog(queryJFrame, "Enter TransactionID:", "Input",
                                JOptionPane.PLAIN_MESSAGE);
                        if (transactionID != null && !transactionID.trim().isEmpty()) {
                            // Update the contents of TextArea
                            ftpClient.setQueryRequest(transactionID); // @change
                            String[] result = waitForResponse();
                            transactionIDTextArea.setText(transactionID);
                            dataTextArea.setText(result[2]);// result
                            handlingFeeTextArea.setText(result[1]);
                            filePreviewTextArea.setText(result[5]);
                            return;
                        } else {
                            errorMessage();
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
            initailizeUploadWindow();;
            initailizeUploadComponent();
            addUploadComponent();
            uploadaddActionListener();
            enterUserName();

            initializeNewWindow(); //new
            initializeNewUploadComponent(); // new
            addNewComponent();// new

            uploadJFrame.setVisible(true);
        }
        private void initializeNewWindow() {
            newUploadFrame = new JFrame("New Window");
            newUploadFrame.setSize(1000, 600);
            newUploadFrame.setDefaultCloseOperation
            (JFrame.DISPOSE_ON_CLOSE);
            newUploadFrame.setLocationRelativeTo
            (fileOpenButton);
            newUploadFrame.setLayout
            (new GridBagLayout());
            newUploadFrame.setVisible(true);
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
                    ftpClient.setUsername(userName); // @add
                    return;
                } else {
                    errorMessage();
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
                            errorMessage();
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
                    uploadJFrame.dispose(); //Close current window
                    setVisible(true); //Show original window
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
            uploadJFrame.setSize(900, 500);
            uploadJFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            uploadJFrame.setLocationRelativeTo(null);
            uploadJFrame.setLayout(new GridBagLayout());
        }

        private void uploadFileAction() {
            // Retrieve the content of the text area
            String content = filePreviewTextArea.getText();
            if (content == null || content.trim().isEmpty()) {
                // If command is empty, display a warning message
                errorMessage();
            } else { //@add wait for upload
                // Call the FTPClientTester to upload the content
                ftpClient.setUploadRequest(content);
                String[] response = waitForResponse();
                for(String info : response){
                    String[] result = commandFormat.uploadCommandParsing(info);
                    // [0]=transactionId, [1]=user, [2]=time, [3]=handlingfee, [4]=height
                    StringBuilder sb = new StringBuilder();
                    sb.append("New transaction -------------------- " + "\n")
                        .append("Transaction ID : " + result[0] + "\n")
                        .append("User : " + result[1] + "\n")
                        .append("Time : " + result[2] + "\n")
                        .append("------------------------------------ " + "\n");
                    addInfoToTextArea(sb.toString());
                }
                // add result in textArea
                returnInfo = null;
                JOptionPane.showMessageDialog(null, "Upload successful", "Nice!!", JOptionPane.INFORMATION_MESSAGE);
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
                    errorMessage();
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

    }


    public static void main(String[] args) {
        // Use invokeLater to ensure that the UI is within the scheduled thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new BlockChainGUI();
            }
        });
    }

    // loading
    // return 
    //private static ExecutorService pool = Executors.newFixedThreadPool(3);
    private String[] returnInfo;
    private final CommandFormat commandFormat = new CommandFormat();
    public void setReturnInfo(String[] returnInfo){
        this.returnInfo = returnInfo;
    }
    
    private String[] waitForResponse(){
        // 創建 JOptionPane
        JOptionPane optionPane = new JOptionPane("loading...", JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
        
        // 創建對話框
        JDialog dialog = optionPane.createDialog("wait for server response");

        // 在新線程中顯示對話框
        new Thread(new Runnable() {
            @Override
            public void run() {
                dialog.setVisible(true);
            }
        }).start();

        // 創建一個新的線程來等待一段時間後關閉對話框
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    if(returnInfo != null){
                        break;
                    }
                }
                dialog.dispose();  // 關閉對話框
            }
        }).start();
        return returnInfo;
    }

    private void addInfoToTextArea(String info){
        newJTextArea.append(info + "\n");
    }
}
*/
}