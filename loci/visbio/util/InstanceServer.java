//
// InstanceServer.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-@year@ Curtis Rueden.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.visbio.util;

import java.net.*;
import java.io.*;
import java.util.Vector;

/**
 * The InstanceServer provides a mechanism for managing the instances of an
 * application running concurrently. The application should attempt to send any
 * desired parameters to an already active instance server using the static
 * sendArguments method.
 *
 * If successful (no exception thrown), the application should then shut down,
 * having successfully passed its arguments to the active application instance.
 *
 * If unsuccessful (caught an exception), the application should create an
 * InstanceServer, thus becoming the active application instance.
 */
public class InstanceServer implements Runnable {

  // -- Fields --

  /** Server socket listening for client connections. */
  protected ServerSocket serverSocket;

  /** List of application instance spawn listeners. */
  protected Vector listeners;

  /** Whether this instance server is still listening for spawn events. */
  protected boolean alive = true;

  // -- Static InstanceServer API methods --

  /**
   * Attempts to send the given arguments to an instance server running on the
   * specified port. Returns true if successful, indicating the application
   * should shut down since an instance is already running and has received the
   * parameters. Returns false if unsuccessful, indicating the application
   * should create its own InstanceServer and become the active application
   * instance.
   */
  public static void sendArguments(String[] args, int port)
    throws IOException
  {
    Socket socket = new Socket("localhost", port);
    PrintWriter out = new PrintWriter(
      new OutputStreamWriter(socket.getOutputStream()));
    out.println(args == null ? -1 : args.length);
    for (int i=0; i<args.length; i++) out.println(args[i]);
    out.close();
    socket.close();
  }

  // -- Constructor --

  /** Creates a new instance server on the given port. */
  public InstanceServer(int port) throws IOException {
    serverSocket = new ServerSocket(port, 1);
    listeners = new Vector();
    new Thread(this, "InstanceServer").start();
  }

  // -- InstanceServer API methods --

  /** Adds an application instance spawn listener. */
  public void addSpawnListener(SpawnListener l) {
    synchronized (listeners) { listeners.addElement(l); }
  }

  /** Removes an application instance spawn listener. */
  public void removeSpawnListener(SpawnListener l) {
    synchronized (listeners) { listeners.removeElement(l); }
  }

  /** Removes all application instance spawn listeners. */
  public void removeAllListeners() {
    synchronized (listeners) { listeners.removeAllElements(); }
  }

  /** Stops this instance server's thread. */
  public void stop() {
    alive = false;
    try { serverSocket.close(); }
    catch (IOException exc) { exc.printStackTrace(); }
  }

  // -- Runnable API methods --

  /**
   * Listens for connections from newly spawned application instances and
   * passes their arguments to all registered listeners.
   */
  public void run() {
    while (alive) {
      try {
        Socket socket = serverSocket.accept();
        BufferedReader in = new BufferedReader(
          new InputStreamReader(socket.getInputStream()));
        int numArgs = -1;
        try {
          String line = in.readLine();
          if (line != null) numArgs = Integer.parseInt(line);
        }
        catch (NumberFormatException exc) { }
        String[] args = numArgs < 0 ? null : new String[numArgs];
        for (int i=0; i<numArgs; i++) args[i] = in.readLine();
        in.close();
        socket.close();
        notifyListeners(new SpawnEvent(args));
      }
      catch (IOException exc) {
        if (alive) exc.printStackTrace();
      }
    }
  }

  // -- Helper methods --

  /**
   * Notifies application instance spawn listeners
   * of a newly spawned application instance.
   */
  protected void notifyListeners(SpawnEvent e) {
    synchronized (listeners) {
      for (int i=0; i<listeners.size(); i++) {
        SpawnListener l = (SpawnListener) listeners.elementAt(i);
        l.instanceSpawned(e);
      }
    }
  }

}
