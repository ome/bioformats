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

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

/** Abstract superclass of all supported biological file format readers. */
public abstract class FormatReader {

  /** Name of this file format. */
  protected String format;

  /** Valid suffixes for this file format. */
  protected String[] suffixes;

  /** File filters for this file format, for use with a JFileChooser. */
  protected FileFilter[] filters;

  /** Name of current file. */
  protected String currentId;

  /** Hashtable containing metadata key/value pairs. */
  protected Hashtable metadata;

  /** OME root node for OME-XML metadata. */
  protected Object ome;


  // -- Constructors --

  /** Constructs a format reader with the given name and default suffix. */
  public FormatReader(String format, String suffix) {
    this(format, suffix == null ? null : new String[] {suffix});
  }

  /** Constructs a format reader with the given name and default suffixes. */
  public FormatReader(String format, String[] suffixes) {
    this.format = format;
    this.suffixes = suffixes == null ? new String[0] : suffixes;
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

  /** Creates JFileChooser file filters for this file format. */
  protected void createFilters() {
    filters = new FormatFilter[] { new FormatFilter(this) };
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

  /** Gets the name of this file format. */
  public String getFormat() { return format; }

  /** Gets the default file suffixes for this file format. */
  public String[] getSuffixes() { return suffixes; }

  /** Gets file filters for this file format, for use with a JFileChooser. */
  public FileFilter[] getFileFilters() {
    if (filters == null) createFilters();
    return filters;
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
    String className = getClass().getName();
    if (args == null || args.length < 1) {
      System.out.println("To test read a file in " + format + " format, run:");
      System.out.println("  java " + className + " in_file");
      return false;
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

    // display pixels in pop-up window
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
        Dimension size = getSize();
        g.setColor(Color.white);
        g.fillRect(0, 0, size.width, size.height);
        g.drawImage(images[ndx], 0, 0, this);
      }
      public Dimension getPreferredSize() {
        int w = images[0].getWidth(this);
        int h = images[0].getHeight(this);
        return new Dimension(w, h);
      }
    };

    // cursor probe
    final JLabel mouseLabel = new JLabel(" ");
    imagePane.addMouseMotionListener(new MouseMotionAdapter() {
      private StringBuffer sb = new StringBuffer();
      public void mouseMoved(MouseEvent e) {
        int ndx = slider == null ? 0 : (slider.getValue() - 1);
        int x = e.getX();
        int y = e.getY();
        sb.setLength(0);
        if (images.length > 1) {
          sb.append("N=");
          sb.append(ndx);
          sb.append("; ");
        }
        BufferedImage image = null;
        if (images[ndx] instanceof BufferedImage) {
          image = (BufferedImage) images[ndx];
          int w = image.getWidth(), h = image.getHeight();
          if (x < 0) x = 0;
          if (x >= w) x = w - 1;
          if (y < 0) y = 0;
          if (y >= h) y = h - 1;
        }
        sb.append("X=");
        sb.append(x);
        sb.append("; Y=");
        sb.append(y);
        if (image != null) {
          Raster r = image.getRaster();
          double[] pix = r.getPixel(x, y, (double[]) null);
          sb.append("; value");
          sb.append(pix.length > 1 ? "s=(" : "=");
          for (int i=0; i<pix.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(pix[i]);
          }
          if (pix.length > 1) sb.append(")");
        }
        mouseLabel.setText(sb.toString());
      }
    });
    p.add(BorderLayout.NORTH, mouseLabel);
    p.add(imagePane);

    // slider for navigating across multiple images
    if (slider != null) {
      slider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          imagePane.repaint();
        }
      });
      p.add(BorderLayout.SOUTH, slider);
    }

    // show frame onscreen
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
    return true;
  }

}
