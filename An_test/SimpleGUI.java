
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;

public class SimpleGUI extends JFrame implements ActionListener {
    FTPClient ftpClient;
    JTextArea textArea; // Text area
    JScrollPane textScrollPane; // Scroll pane
    JLabel statusLabel; // Status label
    JMenuBar mainMenuBar; // Menu bar
    JMenu fileMenu; // File menu
    JMenuItem openFileMenuItem; // Open menu item
    JMenuItem exitMenuItem; // Exit menu item
    JMenuItem uploadMenuItem; // Upload menu item
    JMenuItem queryMenuItem; // Query menu item

    public static void main(String[] args) {
        new SimpleGUI(args);
    }

    SimpleGUI(String[] args) {
        ftpClient = new FTPClient(args[0]);
        initializeWindow();
        initializeComponents();
        setUpLayout();
        configureTextArea();
        configureScrollPane();
        addActionListeners();
        this.setVisible(true); // Show window
        // ftpClient.startConnecting();
        // 添加窗口关闭监听器
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // 关闭 FTPClientTester
                ftpClient.connectionOff();
            }
        });
    }

    private void initializeWindow() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Set window close operation
        this.setTitle("Simple Text Editor"); // Set window title
        this.setSize(900, 500); // Set window size
        this.setLocationRelativeTo(null); // Center the window
    }

    private void configureTextArea() {
        textArea.setLineWrap(false); // Disable word wrapping
        textArea.setWrapStyleWord(false); // Do not wrap at word boundaries
        textArea.setFont(new Font("Arial", Font.PLAIN, 20)); // Set font
        // textArea.setEditable(false); // Make textArea read-only
    }    

    private void configureScrollPane() {
        textScrollPane.setPreferredSize(new Dimension(850, 450)); // Set scroll pane size
        textScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS); // Always show vertical scrollbar
    }

    private void initializeComponents() {
        this.setLayout(new FlowLayout()); // Use flow layout
        textArea = new JTextArea(); // Create text area
        textScrollPane = new JScrollPane(textArea); // Create scroll pane
        mainMenuBar = new JMenuBar(); // Create menu bar
        fileMenu = new JMenu("File"); // Create file menu
        openFileMenuItem = new JMenuItem("Open"); // Create open menu item
        exitMenuItem = new JMenuItem("Exit"); // Create exit menu item
        uploadMenuItem = new JMenuItem("Upload"); // Create upload menu item
        queryMenuItem = new JMenuItem("Query"); // Create query menu item

        fileMenu.add(openFileMenuItem); // Add open menu item to file menu
        fileMenu.add(uploadMenuItem); // Add upload menu item to file menu
        fileMenu.add(exitMenuItem); // Add exit menu item to file menu
        fileMenu.add(queryMenuItem); // Add query menu item to file menu
        mainMenuBar.add(fileMenu); // Add file menu to menu bar
        this.setJMenuBar(mainMenuBar); // Set menu bar to the window
    }

    private void setUpLayout() {
        this.add(textScrollPane); // Add scroll pane to the window
    }

    private void addActionListeners() {
        openFileMenuItem.addActionListener(this); // Add action listener
        exitMenuItem.addActionListener(this); // Add action listener
        uploadMenuItem.addActionListener(this); // Add action listener
        queryMenuItem.addActionListener(this); // Add action listener
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == openFileMenuItem) { // If open menu item is clicked
            openFileAction();
        } else if (e.getSource() == uploadMenuItem) { // If upload menu item is clicked
            uploadFileAction();
        } else if (e.getSource() == queryMenuItem) { // If query menu item is clicked
            queryFileAction();
        } else if (e.getSource() == exitMenuItem) { // If exit menu item is clicked
            ftpClient.connectionOff();
            System.exit(0); // Exit the application
        }
    }

    private void openFileAction() {
        JFileChooser fileChooser = new JFileChooser(); // Create file chooser
        fileChooser.setCurrentDirectory(new File(".")); // Set current directory
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt"); // Create file filter
        fileChooser.setFileFilter(filter); // Set file filter

        int response = fileChooser.showOpenDialog(null); // Show open file dialog

        if (response == JFileChooser.APPROVE_OPTION) { // If a file is selected
            File file = new File(fileChooser.getSelectedFile().getAbsolutePath()); // Get the selected file
            Scanner fileIn = null; // Declare file scanner

            try {
                fileIn = new Scanner(file); // Create file scanner
                if (file.isFile()) { // If it's a file
                    while (fileIn.hasNextLine()) { // Read file line by line
                        String line = fileIn.nextLine() + "\n"; // Read each line and add newline character
                        textArea.append(line); // Add content to text area
                    }
                }
            } catch (FileNotFoundException e1) {
                e1.printStackTrace(); // Print exception
            } finally {
                if (fileIn != null) {
                    fileIn.close(); // Close file scanner
                }
            }
        }
    }

    private void uploadFileAction() {
        // Retrieve the content of the text area
        String content = textArea.getText();
        if (content == null || content.trim().isEmpty()) {
            // If command is empty, display a warning message
            JOptionPane.showMessageDialog(null, "Please enter content", "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            System.out.println("GUI upload->");
            // Call the FTPClientTester to upload the content
            ftpClient.setTransactionInfo("[upload]" + content);
        }
    }

    private void queryFileAction() {
        // Create a new dialog for inputting query file name
        JDialog queryDialog = new JDialog(this, "Query", true);
        queryDialog.setSize(300, 100);
        queryDialog.setLayout(new FlowLayout());

        JLabel queryLabel = new JLabel("Enter transaction code:");
        JTextField queryTextField = new JTextField(20);
        JButton queryButton = new JButton("Query");

        queryDialog.add(queryLabel);
        queryDialog.add(queryTextField);
        queryDialog.add(queryButton);

        queryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String transactionID = queryTextField.getText();
                String[] result = query(transactionID);
                // [0] is transactionID, [1] is text, [2] is Date
                // Show message dialog with the three pieces of information
                JOptionPane.showMessageDialog(queryDialog, 
                                              "Transaction ID: " + result[0] + "\n" +
                                              "Text: " + result[1] + "\n" +
                                              "Date: " + result[2],
                                              "Query Result", JOptionPane.INFORMATION_MESSAGE);
                queryDialog.dispose();
            }
        });
    
        queryDialog.setLocationRelativeTo(this);
        queryDialog.setVisible(true);
    }

    // Mock query method to simulate searching content by file name
    private String[] query(String transactionID) {
        // Implement your query logic here, for example, querying content from a database or file system
        // return "Query result for file: " + transaction;
        ftpClient.setTransactionInfo("[query]" + transactionID);
        return new String[]{"432234", "fsfsffsfsffsfsffsfsffsfsffsfsffsfsffsfsffsfsffsfsffsfsffsfsffsfsffsfsffsfsffsfsffsfsffsfsf", "fdsfds"};
    }

    
}
