package loci.jvmlink;
import java.net.*;
import java.io.*;

import loci.formats.ReflectException;
import loci.formats.ReflectedUniverse;


public class JVMLinkServer {

	protected int port;
	private static final int DEFAULT_PORT = 20345;
	ReflectedUniverse globalR; //make this private?
	int threadNumber;
	ServerSocket listener; //dirty. find another way to close the server from the thread.
						   //the problem is that listener.accept() is blocking the listen function.
	
	public JVMLinkServer(int port) {
		this.port = port;
		globalR = new ReflectedUniverse();
		threadNumber=0;
	} 
	
	public static void main(String[] args) throws ReflectException {
		int port;
		if (args.length > 0) port = Integer.parseInt(args[0]);
		else port = DEFAULT_PORT;
		
		JVMLinkServer ns = new JVMLinkServer(port);
		System.out.println("Server started ..");
		ns.listen();
	}

	
	public void listen() {
		try {
			listener = new ServerSocket(port);
			Socket server;
			while (true) {
				server = listener.accept();
				threadNumber++;
				System.out.println("Server got a connection from " + server.getInetAddress().getHostName()+". Creating thread number "+threadNumber);
				new ConnThread(server, threadNumber, this);
				//handleConnection(server);
			}
		} catch (IOException ioe) {
			System.exit(0);
		} catch (Exception e) {
			System.out.println("Exception: " + e);
			e.printStackTrace();
		}
		
	}
	
	public void shutServer() throws IOException {
		listener.close();
		//System.exit(0);
	}
}