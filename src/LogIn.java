import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class LogIn extends JFrame{	
	JPanel frame = new JPanel();
	JButton logIn = new JButton("Log in");
	JButton registry = new JButton("Registry");
	JPasswordField password = new JPasswordField();
	JTextArea userName = new JTextArea();
	JLabel user = new JLabel("Username: ");
	JLabel pass = new JLabel("Password: ");
	Socket socket;
	PrintWriter writeInfo;
	Scanner readInfo;
	
	
	
	
	public LogIn(Socket s) throws IOException{
		socket = s;
		writeInfo = new PrintWriter(socket.getOutputStream(), true);
		readInfo = new Scanner(socket.getInputStream());
		
		setFrame();
		
		registry.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				registryUser();
			}
		});
		
		logIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					logInUser();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}
	
	
	
	
	private void setFrame() throws IOException{
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = gd.getDisplayMode().getWidth();
		int height = gd.getDisplayMode().getHeight();
		
		setResizable(false);
		setBackground(UIManager.getColor("Label.foreground"));
		setTitle("Log In");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds((width - 365) / 2, (height - 192) / 2, 365, 192);
		frame.setToolTipText("");
		frame.setBackground(UIManager.getColor("Menu.disabledForeground"));
		frame.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(frame);
		frame.setLayout(null);
		
		logIn.setForeground(new Color(204, 0, 0));
		logIn.setToolTipText("Log in");
		logIn.setFont(new Font("Baskerville", Font.PLAIN, 16));
		logIn.setBounds(49, 118, 115, 35);
		frame.add(logIn);
		
		registry.setToolTipText("Registry");
		registry.setForeground(new Color(204, 0, 0));
		registry.setFont(new Font("Baskerville", Font.PLAIN, 16));
		registry.setBounds(196, 118, 115, 35);
		frame.add(registry);
		
		password.setToolTipText("Enter your password");
		password.setFont(new Font("Lucida Grande", Font.PLAIN, 17));
		password.setBackground(UIManager.getColor("List.selectionInactiveBackground"));
		password.setEchoChar('*');
		password.setHorizontalAlignment(SwingConstants.CENTER);
		password.setBounds(165, 68, 166, 28);
		frame.add(password);
		
		userName.setToolTipText("Enter your username");
		userName.setColumns(1);
		userName.setFont(new Font("Lucida Grande", Font.PLAIN, 17));
		userName.setTabSize(0);
		userName.setBackground(UIManager.getColor("List.selectionInactiveBackground"));
		userName.setBounds(165, 25, 166, 28);
		frame.add(userName);
		
		user.setFont(new Font("Lucida Grande", Font.PLAIN, 17));
		user.setLabelFor(userName);
		user.setForeground(new Color(255, 204, 0));
		user.setHorizontalAlignment(SwingConstants.CENTER);
		user.setBackground(new Color(0, 255, 255));
		user.setBounds(39, 25, 106, 28);
		frame.add(user);
		

		pass.setForeground(new Color(255, 204, 0));
		pass.setFont(new Font("Lucida Grande", Font.PLAIN, 17));
		pass.setHorizontalAlignment(SwingConstants.CENTER);
		pass.setLabelFor(password);
		pass.setBounds(39, 68, 106, 28);
		frame.add(pass);
		
		setVisible(true);
	}
	
	
	
	
	private void registryUser(){
		dispose();
		Registry registry = new Registry(socket);
	}
	
	
	
	
	private void logInUser() throws IOException{
		String name = userName.getText();
		String pass = password.getText();
		
		writeInfo.println("log in");
		writeInfo.flush();
		writeInfo.println(userName.getText());
		writeInfo.flush();
		writeInfo.println(password.getText());
		writeInfo.flush();
		
		String situation = readInfo.nextLine();
		
		if(situation.equals("found")){
			dispose();
			
			WorkFrame wf = new WorkFrame(socket, name);
			wf.start();
		}
		
		else if(situation.equals("not found")){
			JOptionPane.showMessageDialog(null, "your username not found", "ERROR", JOptionPane.ERROR_MESSAGE);
			userName.setText("");
			password.setText("");
		}
		
		else if(situation.equals("wrong pass")){
			JOptionPane.showMessageDialog(null, "your password is not corect", "ERROR", JOptionPane.ERROR_MESSAGE);
			password.setText("");
		}	
	}
}
