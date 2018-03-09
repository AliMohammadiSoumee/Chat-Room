import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ThreadFile extends Thread{
	ServerSocket listener;
	PrintWriter writeInfo;
	Scanner readInfo;
	Socket socket;
	
	public ThreadFile() throws IOException{
		listener = new ServerSocket(8989);
	}
	

	
	
	public void run() {
		while(true){
			try {
				socket = listener.accept();
				writeInfo = new PrintWriter(socket.getOutputStream(), true);
				readInfo = new Scanner(socket.getInputStream());
				
				ThreadFileServer th = new ThreadFileServer(socket);
				th.start();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
