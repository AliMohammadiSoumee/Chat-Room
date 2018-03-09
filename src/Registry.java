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
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class Registry extends JFrame{
	JPanel contentPane = new JPanel();
	JTextField userName = new JTextField();
	JPasswordField password = new JPasswordField();
	JPasswordField verify = new JPasswordField();
	JLabel lblNewLabel = new JLabel("Username: ");
	JLabel pass = new JLabel("Password: ");
	JLabel ver = new JLabel("Verify: ");
	JButton accept = new JButton("Submit");
	Socket socket;
	PrintWriter writeInfo;
	Scanner readInfo;
	
	
	
	
	public Registry(Socket s){
		socket = s;	
		setFrame();
		
		accept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					registry();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});	
	}
	
	
	
	
	private void setFrame(){
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = gd.getDisplayMode().getWidth();
		int height = gd.getDisplayMode().getHeight();
		
		setResizable(false);
		setTitle("Registry");
		setFont(new Font("Dialog", Font.PLAIN, 17));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds((width - 365) / 2, (height - 192) / 2, 365, 192);
		contentPane.setBackground(UIManager.getColor("Menu.disabledForeground"));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		userName.setForeground(Color.BLACK);
		userName.setFont(new Font("Lucida Grande", Font.PLAIN, 17));
		userName.setHorizontalAlignment(SwingConstants.CENTER);
		userName.setBackground(UIManager.getColor("List.selectionInactiveBackground"));
		userName.setBounds(157, 17, 166, 26);
		contentPane.add(userName);
		userName.setColumns(10);
		
		password.setEchoChar('*');
		password.setBackground(UIManager.getColor("List.selectionInactiveBackground"));
		password.setHorizontalAlignment(SwingConstants.CENTER);
		password.setFont(new Font("Lucida Grande", Font.PLAIN, 17));
		password.setBounds(157, 57, 166, 26);
		contentPane.add(password);
		
		verify.setBackground(UIManager.getColor("List.selectionInactiveBackground"));
		verify.setEchoChar('*');
		verify.setHorizontalAlignment(SwingConstants.CENTER);
		verify.setFont(new Font("Lucida Grande", Font.PLAIN, 17));
		verify.setBounds(157, 97, 166, 26);
		contentPane.add(verify);
		
		lblNewLabel.setLabelFor(userName);
		lblNewLabel.setForeground(new Color(255, 204, 0));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 17));
		lblNewLabel.setBounds(23, 17, 104, 26);
		contentPane.add(lblNewLabel);
		
		pass.setLabelFor(password);
		pass.setForeground(new Color(255, 204, 0));
		pass.setFont(new Font("Lucida Grande", Font.PLAIN, 17));
		pass.setHorizontalAlignment(SwingConstants.CENTER);
		pass.setBounds(23, 57, 104, 26);
		contentPane.add(pass);
		
		ver.setForeground(new Color(255, 204, 0));
		ver.setFont(new Font("Lucida Grande", Font.PLAIN, 17));
		ver.setLabelFor(verify);
		ver.setHorizontalAlignment(SwingConstants.CENTER);
		ver.setBounds(23, 97, 104, 26);
		contentPane.add(ver);
		
		accept.setForeground(new Color(204, 0, 0));
		accept.setFont(new Font("Baskerville", Font.PLAIN, 17));
		accept.setBounds(113, 133, 138, 33);
		contentPane.add(accept);
		
		setVisible(true);
	}
	
	
	
	
	private void registry() throws IOException{	
		writeInfo = new PrintWriter(socket.getOutputStream(), true);
		readInfo = new Scanner(socket.getInputStream());
		String name = userName.getText();
		String pass = password.getText();
		
		writeInfo.println("new");
		writeInfo.flush();
		writeInfo.println(name);
		writeInfo.flush();
		
		String situation = readInfo.nextLine();
		
		if(situation.equals("found")){
			JOptionPane.showMessageDialog(null, "you can't choice this username", "ERROR", JOptionPane.ERROR_MESSAGE);
		}
		
		else if(situation.equals("not found")){
			if(pass.equals(verify.getText())){
				writeInfo.println(pass);
				writeInfo.flush();
				dispose();
				WorkFrame wf = new WorkFrame(socket, name);
				wf.start();
			}
			
			else{
				JOptionPane.showMessageDialog(null, "your password not equals with verifty", "ERROR", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
