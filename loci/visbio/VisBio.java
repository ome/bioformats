//
// VisBio.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2004 Curtis Rueden.

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

import java.awt.Color;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import loci.visbio.util.InstanceServer;
import loci.visbio.util.SplashScreen;

/**
 * VisBio is a biological visualization tool designed for easy
 * visualization and analysis of multidimensional image data.
 *
 * This class is the main gateway into the application. It creates and displays
 * a VisBioFrame via reflection, so that the splash screen appears as quickly
 * as possible, before the class loader gets too far along.
 */
public class VisBio {

  // -- Constants --

  /** Application title. */
  public static final String TITLE = "VisBio";

  /** Application version. */
  public static final String VERSION = "v3.00 beta5g";

  /** Application author. */
  public static final String AUTHOR = "Curtis Rueden, LOCI";

  /** Application build date. */
  public static final String DATE = "26 May 2005";

  /** Port to use for communicating between application instances. */
  public static final int INSTANCE_PORT = 0xabcd;


  // -- Constructor --

  /** Ensure this class can't be instantiated. */
  private VisBio() { }


  // -- Main --

  /** Launches the VisBio GUI. */
  public static void main(String[] args)
    throws ClassNotFoundException, IllegalAccessException,
    InstantiationException, InvocationTargetException, NoSuchMethodException
  {
    // check whether VisBio is already running
    boolean isRunning = true;
    try { InstanceServer.sendArguments(args, INSTANCE_PORT); }
    catch (IOException exc) { isRunning = false; }
    if (isRunning) System.exit(0);

    // display splash screen
    String[] msg = {
      TITLE + " " + VERSION + " - " + AUTHOR,
      "VisBio is starting up..."
    };
    SplashScreen ss = new SplashScreen(
      VisBio.class.getResource("visbio-logo.png"),
      msg, new Color(255, 255, 220), new Color(255, 50, 50));
    ss.show();

    // toggle window decoration mode via reflection
    boolean b = "true".equals(System.getProperty("visbio.decorateWindows"));
    Class jf = Class.forName("javax.swing.JFrame");
    Method m = jf.getMethod("setDefaultLookAndFeelDecorated",
      new Class[] {boolean.class});
    m.invoke(null, new Object[] {new Boolean(b)});

    // construct VisBio interface via reflection
    Class vb = Class.forName("loci.visbio.VisBioFrame");
    Constructor con = vb.getConstructor(new Class[] {
      ss.getClass(), String[].class
    });
    con.newInstance(new Object[] {ss, args});
  }

}
