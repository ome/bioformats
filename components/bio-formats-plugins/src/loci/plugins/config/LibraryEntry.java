/*
 * #%L
 * Bio-Formats Plugins for ImageJ: a collection of ImageJ plugins including the
 * Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
 * Data Browser and Stack Slicer.
 * %%
 * Copyright (C) 2006 - 2015 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.plugins.config;

import java.io.File;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.HashMap;

import loci.common.Constants;

/**
 * A list entry for the configuration window's Libraries tab.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class LibraryEntry implements Comparable<Object> {

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

  public LibraryEntry(PrintWriter log, HashMap<String, String> props) {
    this(log,
      props.get("name"),
      props.get("type"),
      props.get("class"),
      props.get("version"),
      props.get("url"),
      props.get("license"),
      props.get("notes"));
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
      Class<?> c = Class.forName(libClass);
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
      path = URLDecoder.decode(path, Constants.ENCODING);
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

  @Override
  public int compareTo(Object o) {
    LibraryEntry entry = (LibraryEntry) o;
    return name.compareTo(entry.name);
  }

  // -- Object API methods --

  @Override
  public String toString() {
    String markup = version == null ? MISSING : PRESENT;
    return "<html>" + markup + name;
  }

}
