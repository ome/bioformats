//
// FileFormat.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert and Curtis Rueden.

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

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Hashtable;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/** Abstract superclass of all supported biological file formats. */
public abstract class FileFormat {

  /** Name of this file format. */
  protected String format;

  /** List of valid suffixes for this file format. */
  protected String[] suffixes;

  /** Name of current file. */
  protected String currentId;

  /** Percent complete with current operation. */
  protected double percent;

  /** Hashtable containing metadata key/value pairs. */
  protected Hashtable metadata;

  /** OME root node for OME-XML metadata. */
  protected Object ome;


  // -- Constructors --

  /** Constructs a file format with the given name and default suffix. */
  public FileFormat(String format, String suffix) {
    this(format, suffix == null ? null : new String[] {suffix});
  }

  /** Constructs a file format with the given name and default suffixes. */
  public FileFormat(String format, String[] suffixes) {
    this.format = format;
    this.suffixes = suffixes == null ? new String[0] : suffixes;
  }


  // -- Abstract FileFormat API methods --

  /** Determines the number of images in the given file. */
  public abstract int getImageCount(String id) throws FormatException;

  /** Obtains the specified image from the given file. */
  public abstract Image open(String id, int block_number)
    throws FormatException;

  /** Closes the currently open file. */
  public abstract void close() throws FormatException;

  /** Checks if the given block is a valid header for this file format. */
  public abstract boolean isThisType(byte[] block);


  // -- FileFormat API methods --

  /**
   * Opens an existing file from the given filename.
   *
   * @return Java Images containing pixel data
   */
  public Image[] open(String id) throws FormatException {
    percent = 0;
    int nImages = getImageCount(id);
    Image[] images = new Image[nImages];
    for (int i=0; i<nImages; i++) {
      images[i] = open(id, i);
      percent = (double) (i+1) / nImages;
    }
    close();
    percent = Double.NaN;
    return images;
  }

  /**
   * Checks if the given string is a valid filename for this file format.
   * The default implementation checks filename suffixes against those known
   * for this format.
   */
  public boolean isThisType(String name) {
    String lname = name.toLowerCase();
    for (int i=0; i<suffixes.length; i++) {
      if (lname.endsWith("." + suffixes[i])) return true;
    }
    return false;
  }

  /** Returns the default file suffixes for this file format. */
  public String[] getDefaultSuffixes() { return suffixes; }

  /** Gets the percentage complete of the form's current operation. */
  public double getPercentComplete() { return percent; }

  /**
   * Obtains a loci.ome.xml.OMENode object representing the
   * file's metadata as an OME-XML DOM structure.
   *
   * @return null if the loci.ome.xml package is not present.
   */
  public Object getOMENode(String id) throws FormatException {
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
    throws FormatException
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
  public Hashtable getMetadata(String id) throws FormatException {
    if (!id.equals(currentId)) initFile(id);
    return metadata;
  }

  /**
   * Initializes the given file (parsing header information, etc.).
   * Most subclasses should override this method to perform
   * initialization operations such as parsing metadata.
   */
  protected void initFile(String id) throws FormatException {
    currentId = id;
    metadata = new Hashtable();
  }


  // -- Utility methods --

  /**
   * A utility method for test reading a file from the command line,
   * and displaying the results in a simple display.
   */
  public void testRead(String[] args) throws FormatException {
    String className = getClass().getName();
    if (args == null || args.length < 1) {
      System.out.println("To test read a file in " + format + " format, run:");
      System.out.println("  java " + className + " in_file");
      return;
    }
    String id = args[0];

    // check type
    System.out.print("Checking " + format + " format ");
    System.out.println(isThisType(id) ? "[yes]" : "[no]");

    // read pixels
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

    // pop up frame
    JFrame frame = new JFrame(format + " - " + id);
    frame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) { System.exit(0); }
    });
    JPanel p = new JPanel();
    frame.setContentPane(p);
    p.setLayout(new BorderLayout());
    final JSlider slider = num > 1 ? new JSlider(1, num) : null;
    final JPanel imagePane = new JPanel() {
      public void paint(Graphics g) {
        int ndx = slider == null ? 0 : (slider.getValue() - 1);
        g.drawImage(images[ndx], 0, 0, this);
      }
    };
    p.add(imagePane);
    if (slider != null) {
      slider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          imagePane.repaint();
        }
      });
      p.add(BorderLayout.SOUTH, slider);
    }
    frame.pack();
    frame.setLocation(300, 300);
    frame.show();

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
  }

}
