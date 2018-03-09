import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ThreadFileServer extends Thread{
	Socket socket;
	PrintWriter writeInfo;
	Scanner readInfo;
	File file;
	
	
	
	
	public ThreadFileServer(Socket s) throws IOException{
		socket = s;
		writeInfo = new PrintWriter(socket.getOutputStream(), true);
		readInfo = new Scanner(socket.getInputStream());
	}
	
	public void run() {
		while(true){
			String situation = readInfo.nextLine();
			
			if(situation.equals("get list of files")){
				getListOfFiles();
			}
			
			if(situation.equals("get file")){
				try {
					getFiles();
				} 
				catch (FileNotFoundException e) {
					e.printStackTrace();
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(situation.equals("line")){
				try {
					socket.close();
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}
	
	
	
	
	private void getFiles() throws IOException{
		String nameOfFile = readInfo.nextLine();
		
		for(int i = 0 ; i < FileServer.files.length ; i++){
			if(nameOfFile.equals(FileServer.files[i].getName())){
				FileInputStream in = new FileInputStream(FileServer.files[i]);
				OutputStream out = socket.getOutputStream();
				byte[] b = new byte[1024];
				
				while(in.available() > 1024){
					in.read(b);
					out.write(b);
					out.flush();
				}

				int rest = in.available();
				b = new byte[rest];
				in.read(b);
				out.write(b);
				out.flush();
			}
		}		
	}
	
	
	
	
	private void getListOfFiles(){		
		for(int i = 0; i < FileServer.files.length; i++){
			writeInfo.println(FileServer.files[i].getName());
			writeInfo.flush();
		}
		
		writeInfo.println("end");
		writeInfo.flush();
	}
}
