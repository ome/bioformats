//
// Checker.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
4D Data Browser, Bio-Formats Importer, Bio-Formats Exporter and OME plugins.
Copyright (C) 2005-@year@ Melissa Linkert, Christopher Peterson,
Curtis Rueden, Philip Huettl and Francis Wong.

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

/**
 * Utility methods for verifying that classes
 * are present and versions are correct.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/plugins/Checker.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/plugins/Checker.java">SVN</a></dd></dl>
 */
public final class Checker {

  // -- Constants --

  /** Identifier for checking the Bio-Formats library is present. */
  public static final int BIO_FORMATS = 1;

  /** Identifier for checking the OME Java OME-XML library is present. */
  public static final int OME_JAVA_XML = 2;

  /** Identifier for checking the OME Java OMEDS library is present. */
  public static final int OME_JAVA_DS = 3;

  /** Identifier for checking the JGoodies Forms library is present. */
  public static final int FORMS = 4;

  // -- Constructor --

  private Checker() { }

  // -- Utility methods --

  /** Checks whether the given class is available. */
  public static boolean checkClass(String className) {
    try { Class.forName(className); }
    catch (Throwable t) { return false; }
    return true;
  }

  /**
   * Checks for a required library.
   * @param library One of:<ul>
   *   <li>BIO_FORMATS</li>
   *   <li>OME_JAVA_XML</li>
   *   <li>OME_JAVA_DS</li>
   *   <li>FORMS</li>
   *   </ul>
   */
  public static void checkLibrary(int library, HashSet missing) {
    switch (library) {
      case BIO_FORMATS:
        checkLibrary("loci.formats.FormatHandler", "bio-formats.jar", missing);
        checkLibrary("org.apache.poi.poifs.filesystem.POIFSFileSystem",
          "poi-loci.jar", missing);
        break;
      case OME_JAVA_XML:
        checkLibrary("org.openmicroscopy.xml.OMENode", "ome-java.jar", missing);
        break;
      case OME_JAVA_DS:
        checkLibrary("org.openmicroscopy.ds.DataServer",
          "ome-java.jar", missing);
        checkLibrary("org.apache.xmlrpc.XmlRpcClient",
          "xmlrpc-1.2-b1.jar", missing);
        checkLibrary("org.apache.commons.httpclient.HttpClient",
          "commons-httpclient-2.0-rc2.jar", missing);
        checkLibrary("org.apache.commons.logging.Log",
          "commons-logging.jar", missing);
        break;
      case FORMS:
        checkLibrary("com.jgoodies.forms.layout.FormLayout",
          "forms-1.0.4.jar", missing);
        break;
    }
  }

  /**
   * Checks whether the given class is available; if not,
   * adds the specified JAR file name to the hash set
   * (presumably to report it missing to the user).
   */
  public static void checkLibrary(String className,
    String jarFile, HashSet missing)
  {
    if (!checkClass(className)) missing.add(jarFile);
  }

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
   * Reports missing libraries in the given hash set to the user.
   * @return true iff no libraries are missing (the hash set is empty).
   */
  public static boolean checkMissing(HashSet missing) {
    int num = missing.size();
    if (num == 0) return true;
    StringBuffer sb = new StringBuffer();
    sb.append("The following librar");
    sb.append(num == 1 ? "y was" : "ies were");
    sb.append(" not found:");
    Iterator iter = missing.iterator();
    for (int i=0; i<num; i++) sb.append("\n    " + iter.next());
    String them = num == 1 ? "it" : "them";
    sb.append("\nPlease download ");
    sb.append(them);
    sb.append(" from the LOCI website at");
    sb.append("\n    http://www.loci.wisc.edu/software/");
    sb.append("\nand place ");
    sb.append(them);
    sb.append(" in the ImageJ plugins folder.");
    IJ.error("LOCI Plugins", sb.toString());
    return false;
  }

}
