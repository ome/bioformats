//
// Dataset.java
//

/*
VisBio application for visualization of multidimensional biological
image data. Copyright (C) 2002-@year@ Curtis Rueden and Abraham Sorber.

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

package loci.visbio.data;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.Hashtable;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import loci.common.Location;
import loci.formats.*;
import loci.formats.gui.GUITools;
import loci.formats.meta.MetadataStore;
import loci.visbio.VisBioFrame;
import loci.visbio.state.Dynamic;
import loci.visbio.state.SaveException;
import loci.visbio.util.*;
import org.w3c.dom.Element;
import ome.xml.OMEXMLNode;
import visad.*;

/**
 * A Dataset object encompasses a multidimensional biological image series.
 * Such an object is typically between two and six dimensions as follows:
 * <li>2D: a single image
 * <li>3D: an image stack, or a time series of images
 * <li>4D: a time series of image stacks, a multispectral image stack,
 *         or an image stack with lifetime data at each pixel
 * <li>5D: an image stack with spectral lifetime data at each pixel
 * <li>6D: a time series of image stacks with
 *         spectral lifetime data at each pixel
 * <p>
 * Other configurations are certainly possible, and Dataset provides an
 * interface for multidimensional image data of any organization.
 * <p>
 * Dataset keeps no data in memory itself, leaving that management to the
 * application, and just loads data as necessary to return whatever the
 * application requests, according to the DataTransform API.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/visbio/src/loci/visbio/data/Dataset.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/visbio/src/loci/visbio/data/Dataset.java">SVN</a></dd></dl>
 */
public class Dataset extends ImageTransform {

  // -- Data fields --

  /** A string pattern describing this dataset. */
  protected String pattern;

  /** Optional listener for constructor progress. */
  protected StatusListener listener;

  // -- Computed fields --

  /** Data reader. */
  protected IFormatReader reader;

  /** Controls for this dataset. */
  protected DatasetWidget controls;

  /** Types mapped to spatial components (X, Y). */
  protected RealType[] spatial;

  /** Types mapped to color components (RGBA). */
  protected RealType[] color;

  // -- Constructors --

  /** Constructs an uninitialized multidimensional data object. */
  public Dataset() { super(); }

  /**
   * Constructs a multidimensional data object.
   * See the complete constructor for more information.
   */
  public Dataset(String name, String pattern) { this(name, pattern, null); }

  /**
   * Constructs a new multidimensional data object from the given file pattern.
   * @param name Label for the dataset.
   * @param pattern File pattern identifying the dataset.
   * @param listener Listener object to be informed of construction progress.
   */
  public Dataset(String name, String pattern, StatusListener listener) {
    super(null, name);
    this.pattern = pattern;
    this.listener = listener;
    initState(null);
  }

  // -- Dataset API methods --

  /** Close all open ids. */
  public void close() throws FormatException, IOException {
    reader.close();
  }

  /** Gets the string pattern describing this dataset. */
  public String getPattern() { return pattern; }

  /** Gets filenames of all files in dataset. */
  public String[] getFilenames() { return reader.getUsedFiles(); }

  /** Gets a description of the source files' file format. */
  public String getFileFormat() { return reader.getFormat(); }

  /** Gets metadata associated with the dataset. */
  public Hashtable getMetadata() { return reader.getMetadata(); }

  /** Gets an OME-XML root for the dataset. */
  public OMEXMLNode getOMEXMLRoot() {
    MetadataStore store = reader.getMetadataStore();
    return (OMEXMLNode) store.getRoot();
  }

  // -- ImageTransform API methods --

  /** Obtains an image from the source(s) at the given dimensional position. */
  public BufferedImage getImage(int[] pos) {
    int index = posToIndex(pos);
    BufferedImage img = null;
    try {
      img = reader.openImage(index);
    }
    catch (IOException exc) {
      if (VisBioFrame.DEBUG) exc.printStackTrace();
    }
    catch (FormatException exc) {
      if (VisBioFrame.DEBUG) exc.printStackTrace();
    }
    if (img == null) {
      System.err.println("Could not read image at index #" + index);
      return null;
    }
    return img;
  }

  /** Gets width of each image. */
  public int getImageWidth() { return reader.getSizeX(); }

  /** Gets height of each image. */
  public int getImageHeight() { return reader.getSizeY(); }

  /** Gets number of range components at each pixel. */
  public int getRangeCount() { return 1; }

  // -- Static DataTransform API methods --

  /** Creates a new dataset, with user interaction. */
  public static DataTransform makeTransform(DataManager dm) {
    return makeTransform(dm, null, dm.getControls());
  }

  /**
   * Creates a new dataset, with user interaction,
   * with the given default file.
   */
  public static DataTransform makeTransform(DataManager dm,
    File file, Component parent)
  {
    if (file == null) {
      // prompt for file to open
      IFormatReader reader = new ChannelSeparator();
      JFileChooser fc = GUITools.buildFileChooser(reader);
      int rval = fc.showOpenDialog(parent);
      if (rval != JFileChooser.APPROVE_OPTION) return null;
      file = fc.getSelectedFile();
    }

    FilePattern fp = new FilePattern(new Location(file));
    String pattern = fp.getPattern();
    if (pattern == null) pattern = file.getAbsolutePath();
    String name = fp.getPrefix();
    if (name == null) name = file.getName();

    // confirm file pattern
    pattern = (String) JOptionPane.showInputDialog(parent, "File pattern",
      "VisBio", JOptionPane.QUESTION_MESSAGE, null, null, pattern);
    if (pattern == null) return null;

    // data manager will add the resultant dataset to the Data panel
    dm.createDataset(name, pattern, null);
    return null;
  }

  /**
   * Indicates whether this transform type would accept
   * the given transform as its parent transform.
   */
  public static boolean isValidParent(DataTransform data) { return false; }

  /** Indicates whether this transform type requires a parent transform. */
  public static boolean isParentRequired() { return false; }

  // -- DataTransform API methods --

  /** Gets whether this transform provides data of the given dimensionality. */
  public boolean isValidDimension(int dim) { return dim == 2; }

  /**
   * Gets a string id uniquely describing this data transform at the given
   * dimensional position, for the purposes of thumbnail caching.
   * If global flag is true, the id is suitable for use in the default,
   * global cache file.
   */
  public String getCacheId(int[] pos, boolean global) {
    if (pos == null) return null;
    int index = posToIndex(pos);
    String prefix = global ? pattern : new File(pattern).getName();
    return prefix + "/" + index;
  }

  /** Gets a description of this dataset, with HTML markup. */
  public String getHTMLDescription() {
    StringBuffer sb = new StringBuffer();

    // file pattern
    sb.append(pattern.replaceAll("<", "&lt;").replaceAll(">", "&gt;"));
    sb.append("<p>\n\n");

    // list of dimensional axes
    sb.append("Dimensionality: ");
    sb.append(lengths.length + 2);
    sb.append("D\n");
    sb.append("<ul>\n");
    BigInteger images = BigInteger.ONE;
    if (lengths.length > 0) {
      for (int i=0; i<lengths.length; i++) {
        images = images.multiply(new BigInteger("" + lengths[i]));
        sb.append("<li>");
        sb.append(lengths[i]);
        sb.append(" ");
        sb.append(getUnitDescription(dims[i], lengths[i]));
        sb.append("</li>\n");
      }
    }

    // image resolution
    int resX = getImageWidth(), resY = getImageHeight();
    sb.append("<li>");
    sb.append(resX);
    sb.append(" x ");
    sb.append(resY);
    sb.append(" pixel");
    if (resX * resY != 1) sb.append("s");

    // physical width and height in microns
    if (micronWidth == micronWidth && micronHeight == micronHeight) {
      sb.append(" (");
      sb.append(micronWidth);
      sb.append(" x ");
      sb.append(micronHeight);
      sb.append(" " + MU + ")");
    }
    sb.append("</li>\n");

    // physical distance between slices in microns
    if (micronStep == micronStep) {
      sb.append("<li>");
      sb.append(micronStep);
      sb.append(" " + MU + " between slices</li>\n");
    }

    // range component count
    int numRange = getRangeCount();
    sb.append("<li>");
    sb.append(numRange);
    sb.append(" range component");
    if (numRange != 1) sb.append("s");
    sb.append("</li>\n");
    sb.append("</ul>\n");

    // file format
    String format = getFileFormat();
    sb.append("File format: ");
    sb.append(format);
    sb.append("<br>\n");

    // file count
    sb.append(getFilenames().length);
    sb.append(" files in dataset.<br>\n");

    // image and pixel counts
    BigInteger pixels = images.multiply(new BigInteger("" + resX));
    pixels = pixels.multiply(new BigInteger("" + resY));
    pixels = pixels.multiply(new BigInteger("" + numRange));
    sb.append(images);
    sb.append(" image");
    if (!images.equals(BigInteger.ONE)) sb.append("s");
    sb.append(" totaling ");
    sb.append(MathUtil.getValueWithUnit(pixels, 2));
    sb.append("pixel");
    if (!pixels.equals(BigInteger.ONE)) sb.append("s");
    sb.append(".<p>\n");

    return sb.toString();
  }

  /** Gets associated GUI controls for this transform. */
  public JComponent getControls() { return controls; }

  // -- Dynamic API methods --

  /** Tests whether two dynamic objects have matching states. */
  public boolean matches(Dynamic dyn) {
    if (!super.matches(dyn) || !isCompatible(dyn)) return false;
    Dataset data = (Dataset) dyn;

    return ObjectUtil.objectsEqual(pattern, data.pattern);
  }

  /**
   * Tests whether the given dynamic object can be used as an argument to
   * initState, for initializing this dynamic object.
   */
  public boolean isCompatible(Dynamic dyn) { return dyn instanceof Dataset; }

  /** Modifies this object's state to match that of the given object. */
  public void initState(Dynamic dyn) {
    if (dyn != null && !isCompatible(dyn)) return;
    super.initState(dyn);
    Dataset data = (Dataset) dyn;

    if (data != null) {
      pattern = data.pattern;
    }

    int numTasks = 4;

    // initialize data reader
    reader = new ChannelSeparator(new FileStitcher(true));
    reader.setMetadataStore(MetadataTools.createOMEXMLMetadata());

    // determine number of images per source file
    status(1, numTasks, "Initializing dataset");
    try {
      reader.setId(pattern);
    }
    catch (Exception exc) {
      System.err.println("Could not initialize the dataset. '" +
        pattern + "' may be corrupt or invalid.");
      if (VisBioFrame.DEBUG) exc.printStackTrace();
      return;
    }
    int[] cLen = reader.getChannelDimLengths();
    lengths = new int[2 + cLen.length];
    lengths[0] = reader.getSizeT();
    lengths[1] = reader.getSizeZ();
    System.arraycopy(cLen, 0, lengths, 2, cLen.length);
    String[] cTypes = reader.getChannelDimTypes();
    dims = new String[2 + cTypes.length];
    dims[0] = "Time";
    dims[1] = "Slice";
    System.arraycopy(cTypes, 0, dims, 2, cTypes.length);
    makeLabels();

    // load first image for analysis
    status(2, numTasks, "Reading first image");
    BufferedImage img = null;
    try { img = reader.openImage(0); }
    catch (IOException exc) { img = null; }
    catch (FormatException exc) { img = null; }
    catch (NullPointerException exc) { img = null; }
    if (img == null) {
      System.err.println("Could not read the first image. '" +
        pattern + "' may be corrupt or invalid.");
      return;
    }
    ImageFlatField ff = null;
    try { ff = new ImageFlatField(img); }
    catch (VisADException exc) {
      System.err.println("Could not construct ImageFlatField.");
      exc.printStackTrace();
      return;
    }
    catch (RemoteException exc) {
      System.err.println("Could not construct ImageFlatField.");
      exc.printStackTrace();
      return;
    }

    // extract range components
    FunctionType ftype = (FunctionType) ff.getType();
    MathType range = ftype.getRange();
    if (range instanceof TupleType) {
      TupleType rangeTuple = (TupleType) range;
      color = rangeTuple.getRealComponents();
    }
    else if (range instanceof RealType) {
      color = new RealType[] {(RealType) range};
    }
    else {
      System.err.println("Invalid range type (" +
        range.getClass().getName() + ")");
      return;
    }

    // extract domain types
    RealTupleType domain = ftype.getDomain();
    spatial = domain.getRealComponents();

    // construct metadata controls
    status(3, numTasks, "Finishing");
    controls = new DatasetWidget(this);

    // construct thumbnail handler
    String path = new File(pattern).getParent();
    if (path == null) path = "";
    thumbs = new ThumbnailHandler(this,
      path + File.separator + name + ".visbio");
    status(5, numTasks, "Done");
  }

  // -- Saveable API methods --

  /** Writes the current state to the given DOM element ("DataTransforms"). */
  public void saveState(Element el) throws SaveException {
    Element child = XMLUtil.createChild(el, "Dataset");
    super.saveState(child);
    child.setAttribute("pattern", pattern);
  }

  /** Restores the current state from the given DOM element ("Dataset"). */
  public void restoreState(Element el) throws SaveException {
    super.restoreState(el);
    pattern = el.getAttribute("pattern");
  }

  // -- Helper methods --

  /** Gets the 1-D index for the given position array. */
  private int posToIndex(int[] pos) {
    int t = pos[0];
    int z = pos[1];

    // rasterize C dimensions
    int[] cLen = reader.getChannelDimLengths();
    int[] cPos = new int[pos.length - 2];
    System.arraycopy(pos, 2, cPos, 0, cPos.length);
    int c = FormatTools.positionToRaster(cLen, cPos);

    int index = reader.getIndex(z, c, t);
    return index;
  }

  /** Notifies constructor task listener of a status update. */
  private void status(int current, int max, String message) {
    if (listener == null) return;
    listener.statusUpdated(new StatusEvent(current, max, message));
  }

}
