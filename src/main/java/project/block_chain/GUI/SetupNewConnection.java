package project.block_chain.GUI;

import javax.swing.*;
import java.awt.*;

public class SetupNewConnection extends JFrame {

    public SetupNewConnection() {
        // Set up the frame
        setTitle("Setup New Connection"); // 设置窗口标题
        setSize(900, 600); // 设置窗口大小
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 设置关闭操作
        setLayout(new BorderLayout()); // 设置布局管理器为边界布局

        // Create the main panel
        JPanel mainPanel = new JPanel(new BorderLayout()); // 创建主面板

        // Create the input form panel
        JPanel formPanel = new JPanel(new GridBagLayout()); // 创建输入表单面板
        GridBagConstraints gbc = new GridBagConstraints(); // 创建网格包约束
        gbc.fill = GridBagConstraints.HORIZONTAL; // 设置填充方式为水平填充
        gbc.insets = new Insets(5, 5, 5, 5); // 设置组件之间的间距

        // Add fields to the form panel
        gbc.gridx = 0; // GridBagLayout的xy軸
        gbc.gridy = 0;
        JLabel idLabel = new JLabel("transaction ID:"); // 连接名称标签
        idLabel.setPreferredSize(new Dimension(150, 25)); // 设置标签的首选大小
        formPanel.add(idLabel, gbc); // 添加连接名称标签到表单面板
        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField idField = new JTextField(); // 连接名称文本框
        idField.setPreferredSize(new Dimension(200, 25)); // 设置文本框的首选大小
        idField.setText("dfddffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
        idField.setEditable(false); // 设置文本框不可编辑
        formPanel.add(idField, gbc); // 添加连接名称文本框到表单面板

        // Add other fields with fixed sizes similarly...
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel dateLabel = new JLabel("Date:"); // 连接名称标签
        dateLabel.setPreferredSize(new Dimension(150, 25)); // 设置标签的首选大小
        formPanel.add(dateLabel, gbc); // 添加连接名称标签到表单面板
        gbc.gridx = 1;
        gbc.gridy = 2;
        JTextField dateField = new JTextField(); // 创建主机名文本框，默认为127.0.0.1
        dateField.setEditable(false);
        formPanel.add(dateField, gbc); // 添加主机名文本框到表单面板
        // info 模板

        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel Label = new JLabel(" ??? ");
        Label.setPreferredSize(new Dimension(150, 25)); 
        formPanel.add(Label, gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        JTextField Field = new JTextField(); 
        Field.setEditable(false);
        formPanel.add(Field, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        JTextArea textArea = new JTextArea();
        JScrollPane textScrollPane = new JScrollPane(textArea); // Create scroll pane
        textScrollPane.setPreferredSize(new Dimension(850, 450)); // Set scroll pane size
        textScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS); // Always show vertical scrollbar
        formPanel.add(textArea, gbc);
        formPanel.add(textArea, gbc);
        textArea.setLineWrap(false); // Disable word wrapping
        textArea.setWrapStyleWord(false); // Do not wrap at word boundaries
        textArea.setFont(new Font("Arial", Font.PLAIN, 20)); // Set font
        textArea.setEditable(false); // Make textArea read-only


        mainPanel.add(formPanel, BorderLayout.CENTER); // 将表单面板添加到主面板的中间位置

        // Create the button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // 创建按钮面板，使用右对齐布局
        JButton okButton = new JButton("OK"); // 确定按钮

        buttonPanel.add(okButton); // 添加确定按钮到按钮面板

        mainPanel.add(buttonPanel, BorderLayout.SOUTH); // 将按钮面板添加到主面板的南边

        // Add the main panel to the frame
        add(mainPanel, BorderLayout.CENTER); // 将主面板添加到窗口的中间位置

        // Set the frame to be visible
        setVisible(true); // 设置窗口可见
    }

    public SetupNewConnection(String a) {
        // Set up the frame
        setTitle("trasaction"); // 设置窗口标题
        setSize(450, 400); // 设置窗口大小
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 设置关闭操作
        setLayout(new BorderLayout()); // 设置布局管理器为边界布局

        // Create the main panel
        JPanel mainPanel = new JPanel(new BorderLayout()); // 创建主面板

        // Create the input form panel
        JPanel formPanel = new JPanel(new GridBagLayout()); // 创建输入表单面板
        GridBagConstraints gbc = new GridBagConstraints(); // 创建网格包约束
        gbc.fill = GridBagConstraints.HORIZONTAL; // 设置填充方式为水平填充
        gbc.insets = new Insets(5, 5, 5, 5); // 设置组件之间的间距

        // Add fields to the form panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("transaction ID"), gbc); // 添加连接名称标签
        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField connectionNameField = new JTextField(); // 创建连接名称文本框
        connectionNameField.setEditable(false);
        formPanel.add(connectionNameField, gbc); // 添加连接名称文本框到表单面板
        /* 
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Connection Method:"), gbc); // 添加连接方法标签
        gbc.gridx = 1;
        gbc.gridy = 1;
        JComboBox<String> connectionMethodBox = new JComboBox<>(new String[]{"Standard (TCP/IP)"}); // 创建连接方法下拉框
        formPanel.add(connectionMethodBox, gbc); // 添加连接方法下拉框到表单面板
        */
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("date"), gbc); // 添加主机名标签
        gbc.gridx = 1;
        gbc.gridy = 2;
        JTextField hostnameField = new JTextField(); // 创建主机名文本框，默认为127.0.0.1
        hostnameField.setEditable(false);
        formPanel.add(hostnameField, gbc); // 添加主机名文本框到表单面板

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("else info"), gbc); // 添加端口标签
        gbc.gridx = 1;
        gbc.gridy = 3;
        JTextField portField = new JTextField(); // 创建端口文本框，默认为3306
        portField.setEditable(false);
        formPanel.add(portField, gbc); // 添加端口文本框到表单面板

        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Username:"), gbc); // 添加用户名标签
        gbc.gridx = 1;
        gbc.gridy = 4;
        JTextField usernameField = new JTextField("root"); // 创建用户名文本框，默认为root
        formPanel.add(usernameField, gbc); // 添加用户名文本框到表单面板
        /* 
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Password:"), gbc); // 添加密码标签
        gbc.gridx = 1;
        gbc.gridy = 5;
        JPasswordField passwordField = new JPasswordField(); // 创建密码文本框
        formPanel.add(passwordField, gbc); // 添加密码文本框到表单面板
        
        gbc.gridx = 1;
        gbc.gridy = 6;
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // 创建密码面板
        // passwordPanel.add(passwordField); // 添加密码文本框到密码面板
        JButton storeInVaultButton = new JButton("Store in Vault ..."); // 创建存储到保险库按钮
        passwordPanel.add(storeInVaultButton); // 添加存储到保险库按钮到密码面板
        formPanel.add(passwordPanel, gbc); // 添加密码面板到表单面板
        */
        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(new JLabel("Default Schema:"), gbc); // 添加默认模式标签
        gbc.gridx = 1;
        gbc.gridy = 7;
        JTextField schemaField = new JTextField(); // 创建默认模式文本框
        formPanel.add(schemaField, gbc); // 添加默认模式文本框到表单面板

        mainPanel.add(formPanel, BorderLayout.CENTER); // 将表单面板添加到主面板的中间位置

        // Create the button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // 创建按钮面板，使用右对齐布局
        //JButton testConnectionButton = new JButton("Test Connection"); // 创建测试连接按钮
        //JButton cancelButton = new JButton("Cancel"); // 创建取消按钮
        JButton okButton = new JButton("OK"); // 创建确定按钮

        //buttonPanel.add(testConnectionButton); // 添加测试连接按钮到按钮面板
        //buttonPanel.add(cancelButton); // 添加取消按钮到按钮面板
        buttonPanel.add(okButton); // 添加确定按钮到按钮面板

        mainPanel.add(buttonPanel, BorderLayout.SOUTH); // 将按钮面板添加到主面板的南边

        // Add the main panel to the frame
        add(mainPanel, BorderLayout.CENTER); // 将主面板添加到窗口的中间位置

        // Set the frame to be visible
        setVisible(true); // 设置窗口可见
    }

    public static void main(String[] args) {
        // Run the GUI in the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> new SetupNewConnection()); // 在事件分派线程中运行GUI
    }
}
