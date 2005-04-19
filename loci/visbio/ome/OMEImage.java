//
// OMEImage.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2005 Curtis Rueden.

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

package loci.visbio.ome;

import java.awt.Component;
import java.rmi.RemoteException;
import javax.swing.JOptionPane;
import loci.visbio.data.*;
import loci.visbio.state.Dynamic;
import loci.visbio.util.ObjectUtil;
import org.openmicroscopy.ds.DataFactory;
import org.openmicroscopy.ds.DataServices;
import org.openmicroscopy.ds.dto.Image;
import org.openmicroscopy.ds.st.Pixels;
import org.openmicroscopy.is.PixelsFactory;
import visad.*;

/**
 * A OMEImage object encompasses a multidimensional biological image series
 * obtained from an OME database.
 *
 * OMEImage keeps no data in memory itself, leaving that management to the
 * application, and just downloads data as necessary to return whatever the
 * application requests, according to the DataTransform API.
 */
public class OMEImage extends ImageTransform {

  // -- Static fields --

  /** OMEImage import dialog. */
  protected static OMELoginPane login;


  // -- Data fields --

  /** Associated OME server. */
  protected String server;

  /** Username for OME server. */
  protected String user;

  /** Password for OME server. */
  protected String password;

  /** ID for this image. */
  protected int imageId;


  // -- Computed fields --

  /** Associated download helper object. */
  protected ImageDownloader downloader;

  /** Associated data services. */
  protected DataServices rs;

  /** Associated data factory. */
  protected DataFactory df;

  /** Associated pixels factory. */
  protected PixelsFactory pf;

  /** Image object allowing access to the image on the OME server. */
  protected Image image;

  /** Pixels object allowing access to the default pixels on the OME server. */
  protected Pixels pixels;

  /** Length of X dimension in this image. */
  protected int sizeX;

  /** Length of Y dimension in this image. */
  protected int sizeY;

  /** Number of focal planes in this image. */
  protected int sizeZ;

  /** Number of time points in this image. */
  protected int sizeT;

  /** Number of channels in this image. */
  protected int sizeC;

  /** Index for Slice dimension. */
  protected int indexZ;

  /** Index for Time dimension. */
  protected int indexT;


  // -- Constructors --

  /** Constructs an uninitialized OME image object. */
  public OMEImage() { super(); }

  /**
   * Constructs a new multidimensional data object from the given
   * OME server, username, password and image ID.
   */
  public OMEImage(String server, String user, String password, int imageId) {
    super(null, null);
    this.server = server;
    this.user = user;
    this.password = password;
    this.imageId = imageId;
    initState(null);
  }


  // -- OMEImage API methods --

  /** Gets the OME server from which to access the image. */
  public String getServer() { return server; }

  /** Gets the username to use when accessing the OME server. */
  public String getUsername() { return user; }

  /** Gets the password to use when accessing the OME server. */
  public String getPassword() { return password; }

  /** Gets the image ID for the associated OME image. */
  public int getImageId() { return imageId; }


  // -- ImageTransform API methods --

  /** Gets width of each image. */
  public int getImageWidth() { return sizeX; }

  /** Gets height of each image. */
  public int getImageHeight() { return sizeY; }

  /** Gets number of range components at each pixel. */
  public int getRangeCount() { return sizeC; }


  // -- Static DataTransform API methods --

  /** Creates a new OME image, with user interaction. */
  public static DataTransform makeTransform(DataManager dm) {
    return makeTransform(dm, null, null, -1);
  }

  /**
   * Creates a new OME image, with user interaction,
   * using the given defaults.
   */
  public static DataTransform makeTransform(DataManager dm,
    String server, String user, int imageId)
  {
    // create OME login dialog if it doesn't already exist
    if (login == null) login = new OMELoginPane();
    if (server != null) login.setServer(server);
    if (user != null) login.setUser(user);

    // get login information from login dialog
    Component parent = dm.getControlPanel();
    int rval = login.showDialog(parent);
    if (rval != OMELoginPane.APPROVE_OPTION) return null;
    server = login.getServer();
    user = login.getUser();
    String password = login.getPassword();

    // get image ID to download
    if (imageId < 0) {
      String id = (String) JOptionPane.showInputDialog(parent, "Image ID:",
        "Download OME image", JOptionPane.INFORMATION_MESSAGE, null, null, "");
      if (id == null) return null;
      try { imageId = Integer.parseInt(id); }
      catch (NumberFormatException exc) { }
    }
    if (imageId < 0) return null;

    // confirm download before proceeding
    int val = JOptionPane.showConfirmDialog(parent, "Download image #" +
      imageId + " from server " + server + " as user " + user + "?",
      "VisBio", JOptionPane.YES_NO_OPTION);
    if (val != JOptionPane.YES_OPTION) return null;

    // make sure everything goes ok
    try { return new OMEImage(server, user, password, imageId); }
    catch (Exception exc) {
      JOptionPane.showMessageDialog(parent, "Sorry, there has been a " +
        "problem downloading from the server. Please try again.",
        "VisBio", JOptionPane.ERROR_MESSAGE);
    }
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

  /** Obtains an image from the source(s) at the given dimensional position. */
  public Data getData(int[] pos, int dim, DataCache cache) {
    if (dim != 2) return null;
    if (cache != null) return cache.getData(this, pos, null, dim);

    int z = indexZ < 0 ? 0 : pos[indexZ];
    int t = indexT < 0 ? 0 : pos[indexT];
    float[][] samples = downloader.downloadPixels(pf, pixels, z, t);

    FunctionType fieldType = getType();
    RealTupleType fieldDomain = fieldType.getDomain();
    try {
      Linear2DSet fieldSet = new Linear2DSet(fieldDomain,
        0, sizeX - 1, sizeX, sizeY - 1, 0, sizeY);
      FlatField field = new FlatField(fieldType, fieldSet);
      field.setSamples(samples, false);
      return field;
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
    return null;
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
    return server + ";" + imageId + ";" + ObjectUtil.arrayToString(pos);
  }

  /** Gets a description of this dataset, with HTML markup. */
  public String getHTMLDescription() {
    return super.getHTMLDescription();
/* CTR TODO getHTMLDescription
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
*/
  }


  // -- Dynamic API methods --

  /** Tests whether two dynamic objects have matching states. */
  public boolean matches(Dynamic dyn) {
    if (!super.matches(dyn) || !isCompatible(dyn)) return false;
    OMEImage data = (OMEImage) dyn;

    return ObjectUtil.objectsEqual(server, data.server) &&
      ObjectUtil.objectsEqual(user, data.user) &&
      ObjectUtil.objectsEqual(password, data.password) &&
      imageId == data.imageId;
  }

  /**
   * Tests whether the given dynamic object can be used as an argument to
   * initState, for initializing this dynamic object.
   */
  public boolean isCompatible(Dynamic dyn) { return dyn instanceof OMEImage; }

  /** Modifies this object's state to match that of the given object. */
  public void initState(Dynamic dyn) {
    if (dyn != null && !isCompatible(dyn)) return;
    super.initState(dyn);
    OMEImage data = (OMEImage) dyn;

    if (data != null) {
      server = data.server;
      user = data.user;
      password = data.password;
      imageId = data.imageId;
    }

    if (rs != null) downloader.logout(rs);

    // initialize download helper
    downloader = new ImageDownloader();

    // download image details
    rs = downloader.login(server, user, password);
    df = (DataFactory) rs.getService(DataFactory.class);
    pf = (PixelsFactory) rs.getService(PixelsFactory.class);
    image = downloader.downloadImage(df, imageId);
    name = image.getName();
    pixels = image.getDefaultPixels();
    sizeX = pixels.getSizeX().intValue();
    sizeY = pixels.getSizeY().intValue();
    sizeZ = pixels.getSizeZ().intValue();
    sizeT = pixels.getSizeT().intValue();
    sizeC = pixels.getSizeC().intValue();

    // populate lengths and dims arrays
    if (sizeZ == 1 && sizeT == 1) {
      lengths = new int[0];
      dims = new String[0];
      indexZ = indexT = -1;
    }
    else if (sizeZ == 1) {
      lengths = new int[] {sizeT};
      dims = new String[] {"Time"};
      indexZ = -1; indexT = 0;
    }
    else if (sizeT == 1) {
      lengths = new int[] {sizeZ};
      dims = new String[] {"Slice"};
      indexZ = 0; indexT = -1;
    }
    else {
      lengths = new int[] {sizeZ, sizeT};
      dims = new String[] {"Slice", "Time"};
      indexZ = 0; indexT = 1;
    }
    makeLabels();

    // construct thumbnail handler
    thumbs = new ThumbnailHandler(this, "cache.visbio"); // use global cache
  }

  /**
   * Called when this object is being discarded in favor of
   * another object with a matching state.
   */
  public void discard() { if (rs != null) downloader.logout(rs); }

}
