import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;

public class SimpleGUI extends JFrame implements ActionListener{

    JTextArea textArea; // 文本区域
    JScrollPane textScrollPane; // 滚动面板
    JLabel statusLabel; // 状态标签
    JMenuBar mainMenuBar; // 菜单栏
    JMenu fileMenu; // 文件菜单
    JMenuItem openFileMenuItem; // 打开菜单项
    JMenuItem saveFileMenuItem; // 保存菜单项
    JMenuItem exitMenuItem; // 退出菜单项
    JMenuItem uploadMenuItem; // 上传菜单项
	JMenuItem queryMenuItem; // 查詢菜单项
	public static void main(String[] args){
		new SimpleGUI();
	}

	SimpleGUI(){
        initializeWindow();
        initializeComponents();
        setUpLayout();
        configureTextArea();
        configureScrollPane();
        addActionListeners();
        this.setVisible(true); // 显示窗口
	}

	private void initializeWindow() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 设置关闭窗口的操作
        this.setTitle("Simple Text Editor"); // 设置窗口标题
        this.setSize(500, 500); // 设置窗口大小
        this.setLocationRelativeTo(null); // 将窗口置于屏幕中央
    }

    private void configureTextArea() {
        textArea.setLineWrap(true); // 自动换行
        textArea.setWrapStyleWord(true); // 单词换行
        textArea.setFont(new Font("Arial", Font.PLAIN, 20)); // 设置字体
    }

    private void configureScrollPane() {
        textScrollPane.setPreferredSize(new Dimension(450, 450)); // 设置滚动面板大小
        textScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS); // 垂直滚动条总是可见
    }

    private void initializeComponents() {
        this.setLayout(new FlowLayout()); // 使用流式布局
        textArea = new JTextArea(); // 创建文本区域
        textScrollPane = new JScrollPane(textArea); // 创建滚动面板
        mainMenuBar = new JMenuBar(); // 创建菜单栏
        fileMenu = new JMenu("File"); // 创建文件菜单
        openFileMenuItem = new JMenuItem("Open"); // 创建打开菜单项
        saveFileMenuItem = new JMenuItem("Save"); // 创建保存菜单项
        exitMenuItem = new JMenuItem("Exit"); // 创建退出菜单项
        uploadMenuItem = new JMenuItem("Upload"); // 创建上传菜单项
		queryMenuItem = new JMenuItem("Query");

        fileMenu.add(openFileMenuItem); // 将打开菜单项添加到文件菜单
        fileMenu.add(saveFileMenuItem); // 将保存菜单项添加到文件菜单
        fileMenu.add(uploadMenuItem); // 将上传菜单项添加到文件菜单
        fileMenu.add(exitMenuItem); // 将退出菜单项添加到文件菜单
		fileMenu.add(queryMenuItem);
        mainMenuBar.add(fileMenu); // 将文件菜单添加到菜单栏
        this.setJMenuBar(mainMenuBar); // 将菜单栏设置到窗口中
    }

    private void setUpLayout() {
        this.add(textScrollPane); // 将滚动面板添加到窗口中
    }

    private void addActionListeners() {
        openFileMenuItem.addActionListener(this); // 添加动作监听
        saveFileMenuItem.addActionListener(this); // 添加动作监听
        exitMenuItem.addActionListener(this); // 添加动作监听
        uploadMenuItem.addActionListener(this); // 添加动作监听
		queryMenuItem.addActionListener(this);
    }

	
	@Override
	public void actionPerformed(ActionEvent e) {	
		if(e.getSource()==openFileMenuItem) { // 如果點擊了開啟選單項目
			openFileAction();
		} else if(e.getSource()==saveFileMenuItem) { // 如果點擊了儲存選單項目
			saveFileAction();	
		} else if(e.getSource()==uploadMenuItem) {
			uploadFileAction();
		} else if(e.getSource()==queryMenuItem){
			queryFileAction();
		} else if(e.getSource()==exitMenuItem) { // 如果點擊了退出選單項目
		    System.exit(0); // 退出應用程式
	    }		
	}

	private void openFileAction(){
		JFileChooser fileChooser = new JFileChooser(); // 創建檔案選擇器
		fileChooser.setCurrentDirectory(new File(".")); // 設置當前目錄
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt"); // 創建檔案過濾器
		fileChooser.setFileFilter(filter); // 設置檔案過濾器
		
		int response = fileChooser.showOpenDialog(null); // 顯示開啟檔案對話框
		
		if(response == JFileChooser.APPROVE_OPTION) { // 如果選擇了檔案(用于表示用户选择了一个文件或目录并点击了“确认”或“打开”按钮)
			File file = new File(fileChooser.getSelectedFile().getAbsolutePath()); // 獲取選擇的檔案
			Scanner fileIn = null; // 宣告檔案掃描器
			
			try {
				fileIn = new Scanner(file); // 創建檔案掃描器
				if(file.isFile()) { // 如果是檔案
					while(fileIn.hasNextLine()) { // 逐行讀取檔案內容
						String line = fileIn.nextLine()+"\n"; // 讀取每行內容並添加換行符
						textArea.append(line); // 在文本區域中添加內容
					}
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace(); // 輸出異常信息
			}
			finally {
				fileIn.close(); // 關閉檔案掃描器
			}
		}
	}

	private void saveFileAction(){
		JFileChooser fileChooser = new JFileChooser(); // 創建檔案選擇器
		fileChooser.setCurrentDirectory(new File(".")); // 設置當前目錄
		
		int response = fileChooser.showSaveDialog(null); // 顯示儲存檔案對話框
		
		if(response == JFileChooser.APPROVE_OPTION) { // 如果選擇了儲存檔案
			File file; // 宣告檔案物件
			PrintWriter fileOut = null; // 宣告檔案輸出流
			
			file = new File(fileChooser.getSelectedFile().getAbsolutePath()); // 獲取選擇的檔案
			try {
				fileOut = new PrintWriter(file); // 創建檔案輸出流
				fileOut.println(textArea.getText()); // 將文本區域內容寫入檔案
			} 
			catch (FileNotFoundException e1) {
				e1.printStackTrace(); // 輸出異常信息
				}
			finally {
				fileOut.close(); // 關閉檔案輸出流
			}			
		}
	}

	private void uploadFileAction(){
		// textArea.getText() 文本區域內容
	}

    private void queryFileAction() {
        // 创建一个新的对话框，用于输入查询文件名
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
                String fileName = queryTextField.getText();
                String[] result = query(fileName);
                // 如何展示?  textArea.setText(result);
                queryDialog.dispose();
            }
        });

        queryDialog.setLocationRelativeTo(this);
        queryDialog.setVisible(true);
    }

    // 模拟的查询方法，根据文件名查询内容
    private String[] query(String transaction) {
        // 此处实现你的查询逻辑，例如从数据库或文件系统查询内容
        //return "Query result for file: " + transaction;
		return new String[]{"success"};
    }
}
