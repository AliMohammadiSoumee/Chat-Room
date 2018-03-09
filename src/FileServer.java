import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class FileServer {
	static File directory;
	static File[] files;
	static JFileChooser fc;
	static Socket serverSocket;
	static PrintWriter sWriteInfo;
	static Scanner sReadInfo;
	static JFrame frame = new JFrame();
	static JPanel contentPane = new JPanel();
	static JLabel addFile = new JLabel("add a new file: ");
	static JButton exit = new JButton("Exit");
	static JButton browse = new JButton("Browse");
	static JTextArea textArea = new JTextArea();
	static JLabel listOfClients = new JLabel("Get list of clients:");
	static JButton getClients = new JButton("Get list");
	static JLabel listOfFiles = new JLabel("Get list of files: ");
	static JButton getFiles = new JButton("Get list");
	
	
	
	
	public static void main(String[] args) throws IOException {
		serverSocket = new Socket("localhost", 6766);
		sWriteInfo = new PrintWriter(serverSocket.getOutputStream(), true);
		sReadInfo = new Scanner(serverSocket.getInputStream());
		System.out.println(sReadInfo.nextLine());
		
		setFrame();

		getDir();
		
		ThreadFile thf = new ThreadFile();
		thf.start();	
		
		getClients.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sWriteInfo.println("list of clients");
				sWriteInfo.flush();
				listOfClients();
			}
		});
		
		getFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				listOfFiles();
			}
		});
		
		browse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					browse();
				} 
				catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} 
				catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					sWriteInfo.println("off");
					sWriteInfo.flush();
					frame.dispose();

					
					thf.stop();
					
					sReadInfo.close();
					sWriteInfo.close();
					serverSocket.close();
				} 
				catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
	}

	
	
	
	public static void setFrame(){
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = gd.getDisplayMode().getWidth();
		int height = gd.getDisplayMode().getHeight();
		
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(width - 370, height - 200, 359, 180);
		
		contentPane.setBackground(UIManager.getColor("Menu.disabledForeground"));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.setContentPane(contentPane);
		contentPane.setLayout(null);
		
		addFile.setForeground(new Color(255, 204, 0));
		addFile.setHorizontalAlignment(SwingConstants.CENTER);
		addFile.setBounds(6, 6, 100, 25);
		contentPane.add(addFile);	
		
		exit.setFont(new Font("Baskerville", Font.PLAIN, 17));
		exit.setForeground(new Color(204, 0, 0));
		exit.setBounds(253, 5, 100, 25);
		contentPane.add(exit);
		
		browse.setForeground(new Color(204, 0, 0));
		browse.setFont(new Font("Baskerville", Font.PLAIN, 17));
		browse.setBounds(253, 38, 100, 29);
		contentPane.add(browse);
		
		textArea.setForeground(new Color(0, 51, 153));
		textArea.setBackground(UIManager.getColor("List.selectionInactiveBackground"));
		textArea.setEditable(false);
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		textArea.setBounds(6, 38, 235, 25);
		contentPane.add(textArea);
		
		listOfClients.setForeground(new Color(255, 204, 0));
		listOfClients.setHorizontalAlignment(SwingConstants.CENTER);
		listOfClients.setBounds(22, 123, 138, 25);
		contentPane.add(listOfClients);

		getClients.setFont(new Font("Baskerville", Font.PLAIN, 17));
		getClients.setForeground(new Color(204, 0, 0));
		getClients.setBounds(199, 123, 125, 29);
		contentPane.add(getClients);
		
		listOfFiles.setForeground(new Color(255, 204, 0));
		listOfFiles.setHorizontalAlignment(SwingConstants.CENTER);
		listOfFiles.setBounds(22, 88, 138, 25);
		contentPane.add(listOfFiles);
		
		getFiles.setFont(new Font("Baskerville", Font.PLAIN, 17));
		getFiles.setForeground(new Color(204, 0, 0));
		getFiles.setBounds(199, 87, 125, 29);
		contentPane.add(getFiles);
		
		frame.setVisible(true);
	}
	
	
	
	
	private static void browse() throws IOException{
		fc = new JFileChooser();
		fc.showOpenDialog(null);
		
		File file = fc.getSelectedFile();
		File copyFile = new File(directory.getPath() + "/" + file.getName());
		
		FileOutputStream out = new FileOutputStream(copyFile);
		FileInputStream in = new FileInputStream(file);
		BufferedOutputStream bout = new BufferedOutputStream(out);
		
		byte[] b = new byte[1024];
		
		while(in.available() > 1024){
			in.read(b);
			bout.write(b);
			bout.flush();
		}
		
		int rest = in.available();
		b = new byte[rest];
		in.read(b);
		bout.write(b);
		bout.flush();
		
		textArea.setText(file.getPath());
		
		files = directory.listFiles();
		
		sWriteInfo.println("new file");
		sWriteInfo.flush();
	}
	
	
	
	
	private static void listOfFiles(){
		Vector<String> list = new Vector<>();
		
		for(int i = 0; i < files.length; i++){
			list.addElement(files[i].getName());
		}
		JList<String> jList = new JList<>(list);
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 228, 166);
		contentPane = new JPanel();
		contentPane.setBackground(UIManager.getColor("MenuItem.disabledForeground"));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane(jList);
		scrollPane.setForeground(new Color(0, 0, 0));
		scrollPane.setBackground(UIManager.getColor("Label.disabledForeground"));
		scrollPane.setBounds(6, 6, 216, 96);
		contentPane.add(scrollPane);
		
		JButton exit = new JButton("Exit");
		exit.setFont(new Font("Baskerville", Font.PLAIN, 17));
		exit.setForeground(new Color(204, 0, 0));
		exit.setBounds(54, 109, 117, 29);
		contentPane.add(exit);
		
		frame.setResizable(false);
		frame.setVisible(true);
		
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});
	}
	
	
	
	
	private static void listOfClients(){
		Vector<String> list = new Vector<>();
		String str = sReadInfo.nextLine();
		
		while(!(str.equals("end"))){
			list.addElement(str);
			str = sReadInfo.nextLine();
		}
		JList<String> jList = new JList<>(list);
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 228, 166);
		contentPane = new JPanel();
		contentPane.setBackground(UIManager.getColor("MenuItem.disabledForeground"));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane(jList);
		scrollPane.setForeground(new Color(0, 0, 0));
		scrollPane.setBackground(UIManager.getColor("Label.disabledForeground"));
		scrollPane.setBounds(6, 6, 216, 96);
		contentPane.add(scrollPane);
		
		JButton exit = new JButton("Exit");
		exit.setFont(new Font("Baskerville", Font.PLAIN, 17));
		exit.setForeground(new Color(204, 0, 0));
		exit.setBounds(54, 109, 117, 29);
		contentPane.add(exit);
		
		frame.setResizable(false);
		frame.setVisible(true);
		
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});
	}
	
	
	
	
	private static void getDir(){		
		JFrame frame = new JFrame();
		JLabel choose = new JLabel("choose a directory for your files");
		JButton browse = new JButton("Browse");

		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = gd.getDisplayMode().getWidth();
		int height = gd.getDisplayMode().getHeight();
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(width - 340, height - 150, 287, 107);
		contentPane = new JPanel();
		contentPane.setBackground(UIManager.getColor("MenuItem.disabledForeground"));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.setContentPane(contentPane);
		contentPane.setLayout(null);
		
		choose.setFont(new Font("Lucida Grande", Font.PLAIN, 17));
		choose.setForeground(new Color(255, 204, 0));
		choose.setHorizontalAlignment(SwingConstants.CENTER);
		choose.setBounds(6, 6, 275, 25);
		contentPane.add(choose);
		
		browse.setForeground(new Color(204, 0, 0));
		browse.setBounds(83, 43, 117, 29);
		contentPane.add(browse);
		
		frame.setResizable(false);
		frame.setVisible(true);
		
		browse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc.showOpenDialog(null);
				directory = fc.getSelectedFile();
				files = directory.listFiles();
			}
		});
	}
}