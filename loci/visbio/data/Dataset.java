//
// Dataset.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2004 Curtis Rueden.

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

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.*;
import java.io.*;
import java.math.BigInteger;
import java.util.Hashtable;
import javax.swing.JComponent;
import loci.visbio.state.Dynamic;
import loci.visbio.state.SaveException;
import loci.visbio.util.*;
import org.w3c.dom.Element;
import visad.*;
import visad.data.BadFormException;

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
 */
public class Dataset extends ImageTransform {

  // -- Static fields --

  /** Dataset import dialog. */
  protected static DatasetPane datasetImporter;


  // -- Data fields --

  /** A string pattern describing this dataset. */
  protected String pattern;

  /** Source file array. */
  protected String[] ids;


  // -- Computed fields --

  /** Data loaders, one for each source file in the array. */
  protected ImageFamily[] loaders;

  /** Controls for this dataset. */
  protected DatasetWidget controls;

  /** Number of images per source file. */
  protected int numImages;

  /** File format description of source files. */
  protected String format;

  /** Metadata associated with each source file. */
  protected Hashtable[] metadata;

  /** Range component count for each image. */
  protected int numRange;

  /** Width of each image. */
  protected int resX;

  /** Height of each image. */
  protected int resY;

  /** Computed offset coefficients into rasterized source file array. */
  protected int[] offsets;

  /** Types mapped to spatial components (X, Y). */
  protected RealType[] spatial;

  /** Types mapped to color components (RGBA). */
  protected RealType[] color;


  // -- Constructors --

  /** Constructs an uninitialized multidimensional data object. */
  public Dataset() { super(); }

  /**
   * Constructs a new multidimensional data object from the given list of
   * source files. Any multidimensional file organization can be specified,
   * rasterized, using the ids source file list coupled with the lengths
   * array, which lists the length of each dimension in the structure.
   * <p>
   * Also, a list of what each dimension in the multidimensional structure
   * means must be provided, using the dims array. Since each file can contain
   * multiple images, those images implicitly define another dimension whose
   * type must be specified as well. Thus, the dims array should be one element
   * longer than the lengths array, with the final element of dims describing
   * the type of this implicit dimension. Note that the number of images per
   * file is automatically detected and appended to the lengths array, so there
   * is no need to append it manually. Lastly, if there is only one image per
   * file, the final dims element is ignored, and lengths is not affected.
   *
   * @param pattern String pattern describing the dataset.
   * @param ids List of source files, with dimensions rasterized.
   * @param lengths List of dimension lengths.
   * @param dims List of each dimension's meaning (Time, Slice, etc.).
   */
  public Dataset(String name, String pattern,
    String[] ids, int[] lengths, String[] dims)
  {
    super(null, name);
    this.pattern = pattern;
    this.ids = ids;
    this.lengths = lengths;
    this.dims = dims;
    initState(null);
  }


  // -- Dataset API methods --

  /** Close all open ids. */
  public void close() throws VisADException, IOException {
    for (int i=0; i<ids.length; i++) loaders[i].close();
  }

  /** Gets the string pattern describing this dataset. */
  public String getPattern() { return pattern; }

  /** Gets filenames of all files in dataset. */
  public String[] getFilenames() { return ids; }

  /** Gets the number of images per source file. */
  public int getImagesPerSource() { return numImages; }

  /** Gets a description of the source files' file format. */
  public String getFileFormat() { return format; }

  /** Gets metadata associated with each source file. */
  public Hashtable[] getMetadata() { return metadata; }


  // -- ImageTransform API methods --

  /** Gets width of each image. */
  public int getImageWidth() { return resX; }

  /** Gets height of each image. */
  public int getImageHeight() { return resY; }

  /** Gets number of range components at each pixel. */
  public int getRangeCount() { return numRange; }


  // -- Static DataTransform API methods --

  /** Creates a new dataset, with user interaction. */
  public static DataTransform makeTransform(DataManager dm) {
    return makeTransform(dm, null, dm.getControlPanel());
  }

  /**
   * Creates a new dataset, with user interaction,
   * with the given default file.
   */
  public static DataTransform makeTransform(DataManager dm,
    File file, Component parent)
  {
    // create dataset import dialog if it doesn't already exist
    if (datasetImporter == null) {
      datasetImporter = new DatasetPane(SwingUtil.getVisBioFileChooser());
    }

    // if clipboard contains a file name, use it as the default file pattern
    if (file == null) {
      Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
      if (clip != null) {
        Transferable t = clip.getContents(null);
        if (t != null && t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
          String s = null;
          try { s = (String) t.getTransferData(DataFlavor.stringFlavor); }
          catch (IOException exc) { exc.printStackTrace(); }
          catch (UnsupportedFlavorException exc) { exc.printStackTrace(); }
          if (s != null) {
            File f = new File(s);
            File dir = f.getParentFile();
            if (f.exists() || (dir != null && dir.exists())) file = f;
          }
        }
      }
    }
    datasetImporter.selectFile(file);

    // get file pattern from dataset import dialog
    int rval = datasetImporter.showDialog(parent);
    return rval == DatasetPane.APPROVE_OPTION ?
      datasetImporter.getDataset() : null;
  }

  /**
   * Indicates whether this transform type would accept
   * the given transform as its parent transform.
   */
  public static boolean isValidParent(DataTransform data) { return false; }

  /** Indicates whether this transform type requires a parent transform. */
  public static boolean isParentRequired() { return false; }


  // -- DataTransform API methods --

  /** Obtains an image from the source(s) at the given dimensional position. */
  public Data getData(int[] pos, int dim, DataCache cache) {
    if (dim != 2) return null;
    if (cache != null) return cache.getData(this, pos, null, dim);

    int[] indices = getIndices(pos);
    int fileIndex = indices[0];
    if (fileIndex < 0 || fileIndex >= ids.length) {
      System.err.println("Invalid file number #" + fileIndex);
      return null;
    }
    int imgIndex = indices[1];
    String filename = "\"" + new File(ids[fileIndex]).getName() + "\"";

    DataImpl d = null;

    int numImg = -1;
    try { numImg = loaders[fileIndex].getBlockCount(ids[fileIndex]); }
    catch (IOException exc) { numImg = -1; }
    catch (VisADException exc) { numImg = -1; }
    if (numImg < 0) {
      System.err.println("Could not read file " + filename);
      return null;
    }
    else if (numImg == 0) {
      System.err.println("File " + filename + " contains no images");
      return null;
    }
    if (imgIndex < 0 || imgIndex >= numImg) {
      System.err.println("Invalid image number #" + (imgIndex + 1) +
        " for file " + filename + " (" + numImg + " found)");
      return null;
    }

    int tries = 3;
    while (tries > 0) {
      boolean again = false;
      try { d = loaders[fileIndex].open(ids[fileIndex], imgIndex); }
      catch (IOException exc) {
        String msg = exc.getMessage();
        if (msg != null && msg.indexOf("Bad file descriptor") >= 0) {
          // HACK - trap for catching sporadic exception; try again!
          if (tries == 0) {
            System.err.println("Unable to read image #" + (imgIndex + 1) +
              " from file " + filename);
            return null;
          }
          else again = true;
        }
      }
      catch (VisADException exc) {
        System.err.println("Unable to read image #" + (imgIndex + 1) +
          " from file " + filename);
        return null;
      }
      if (again) tries--;
      else break;
    }
    if (!(d instanceof FlatField)) {
      String className = d == null ? "null" : d.getClass().getName();
      System.err.println("Data chunk #" + (imgIndex + 1) + " from file " +
        filename + " is not an image (" + className + ")");
      return null;
    }
    return (FlatField) d;
  }

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
    int[] indices = getIndices(pos);
    int fileIndex = indices[0];
    int imgIndex = indices[1];
    String file = global ? ids[fileIndex] : new File(ids[fileIndex]).getName();
    return file + "/" + imgIndex;
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
        sb.append(getUnitDescription(dims[i]));
        if (lengths[i] != 1) sb.append("s");
        sb.append("</li>\n");
      }
    }

    // image resolution
    sb.append("<li>");
    sb.append(resX);
    sb.append(" x ");
    sb.append(resY);
    sb.append(" pixel");
    if (resX * resY != 1) sb.append("s");
    sb.append("</li>\n");

    // range component count
    sb.append("<li>");
    sb.append(numRange);
    sb.append(" range component");
    if (numRange != 1) sb.append("s");
    sb.append("</li>\n");
    sb.append("</ul>\n");

    // file count
    sb.append(ids.length);
    sb.append(" ");
    sb.append(format);
    sb.append(" in dataset.<br>\n");

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

    return ObjectUtil.objectsEqual(pattern, data.pattern) &&
      ObjectUtil.arraysEqual(ids, data.ids);
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
      ids = data.ids;
    }

    metadata = new Hashtable[ids.length];

    // make sure each file exists
    for (int i=0; i<ids.length; i++) {
      File file = new File(ids[i]);
      if (!file.exists()) {
        System.err.println("File \"" + file.getName() + "\" does not exist.");
        return;
      }
    }

    // initialize data loaders
    loaders = new ImageFamily[ids.length];
    for (int i=0; i<ids.length; i++) loaders[i] = new ImageFamily();

    // determine number of images per source file
    String filename = "\"" + new File(ids[0]).getName() + "\"";
    try {
      numImages = loaders[0].getBlockCount(ids[0]);
      format = loaders[0].getFormatString();
      if (format.startsWith("TIFF")) {
        format = (numImages > 1 ? "multi-page " : "single-image ") + format;
      }
      if (ids.length > 1) format += "s";
    }
    catch (BadFormException exc) {
      if (ids[0].toLowerCase().endsWith(".mov")) {
        if (loaders[0].canDoQT()) {
          System.err.println("The file seems to be in QuickTime format, " +
            "but the data appears corrupt or invalid. It may use an " +
            "unsupported codec or other feature.");
        }
        else {
          System.err.println("The file seems to be in QuickTime format, " +
            "but QuickTime for Java does not appear to be installed. " +
            "Please install QuickTime for Java from: " +
            "http://www.apple.com/quicktime/download/");
        }
      }
      else {
        System.err.println("The file format is not supported, " +
          "or the file data is corrupt or invalid.");
      }
      return;
    }
    catch (IOException exc) {
      System.err.println("Could not determine number of images per file. " +
        filename + " may be corrupt or invalid.");
      return;
    }
    catch (VisADException exc) {
      System.err.println("Could not determine number of images per file. " +
        filename + " may be corrupt or invalid.");
      return;
    }
    if (numImages > 1 && dims.length > lengths.length) {
      int[] nlen = new int[lengths.length + 1];
      System.arraycopy(lengths, 0, nlen, 0, lengths.length);
      nlen[lengths.length] = numImages;
      lengths = nlen;
    }
    makeLabels();

    // initialize offsets convenience array
    int len = numImages > 1 ? lengths.length - 1 : lengths.length;
    offsets = new int[len];
    if (len > 0) offsets[0] = 1;
    for (int i=1; i<len; i++) {
      offsets[i] = offsets[i - 1] * lengths[i - 1];
    }

    // load first image for analysis
    Data d = null;
    try { d = loaders[0].open(ids[0], 0); }
    catch (IOException exc) { d = null; }
    catch (VisADException exc) { d = null; }
    catch (NullPointerException exc) { d = null; }
    if (d == null) {
      System.err.println("Could not read the first image. " +
        filename + " may be corrupt or invalid.");
      return;
    }
    if (!(d instanceof FlatField)) {
      System.err.println("First data chunk is not an image. " +
        filename + " may be corrupt or invalid.");
      return;
    }
    FlatField img = (FlatField) d;

    // determine image resolution
    GriddedSet set = (GriddedSet) img.getDomainSet();
    int[] setLen = set.getLengths();
    resX = setLen[0];
    resY = setLen[1];

    // determine number of range components
    FunctionType ftype = (FunctionType) img.getType();
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
    numRange = color.length;

    // construct suggested mappings
    RealTupleType domain = ftype.getDomain();
    spatial = domain.getRealComponents();
    if (spatial.length < 2) {
      System.err.println("Data is not an image (" + ftype + ")");
      return;
    }

    // load metadata for each source file
    for (int i=0; i<ids.length; i++) {
      String fname = new File(ids[i]).getName();
      try { metadata[i] = loaders[i].getMetadata(ids[i]); }
      catch (IOException exc) { metadata[i] = null; }
      catch (VisADException exc) { metadata[i] = null; }
      if (metadata[i] == null) {
        System.err.println("Could not read metadata from " +
          fname + ". The file may be corrupt or invalid.");
        return;
      }
    }

    // construct metadata controls
    controls = new DatasetWidget(this);

    // construct thumbnail handler
    String path = new File(ids[0]).getParent();
    if (path == null) path = "";
    thumbs = new ThumbnailHandler(this,
      path + File.separator + name + ".visbio");
  }

  /**
   * Called when this object is being discarded in favor of
   * another object with a matching state.
   */
  public void discard() { }


  // -- Saveable API methods --

  /** Writes the current state to the given DOM element ("DataTransforms"). */
  public void saveState(Element el) throws SaveException {
    Element child = XMLUtil.createChild(el, "Dataset");
    super.saveState(child);
    child.setAttribute("pattern",
      pattern.replaceAll("<", "&lt;").replaceAll(">", "&gt;"));
    for (int i=0; i<ids.length; i++) {
      Element fel = XMLUtil.createChild(child, "Filename");
      XMLUtil.createText(fel, ids[i]);
    }
  }

  /** Restores the current state from the given DOM element ("Dataset"). */
  public void restoreState(Element el) throws SaveException {
    super.restoreState(el);
    pattern = el.getAttribute("pattern").replaceAll(
      "&lt;", "<").replaceAll("&gt;", ">");
    ids = ObjectUtil.stringToStringArray(el.getAttribute("ids"));
  }


  // -- Helper methods --

  /**
   * Gets the corresponding file and image indices
   * for the given dimensional position.
   */
  private int[] getIndices(int[] pos) {
    int fileIndex = 0;
    int imgIndex = 0;
    if (numImages > 1) {
      // file images form a new dimension
      for (int i=0; i<offsets.length; i++) fileIndex += offsets[i] * pos[i];
      imgIndex = pos[pos.length - 1];
    }
    else {
      // only one image per file
      for (int i=0; i<offsets.length; i++) fileIndex += offsets[i] * pos[i];
      imgIndex = 0;
    }
    return new int[] {fileIndex, imgIndex};
  }

}
