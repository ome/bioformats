//
// Util.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the 4D
Data Browser, OME Plugin and Bio-Formats Exporter. Copyright (C) 2006
Melissa Linkert, Christopher Peterson, Curtis Rueden, Philip Huettl
and Francis Wong.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.plugins;

import ij.IJ;
import java.util.HashSet;
import java.util.Iterator;

/** Utility methods common to multiple plugins. */
public final class Util {

  private Util() { }

  /** Checks for a new enough version of the Java Runtime Environment. */
  public static boolean checkVersion() {
    String version = System.getProperty("java.version");
    double ver = Double.parseDouble(version.substring(0, 3));
    if (ver < 1.4) {
      IJ.error("LOCI Plugins",
        "Sorry, the LOCI Plugins require Java 1.4 or later." +
        "\nYou can download ImageJ with JRE 5.0 from the ImageJ web site.");
      return false;
    }
    return true;
  }

  /**
   * Checks for libraries required by the LOCI plugins.
   * @return true if libraries are present, false if some are missing.
   */
  public static boolean checkLibraries(boolean bioFormats,
    boolean omeJavaXML, boolean omeJavaDS)
  {
    HashSet hs = new HashSet();
    if (bioFormats) {
      checkLibrary("loci.formats.FormatHandler", "bio-formats.jar", hs);
      checkLibrary("org.apache.poi.poifs.filesystem.POIFSFileSystem",
        "poi-loci.jar", hs);
    }
    if (omeJavaXML) {
      checkLibrary("org.openmicroscopy.xml.OMENode", "ome-java.jar", hs);
    }
    if (omeJavaDS) {
      checkLibrary("org.openmicroscopy.ds.DataServer", "ome-java.jar", hs);
      checkLibrary("org.apache.xmlrpc.XmlRpcClient", "xmlrpc-1.2-b1.jar", hs);
      checkLibrary("org.apache.commons.httpclient.HttpClient",
        "commons-httpclient-2.0-rc2.jar", hs);
      checkLibrary("org.apache.commons.logging.Log", "commons-logging.jar", hs);
    }
    int missing = hs.size();
    if (missing > 0) {
      StringBuffer sb = new StringBuffer();
      sb.append("The following librar");
      sb.append(missing == 1 ? "y was" : "ies were");
      sb.append(" not found:");
      Iterator iter = hs.iterator();
      for (int i=0; i<missing; i++) {
        sb.append("\n    " + iter.next());
      }
      String them = missing == 1 ? "it" : "them";
      sb.append("\nPlease download ");
      sb.append(them);
      sb.append(" from the LOCI website");
      sb.append("\nand place ");
      sb.append(them);
      sb.append(" in the ImageJ plugins folder.");
      IJ.error("LOCI Plugins", sb.toString());
      return false;
    }
    return true;
  }

  private static void checkLibrary(String className,
    String jarFile, HashSet hs)
  {
    try { Class.forName(className); }
    catch (Throwable t) { hs.add(jarFile); }
  }

}
