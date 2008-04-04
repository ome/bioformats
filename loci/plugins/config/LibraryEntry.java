//
// LibraryEntry.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Data Browser, Stack Colorizer,
Stack Slicer, and OME plugins. Copyright (C) 2005-@year@ Melissa Linkert,
Curtis Rueden, Christopher Peterson and Philip Huettl.

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

package loci.plugins.config;

import java.io.File;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.Hashtable;

/**
 * A list entry for the configuration window's Libraries tab.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/plugins/config/LibraryEntry.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/plugins/config/LibraryEntry.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class LibraryEntry implements Comparable {

  // -- Constants --

  /** Version code to indicate a missing library. */
  public static final String MISSING_VERSION_CODE = "!";

  /** HTML markup for installed library. */
  private static final String PRESENT = "";

  /** HTML markup for missing library. */
  private static final String MISSING = "<font color=\"7f7f7f\"><i>";

  // -- Fields --

  protected String name;
  protected String type;
  protected String libClass;
  protected String url;
  protected String license;
  protected String notes;

  protected String status;
  protected String version;

  protected String path;

  // -- Constructor --

  public LibraryEntry(PrintWriter log, Hashtable props) {
    this(log,
      (String) props.get("name"),
      (String) props.get("type"),
      (String) props.get("class"),
      (String) props.get("version"),
      (String) props.get("url"),
      (String) props.get("license"),
      (String) props.get("notes"));
  }

  public LibraryEntry(PrintWriter log, String name, String type,
    String libClass, String version, String url, String license, String notes)
  {
    this.name = name;
    this.type = type;
    this.libClass = libClass;
    this.url = url;
    this.license = license;
    this.notes = notes;

    // check library status
    try {
      Class c = Class.forName(libClass);
      if (version == null) {
        // try to detect version from the package
        version = "";
        if (c != null) {
          Package p = c.getPackage();
          if (p != null) {
            String pVendor = p.getImplementationVendor();
            String pVersion = p.getImplementationVersion();
            if (pVendor != null && pVersion != null) {
              version = pVersion + " (" + pVendor + ")";
            }
            else if (pVersion != null) version = pVersion;
            else if (pVendor != null) version = pVendor;
          }
        }
      }
      else if (version.equals(MISSING_VERSION_CODE)) {
        // we know a priori that this library is not properly installed
        version = null;
        throw new ClassNotFoundException();
      }
      this.version = version;

      // get resource containing the class
      String className = libClass.substring(libClass.lastIndexOf(".") + 1);
      path = c.getResource(className + ".class").toString();
      path = path.replaceAll("^jar:", "");
      path = path.replaceAll("^file:", "");
      path = path.replaceAll("^/*/", "/");
      path = path.replaceAll("^/([A-Z]:)", "$1");
      path = path.replaceAll("!.*", "");
      path = URLDecoder.decode(path, "UTF-8");
			String slash = File.separator;
			if (slash.equals("\\")) slash = "\\\\";
      path = path.replaceAll("/", slash);

      log.println("Found library " + name + ":");
      if (!"".equals(version)) log.println("    Version = " + version);
      log.println("    Path = " + path);
    }
    catch (Throwable t) {
      if (t instanceof ClassNotFoundException) {
        log.println("No library " + name + ".");
      }
      else {
        log.println("Error communicating with library " + name + ":");
        t.printStackTrace(log);
      }
    }
    status = version == null ? "Missing" : "Installed";
  }

  // -- Comparable API methods --

  public int compareTo(Object o) {
    LibraryEntry entry = (LibraryEntry) o;
    return name.compareTo(entry.name);
  }

  // -- Object API methods --

  public String toString() {
    String markup = version == null ? MISSING : PRESENT;
    return "<html>" + markup + name;
  }

}
