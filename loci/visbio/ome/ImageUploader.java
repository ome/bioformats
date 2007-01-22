//
// ImageUploader.java
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

package loci.visbio.ome;

import java.io.*;
import java.util.*;
import loci.formats.FilePattern;
import loci.ome.upload.OMEUploader;
import loci.visbio.*;

/**
 * ImageUploader is a helper class for uploading VisBio datasets
 * (OME images) to the Open Microscopy Environment.
 */
public class ImageUploader {

  // -- Fields --

  /** List of objects listening for updates to upload tasks. */
  protected Vector listeners;

  // -- Constructor --

  /** Constructs a new OME image uploader. */
  public ImageUploader() { listeners = new Vector(); }

  // -- ImageUploader API methods --

  /**
   * Uploads the given VisBio dataset (OME image) to the specified
   * OME server, using the given username and password.
   */
  public void upload(loci.visbio.data.Dataset data,
    String server, String username, String password)
  {
    try {  
      OMEUploader ul = new OMEUploader(server, username, password);
      String[] ids = data.getFilenames();
      for (int i=0; i<ids.length; i++) {
        ul.uploadFile(ids[0], true);
      }
    }
    catch (Exception exc) {
      notifyListeners(new TaskEvent(1, 1,
        "Error uploading (see error console for details)"));
      exc.printStackTrace();
    }
  }

  /** Adds an upload task listener. */
  public void addTaskListener(TaskListener l) {
    synchronized (listeners) { listeners.addElement(l); }
  }

  /** Removes an upload task listener. */
  public void removeTaskListener(TaskListener l) {
    synchronized (listeners) { listeners.removeElement(l); }
  }

  /** Removes all upload task listeners. */
  public void removeAllTaskListeners() {
    synchronized (listeners) { listeners.removeAllElements(); }
  }

  /** Notifies listeners of an upload task update. */
  protected void notifyListeners(TaskEvent e) {
    synchronized (listeners) {
      for (int i=0; i<listeners.size(); i++) {
        TaskListener l = (TaskListener) listeners.elementAt(i);
        l.taskUpdated(e);
      }
    }
  }

  // -- Main method --

  /**
   * A command-line tool for uploading data to an
   * OME server using client-side Java tools.
   */
  public static void main(String[] args) {
    String server = null, user = null, pass = null;
    Vector files = new Vector();

    // parse command-line arguments
    boolean doUsage = false;
    if (args.length == 0) doUsage = true;
    for (int i=0; i<args.length; i++) {
      if (args[i].startsWith("-")) {
        // argument is a command line flag
        String param = args[i];
        try {
          if (param.equalsIgnoreCase("-s")) server = args[++i];
          else if (param.equalsIgnoreCase("-u")) user = args[++i];
          else if (param.equalsIgnoreCase("-p")) pass = args[++i];
          else if (param.equalsIgnoreCase("-h") ||
            param.equalsIgnoreCase("-?"))
          {
            doUsage = true;
          }
          else {
            System.out.println("Error: unknown flag: " + param);
            System.out.println();
            doUsage = true;
            break;
          }
        }
        catch (ArrayIndexOutOfBoundsException exc) {
          if (i == args.length - 1) {
            System.out.println("Error: flag " + param +
              " must be followed by a parameter value.");
            System.out.println();
            doUsage = true;
            break;
          }
          else throw exc;
        }
      }
      else {
        files.add(args[i]);
      }
    }
    if (doUsage) {
      System.out.println("Usage: omeul [-s server.address] " +
        "[-u username] [-p password] filename");
      System.out.println();
      System.exit(1);
    }

    // ask for information if necessary
    BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));
    if (server == null) {
      System.out.print("Server address? ");
      try { server = cin.readLine(); }
      catch (IOException exc) { }
    }
    if (user == null) {
      System.out.print("Username? ");
      try { user = cin.readLine(); }
      catch (IOException exc) { }
    }
    if (pass == null) {
      System.out.print("Password? ");
      try { pass = cin.readLine(); }
      catch (IOException exc) { }
    }

    if (server == null || user == null || pass == null) {
      System.out.println("Error: could not obtain server login information");
      System.exit(2);
    }
    System.out.println("Using server " + server + " as user " + user);

    // create image uploader
    ImageUploader uploader = new ImageUploader();
    uploader.addTaskListener(new TaskListener() {
      public void taskUpdated(TaskEvent e) {
        System.out.println(e.getStatusMessage());
      }
    });

    for (int i=0; i<files.size(); i++) {
      FilePattern fp = new FilePattern((String) files.get(i));
      int[] lengths = fp.getCount();
      if (lengths.length == 0) {
        lengths = new int[1];
        lengths[0] = 1;
      }
      
      loci.visbio.data.Dataset data = new loci.visbio.data.Dataset(
        (String) files.get(i), fp.getPattern(), fp.getFiles(), lengths, 
        new String[lengths.length]);
      uploader.upload(data, server, user, pass);
    }
  }

}
