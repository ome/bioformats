//
// FormatReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden and Eric Kjellman.

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

package loci.formats;

import java.awt.Image;
import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import javax.swing.filechooser.FileFilter;

/** Abstract superclass of all biological file format readers. */
public abstract class FormatReader extends FormatHandler {

  // -- Fields --

  /** Hashtable containing metadata key/value pairs. */
  protected Hashtable metadata;

  /** OME root node for OME-XML metadata. */
  protected Object ome;


  // -- Constructors --

  /** Constructs a format reader with the given name and default suffix. */
  public FormatReader(String format, String suffix) { super(format, suffix); }

  /** Constructs a format reader with the given name and default suffixes. */
  public FormatReader(String format, String[] suffixes) {
    super(format, suffixes);
  }


  // -- Abstract FormatReader API methods --

  /** Checks if the given block is a valid header for this file format. */
  public abstract boolean isThisType(byte[] block);

  /** Determines the number of images in the given file. */
  public abstract int getImageCount(String id)
    throws FormatException, IOException;

  /** Obtains the specified image from the given file. */
  public abstract Image open(String id, int no)
    throws FormatException, IOException;

  /** Closes the currently open file. */
  public abstract void close() throws FormatException, IOException;


  // -- FormatReader API methods --

  /**
   * Initializes the given file (parsing header information, etc.).
   * Most subclasses should override this method to perform
   * initialization operations such as parsing metadata.
   */
  protected void initFile(String id) throws FormatException, IOException {
    close();
    currentId = id;
    metadata = new Hashtable();
    ome = OMETools.createRoot();
  }

  /**
   * Opens an existing file from the given filename.
   *
   * @return Java Images containing pixel data
   */
  public Image[] open(String id) throws FormatException, IOException {
    int nImages = getImageCount(id);
    Image[] images = new Image[nImages];
    for (int i=0; i<nImages; i++) images[i] = open(id, i);
    close();
    return images;
  }

  /**
   * Obtains a loci.ome.xml.OMENode object representing the
   * file's metadata as an OME-XML DOM structure.
   *
   * @return null if the loci.ome.xml package is not present.
   */
  public Object getOMENode(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return ome;
  }

  /**
   * Obtains the specified metadata field's value for the given file.
   *
   * @param field the name associated with the metadata field
   * @return the value, or null if the field doesn't exist
   */
  public Object getMetadataValue(String id, String field)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return metadata.get(field);
  }

  /**
   * Obtains the hashtable containing the metadata field/value pairs from
   * the given file.
   *
   * @param id the filename
   * @return the hashtable containing all metadata from the file
   */
  public Hashtable getMetadata(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return metadata;
  }

  /**
   * A utility method for test reading a file from the command line,
   * and displaying the results in a simple display.
   */
  public boolean testRead(String[] args) throws FormatException, IOException {
    String id = null;
    boolean pixels = true;
    if (args != null) {
      for (int i=0; i<args.length; i++) {
        if (args[i].startsWith("-")) {
          if (args[i].equals("-nopix")) pixels = false;
          else System.out.println("Ignoring unknown command flag: " + args[i]);
        }
        else {
          if (id == null) id = args[i];
          else System.out.println("Ignoring unknown argument: " + args[i]);
        }
      }
    }
    if (id == null) {
      String className = getClass().getName();
      System.out.println("To test read a file in " + format + " format, run:");
      System.out.println("  java " + className + " [-nopix] in_file");
      return false;
    }

    // check type
    System.out.print("Checking " + format + " format ");
    System.out.println(isThisType(id) ? "[yes]" : "[no]");

    // read pixels
    if (pixels) {
      System.out.print("Reading " + id + " pixel data ");
      long s1 = System.currentTimeMillis();
      int num = getImageCount(args[0]);
      long e1 = System.currentTimeMillis();
      final Image[] images = new Image[num];
      long s2 = System.currentTimeMillis();
      for (int i=0; i<num; i++) {
        images[i] = open(args[0], i);
        System.out.print(".");
      }
      long e2 = System.currentTimeMillis();
      System.out.println(" [done]");

      // output timing results
      float sec = (e2 - s1) / 1000f;
      float avg = (float) (e2 - s2) / num;
      long initial = e1 - s1;
      System.out.println(sec + "s elapsed (" +
        avg + "ms per image, " + initial + "ms overhead)");

      // display pixels in image viewer
      ImageViewer viewer = new ImageViewer();
      viewer.setImages(id, format, images);
      viewer.show();
    }

    // read metadata
    System.out.print("Reading " + id + " metadata ");
    Hashtable meta = getMetadata(id);
    System.out.println("[done]");

    // output metadata
    String[] keys = (String[]) meta.keySet().toArray(new String[0]);
    Arrays.sort(keys);
    for (int i=0; i<keys.length; i++) {
      System.out.print(keys[i] + ": ");
      System.out.print(getMetadataValue(id, keys[i]) + "\n");
    }
    System.out.println();

    // output OME-XML
    Object root = null;
    try { root = getOMENode(id); }
    catch (FormatException exc) { }
    if (root == null) {
      System.out.println("OME-XML functionality not available " +
        "(package loci.ome.xml not installed)");
      System.out.println();
    }
    else {
      System.out.println(OMETools.dumpXML(root));
      System.out.println();
    }
    return true;
  }


  // -- FormatHandler API methods --

  /** Creates JFileChooser file filters for this file format. */
  protected void createFilters() {
    filters = new FileFilter[] { new FormatFileFilter(this) };
  }

}
