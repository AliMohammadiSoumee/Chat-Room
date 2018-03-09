import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

public class ChatServer {
	static File file = new File("Data.txt");
	static Vector<User> users = new Vector<>();
	static int numOfClients;
	static User uTemp;
	static Scanner readFile;
	static FileWriter writeFile;
	static ServerSocket listener;
	static Socket socket;
	static Scanner readInfo;
	static PrintWriter writeInfo;
	
	
	
	
	public static void main(String[] args) throws IOException{	
		listener = new ServerSocket(6766);
		readFile = new Scanner(file);
		writeFile = new FileWriter(file, true);
		
		setUsers();
		
		socket = listener.accept();
		ThreadServerForFile thf = new ThreadServerForFile(socket);
		thf.start();
		
		while(true){
			socket = listener.accept();
			readInfo = new Scanner(socket.getInputStream());
			writeInfo = new PrintWriter(socket.getOutputStream(), true);
			
			while(true){
				String situation = readInfo.nextLine();
				
				if(situation.equals("new")){
					if(searchUserForRegistry() == 1){
						break;
					}
				}
			
				else if(situation.equals("log in")){
					if(searchUserForLogIn() == 1){
						break;
					}
				}
			}
		}	
	}
	
	
	
	
	private static int searchUserForRegistry() throws IOException{
		String name = readInfo.nextLine();
		readFile = new Scanner(file);
		int found = 0;
		
		while(readFile.hasNextLine()){
			if(name.equals(readFile.nextLine())){
				writeInfo.println("found");
				writeInfo.flush();
				found = 1;
			}
			readFile.nextLine();
		}
		
		if(found == 0){
			writeInfo.println("not found");
			writeInfo.flush();
			String pass = readInfo.nextLine();
			newUser(name, pass);
			return 1;
		}
	
		return 0;
	}
	
	
	
	
	private static int searchUserForLogIn() throws IOException {
		String name = readInfo.nextLine();
		String pass = readInfo.nextLine();
		readFile = new Scanner(file);
		int found = 0;
		int i = 0;
		
		while(readFile.hasNextLine()){
			String s = readFile.nextLine();
			
			if(name.equals(s)){
				found = 1;
				
				if(pass.equals(readFile.nextLine())){
					writeInfo.println("found");
					writeInfo.flush();
					logInUser(i / 2);
					return 1;
				}
				
				else{
					writeInfo.println("wrong pass");
					writeInfo.flush();
				}
			}	
			i++;
		}
		
		if(found == 0){
			writeInfo.println("not found");
			writeInfo.flush();
		}
		
		return 0;
	}


	
	
	private static void setUsers() throws IOException{
		while(readFile.hasNextLine()){
			String name = readFile.nextLine();
			String pass = readFile.nextLine();
			uTemp = new User(name, pass);
			users.addElement(uTemp);
		}
	}
	
	
	private static void newUser(String name, String password) throws IOException{
		writeFile.append(name + "\n" + password + "\n");
		writeFile.flush();
		uTemp = new User(name, password);
		uTemp.socket = socket;
		uTemp.readInfo = new Scanner(uTemp.socket.getInputStream());
		uTemp.writeInfo = new PrintWriter(uTemp.socket.getOutputStream(), true);
		//uTemp.writeInfo.println("OK1");
		//uTemp.writeInfo.flush();
		uTemp.sit = true;
		users.addElement(uTemp);
		ThreadServer th = new ThreadServer(users.elementAt(users.size() - 1).socket);
		th.start();
		setting(users.size() - 1);
	}
	
	
	
	
	private static void logInUser(int number) throws IOException{
		users.elementAt(number).socket = socket;
		users.elementAt(number).readInfo = new Scanner(users.elementAt(number).socket.getInputStream());
		users.elementAt(number).writeInfo = new PrintWriter(users.elementAt(number).socket.getOutputStream(), true);
		users.elementAt(number).sit = true;
		ThreadServer th = new ThreadServer(users.elementAt(number).socket);
		th.start();
		setting(number);
	}
	
	
	
	
	public static void setting(int n){
		for(int i = 0 ; i < users.size() ; i++){
			if(users.elementAt(i).sit == true && i != n){
				users.elementAt(i).writeInfo.println("setting");
				users.elementAt(i).writeInfo.flush();
			}
		}
	}
}



