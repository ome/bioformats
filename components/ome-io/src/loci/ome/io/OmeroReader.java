//
// OmeroReader.java
//

/*
OME database I/O package for communicating with OME and OMERO servers.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden and Philip Huettl.

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

package loci.ome.io;

import java.io.IOException;

import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import ome.api.RawPixelsStore;
import ome.system.Login;
import ome.system.Server;
import ome.system.ServiceFactory;
import omero.api.RawPixelsStorePrx;
import omero.model.Image;
import omero.model.Pixels;

/**
 * Implementation of {@link loci.formats.IFormatReader}
 * for use in export from an OMERO Beta 4.0.x database.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/ome-io/src/loci/ome/io/OmeroReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/ome-io/src/loci/ome/io/OmeroReader.java">SVN</a></dd></dl>
 */
public class OmeroReader extends FormatReader {

  // -- Fields --

  private RawPixelsStore raw;
  private RawPixelsStorePrx prx;
  private Pixels pix;

  // -- Constructors --

  public OmeroReader() {
    super("OMERO", "*");
  }

  public OmeroReader(RawPixelsStore raw, Pixels pix) {
    this(pix, raw, null);
  }

  public OmeroReader(RawPixelsStorePrx prx, Pixels pix) {
    this(pix, null, prx);
  }

  private OmeroReader(Pixels pix, RawPixelsStore raw, RawPixelsStorePrx prx) {
    super("OMERO", "*");
    this.pix = pix;
    this.prx = prx;
    this.raw = raw;
    if (this.raw == null && this.prx == null) {
      throw new IllegalArgumentException(
        "Must specify either RawPixelsStore or RawPixelsStorePrx");
    }
    else if (this.raw != null && this.prx != null) {
      throw new IllegalArgumentException(
        "Must not specify both RawPixelsStore and RawPixelsStorePrx");
    }
  }

  // -- IFormatReader methods --

  public boolean isThisType(String name, boolean open) {
    return name.startsWith("omero:");
  }

  public byte[] openBytes(int no, byte[] buf, int x1, int y1, int w1, int h1)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length);

    int[] zct = FormatTools.getZCTCoords(this, no);

    byte[] plane = null;
    if (raw != null) {
      plane = raw.getPlane(zct[0], zct[1], zct[2]);
    }
    else if (prx != null) {
      try {
        plane = prx.getPlane(zct[0], zct[1], zct[2]);
      }
      catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    else throw new IllegalStateException("No raw pixels store available");

    int len = getSizeX() * getSizeY() *
      FormatTools.getBytesPerPixel(getPixelType());
    System.arraycopy((byte[]) plane, 0, buf, 0, len);

    return buf;
  }

  public void close() throws IOException {
    super.close();
  }

  protected void initFile(String id) throws FormatException, IOException {
    LOGGER.debug("OmeroReader.initFile({})", id);

    super.initFile(id);

    // parse credentials from id string

    LOGGER.info("Parsing credentials");

    if (!id.startsWith("omero:")) {
      throw new IllegalArgumentException("Not an OMERO id: " + id);
    }

    String address = null, user = null, pass = null;
    int port = 1099;
    long pid = -1;

    String[] tokens = id.split("\n");
    for (String token : tokens) {
      int equals = token.indexOf("=");
      if (equals < 0) continue;
      String key = token.substring(0, equals);
      String val = token.substring(equals + 1);
      if (key.equals("server")) address = val;
      else if (key.equals("user")) user = val;
      else if (key.equals("pass")) pass = val;
      else if (key.equals("port")) {
        try {
          port = Integer.parseInt(val);
        }
        catch (NumberFormatException exc) { }
      }
      else if (key.equals("pid")) {
        try {
          pid = Long.parseLong(val);
        }
        catch (NumberFormatException exc) { }
      }
    }

    if (address == null) {
      throw new IllegalArgumentException("No server address in id");
    }
    if (user == null) {
      throw new IllegalArgumentException("No username in id");
    }
    if (pass == null) {
      throw new IllegalArgumentException("No password in id");
    }
    if (pid < 0) {
      throw new IllegalArgumentException("No pixels ID in id");
    }

    System.out.println("Server: " + address);//TEMP
    System.out.println("Port: " + port);//TEMP
    System.out.println("User: " + user);//TEMP
    System.out.println("Pixels ID: " + pid);//TEMP

    // authenticate with OMERO server

    LOGGER.info("Logging in");

    Login login = new Login(user, pass);
    Server server = new Server(address, port);
    ServiceFactory sf = new ServiceFactory(login);

    // get raw pixels store and pixels

    LOGGER.info("Getting raw pixels store");

    raw = sf.createRawPixelsStore();
    raw.setPixelsId(pid, false);

/*
    CTR: Need to obtain omero.model.Pixels object from OMERO somehow.
    Ideally, utilize higher-level client-side Gateway API in OMERO SVN:
      components/tools/OmeroImageJ/Omero_ImageJ/src/ome/ij/data

    IAdmin admin = sf.getAdminService();
    EventContext ec = admin.getEventContext();
    Long userId = ec.getCurrentUserId();

    Parameters params = new Parameters().addId(userId);

    Query query = sf.getQueryService();
    String q = "select p from Pixels as p " +
      "left outer join fetch p.pixelsType as pt " +
      "left outer join fetch p.channels as c " +
      "left outer join fetch p.pixelsDimensions " +
      "left outer join fetch p.image " +
      "left outer join fetch c.colorComponent " +
      "left outer join fetch c.logicalChannel as lc " +
      "left outer join fetch c.statsInfo " +
      "left outer join fetch lc.photometricInterpretation " +
      "where p.id = :id";

    params = new Parameters();
    params.addId(idObj);

    Pixels results = query.findByQuery(q, params);
    PixelsData pix = new PixelsData(results);
*/

    // populate metadata

    LOGGER.info("Populating metadata");

    int sizeX = pix.getSizeX().getValue();
    int sizeY = pix.getSizeY().getValue();
    int sizeZ = pix.getSizeZ().getValue();
    int sizeC = pix.getSizeC().getValue();
    int sizeT = pix.getSizeT().getValue();
    String pixelType = pix.getPixelsType().getValue().getValue();

    core[0].sizeX = sizeX;
    core[0].sizeY = sizeY;
    core[0].sizeZ = sizeZ;
    core[0].sizeC = sizeC;
    core[0].sizeT = sizeT;
    core[0].rgb = false;
    core[0].littleEndian = false;
    core[0].dimensionOrder = "XYZCT";
    core[0].imageCount = sizeZ * sizeC * sizeT;
    core[0].pixelType = FormatTools.pixelTypeFromString(pixelType);

    // CTR TODO this is wrong
    double px = pix.getSizeX().getValue();
    double py = pix.getSizeY().getValue();
    double pz = pix.getSizeZ().getValue();

    Image image = pix.getImage();

    String name = image.getName().getValue();
    String description = image.getDescription().getValue();

    MetadataStore store = getMetadataStore();
    store.setImageName(name, 0);
    store.setImageDescription(description, 0);
    MetadataTools.populatePixels(store, this);

    store.setDimensionsPhysicalSizeX(new Double(px), 0, 0);
    store.setDimensionsPhysicalSizeY(new Double(py), 0, 0);
    store.setDimensionsPhysicalSizeZ(new Double(pz), 0, 0);
  }

}
