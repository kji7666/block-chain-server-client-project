import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;

public class TextEditor extends JFrame implements ActionListener{

	JTextArea textArea; // 文本區域
	JScrollPane scrollPane; // 滾動面板
	JLabel fontLabel; // 字體標籤
	JSpinner fontSizeSpinner; // 字體大小微調器
	JButton fontColorButton; // 字體顏色按鈕
	JComboBox fontBox; // 字體下拉列表
	
	JMenuBar menuBar; // 選單欄
	JMenu fileMenu; // 檔案選單
	JMenuItem openItem; // 開啟選單項目
	JMenuItem saveItem; // 儲存選單項目
	JMenuItem exitItem; // 退出選單項目
	public static void main(String[] args){
		new TextEditor();
	}

	TextEditor(){
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 設置關閉視窗的操作
		this.setTitle("Bro text Editor"); // 設置視窗標題
		this.setSize(500, 500); // 設置視窗大小
		this.setLayout(new FlowLayout()); // 使用流式佈局
		this.setLocationRelativeTo(null); // 將視窗置於螢幕中央
		
		textArea = new JTextArea(); // 創建文本區域
		textArea.setLineWrap(true); // 自動換行
		textArea.setWrapStyleWord(true); // 單字換行
		textArea.setFont(new Font("Arial",Font.PLAIN,20)); // 設置字體
		
		scrollPane = new JScrollPane(textArea); // 創建滾動面板
		scrollPane.setPreferredSize(new Dimension(450,450)); // 設置滾動面板大小
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS); // 垂直滾動條總是可見
		
		fontLabel = new JLabel("Font: "); // 字體標籤
		
		fontSizeSpinner = new JSpinner(); // 創建字體大小微調器
		fontSizeSpinner.setPreferredSize(new Dimension(50,25)); // 設置微調器大小
		fontSizeSpinner.setValue(20); // 初始值為 20
		fontSizeSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				textArea.setFont(new Font(textArea.getFont().getFamily(),Font.PLAIN,(int) fontSizeSpinner.getValue()));	
			}
		});
		
		fontColorButton = new JButton("Color"); // 字體顏色按鈕
		fontColorButton.addActionListener(this); // 添加動作監聽
		
		String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames(); // 獲取可用字體列表
		
		fontBox = new JComboBox(fonts); // 創建字體下拉列表
		fontBox.addActionListener(this); // 添加動作監聽
		fontBox.setSelectedItem("Arial"); // 預設選擇 Arial 字體
		
		// ------ 選單欄 ------
		menuBar = new JMenuBar(); // 創建選單欄
		fileMenu = new JMenu("File"); // 創建檔案選單
		openItem = new JMenuItem("Open"); // 創建開啟選單項目
		saveItem = new JMenuItem("Save"); // 創建儲存選單項目
		exitItem = new JMenuItem("Exit"); // 創建退出選單項目
		
		openItem.addActionListener(this); // 添加動作監聽
		saveItem.addActionListener(this); // 添加動作監聽
		exitItem.addActionListener(this); // 添加動作監聽
		
		fileMenu.add(openItem); // 將開啟選單項目添加到檔案選單
		fileMenu.add(saveItem); // 將儲存選單項目添加到檔案選單
		fileMenu.add(exitItem); // 將退出選單項目添加到檔案選單
		menuBar.add(fileMenu); // 將檔案選單添加到選單欄
		// ------ /選單欄 ------
			
        // flowLayout有擺放順序問題
		this.setJMenuBar(menuBar); // 將選單欄設置到視窗中
		this.add(fontLabel); // 將字體標籤添加到視窗中
		this.add(fontSizeSpinner); // 將字體大小微調器添加到視窗中
		this.add(fontColorButton); // 將字體顏色按鈕添加到視窗中
		this.add(fontBox); // 將字體下拉列表添加到視窗中
		this.add(scrollPane); // 將滾動面板添加到視窗中
		this.setVisible(true); // 顯示視窗
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==fontColorButton) { // 如果點擊了字體顏色按鈕
			JColorChooser colorChooser = new JColorChooser(); // 創建顏色選擇器
			Color color = colorChooser.showDialog(null, "Choose a color", Color.black); // 顯示顏色選擇對話框
			textArea.setForeground(color); // 設置文本區域的字體顏色
		}
		
		if(e.getSource()==fontBox) { // 如果選擇了字體下拉列表
			textArea.setFont(new Font((String)fontBox.getSelectedItem(),Font.PLAIN,textArea.getFont().getSize())); // 設置文本區域的字體
		}
		
		if(e.getSource()==openItem) { // 如果點擊了開啟選單項目
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
		if(e.getSource()==saveItem) { // 如果點擊了儲存選單項目
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
		if(e.getSource()==exitItem) { // 如果點擊了退出選單項目
		    System.exit(0); // 退出應用程式
	        }		
	}
}