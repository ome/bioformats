//
// OMEUpload.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan
and Eric Kjellman.

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

package loci.formats;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import org.openmicroscopy.ds.*;
import org.openmicroscopy.ds.dto.*;
import org.openmicroscopy.ds.managers.*;
import org.openmicroscopy.ds.st.*;
import org.openmicroscopy.is.*;
import org.openmicroscopy.xml.*;
import org.w3c.dom.*;

/**
 * Utility class for uploading files to an OME database.
 */
public class OMEUpload {

  // -- Fields --

  /** The server to login to. */
  private String server;

  /** OMEIS */
  private String omeis;

  /** Unique key for this login session. */
  private String sessionKey;

  /** Flag indicating that we have logged in. */
  private boolean loggedIn = false;

  /** Import helpers. */
  private DataServices rs;
  private RemoteCaller rc;
  private DataFactory df;
  private ImportManager im;
  private PixelsFactory pf;
  private Experimenter user;
  private Repository r;

  // -- OMEUpload API methods --

  /** Attempt to log in to the server. Returns true if successful. */
  public boolean login(String server, String user, String pass)
    throws FormatException, IOException
  {
    this.server = server + "/shoola";
    omeis = server + "/cgi-bin/omeis";

    Vector v = new Vector();
    v.add(user);
    v.add(pass);

    sessionKey = sendRequest(buildXML("createSession", v));
    if (sessionKey.indexOf("fault") != -1) return false;

    try {
      sessionKey = sessionKey.substring(sessionKey.indexOf("<string>") + 8,
        sessionKey.indexOf("</string>"));
    }
    catch (Exception e) { return false; }

    loggedIn = true;
    return true;
  }

  /**
   * Close the current OME session.
   */
  public void logout() throws IOException {
    Vector v = new Vector();
    v.add(sessionKey);
    sendRequest(buildXML("closeSession", v));
  }

  /** Upload a set of planes. */
  public void uploadPlanes(byte[][] planes, int x, int y, int z, int c, int t,
    int[] zs, int[] cs, int[] ts, String name, String xml)
    throws FormatException, IOException
  {
    if (!loggedIn) {
      throw new FormatException("Not logged in to OME server.");
    }

    initialize(planes.length * planes[0].length);
    im.startImport(user);

    int bpp = planes[0].length / (x * y);
    if (bpp == 0) bpp++;
    String pix = newPixels(x, y, z, c, t, bpp);

    for (int i=0; i<planes.length; i++) {
      uploadPlane(planes[i], zs[i], cs[i], ts[i], pix);
    }

    pix = closePixels(pix).trim();
    OMEXMLMetadataStore store = new OMEXMLMetadataStore();
    store.createRoot(xml);
    createPixelData(x, y, z, c, t, bpp, pix, name, store);

    logout();
  }

  /**
   * Upload a file.
   *
   * @param file - the filename to upload
   * @param stitch - set to true if files with a similar name should be uploaded
   */
  public void uploadFile(String file, boolean stitch)
    throws FormatException, IOException
  {
    if (!loggedIn) {
      throw new FormatException("Not logged in to OME server.");
    }

    // initialize readers

    ImageReader reader = new ImageReader();
    FormatReader fr = (FormatReader) reader.getReader(file);
    FileStitcher fs = stitch ? new FileStitcher(fr) : null;
    fr.setSeparated(true);
    OMEXMLMetadataStore store = new OMEXMLMetadataStore();
    store.createRoot();
    fr.setMetadataStore(store);
    if (fs != null) fs.setSeparated(true);
    if (fs != null) fs.setMetadataStore(store);

    // add the logical channel elements

    int sizeX = stitch ? fs.getSizeX(file) : fr.getSizeX(file);
    int sizeY = stitch ? fs.getSizeY(file) : fr.getSizeY(file);
    int sizeZ = stitch ? fs.getSizeZ(file) : fr.getSizeZ(file);
    int sizeC = stitch ? fs.getSizeC(file) : fr.getSizeC(file);
    int sizeT = stitch ? fs.getSizeT(file) : fr.getSizeT(file);

    store = (OMEXMLMetadataStore)
      (stitch ? fs.getMetadataStore(file) : fr.getMetadataStore(file));

    int num = stitch ? fs.getImageCount(file) : fr.getImageCount(file);

    initialize(sizeX * sizeY * sizeZ * sizeC * sizeT);
    im.startImport(user);

    int bpp = fr.openBytes(file, 0).length / (sizeX * sizeY);
    if (bpp == 0) bpp++;
    String pix = newPixels(sizeX, sizeY, sizeZ, sizeC, sizeT, bpp);

    byte[] b = null;

    for (int i=0; i<num; i++) {
      b = stitch ? fs.openBytes(file, i) : fr.openBytes(file, i);

      int[] coords =
        stitch ? fs.getZCTCoords(file, i) : fr.getZCTCoords(file, i);
      uploadPlane(b, coords[0], coords[1], coords[2], pix);
    }

    pix = closePixels(pix).trim();
    createPixelData(sizeX, sizeY, sizeZ, sizeC, sizeT, bpp, pix,
      file.substring(file.lastIndexOf(File.separator)), store);

    logout();
  }

  // -- Helper methods --

  /** Initialize helper objects. */
  private void initialize(int size) {
    try {
      rs = DataServer.getDefaultServices(server);
    }
    catch (Exception e) { }

    rc = rs.getRemoteCaller();
    rc.setSessionKey(sessionKey);

    df = (DataFactory) rs.getService(DataFactory.class);
    im = (ImportManager) rs.getService(ImportManager.class);
    pf = (PixelsFactory) rs.getService(PixelsFactory.class);

    FieldsSpecification fields = new FieldsSpecification();
    fields.addWantedField("id");
    fields.addWantedField("experimenter");
    fields.addWantedField("experimenter", "id");
    UserState userState = df.getUserState(fields);
    user = userState.getExperimenter();

    try {
      r = pf.findRepository(size);
    }
    catch (Exception e) { }
  }

  /** Make pixel data available to the data server. */
  private void createPixelData(int x, int y, int z, int c, int t, int bpp,
    String pix, String name, OMEXMLMetadataStore store)
  {
    Image image = (Image) df.createNew(Image.class);
    image.setName(name);
    image.setOwner(user);
    image.setInserted("now");
    image.setCreated("now");
    df.markForUpdate(image);

    List images = new ArrayList();

    images.add(image);

    ModuleExecution ii = im.getImageImportMEX(image);
    ii.setExperimenter(user);

    Pixels pixels = (Pixels) df.createNew("Pixels");
    pixels.setRepository(r);
    pixels.setImage(image);
    pixels.setModuleExecution(ii);
    pixels.setImageServerID(new Long(pix));
    pixels.setSizeX(new Integer(x));
    pixels.setSizeY(new Integer(y));
    pixels.setSizeZ(new Integer(z));
    pixels.setSizeC(new Integer(c));
    pixels.setSizeT(new Integer(t));
    pixels.setPixelType(PixelTypes.getPixelType(bpp, false, false));

    df.markForUpdate(pixels);

    try {
      pf.setThumbnail(pixels, CompositingSettings.createDefaultPGISettings(
        z, c, t));
    }
    catch (Exception e) { }

    LogicalChannel logical = (LogicalChannel) df.createNew("LogicalChannel");
    logical.setImage(image);
    logical.setModuleExecution(ii);
    logical.setFluor("Gray 00");
    logical.setPhotometricInterpretation("monochrome");
    df.markForUpdate(logical);

    PixelChannelComponent physical = (PixelChannelComponent)
      df.createNew("PixelChannelComponent");
    physical.setImage(image);
    physical.setPixels(pixels);
    physical.setIndex(new Integer(0));
    physical.setLogicalChannel(logical);
    physical.setModuleExecution(ii);
    df.markForUpdate(physical);

    df.updateMarked();
    parseXML((OMEXMLNode) store.getRoot(), df, ii);

    ii.setStatus("FINISHED");
    df.markForUpdate(ii);
    try { df.updateMarked(); }
    catch (Exception e) { }

    image.setDefaultPixels(pixels);
    df.update(image);
    try { df.updateMarked(); }
    catch (Exception e) { }
  }

  /** Parse OME-XML tree and mark it for updating. */
  private void parseXML(OMEXMLNode xml, DataFactory df, ModuleExecution mex) {
    NodeList children = xml.getDOMElement().getChildNodes();
    Object[] childs = new Object[children.getLength()];

    for (int i=0; i<childs.length; i++) {
      OMEXMLNode child = OMEXMLNode.createNode((Element) children.item(i));
      Class dtoClass = child.getDTOType();
      Object o = null;
      if (dtoClass == null) {
        parseXML(child, df, mex);
        break;
      }

      try {
        dtoClass = Class.forName(dtoClass.getName() + "DTO");
        o = dtoClass.newInstance();
      }
      catch (Exception e) { }

      if (o != null && dtoClass != null) {
        Method[] dtoMethods = dtoClass.getMethods();
        Method[] childMethods = child.getClass().getMethods();

        Vector getMethods = new Vector();
        Vector setMethods = new Vector();

        for (int j=0; j<dtoMethods.length; j++) {
          if (dtoMethods[j].getName().startsWith("set")) {
            if (dtoMethods[j].getParameterTypes().length == 1) {
              setMethods.add(dtoMethods[j]);
            }
          }
        }

        for (int j=0; j<childMethods.length; j++) {
          if (childMethods[j].getName().startsWith("get")) {
            if (childMethods[j].getParameterTypes().length == 0) {
              getMethods.add(childMethods[j]);
            }
          }
        }

        for (int j=0; j<getMethods.size(); j++) {
          Method getMethod = (Method) getMethods.get(j);
          String getName = getMethod.getName();
          String setName = "set" + getName.substring(3);
          Method setMethod = null;
          for (int k=0; k<setMethods.size(); k++) {
            if (((Method) setMethods.get(k)).getName().equals(setName)) {
              setMethod = (Method) setMethods.get(k);
              break;
            }
          }

          try {
            Object tmp = getMethod.invoke(child, null);
            setMethod.invoke(o, new Object[] {tmp});
          }
          catch (Exception e) { }
        }

        try {
          Method mexMethod = dtoClass.getMethod("setModuleExecution",
            new Class[] {ModuleExecution.class});
          mexMethod.invoke(o, new Object[] {mex});
        }
        catch (Exception e) { }
        df.markForUpdate((DataInterface) o);

        parseXML(child, df, mex);
      }
    }
  }

  /** Create a new pixels file with the specified dimensions. */
  private String newPixels(int x, int y, int z, int c, int t, int bpp)
    throws IOException
  {
    Vector keys = new Vector();
    Vector values = new Vector();

    keys.add("SessionKey");
    keys.add("Method");
    keys.add("Dims");

    values.add(sessionKey);
    values.add("NewPixels");
    values.add(x + "," + y + "," + z + "," + c + "," + t + "," + bpp);

    return buildMultipart(keys, values).trim();
  }

  /** Close the given pixels file. */
  private String closePixels(String id)
    throws IOException
  {
    Vector keys = new Vector();
    Vector values = new Vector();

    keys.add("SessionKey");
    keys.add("Method");
    keys.add("PixelsID");

    values.add(sessionKey);
    values.add("FinishPixels");
    values.add(id);

    return buildMultipart(keys, values);
  }

  /** Upload a single plane to OMEIS (creates a new pixels file). */
  private void uploadPlane(byte[] b, int z, int c, int t, String id)
    throws IOException
  {
    Vector keys = new Vector();
    Vector values = new Vector();

    keys.add("SessionKey");
    keys.add("Method");
    keys.add("PixelsID");
    keys.add("theZ");
    keys.add("theC");
    keys.add("theT");
    keys.add("Pixels");

    values.add(sessionKey);
    values.add("SetPlane");
    values.add(id);
    values.add("" + z);
    values.add("" + c);
    values.add("" + t);
    values.add(new String(b));

    buildMultipart(keys, values);
  }

  /** Send a generic request to the server and return the response. */
  private String sendRequest(String request) throws IOException {
    HttpURLConnection conn =
      (HttpURLConnection) (new URL(server)).openConnection();
    conn.setRequestMethod("POST");
    conn.setDoOutput(true);
    OutputStream os = conn.getOutputStream();

    os.write(request.getBytes());
    os.close();

    InputStream is = conn.getInputStream();
    int numRead = 0;
    byte[] b = new byte[0];
    while (numRead == 0) {
      b = new byte[is.available()];
      numRead = is.read(b);
    }
    is.close();
    return new String(b);
  }

  /** Build an XMLRPC request. */
  private String buildXML(String method, Vector params) throws IOException {
    String request = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
      "<methodCall><methodName>" + method + "</methodName><params>";

    for (int i=0; i<params.size(); i++) {
      request += "<param><value>" + params.get(i) + "</value></param>";
    }

    request += "</params></methodCall>";
    return request;
  }

  /** Build a multipart form request and send to the server. */
  private String buildMultipart(Vector keys, Vector values) throws IOException {
    String boundary =
      "------------------------------" + System.currentTimeMillis();
    String lf = "\r\n";

    HttpURLConnection conn =
      (HttpURLConnection) (new URL(omeis)).openConnection();
    conn.setRequestMethod("POST");
    conn.setDoOutput(true);

    conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" +
      boundary);

    OutputStream os = conn.getOutputStream();

    os.write((boundary + lf).getBytes());

    for (int i=0; i<keys.size(); i++) {
      os.write(("Content-Disposition: form-data; ").getBytes());
      os.write(("name=\"" + keys.get(i) + "\"" + lf + lf).getBytes());
      if (keys.get(i).toString().equals("Pixels")) {
        os.write(
          ("Content-Type: application/octet-stream" + lf + lf).getBytes());
      }
      os.write((values.get(i) + lf + boundary + lf).getBytes());
    }

    os.close();

    InputStream is = conn.getInputStream();
    int numRead = 0;
    byte[] b = new byte[0];
    while (numRead == 0) {
      b = new byte[is.available()];
      numRead = is.read(b);
    }
    is.close();

    return new String(b);
  }

}
