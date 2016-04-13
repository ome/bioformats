/*
 * #%L
 * Bio-Formats Plugins for ImageJ: a collection of ImageJ plugins including the
 * Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
 * Data Browser and Stack Slicer.
 * %%
 * Copyright (C) 2006 - 2016 Open Microscopy Environment:
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

package loci.plugins.util;

import ij.IJ;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Utility methods for verifying that classes
 * are present and versions are correct.
 */
public final class LibraryChecker {

  // -- Constants --

  /** List of possible libraries for which to check. */
  public enum Library {
    BIO_FORMATS,  // Bio-Formats
    OME_JAVA_XML, // OME-XML Java
    FORMS         // JGoodies Forms
  }

  /** Minimum version of ImageJ necessary for Bio-Formats plugins. */
  public static final String IMAGEJ_VERSION = "1.43";

  /** Message to be displayed if ImageJ is too old for Bio-Formats plugins. */
  public static final String IMAGEJ_MSG =
    "Sorry, the Bio-Formats plugins require ImageJ v" + IMAGEJ_VERSION + " or later.";

  /** URL for Bio-Formats web page. */
  public static final String URL_BF_SOFTWARE =
    "http://www.openmicroscopy.org/site/products/bio-formats";

  // -- Constructor --

  private LibraryChecker() { }

  // -- Utility methods --

  /** Checks whether the given class is available. */
  public static boolean checkClass(String className) {
    try { Class.forName(className); }
    catch (Throwable t) { return false; }
    return true;
  }

  /** Checks for a required library. */
  public static void checkLibrary(Library library, HashSet<String> missing) {
    switch (library) {
      case BIO_FORMATS:
        checkLibrary("org.slf4j.Logger", "slf4j-api-1.7.2.jar", missing);
        checkLibrary("loci.common.RandomAccessInputStream",
          "common.jar", missing);
        checkLibrary("loci.formats.FormatHandler", "bio-formats.jar", missing);
        checkLibrary("loci.poi.poifs.filesystem.POIFSDocument",
          "ome-poi.jar", missing);
        checkLibrary("mdbtools.libmdb.MdbFile", "mdbtools-java.jar", missing);
        break;
      case OME_JAVA_XML:
        checkLibrary("ome.xml.model.OMEModelObject", "ome-xml.jar", missing);
        break;
      case FORMS:
        checkLibrary("com.jgoodies.forms.layout.FormLayout",
          "jgoodies-forms-1.7.2.jar", missing);
        break;
    }
  }

  /**
   * Checks whether the given class is available; if not,
   * adds the specified JAR file name to the hash set
   * (presumably to report it missing to the user).
   */
  public static void checkLibrary(String className,
    String jarFile, HashSet<String> missing)
  {
    if (!checkClass(className)) missing.add(jarFile);
  }

  /** Checks for a new enough version of the Java Runtime Environment. */
  public static boolean checkJava() {
    String version = System.getProperty("java.version");
    double ver = Double.parseDouble(version.substring(0, 3));
    if (ver < 1.7) {
      IJ.error("Bio-Formats Plugins",
        "Sorry, the Bio-Formats plugins require Java 1.7 or later.");
      return false;
    }
    return true;
  }

  /** Checks whether ImageJ is new enough for the Bio-Formats plugins. */
  public static boolean checkImageJ() {
    return checkImageJ(IMAGEJ_VERSION, IMAGEJ_MSG);
  }

  /**
   * Returns true the current ImageJ version is greater than or equal to the
   * specified version. Displays the given warning message if the current
   * version is too old.
   */
  public static boolean checkImageJ(String target, String msg) {
    return checkImageJ(target, msg, "Bio-Formats Plugins");
  }

  /**
   * Returns true the current ImageJ version is greater than or equal to the
   * specified version. Displays the given warning message with the specified
   * title if the current version is too old.
   */
  public static boolean checkImageJ(String target, String msg, String title) {
    boolean success;
    try {
      String current = IJ.getVersion();
      success = current != null && current.compareTo(target) >= 0;
    }
    catch (NoSuchMethodError err) {
      success = false;
    }
    if (!success) IJ.error(title, msg);
    return success;
  }

  /**
   * Reports missing libraries in the given hash set to the user.
   * @return true iff no libraries are missing (the hash set is empty).
   */
  public static boolean checkMissing(HashSet<String> missing) {
    int num = missing.size();
    if (num == 0) return true;
    StringBuffer sb = new StringBuffer();
    sb.append("The following librar");
    sb.append(num == 1 ? "y was" : "ies were");
    sb.append(" not found:");
    Iterator<String> iter = missing.iterator();
    for (int i=0; i<num; i++) sb.append("\n    " + iter.next());
    String them = num == 1 ? "it" : "them";
    sb.append("\nPlease download ");
    sb.append(them);
    sb.append(" from the Bio-Formats website at");
    sb.append("\n    " + URL_BF_SOFTWARE);
    sb.append("\nand place ");
    sb.append(them);
    sb.append(" in the ImageJ plugins folder.");
    IJ.error("Bio-Formats Plugins", sb.toString());
    return false;
  }

}
