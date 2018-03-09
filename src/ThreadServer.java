import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

import javax.xml.ws.RequestWrapper;

public class ThreadServer extends Thread{
	Socket socket;
	PrintWriter writeInfo;
	Scanner readInfo;
	
	public ThreadServer(Socket s) throws IOException{
		socket = s;
		writeInfo = new PrintWriter(socket.getOutputStream(), true);
		readInfo = new Scanner(socket.getInputStream());
	}


	
	
	public void run(){
	while(true){
		String situation = readInfo.nextLine();

		if(situation.equals("contacts")){
			contacts();
		}
		
		else if(situation.equals("line situation")){
			lineStiation();
		}
		
		else if(situation.equals("message")){
			message();
		}
		
		else if(situation.equals("line")){
			try {
				line();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		}
	}
}
	
	
	
	
	private void line() throws IOException {
		writeInfo.println("off");
		writeInfo.flush();
		String name = readInfo.nextLine();
		int i;
		for(i = 0 ; i < ChatServer.users.size() ; i++){
			if(name.equals(ChatServer.users.elementAt(i).name)){
				ChatServer.users.elementAt(i).sit = false;
				break;
			}
		}
		
		ChatServer.setting(i);
		writeInfo.close();
		readInfo.close();
		socket.close();
	}




	private void message() {
		String request = readInfo.nextLine();
		for(int i = 0 ; i < ChatServer.users.size() ; i++){
			if(request.equals(ChatServer.users.elementAt(i).name)){
				ChatServer.users.elementAt(i).writeInfo.println("message");
				ChatServer.users.elementAt(i).writeInfo.flush();
				ChatServer.users.elementAt(i).writeInfo.println(readInfo.nextLine());
				ChatServer.users.elementAt(i).writeInfo.flush();
				ChatServer.users.elementAt(i).writeInfo.println(readInfo.nextLine());
				ChatServer.users.elementAt(i).writeInfo.flush();
			}
		}
	}




	private void lineStiation() {
		String request = readInfo.nextLine();
		
		for(int i = 0 ; i < ChatServer.users.size() ; i++){
			if(request.equals(ChatServer.users.elementAt(i).name)){
				writeInfo.println(ChatServer.users.elementAt(i).sit);
			}
		}
	}




	private void contacts() {
		for(int i = 0 ; i < ChatServer.users.size() ; i++){
			writeInfo.println(ChatServer.users.elementAt(i).name);
			writeInfo.flush();
			
			if(ChatServer.users.elementAt(i).sit == false){
				writeInfo.println("off");
				writeInfo.flush();
			}
			
			else{
				writeInfo.println("on");
				writeInfo.flush();
			}
		}
		
		writeInfo.println("end");
		writeInfo.flush();
	}
}
