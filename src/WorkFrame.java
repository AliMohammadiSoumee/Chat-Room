import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class WorkFrame extends Thread{
	JComboBox<String> files = new JComboBox<>();
	JFrame frame = new JFrame();
	JPanel contentPane = new JPanel();
	JPanel westPanel = new JPanel();
	JPanel centerPanel = new JPanel();
	JPanel northPanel = new JPanel();
	JPanel southPanel = new JPanel();
	JTextArea sendMessage = new JTextArea();
	JButton logout = new JButton("Log out");
	JButton send = new JButton("Send");
	JButton download = new JButton("Download");
	JLabel users = new JLabel(" Users: ");
	JTextArea dir = new JTextArea();
	JButton listFile = new JButton("List");
	Vector<JTextArea> haveMessages = new Vector<>();
	JList jList = new JList();
	String[] list;
	String[] listSit;
	int selected = 0;
	Socket socket;
	PrintWriter writeInfo;
	Scanner readInfo;
	Socket fileSocket;
	String name;
	PrintWriter fWriteInfo;
	Scanner fReadInfo;
	
	
	
	
	public WorkFrame(Socket s, String n) throws IOException{
		name = n;
		
		socket = s;
		writeInfo = new PrintWriter(socket.getOutputStream(), true);
		readInfo = new Scanner(socket.getInputStream());
		
		fileSocket = new Socket("localhost", 8989);
		fWriteInfo = new PrintWriter(fileSocket.getOutputStream(), true);
		fReadInfo = new Scanner(fileSocket.getInputStream());
		
		setFrame();	
		
		logout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logOut();
			}
		});
		
		send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendMessage();
			}
		});
		
		download.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					downloadFile();
				} 
				catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		jList.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {
				selected = jList.getSelectedIndex();
				setTextArea(selected);
			}
		});
		
		listFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setFiles();
			}
		});
	}
	
	
	
	
	public void run(){
		while(true){
			String situation = readInfo.nextLine();
			
			if(situation.equals("message")){
				String s = readInfo.nextLine();
				for(int i = 0 ; i < list.length ; i++){
					if(s.equals(list[i])){	
						haveMessages.elementAt(i).append(s + " :  " + readInfo.nextLine() + "\n");
					}
				}
			}
			
			else if(situation.equals("set comboBox")){
				setFiles();
			}
			
			else if(situation.equals("setting")){
				setContacts();
			}	
			
			else if(situation.equals("off")){
				break;
			}
		}
	}
	
	
	
	
	private void setTextArea(int i){
		if(listSit[i].equals("off")){
			send.setEnabled(false);
			sendMessage.setEnabled(false);
		}
		else{
			send.setEnabled(true);
			sendMessage.setEnabled(true);
		}
		centerPanel.removeAll();
		centerPanel.add(haveMessages.elementAt(i));
		centerPanel.setLayout(new GridLayout(1, 1));
		centerPanel.revalidate();
		centerPanel.repaint();
	}
	
	
	
	
	private void sendMessage() {
		haveMessages.elementAt(jList.getSelectedIndex()).append("you : " + sendMessage.getText() + "\n");
		writeInfo.println("message");
		writeInfo.flush();
		writeInfo.println(jList.getSelectedValue().toString());
		writeInfo.flush();
		writeInfo.println(name);
		writeInfo.flush();
		writeInfo.println(sendMessage.getText());
		writeInfo.flush();
		sendMessage.setText("");
	}




	private void logOut(){
		writeInfo.println("line");
		writeInfo.flush();
		writeInfo.println(name);
		writeInfo.flush();
		fWriteInfo.println("line");
		fWriteInfo.flush();
		
		try {
			writeInfo.close();
			readInfo.close();
			socket.close();
		} 
		catch (IOException e){
			e.printStackTrace();
		}
		frame.dispose();
	}
	
	
	
	
	private void setFrame(){
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = gd.getDisplayMode().getWidth();
		
		frame.setResizable(false);
		frame.setTitle(name);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(width - 550, 50, 520, 332);
		contentPane.setBackground(UIManager.getColor("Menu.disabledForeground"));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.setContentPane(contentPane);
		contentPane.setLayout(null);
		
		westPanel.setBackground(UIManager.getColor("List.selectionInactiveBackground"));
		westPanel.setBounds(6, 6, 95, 264);
		contentPane.add(westPanel);
		
		users.setHorizontalAlignment(SwingConstants.LEFT);
		users.setEnabled(true);
		users.setVerticalAlignment(SwingConstants.CENTER);
		users.setForeground(new Color(0, 102, 0));
		users.setFont(new Font("Dialog", Font.PLAIN, 15));
		users.setBackground(Color.RED);
		westPanel.add(users);
		
		northPanel.setBackground(UIManager.getColor("Menu.disabledForeground"));
		northPanel.setBounds(113, 6, 401, 53);
		contentPane.add(northPanel);
		northPanel.setLayout(null);
		
		files.setBounds(-5, 0, 405, 27);
		setFiles();
		northPanel.add(files);
		
		dir.setToolTipText("Requested file");
		dir.setBackground(new Color(202, 202, 202));
		dir.setForeground(new Color(0, 51, 153));
		dir.setFont(new Font("Lucida Grande", Font.PLAIN, 17));
		dir.setLineWrap(true);
		dir.setBounds(0, 28, 281, 23);
		northPanel.add(dir);
		
		download.setToolTipText("Download file");
		download.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		download.setBounds(284, 28, 117, 24);
		northPanel.add(download);
		
		centerPanel.setBounds(113, 72, 401, 197);
		contentPane.add(centerPanel);
		
		southPanel.setBackground(UIManager.getColor("Menu.disabledForeground"));
		southPanel.setBounds(113, 281, 401, 23);
		contentPane.add(southPanel);
		southPanel.setLayout(null);
		
		sendMessage.setToolTipText("To send a message");
		sendMessage.setForeground(new Color(0, 51, 153));
		sendMessage.setBackground(UIManager.getColor("List.selectionInactiveBackground"));
		sendMessage.setFont(new Font("Lucida Grande", Font.PLAIN, 17));
		sendMessage.setRows(1);
		sendMessage.setTabSize(4);
		sendMessage.setLineWrap(true);
		sendMessage.setBounds(0, 0, 303, 23);
		southPanel.add(sendMessage);
		
		send.setToolTipText("Send");
		send.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		send.setBounds(305, 1, 96, 23);
		southPanel.add(send);
		
		logout.setToolTipText("Log out");
		logout.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		logout.setBounds(6, 282, 96, 23);
		contentPane.add(logout);

		setContacts();
		
		frame.setVisible(true);
	}

	
	
	
	private void downloadFile() throws IOException{
		
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.showOpenDialog(null);
		dir.setText(fc.getSelectedFile().getPath());
		
		ThreadFileClient th = new ThreadFileClient(fileSocket, fc.getSelectedFile().getPath() + "/" + files.getSelectedItem().toString(), files.getSelectedItem().toString());
		th.start();
	}


	
	
	private void setFiles(){
		files.removeAllItems();
		fWriteInfo.println("get list of files");
		fWriteInfo.flush();
		String str = fReadInfo.nextLine();
		while(!(str.equals("end"))){
			files.addItem(str);
			str = fReadInfo.nextLine();
		}
	}
	
	
	

	private void setContacts() {
		westPanel.removeAll();
		westPanel.add(users);
		list = new String[15];
		listSit = new String[15];
		writeInfo.println("contacts");
		writeInfo.flush();	
		String str = readInfo.nextLine();
		int i = 0;
		
		while(!(str.equals("end"))){
			list[i] = str;
			str = readInfo.nextLine();
			listSit[i] = str;
			str = readInfo.nextLine();
			
			JTextArea ta = new JTextArea();
			ta.setBackground(UIManager.getColor("List.selectionInactiveBackground"));
			ta.setToolTipText("Message Received");
			ta.setForeground(new Color(153, 0, 0));
			ta.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
			ta.setTabSize(4);
			ta.setLineWrap(true);
			
			if(i < haveMessages.size()){
				ta.setText(haveMessages.elementAt(i).getText());
				haveMessages.setElementAt(ta, i);
			}
			
			else{
				haveMessages.addElement(ta);
			}
			
			i++;
		}
		
		jList.removeAll();
		jList.setListData(list);
		jList.setBackground(UIManager.getColor("List.selectionInactiveBackground"));
		jList.setVisibleRowCount(13);
		jList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		JScrollPane sl = new JScrollPane(jList);
		westPanel.add(sl);
		jList.setVisible(true);
		
		jList.setSelectedIndex(selected);
		setTextArea(selected);
		
		frame.revalidate();
		frame.repaint();
	}
}
