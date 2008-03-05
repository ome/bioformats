//
// JVMLinkServer.java
//

/*
JVMLink client/server architecture for communicating between Java and
non-Java programs using sockets.
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

import java.io.IOException;
import java.net.*;

/**
 * Java server for JVMLink. Clients can connect to the server and issue
 * arbitrary Java commands according to the legal syntax of the
 * {@link loci.formats.ReflectedUniverse} class.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/jvmlink/JVMLinkServer.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/jvmlink/JVMLinkServer.java">SVN</a></dd></dl>
 */
public class JVMLinkServer implements Runnable {

  // -- Constants --

  private static final int DEFAULT_PORT = 20345;
  private static final int MIN_PORT = 1024;
  private static final int MAX_PORT = 65535;
  private static final int TIMEOUT = 500;

  // -- Static fields --

  /** Debugging flag. */
  protected static boolean debug = false;

  // -- Fields --

  private int port;
  private boolean alive = true;
  private ServerSocket listener;

  // -- Constructor --

  public JVMLinkServer(int port) {
    this.port = port;
  }

  // -- JVMLinkServer API methods --

  /** Orders the server to begin listening for incoming connections. */
  public void listen() throws IOException {
    if (listener == null) {
      listener = new ServerSocket(port);
      listener.setSoTimeout(TIMEOUT);
      new Thread(this, "JVMLink-Listener").start();
    }
  }

  /** Orders the server to terminate. */
  public void shutServer() throws IOException {
    debug("Shutting down");
    alive = false;
    listener.close();
  }

  // -- Runnable API methods --

  /** Continually listens for incoming client connections. */
  public void run() {
    System.out.println("JVMLink server started on port " + port);
    while (alive) {
      try {
        Socket socket = listener.accept();
        debug("Got a connection from " +
          socket.getInetAddress().getHostName());
        new ConnThread(socket, this);
      }
      catch (SocketException exc) {
        // socket was probably closed; terminate server
        debug("Server terminated");
        break;
      }
      catch (SocketTimeoutException exc) {
        //if (debug) System.out.print(".");
      }
      catch (IOException exc) {
        if (debug) exc.printStackTrace();
        try {
          Thread.sleep(100);
        }
        catch (InterruptedException exc2) {
          if (debug) exc2.printStackTrace();
        }
      }
    }
  }

  // -- Helper methods --

  private void debug(String msg) {
    if (debug) System.out.println("JVMLinkServer: " + msg);
  }

  // -- Main method --

  public static void main(String[] args) throws IOException {
    int port = DEFAULT_PORT;
    for (int i=0; i<args.length; i++) {
      if ("-debug".equals(args[i])) debug = true;
      else {
        try {
          int num = Integer.parseInt(args[i]);
          if (num < MIN_PORT || num > MAX_PORT) {
            System.out.println("Invalid port: " + num);
          }
          else port = num;
        }
        catch (NumberFormatException exc) {
          System.out.println("Unknown parameter: " + args[i]);
        }
      }
    }
    JVMLinkServer ns = new JVMLinkServer(port);
    ns.listen();
  }

}
