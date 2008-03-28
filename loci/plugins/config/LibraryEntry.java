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

  private static final String RED = "7f0000";
  private static final String ORANGE = "7f3f00";
  private static final String BLACK = "000000";

  // -- Fields --

  protected String name;
  protected String type;
  protected String libClass;
  protected String url;
  protected String license;
  protected String notes;

  protected String status;
  protected String version;
  protected String latest;

  protected String path;

  // -- Constructor --

  public LibraryEntry(String name, String type, String libClass,
    String version, String url, String license, String notes)
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
      this.version = version;
      latest = version; // TEMP

      // get resource containing the class
      String className = libClass.substring(libClass.lastIndexOf(".") + 1);
      path = c.getResource(className + ".class").toString();
      path = path.replaceAll("^jar:", "");
      path = path.replaceAll("^file:", "");
      path = path.replaceAll("^/*/", "/");
      path = path.replaceAll("!.*", "");
    }
    catch (Throwable t) { }
    status = version == null ? "Missing" : "Installed";
  }

  // -- Comparable API methods --

  public int compareTo(Object o) {
    LibraryEntry entry = (LibraryEntry) o;
    return name.compareTo(entry.name);
  }

  // -- Object API methods --

  public String toString() {
    String color = BLACK;
    if (version == null) color = RED;
    else if (!version.equals(latest)) color = ORANGE;
    return "<html><font color=\"" + color + "\">" + name;
  }

}
