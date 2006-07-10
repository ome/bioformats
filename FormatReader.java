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

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.filechooser.FileFilter;

/** Abstract superclass of all biological file format readers. */
public abstract class FormatReader extends FormatHandler {

  // -- Constants --

  /** Debugging flag. */
  protected static final boolean DEBUG = false;

  /** Debugging level. 1=basic, 2=extended, 3=everything. */
  protected static final int DEBUG_LEVEL = 1;


  // -- Fields --

  /** Hashtable containing metadata key/value pairs. */
  protected Hashtable metadata;

  /** Flag set to true if multi-channel planes are to be separated. */
  protected boolean separated = false;


  /** Files with the same pattern. */
  protected String[] stitchedFiles;

  /** Image counts for each file. */
  protected int[] imageCounts;

  /** Current metadata store. Should <b>never</b> be accessed directly as the
   * semantics of {@link #getMetadataStore()} prevent "null" access. */
  private MetadataStore store = new DummyMetadataStore();

  // -- Constructors --

  /** Constructs a format reader with the given name and default suffix. */
  public FormatReader(String format, String suffix) { super(format, suffix); }

  /** Constructs a format reader with the given name and default suffixes. */
  public FormatReader(String format, String[] suffixes) {
    super(format, suffixes);
  }

  // -- Abstract FormatReader API methods --
  
  /**
   * Sets the default metadata store for this reader. This invalidates the
   * current metadata state of the reader and will trigger a call to
   * {@link #initFile(String)}.
   * @param store a metadata store implementation.
   */
  public void setMetadataStore(MetadataStore store) {
    this.store = store;
    // Re-parse the file if we currently have one
    if (currentId == null) {
      // We're going to eat these exceptions as they will come up again if
      // method calls are made to the format.
      try {
        initFile(currentId);
      }
      catch (FormatException e1) {}
      catch (IOException e2) {}
    }
  }
  
  /**
   * Retrieves the current metadata store for this reader. You can be
   * assured that this method will <b>never</b> return a <code>null</code>
   * metadata store.
   * @return a metadata store implementation.
   */
  public MetadataStore getMetadataStore() {
    return store;
  }
  
  
  /**
   * Retrieves the current metadata store's root object. It is guaranteed that
   * all file parsing has been performed by the reader prior to retrieval.
   * Requests for a full populated root object should be made using this method.
   * @param id a fully qualified path to the file.
   * @return current metadata store's root object fully populated.
   * @throws IOException if there is an IO error when reading the file specified
   * by <code>path</code>.
   * @throws FormatException if the file specified by <code>path</code> is of an
   * unsupported type. 
   */
  public Object getMetadataStoreRoot(String id)
    throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return getMetadataStore().getRoot();
  }

  /** Checks if the given block is a valid header for this file format. */
  public abstract boolean isThisType(byte[] block);

  /** Determines the number of images in the given file. */
  public abstract int getImageCount(String id)
    throws FormatException, IOException;

  /** Checks if the images in the file are RGB. */
  public abstract boolean isRGB(String id)
    throws FormatException, IOException;

  /** Returns the number of channels in the file. */
  public abstract int getChannelCount(String id)
    throws FormatException, IOException;

  /** Obtains the specified image from the given file. */
  public abstract BufferedImage openImage(String id, int no)
    throws FormatException, IOException;

  /**
   * Obtains the specified image from the given file as a byte array.
   */
  public abstract byte[] openBytes(String id, int no)
    throws FormatException, IOException;

  /** Closes the currently open file. */
  public abstract void close() throws FormatException, IOException;


  // -- Internal FormatReader API methods --

  /**
   * Initializes the given file (parsing header information, etc.).
   * Most subclasses should override this method to perform
   * initialization operations such as parsing metadata.
   */
  protected void initFile(String id) throws FormatException, IOException {
    close();
    currentId = id;
    metadata = new Hashtable();
    imageCounts = null;
    stitchedFiles = null;
	// Re-initialize the MetadataStore
    getMetadataStore().createRoot();
  }

  /**
   * Allows the client to specify whether or not to separate channels.
   * By default, channels are left unseparated; thus if we encounter an RGB
   * image plane, it will be left as RGB and not split into 3 separate planes.
   */
  protected void setSeparated(boolean separate) {
    separated = separate;
  }

  /**
   * Opens the given file, reads in the first few KB and calls
   * isThisType(byte[]) to check whether it matches this format.
   */
  protected boolean checkBytes(String name, int maxLen) {
    long len = new File(name).length();
    byte[] buf = new byte[len < maxLen ? (int) len : maxLen];
    try {
      FileInputStream fin = new FileInputStream(name);
      int r = 0;
      while (r < buf.length) r += fin.read(buf, r, buf.length - r);
      fin.close();
      return isThisType(buf);
    }
    catch (IOException e) { return false; }
  }


  // -- FormatReader API methods --

  /**
   * Opens an existing file from the given filename.
   *
   * @return Java Images containing pixel data
   */
  public BufferedImage[] openImage(String id)
    throws FormatException, IOException
  {
    int nImages = getImageCount(id);
    BufferedImage[] images = new BufferedImage[nImages];
    for (int i=0; i<nImages; i++) images[i] = openImage(id, i);
    close();
    return images;
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
      ChannelMerger cm = new ChannelMerger(this);
      int num = cm.getTotalImageCount(args[0]);
      System.out.print("(" + num + ") ");
      long e1 = System.currentTimeMillis();
      BufferedImage[] images = new BufferedImage[num];
      long s2 = System.currentTimeMillis();
      for (int i=0; i<num; i++) {
        images[i] = cm.openStitchedImage(args[0], i);
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
    MetadataStore store = getMetadataStore();
    if (store instanceof OMEXMLMetadataStore) {
      OMEXMLMetadataStore xmlStore = (OMEXMLMetadataStore) store;
      System.out.println(xmlStore.dumpXML());
      System.out.println();
    }
    
    return true;
  }

  // -- File stitching API methods --

  /** Gets a list of files matching the pattern in the given file name. */
  public String[] getMatchingFiles(String id)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    FilePattern fp = new FilePattern(new File(id));
    return fp.getFiles();
  }

  /**
   * Open all of the files matching the given file, and return the corresponding
   * array of BufferedImages.
   */
  public BufferedImage[] openAllImages(String id)
    throws FormatException, IOException
  {
    String[] files = getMatchingFiles(id);
    Vector v = new Vector();
    for (int i=0; i<files.length; i++) {
      for (int j=0; j<getImageCount(id); j++) {
        v.add(openImage(files[i], j));
      }
    }
    return (BufferedImage[]) v.toArray(new BufferedImage[0]);
  }

  /** Get the total number of images in the given file and all matching files.*/
  public int getTotalImageCount(String id) throws FormatException, IOException {
    String[] files = getMatchingFiles(id);
    int num = 0;
    for (int i=0; i<files.length; i++) {
      num += getImageCount(files[i]);
    }
    return num;
  }

  /**
   * Open the given image from the appropriate file matching the
   * given file name.  The number given should be between 0 and
   * getTotalImageCount(id); this method will automatically adjust the file
   * name and plane number accordingly.
   */
  public BufferedImage openStitchedImage(String id, int no)
    throws FormatException, IOException
  {
    String[] files = getMatchingFiles(id);

    // first find the appropriate file
    boolean found = false;
    String file = files[0];
    int ndx = 1;  // index into the array of file names
    while (!found) {
      if (no < getImageCount(file)) {
        found = true;
      }
      else {
        no -= getImageCount(file);
        file = files[ndx];
        ndx++;
      }
    }

    return openImage(file, no);
  }

  /**
   * Open the given image from the appropriate file matching the
   * given file name.  The number given should be between 0 and
   * getTotalImageCount(id); this method will automatically adjust the file
   * name and plane number accordingly.
   */
  public byte[] openStitchedBytes(String id, int no)
    throws FormatException, IOException
  {
    String[] files = getMatchingFiles(id);

    // first find the appropriate file
    boolean found = false;
    String file = files[0];
    int ndx = 1;  // index into the array of file names
    while (!found) {
      if (no < getImageCount(file)) {
        found = true;
      }
      else {
        no -= getImageCount(file);
        file = files[ndx];
        ndx++;
      }
    }

    return openBytes(file, no);
  }

  // -- FormatHandler API methods --

  /** Creates JFileChooser file filters for this file format. */
  protected void createFilters() {
    filters = new FileFilter[] { new FormatFileFilter(this) };
  }
}