//
// VisBioFrame.java
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

package loci.visbio;

import com.jgoodies.plaf.LookUtils;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Vector;
import javax.swing.*;
import loci.formats.ReflectedUniverse;
import loci.visbio.data.DataManager;
import loci.visbio.data.DataTransform;
import loci.visbio.ext.ExtManager;
import loci.visbio.help.HelpManager;
import loci.visbio.overlays.OverlayManager;
import loci.visbio.ome.OMEImage;
import loci.visbio.ome.OMEManager;
import loci.visbio.state.OptionManager;
import loci.visbio.state.StateManager;
import loci.visbio.util.*;
import loci.visbio.view.DisplayManager;
import visad.util.GUIFrame;
import visad.util.Util;

/** VisBioFrame is the main GUI frame for VisBio. */
public class VisBioFrame extends GUIFrame implements Runnable, SpawnListener {

  // -- Constants --

  /** Debugging flag for event logic. */
  public static final boolean DEBUG =
    "true".equalsIgnoreCase(System.getProperty("visbio.debug"));

  // -- Static fields --

  /** Running instance of VisBio. */
  protected static VisBioFrame visbio;

  // -- Fields --

  /** Logic managers. */
  protected Vector managers;

  /** Associated splash screen. */
  protected SplashScreen splash;

  /** VisBio program icon. */
  protected Image icon;

  /** Instance server for this instance of VisBio. */
  protected InstanceServer instanceServer;

  // -- Constructor --

  /** Constructs a new VisBio frame with no splash screen. */
  public VisBioFrame() { this(null, null); }

  /** Constructs a new VisBio frame with the associated splash screen. */
  public VisBioFrame(SplashScreen splash) { this(splash, null); }

  /**
   * Constructs a new VisBio frame with the associated splash screen
   * and command line arguments.
   */
  public VisBioFrame(SplashScreen splash, String[] args) {
    super(true);
    if (visbio != null) {
      throw new RuntimeException("Only once instance of VisBio is allowed");
    }
    visbio = this;
    try {
      // initialize server for responding to newly spawned instances
      try {
        instanceServer = new InstanceServer(VisBio.INSTANCE_PORT);
        instanceServer.addSpawnListener(this);
      }
      catch (IOException exc) {
        System.err.println("Warning: could not initialize instance server " +
          "on port " + VisBio.INSTANCE_PORT + ". VisBio will not be able " +
          "to regulate multiple instances of itself. Details follow:");
        exc.printStackTrace();
      }

      setTitle(VisBio.TITLE);
      managers = new Vector();
      this.splash = splash;

      // initialize Look & Feel parameters
      LAFUtil.initLookAndFeel();

      // load program icon
      URL urlIcon = getClass().getResource("visbio-icon.gif");
      if (urlIcon != null) icon = new ImageIcon(urlIcon).getImage();
      if (icon != null) setIconImage(icon);

      // set system properties
      System.setProperty("plugins.dir", ".");

      // make menus appear in the right order
      getMenu("File");
      getMenu("Edit").setEnabled(false); // CTR TODO fix state logic
      getMenu("Window");
      getMenu("Help");

      // create logic managers
      OptionManager om = new OptionManager(this);
      StateManager sm = new StateManager(this);
      WindowManager wm = new WindowManager(this);
      sm.setRestoring(true);
      LogicManager[] lm = {
        sm, // StateManager
        om, // OptionManager
        wm, // WindowManager
        new HelpManager(this),
        new PanelManager(this),
        new DataManager(this),
        new ExtManager(this),
        new OMEManager(this),
        new DisplayManager(this),
        new OverlayManager(this),
        new TaskManager(this),
        new SystemManager(this),
        new ConsoleManager(this),
        new ExitManager(this)
      };
      int tasks = 1;
      for (int i=0; i<lm.length; i++) tasks += lm[i].getTasks();
      if (splash != null) splash.setTaskCount(tasks);

      // add logic managers
      for (int i=0; i<lm.length; i++) addManager(lm[i]);
      setSplashStatus("Finishing");

      // read configuration file
      om.readIni();

      // handle Mac OS X application menu items
      if (LookUtils.IS_OS_MAC) {
        ReflectedUniverse r = new ReflectedUniverse();
        r.setDebug(true);
        r.exec("import loci.visbio.MacAdapter");
        r.setVar("bio", this);
        r.exec("MacAdapter.link(bio)");
      }

      // distribute menu bar across all frames
      if (LAFUtil.isMacLookAndFeel()) wm.setDistributedMenus(true);

      // show VisBio window onscreen
      SwingUtil.pack(this);
      Util.centerWindow(this);
      setVisible(true);

      // hide splash screen
      if (splash != null) {
        int task = splash.getTask();
        if (task != tasks) {
          System.out.println("Warning: completed " +
            task + "/" + tasks + " initialization tasks");
        }
        splash.setVisible(false);
        splash = null;
      }

      // preload classes
      if (true) {
        new Thread(this).start();
      }

      // determine if VisBio crashed last time
      sm.setRestoring(false);
      /* CTR TODO fix state logic
      sm.checkCrash();
      */

      // process arguments
      processArguments(args);
    }
    catch (Throwable t) {
      // dump stack trace to a string
      StringWriter sw = new StringWriter();
      t.printStackTrace(new PrintWriter(sw));

      // save stack trace to errors.log file
      try {
        PrintWriter fout = new PrintWriter(new FileWriter("errors.log"));
        fout.println(sw.toString());
        fout.close();
      }
      catch (IOException exc) { }

      // apologize to the user
      if (splash != null) splash.setVisible(false);
      JOptionPane.showMessageDialog(this,
        "Sorry, there has been a problem launching VisBio.\n\n" +
        sw.toString() +
        "\nA copy of this information has been saved to the errors.log file.",
        "VisBio", JOptionPane.ERROR_MESSAGE);

      // quit program
      ExitManager xm = (ExitManager) getManager(ExitManager.class);
      if (xm != null) xm.fileExit();
      else System.exit(0);
    }
  }

  // -- Static VisBioFrame API methods --

  /** Gets the running instance of VisBio. */
  public static VisBioFrame getVisBio() {
    return visbio;
  }

  // -- VisBioFrame API methods --

  /** Updates the splash screen to report the given status message. */
  public void setSplashStatus(String s) {
    if (splash == null) return;
    if (s != null) splash.setText(s + "...");
    splash.nextTask();
  }

  /** Adds a logic manager to the VisBio interface. */
  public void addManager(LogicManager lm) {
    managers.add(lm);
    generateEvent(new VisBioEvent(lm, VisBioEvent.LOGIC_ADDED, null, false));
  }

  /** Gets the logic manager of the given class. */
  public LogicManager getManager(Class c) {
    for (int i=0; i<managers.size(); i++) {
      LogicManager lm = (LogicManager) managers.elementAt(i);
      if (lm.getClass().equals(c)) return lm;
    }
    return null;
  }

  /** Gets all logic managers. */
  public LogicManager[] getManagers() {
    LogicManager[] lm = new LogicManager[managers.size()];
    managers.copyInto(lm);
    return lm;
  }

  /** Generates a state change event. */
  public void generateEvent(Object src, String msg, boolean undo) {
    generateEvent(new VisBioEvent(src, VisBioEvent.STATE_CHANGED, msg, undo));
  }

  /** Generates an event and notifies all linked logic managers. */
  public void generateEvent(VisBioEvent evt) {
    if (DEBUG) System.out.println(evt.toString());
    for (int i=0; i<managers.size(); i++) {
      LogicManager lm = (LogicManager) managers.elementAt(i);
      lm.doEvent(evt);
    }
  }

  /** Processes the given list of arguments. */
  public void processArguments(String[] args) {
    if (args == null) args = new String[0];
    for (int i=0; i<args.length; i++) {
      if (DEBUG) System.out.println("Argument #" + (i + 1) + "] = " + args[i]);
      int equals = args[i].indexOf("=");
      if (equals < 0) continue;
      String key = args[i].substring(0, equals);
      String value = args[i].substring(equals + 1);
      processArgument(key, value);
    }
  }

  /** Processes the given argument key/value pair. */
  public void processArgument(String key, String value) {
    if (key == null || value == null) return;
    if (DEBUG) {
      System.out.println("Processing argument: " + key + " = " + value);
    }
    key = key.toLowerCase();
    if (key.equals("ome-image")) {
      // value syntax: key:sessionKey@server:imageId
      //   or:         user:username@server:imageId
      //   or:         username@server:imageId
      int index = value.lastIndexOf("@");
      if (index < 0) return;
      String prefix = value.substring(0, index);
      String username = null, sessionKey = null;
      if (prefix.startsWith("key:")) sessionKey = prefix.substring(4);
      else if (prefix.startsWith("user:")) username = prefix.substring(5);
      else username = prefix; // assume prefix is a username
      value = value.substring(index + 1);
      index = value.indexOf(":");
      String server = value.substring(0, index);
      int imageId = -1;
      try { imageId = Integer.parseInt(value.substring(index + 1)); }
      catch (NumberFormatException exc) { }
      if (imageId < 0) return;

      // construct OME image object
      DataManager dm = (DataManager) getManager(DataManager.class);
      DataTransform data =
        OMEImage.makeTransform(dm, server, sessionKey, username, imageId);
      if (data != null) dm.addData(data);
    }
  }

  /**
   * This method executes action commands of the form
   *
   *   "loci.visbio.data.DataManager.importData(param)"
   *
   * by stripping off the fully qualified class name, checking VisBio's
   * list of logic managers for one of that class, and calling the given
   * method on that object, optionally with the given String parameter.
   */
  public void call(String cmd) {
    try {
      // determine class from fully qualified name
      int dot = cmd.lastIndexOf(".");
      Class c = Class.forName(cmd.substring(0, dot));

      // determine if action command has an argument
      Class[] param = null;
      Object[] s = null;
      int paren = cmd.indexOf("(");
      if (paren >= 0) {
        param = new Class[] {String.class};
        s = new String[] {cmd.substring(paren + 1, cmd.indexOf(")", paren))};
      }
      else paren = cmd.length();

      // execute appropriate method of that logic manager
      LogicManager lm = getManager(c);
      if (lm != null) {
        Method method = c.getMethod(cmd.substring(dot + 1, paren), param);
        if (method != null) method.invoke(lm, s);
      }
    }
    catch (ClassNotFoundException exc) { exc.printStackTrace(); }
    catch (IllegalAccessException exc) { exc.printStackTrace(); }
    catch (IllegalArgumentException exc) { exc.printStackTrace(); }
    catch (InvocationTargetException exc) {
      Throwable t = exc.getTargetException();
      if (t == null) exc.printStackTrace();
      else t.printStackTrace();
    }
    catch (NoSuchMethodException exc) { exc.printStackTrace(); }
  }

  /** Gets VisBio program icon. */
  public Image getIcon() { return icon; }

  /**
   * Cleans up and releases resources before
   * abandoning this instance of VisBio.
   */
  public void destroy() {
    // the following doesn't fully clean up (maybe because of Java3D?)
    WindowManager wm = (WindowManager) getManager(WindowManager.class);
    wm.hideWindows();
    setVisible(false);
    wm.disposeWindows();
    StateManager sm = (StateManager) getManager(StateManager.class);
    sm.destroy();
    instanceServer.stop();
    dispose();
  }

  // -- ActionListener API methods --

  /**
   * Internal method enabling logic managers to access
   * their own methods via VisBio's menu system.
   */
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if (cmd.lastIndexOf(".") < 0) super.actionPerformed(e);
    else call(cmd);
  }

  // -- Runnable API methods --

  /** Preloads relevant classes. */
  public void run() {
    TaskManager tm = (TaskManager) getManager(TaskManager.class);
    BioTask task = tm.createTask("Preload classes");

    // extract classes to preload from data file
    task.setStatus("Reading classes list");
    Vector preloadClasses = new Vector();
    try {
      InputStream rc = getClass().getResourceAsStream("classes.txt");
      if (rc != null) {
        BufferedReader fin = new BufferedReader(new InputStreamReader(rc));
        while (true) {
          String line = fin.readLine();
          if (line == null) break; // eof
          preloadClasses.add(line);
        }
        fin.close();
      }
    }
    catch (IOException exc) { } // ignore data file I/O errors

    // preload classes
    int size = preloadClasses.size();
    String pkg = "";
    for (int i=0; i<size; i++) {
      String className = (String) preloadClasses.elementAt(i);
      int dot = className.lastIndexOf(".");
      String prefix = className.substring(0, dot);
      if (!prefix.equals(pkg)) {
        pkg = prefix;
        task.setStatus(i, size, pkg);
      }
      else task.setStatus(i, size);

      // preload class, ignoring errors
      try { Class.forName(className); }
      catch (Throwable t) { }
    }
    task.setCompleted();
  }

  // -- SpawnListener API methods --

  /** Responds when new instances of VisBio are spawned. */
  public void instanceSpawned(SpawnEvent e) {
    processArguments(e.getArguments());
  }

}
