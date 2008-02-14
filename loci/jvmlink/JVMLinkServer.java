//
// JVMLinkServer.java
//

/*
JVMLink package for communicating between Java and non-Java programs via IP.
Copyright (c) 2008 Hidayath Ansari and Curtis Rueden. All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
  * Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
  * Neither the name of the UW-Madison LOCI nor the names of its
    contributors may be used to endorse or promote products derived from
    this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE UW-MADISON LOCI ``AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package loci.jvmlink;

import java.io.*;
import java.net.*;
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
