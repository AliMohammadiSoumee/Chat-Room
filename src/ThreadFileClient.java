import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ThreadFileClient extends Thread{
	File file;
	PrintWriter writeInfo;
	Scanner readInfo;
	Socket socket;
	String fileName;
	
	
	
	
	public ThreadFileClient(Socket s, String str, String n) throws IOException{
		socket = s;
		writeInfo = new PrintWriter(socket.getOutputStream(), true);
		readInfo = new Scanner(socket.getInputStream());
		file = new File(str);
		fileName = n;
	}

	
	
	
	public void run() {
		writeInfo.println("get file");
		writeInfo.flush();
		writeInfo.println(fileName);
		writeInfo.flush();
		
		try {
			InputStream in = socket.getInputStream();
			FileOutputStream out = new FileOutputStream(file);
			byte[] b = new byte[1024];
			
			sleep(2000);
		
			while(in.available() > 1024){
				in.read(b);
				out.write(b);
				out.flush();
			}
			
			b = new byte[in.available()];
			in.read(b);
			out.write(b);
			out.flush();
		} 

		catch (IOException e) {
			e.printStackTrace();
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
