//
// OMEROReader.java
//

/*
OME database I/O package for communicating with OME and OMERO servers.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden and Philip Huettl.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.ome.io;

import java.io.IOException;
import java.util.*;
import loci.common.*;
import loci.formats.*;
import loci.formats.meta.MetadataStore;
import ome.api.IAdmin;
import ome.api.IPixels;
import ome.api.IQuery;
import ome.api.RawPixelsStore;
import ome.model.IObject;
//import ome.model.core.Pixels;
import ome.parameters.Parameters;
import ome.system.EventContext;
import ome.system.Login;
import ome.system.Server;
import ome.system.ServiceFactory;
import pojos.ImageData;
import pojos.PixelsData;

/**
 * OMEROReader is a file format reader for downloading images from an
 * OMERO database.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/ome-io/src/loci/formats/ome/OMEROReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/ome-io/src/loci/formats/ome/OMEROReader.java">SVN</a></dd></dl>
 */
public class OMEROReader extends FormatReader {

  // -- Constants --

  private static final String NO_OMERO_MSG =
    "OMERO client libraries not found. " +
    "Please install the OMERO-client, OMERO-common, OMERO-importer and " +
    "OMERO-model libraries from " +
    "https://skyking.microscopy.wisc.edu/svn/java/trunk/jar/";

  // -- Static fields --

  private static boolean noOMERO = false;

  // -- Fields --

  private String username;
  private String password;
  private String serverName;
  private String port;

  private static ServiceFactory sf;
  private static Login login;
  private static Server server;
  private static long id;
  private static Parameters params;

  /** OMERO query service */
  private static IQuery query;

  /** OMERO raw pixels service */
  private static RawPixelsStore raw;

  // -- Constructor --

  /** Constructs a new OMERO reader. */
  public OMEROReader() { super("OMERO", "*"); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    StringTokenizer st = new StringTokenizer(name, "\n");
    return st.countTokens() == 5;
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessStream) */
  public boolean isThisType(RandomAccessStream stream) throws IOException {
    return true;
  }

  /*
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x1, int y1, int w1, int h1)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length);

    int[] zct = FormatTools.getZCTCoords(this, no);

    byte[] plane = raw.getPlane(zct[0], zct[1], zct[2]);
    int len = getSizeX() * getSizeY() *
      FormatTools.getBytesPerPixel(getPixelType());
    System.arraycopy((byte[]) plane, 0, buf, 0, len);

    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("OMEROReader.initFile(" + id + ")");

    OMECredentials cred = new OMECredentials(id);
    id = String.valueOf(cred.imageID);
    super.initFile(id);

    cred.isOMERO = true;

    username = cred.username;
    password = cred.password;
    port = cred.port;
    Long idObj = new Long(cred.imageID);

    login = new Login("allison","omero");
    server = new Server("http://localhost");
    sf = new ServiceFactory( login);

    IAdmin admin = sf.getAdminService();
    EventContext ec = admin.getEventContext();
    idObj = ec.getCurrentUserId();

    params = new Parameters().addId(idObj);

    query = sf.getQueryService();
    raw = sf.createRawPixelsStore();
    raw.setPixelsId(idObj.longValue());
    String q = ( "select p from Pixels as p " +
      "left outer join fetch p.pixelsType as pt " +
      "left outer join fetch p.channels as c " +
      "left outer join fetch p.pixelsDimensions " +
      "left outer join fetch p.image " +
      "left outer join fetch c.colorComponent " +
      "left outer join fetch c.logicalChannel as lc " +
      "left outer join fetch c.statsInfo " +
      "left outer join fetch lc.photometricInterpretation " +
      "where p.id = :id");

    params = new Parameters();
    params.addId(idObj);

    // NB - Need to reflect references to ome.model.core.Pixels,
    // because J2SE cannot it properly.
    //Pixels results = query.findByQuery(q, params);
    //PixelsData pix = new PixelsData(results);
    ReflectedUniverse r = new ReflectedUniverse();
    PixelsData pix = null;
    try {
      r.exec("import ome.model.core.Pixels");
      r.setVar("q", q);
      r.setVar("params", params);
      r.exec("results = query.findByQuery(q, params)");
      pix = (PixelsData) r.exec("new PixelsData(results)");
    }
    catch (ReflectException exc) {
      throw new FormatException(exc);
    }

    String ptype = pix.getPixelType();

    core[0].sizeX = pix.getSizeX();
    core[0].sizeY = pix.getSizeY();
    core[0].sizeZ = pix.getSizeZ();
    core[0].sizeC = pix.getSizeC();
    core[0].sizeT = pix.getSizeT();
    core[0].rgb = false;
    core[0].littleEndian = false;
    core[0].dimensionOrder = "XYZCT";
    core[0].imageCount = getSizeZ() * getSizeC() * getSizeT();
    core[0].pixelType = FormatTools.pixelTypeFromString((String) ptype);

    double px = pix.getPixelSizeX();
    double py = pix.getPixelSizeY();
    double pz = pix.getPixelSizeZ();

    ImageData image = pix.getImage();

    String name = image.getName();
    String description = image.getDescription();

    MetadataStore store = getMetadataStore();
    store.setImageName(name, 0);
    store.setImageDescription(description, 0);
    MetadataTools.populatePixels(store, this);

    store.setDimensionsPhysicalSizeX(new Float(px), 0, 0);
    store.setDimensionsPhysicalSizeY(new Float(py), 0, 0);
    store.setDimensionsPhysicalSizeZ(new Float(pz), 0, 0);
  }

}
