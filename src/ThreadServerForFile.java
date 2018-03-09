import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ThreadServerForFile extends Thread{
	Socket socket;
	PrintWriter writeInfo;
	Scanner readInfo;
	
	public ThreadServerForFile(Socket s) throws IOException{
		socket = s;
		writeInfo = new PrintWriter(socket.getOutputStream(), true);
		readInfo = new Scanner(socket.getInputStream());
		writeInfo.println("OK");
		writeInfo.flush();
	}
	
	
	
	
	public void run() {
		while(true){
			String situation = readInfo.nextLine();
			
			if(situation.equals("new file")){
				setComboBoxes();
			}
			
			else if(situation.equals("list of clients")){
				sendList();
			}
			
			else if(situation.equals("off")){
				break;
			}
		}
	}
	
	
	
	
	public void sendList(){
		for(int i = 0; i < ChatServer.users.size(); i++){
			writeInfo.println(ChatServer.users.elementAt(i).name);
			writeInfo.flush();
		}
		writeInfo.println("end");
		writeInfo.flush();
	}
	
	
	
	
	public void setComboBoxes(){
		for(int i = 0; i < ChatServer.users.size(); i++){
			if(ChatServer.users.elementAt(i).sit == true){
				ChatServer.users.elementAt(i).writeInfo.println("set comboBox");
				ChatServer.users.elementAt(i).writeInfo.flush();
			}
		}
	}
}
