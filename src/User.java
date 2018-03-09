import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JButton;

public class User {
	String name;
	Socket socket;
	String password;
	Scanner readInfo;
	PrintWriter writeInfo;
	boolean sit = false;
	
	
	
	
	public User(String n, String p) throws IOException{
		socket = new Socket();
		name = n;
		password = p;
	}
}
