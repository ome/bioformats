//
// OMEROReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.ome;

import java.io.IOException;
import java.util.*;
import loci.formats.*;
import loci.formats.meta.MetadataStore;

/**
 * OMEROReader is the file format reader for downloading images from an
 * OMERO database.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/ome/OMEROReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/ome/OMEROReader.java">SVN</a></dd></dl>
 */
public class OMEROReader extends FormatReader {

  // -- Constants --

  private static final String NO_OMERO_MSG = "OMERO client libraries not " +
    "found.  Please install omero-common.jar and omero-client.jar from " +
    "http://www.loci.wisc.edu/ome/formats.html";

  // -- Static fields --

  private static boolean noOMERO = false;
  private static ReflectedUniverse r = createReflectedUniverse();

  private static ReflectedUniverse createReflectedUniverse() {
    r = null;
    try {
      r = new ReflectedUniverse();
      r.exec("import ome.api.IQuery");
      r.exec("import ome.api.RawPixelsStore");
      r.exec("import ome.parameters.Parameters");
      r.exec("import ome.system.Login");
      r.exec("import ome.system.Server");
      r.exec("import ome.system.ServiceFactory");
      r.exec("import pojos.ImageData");
      r.exec("import pojos.PixelsData");
    }
    catch (ReflectException e) {
      noOMERO = true;
      if (debug) LogTools.trace(e);
    }
    return r;
  }

  // -- Fields --

  private String username;
  private String password;
  private String serverName;
  private String port;

  // -- Constructor --

  /** Constructs a new OMERO reader. */
  public OMEROReader() { super("OMERO", "*"); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    return false;
  }

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length);

    int[] zct = FormatTools.getZCTCoords(this, no);
    try {
      r.setVar("z", new Integer(zct[0]));
      r.setVar("c", new Integer(zct[1]));
      r.setVar("t", new Integer(zct[2]));
      r.exec("plane = raw.getPlane(z, c, t)");
      int len = core.sizeX[0] * core.sizeY[0] *
        FormatTools.getBytesPerPixel(core.pixelType[0]);
      System.arraycopy((byte[]) r.getVar("plane"), 0, buf, 0, len);
    }
    catch (ReflectException e) {
      throw new FormatException(e);
    }
    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
  }

  /* @see loci.formats.IFormatHandler#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    StringTokenizer st = new StringTokenizer(name, "\n");
    return st.countTokens() == 5;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("OMEROReader.initFile(" + id + ")");

    OMECredentials cred = OMEUtils.parseCredentials(id);
    id = String.valueOf(cred.imageID);
    super.initFile(id);

    cred.isOMERO = true;

    try {
      r.setVar("user", cred.username);
      r.setVar("pass", cred.password);
      r.setVar("port", Integer.parseInt(cred.port));
      r.setVar("sname", cred.server);
      r.setVar("id", cred.imageID);
      r.setVar("idObj", new Long(cred.imageID));

      r.exec("login = new Login(user, pass)");
      r.exec("server = new Server(sname, port)");
      r.exec("sf = new ServiceFactory(server, login)");
      r.exec("query = sf.getQueryService()");
      r.exec("raw = sf.createRawPixelsStore()");
      r.exec("raw.setPixelsId(id)");
      r.setVar("q", "select p from Pixels as p " +
        "left outer join fetch p.pixelsType as pt " +
        "left outer join fetch p.channels as c " +
        "left outer join fetch p.pixelsDimensions " +
        "left outer join fetch p.image " +
        "left outer join fetch c.colorComponent " +
        "left outer join fetch c.logicalChannel as lc " +
        "left outer join fetch c.statsInfo " +
        "left outer join fetch lc.photometricInterpretation " +
        "where p.id = :id");

      r.exec("params = new Parameters()");
      r.exec("params.addId(idObj)");
      r.exec("results = query.findByQuery(q, params)");
      r.exec("pix = new PixelsData(results)");

      r.exec("ptype = pix.getPixelType()");
      r.exec("x = pix.getSizeX()");
      r.exec("y = pix.getSizeY()");
      r.exec("z = pix.getSizeZ()");
      r.exec("c = pix.getSizeC()");
      r.exec("t = pix.getSizeT()");

      core.sizeX[0] = ((Integer) r.getVar("x")).intValue();
      core.sizeY[0] = ((Integer) r.getVar("y")).intValue();
      core.sizeZ[0] = ((Integer) r.getVar("z")).intValue();
      core.sizeC[0] = ((Integer) r.getVar("c")).intValue();
      core.sizeT[0] = ((Integer) r.getVar("t")).intValue();
      core.rgb[0] = false;
      core.littleEndian[0] = false;
      core.currentOrder[0] = "XYZCT";
      core.imageCount[0] = core.sizeZ[0] * core.sizeC[0] * core.sizeT[0];
      core.pixelType[0] =
        FormatTools.pixelTypeFromString((String) r.getVar("ptype"));

      r.exec("px = pix.getPixelSizeX()");
      r.exec("py = pix.getPixelSizeY()");
      r.exec("pz = pix.getPixelSizeZ()");
      double px = ((Double) r.getVar("px")).doubleValue();
      double py = ((Double) r.getVar("py")).doubleValue();
      double pz = ((Double) r.getVar("pz")).doubleValue();

      r.exec("image = pix.getImage()");
      r.exec("name = image.getName()");
      r.exec("description = image.getDescription()");

      String name = (String) r.getVar("name");
      String description = (String) r.getVar("description");

      MetadataStore store = getMetadataStore();
      store.setImageName(name, 0);
      store.setImageDescription(description, 0);
      MetadataTools.populatePixels(store, this);

      store.setDimensionsPhysicalSizeX(new Float((float) px), 0, 0);
      store.setDimensionsPhysicalSizeY(new Float((float) py), 0, 0);
      store.setDimensionsPhysicalSizeZ(new Float((float) pz), 0, 0);
      // CTR CHECK
//      for (int i=0; i<core.sizeC[0]; i++) {
//        store.setLogicalChannel(i, null, null, null, null, null, null, null,
//          null, null, null, null, null, null, null, null, null, null, null,
//          null, null, null, null, null, null);
//      }
    }
    catch (ReflectException e) {
      throw new FormatException(e);
    }
  }
}
