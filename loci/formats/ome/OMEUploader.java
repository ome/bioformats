//
// OMEUploader.java
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

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.*;
import loci.formats.*;
import org.openmicroscopy.ds.*;
import org.openmicroscopy.ds.dto.*;
import org.openmicroscopy.ds.managers.*;
import org.openmicroscopy.ds.st.*;
import org.openmicroscopy.is.*;
import org.openmicroscopy.xml.*;

/** Utility methods for uploading pixels and metadata to an OME server. */
public class OMEUploader implements Uploader {

  // -- Fields --

  /** Data server with which to communicate. */
  private String server;

  /** Image server with which to communicate. */
  private String omeis;

  /** Name of the current user. */
  private String user;

  /** Password */
  private String pass;

  /** Unique key for this session. */
  private String sessionKey;

  /** ImageReader to use for opening files. */
  private static ImageReader reader = new ImageReader();

  /** Set to true if someone is currently logged in. */
  private boolean validLogin = false;

  /** List of original files. */
  private Vector files;

  /** Import helpers. */
  private DataServices rs;
  private RemoteCaller rc;
  private DataFactory df;
  private ImportManager im;
  private RemoteImportManager rim;
  private PixelsFactory pf;
  private Experimenter exp;
  private Repository r;

  // -- Constructors --

  /** Construct a new OMEUploader for the specified server. */
  public OMEUploader(String server) {
    this.server = server;
    files = new Vector();
  }

  /** Construct a new OMEUploader for the specified user on the server. */
  public OMEUploader(String server, String user, String pass)
    throws UploadException
  {
    this.server = server;
    this.user = user;
    this.pass = pass;
    login(server, user, pass);
    files = new Vector();
  }

  // -- Uploader API methods --

  /** Log in to the specified server, with the specified credentials. */
  public void login(String server, String user, String pass)
    throws UploadException
  {
    this.server = server + "/shoola";
    omeis = server + "/cgi-bin/omeis";

    if (!omeis.startsWith("http://")) omeis = "http://" + omeis;
    if (!this.server.startsWith("http://")) {
      this.server = "http://" + this.server;
    }

    Vector v = new Vector();
    v.add(user);
    v.add(pass);

    String xml = buildXML("createSession", v);

    sessionKey = sendRequest(xml);
    if (sessionKey.indexOf("fault") != -1) {
      throw new UploadException("Login failed.");
    }

    try {
      int ndx = sessionKey.indexOf("<string>");
      sessionKey = sessionKey.substring(ndx + 8,
        sessionKey.indexOf("</string>"));
    }
    catch (Exception e) {
      throw new UploadException("Login failed - invalid server response.", e);
    }
    validLogin = true;
    setupImport(0);
  }

  /** Log all users out of the current server. */
  public void logout() {
    Vector v = new Vector();
    v.add(sessionKey);
    try { sendRequest(buildXML("closeSession", v)); }
    catch (Exception e) { /* do nothing; we don't care if this fails */ }
    validLogin = false;
  }

  /**
   * Upload the planes in the given file to the server, creating a new
   * image in the process.  If the "stitch" flag is set to true, then all files
   * that are similar to the given file will be uploaded as well.
   *
   * @return the number of pixel bytes uploaded
   */
  public int uploadFile(String file, boolean stitch) throws UploadException {
    if (!validLogin) throw new UploadException("Not logged in to the server.");

    try {
      IFormatReader f = reader.getReader(file);
      OMEXMLMetadataStore store = new OMEXMLMetadataStore();
      store.createRoot();
      f.setMetadataStore(store);
      if (stitch) {
        f = new FileStitcher(f);
        FilePattern fp = new FilePattern(new loci.formats.Location(file));
        String[] names = fp.getFiles();
        for (int i=0; i<names.length; i++) files.add(names[i]);
      }
      else files.add(file);
      f = new ChannelSeparator(f);

      int bytesPerPixel = 4;
      String pixelType = FormatTools.getPixelTypeString(f.getPixelType(file));
      if (pixelType.indexOf("int") != -1) {
        pixelType = pixelType.substring(pixelType.indexOf("int") + 3);
        bytesPerPixel = Integer.parseInt(pixelType) / 8;
      }

      String id = newPixels(f.getSizeX(file), f.getSizeY(file),
        f.getSizeZ(file), f.getSizeC(file), f.getSizeT(file), bytesPerPixel);

      int num = f.getImageCount(file);
      int bytesUploaded = 0;

      for (int i=0; i<num; i++) {
        int[] coords = f.getZCTCoords(file, i);
        bytesUploaded += uploadPlane(f.openBytes(file, i), coords[0],
          coords[1], coords[2], id, !f.isLittleEndian(file));
      }

      id = closePixels(id).trim();
      uploadMetadata(f.getMetadataStore(file), id);
      return bytesUploaded;
    }
    catch (Exception e) { throw new UploadException(e); }
  }

  /**
   * Upload the planes in the given file to the server, placing them in the
   * given image, and optionally the given dataset.  If the "stitch" flag is
   * set to true, then all files that are similar to the given file will be
   * uploaded as well.
   *
   * @return the number of pixel bytes uploaded
   */
  public int uploadFile(String file, boolean stitch, Integer image,
    Integer dataset) throws UploadException
  {
    if (!validLogin) throw new UploadException("Not logged in to the server.");

    try {
      IFormatReader f = reader.getReader(file);
      OMEXMLMetadataStore store = new OMEXMLMetadataStore();
      store.createRoot();
      f.setMetadataStore(store);
      f = new ChannelSeparator(f);
      if (stitch) {
        f = new FileStitcher(f);
        FilePattern fp = new FilePattern(new loci.formats.Location(file));
        String[] names = fp.getFiles();
        for (int i=0; i<names.length; i++) files.add(names[i]);
      }
      else files.add(file);

      int num = f.getImageCount(file);
      int bytesUploaded = 0;

      for (int i=0; i<num; i++) {
        int[] coords = f.getZCTCoords(file, i);
        bytesUploaded += uploadPlane(f.openBytes(file, i), coords[0], coords[1],
          coords[2], image.toString(), !f.isLittleEndian(file));
      }

      String id = closePixels(image.toString());
      uploadMetadata(f.getMetadataStore(file), id.trim(), dataset);

      return bytesUploaded;
    }
    catch (Exception e) { throw new UploadException(e); }
  }

  /**
   * Upload a single BufferedImage to the server, creating a new
   * image in the process.  If the 'close' flag is set,
   * the pixels file will be closed.
   *
   * @return the number of pixel bytes uploaded
   */
  public int uploadPlane(BufferedImage plane, int num, MetadataStore store,
    boolean close) throws UploadException
  {
    if (!validLogin) throw new UploadException("Not logged in to the server.");

    byte[][] b = ImageTools.getBytes(plane);
    byte[] data = new byte[b.length * b[0].length];
    if (b.length == 1) data = b[0];
    else {
      for (int i=0; i<b.length; i++) {
        System.arraycopy(b[i], 0, data, i*b[i].length, b[i].length);
      }
    }
    return uploadPlane(data, num, store, close);
  }

  /**
   * Upload a single BufferedImage to the server, placing it in the
   * given image, and optionally the given dataset.  If the 'close' flag is
   * set, the pixels file will be closed.
   *
   * @return the number of pixel bytes uploaded
   */
  public int uploadPlane(BufferedImage plane, int num, MetadataStore store,
    Integer image, Integer dataset, boolean close) throws UploadException
  {
    if (!validLogin) throw new UploadException("Not logged in to the server.");

    byte[][] b = ImageTools.getBytes(plane);
    byte[] data = new byte[b.length * b[0].length];
    if (b.length == 1) data = b[0];
    else {
      for (int i=0; i<b.length; i++) {
        System.arraycopy(b[i], 0, data, i*b[i].length, b[i].length);
      }
    }
    return uploadPlane(data, num, store, image, dataset, close);
  }

  /**
   * Upload a single byte array to the server, creating a new
   * image in the process.  Assumes the byte array represents the first plane.
   *
   * @return the number of pixel bytes uploaded
   */
  public int uploadPlane(byte[] plane, int num, MetadataStore store,
    boolean close) throws UploadException
  {
    if (!validLogin) throw new UploadException("Not logged in to the server.");

    OMEXMLMetadataStore xmlStore = (OMEXMLMetadataStore) store;
    int bytesPerPixel = 4;
    String pixelType = xmlStore.getPixelType(null);
    if (pixelType.indexOf("int") != -1) {
      pixelType = pixelType.substring(pixelType.indexOf("int") + 3);
      bytesPerPixel = Integer.parseInt(pixelType) / 8;
    }

    int sizeX = xmlStore.getSizeX(null).intValue();
    int sizeY = xmlStore.getSizeY(null).intValue();
    int sizeZ = xmlStore.getSizeZ(null).intValue();
    int sizeC = xmlStore.getSizeC(null).intValue();
    int sizeT = xmlStore.getSizeT(null).intValue();

    String id = newPixels(sizeX, sizeY, sizeZ, sizeC, sizeT, bytesPerPixel);

    int bytes = uploadPlane(plane, num, store, new Integer(id), null, close);
    if (close) {
      id = closePixels(id).trim();
      uploadMetadata(store, id);
    }
    return bytes;
  }

  /**
   * Upload a single byte array to the server, placing it in the given image,
   * and optionally the given dataset.
   *
   * @return the number of pixel bytes uploaded
   */
  public int uploadPlane(byte[] plane, int num, MetadataStore store,
    Integer image, Integer dataset, boolean close) throws UploadException
  {
    if (!validLogin) throw new UploadException("Not logged in to the server.");

    OMEXMLMetadataStore xmlStore = (OMEXMLMetadataStore) store;
    int bytesPerPixel = 4;
    String pixelType = xmlStore.getPixelType(null);
    if (pixelType.indexOf("int") != -1) {
      pixelType = pixelType.substring(pixelType.indexOf("int") + 3);
      bytesPerPixel = Integer.parseInt(pixelType) / 8;
    }

    int sizeX = xmlStore.getSizeX(null).intValue();
    int sizeY = xmlStore.getSizeY(null).intValue();
    int sizeZ = xmlStore.getSizeZ(null).intValue();
    int sizeC = xmlStore.getSizeC(null).intValue();
    int sizeT = xmlStore.getSizeT(null).intValue();

    String id = image.toString();

    // check if the plane contains multiple channels; if so, we need to split
    // the plane and upload each channel separately

    int bytesPerChannel = sizeX * sizeY * bytesPerPixel;
    int bytesUploaded = 0;
    try {
      if (plane.length > bytesPerChannel) {
        byte[] b = new byte[bytesPerChannel];
        for (int i=0; i<plane.length / bytesPerChannel; i++) {
          System.arraycopy(plane, i*bytesPerChannel, b, 0, bytesPerChannel);
          int[] indices = FormatTools.getZCTCoords(
            xmlStore.getDimensionOrder(null), sizeZ, sizeC, sizeT,
            sizeZ * sizeC * sizeT, num);
          bytesUploaded +=
            uploadPlane(plane, indices[0], indices[1], indices[2], id,
              xmlStore.getBigEndian(null).booleanValue());
        }

        if (close) {
          id = closePixels(id).trim();
          uploadMetadata(store, id, dataset);
        }

        return bytesUploaded;
      }
      int[] indices = FormatTools.getZCTCoords(
        xmlStore.getDimensionOrder(null), sizeZ, sizeC, sizeT,
        sizeZ * sizeC * sizeT, num);
      int bytes = uploadPlane(plane, indices[0], indices[1], indices[2], id,
        xmlStore.getBigEndian(null).booleanValue());

      if (close) {
        id = closePixels(id).trim();
        uploadMetadata(store, id, dataset);
      }
      return bytes;
    }
    catch (Exception f) { throw new UploadException(f); }
  }

  /**
   * Upload an array of BufferedImages to the server, creating a new image
   * in the process.
   *
   * @return the number of pixel bytes uploaded
   */
  public int uploadPlanes(BufferedImage[] planes, int first, int last,
    int step, MetadataStore store, boolean close) throws UploadException
  {
    if (!validLogin) throw new UploadException("Not logged in to the server.");

    OMEXMLMetadataStore xmlStore = (OMEXMLMetadataStore) store;

    int sizeX = xmlStore.getSizeX(null).intValue();
    int sizeY = xmlStore.getSizeY(null).intValue();
    int sizeZ = xmlStore.getSizeZ(null).intValue();
    int sizeC = xmlStore.getSizeC(null).intValue();
    int sizeT = xmlStore.getSizeT(null).intValue();
    int bytesPerPixel = 4;
    String pixelType = xmlStore.getPixelType(null);
    if (pixelType.indexOf("int") != -1) {
      pixelType = pixelType.substring(pixelType.indexOf("int") + 3);
      bytesPerPixel = Integer.parseInt(pixelType) / 8;
    }

    String id = newPixels(sizeX, sizeY, sizeZ, sizeC, sizeT, bytesPerPixel);

    return uploadPlanes(planes, first, last, step, store, new Integer(id),
      null, close);
  }

  /**
   * Upload an array of BufferedImages to the server, placing them in the given
   * image, and optionally the given dataset.
   *
   * @return the number of pixel bytes uploaded
   */
  public int uploadPlanes(BufferedImage[] planes, int first, int last,
    int step, MetadataStore store, Integer image, Integer dataset,
    boolean close) throws UploadException
  {
    if (!validLogin) throw new UploadException("Not logged in to the server.");

    int uploaded = 0;
    int ndx = 0;
    for (int i=first; i<=last; i+=step) {
      uploaded += uploadPlane(planes[ndx], i, store, image, dataset,
        i == last && close);
      ndx++;
    }
    return uploaded;
  }

  /**
   * Upload an array of byte arrays to the server, creating a new image
   * in the process.
   *
   * @return the number of pixel bytes uploaded
   */
  public int uploadPlanes(byte[][] planes, int first, int last, int step,
    MetadataStore store, boolean close) throws UploadException
  {
    if (!validLogin) throw new UploadException("Not logged in to the server.");

    OMEXMLMetadataStore xmlStore = (OMEXMLMetadataStore) store;

    int sizeX = xmlStore.getSizeX(null).intValue();
    int sizeY = xmlStore.getSizeY(null).intValue();
    int sizeZ = xmlStore.getSizeZ(null).intValue();
    int sizeC = xmlStore.getSizeC(null).intValue();
    int sizeT = xmlStore.getSizeT(null).intValue();
    int bytesPerPixel = 4;
    String pixelType = xmlStore.getPixelType(null);
    if (pixelType.indexOf("int") != -1) {
      pixelType = pixelType.substring(pixelType.indexOf("int") + 3);
      bytesPerPixel = Integer.parseInt(pixelType) / 8;
    }

    String id = newPixels(sizeX, sizeY, sizeZ, sizeC, sizeT, bytesPerPixel);

    return uploadPlanes(planes, first, last, step, store, new Integer(id),
      null, close);
  }

  /**
   * Upload an array of byte arrays to the server, placing them in the given
   * image, and optionally the given dataset.
   *
   * @return the number of pixel bytes uploaded
   */
  public int uploadPlanes(byte[][] planes, int first, int last, int step,
    MetadataStore store, Integer image, Integer dataset, boolean close)
    throws UploadException
  {
    if (!validLogin) throw new UploadException("Not logged in to the server.");

    int uploaded = 0;
    int ndx = 0;
    for (int i=first; i<=last; i+=step) {
      uploaded += uploadPlane(planes[ndx], i, store, image, dataset,
        i == last && close);
      ndx++;
    }
    return uploaded;
  }

  // -- Utility methods --

  /** Send a request string to the server and return the response. */
  private String sendRequest(String request) throws UploadException {
    try {
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
    catch (Exception e) { throw new UploadException(e); }
  }

  /** Build an XML-RPC request. */
  private String buildXML(String method, Vector params) throws UploadException
  {
    try {
      String request = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
        "<methodCall><methodName>" + method + "</methodName><params>";

      for (int i=0; i<params.size(); i++) {
        request += "<param><value>" + params.get(i) + "</value></param>";
      }

      request += "</params></methodCall>";

      return request;
    }
    catch (Exception e) { throw new UploadException(e); }
  }

  /** Build a multi-part form request and send to the server. */
  private String buildMultipart(Vector keys, Vector values)
    throws UploadException
  {
    try {
      String boundary = "------------------------------" +
        System.currentTimeMillis();

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
        os.write(("name=\"" + keys.get(i) + "\"").getBytes());
        if (keys.get(i).toString().equals("File")) {
          String file = files == null || files.size() == 0 ? "unknown" :
            files.get(0).toString();
          os.write(("; filename=\"" + file + "\"" + lf +
            "Content-Type: application/octet-stream").getBytes());
        }
        os.write((lf + lf + values.get(i) + lf + boundary + lf).getBytes());
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
    catch (Exception e) { throw new UploadException(e); }
  }

  /** Create a new pixels file with the specified dimensions. */
  private String newPixels(int x, int y, int z, int c, int t, int bpp)
    throws UploadException
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
  private String closePixels(String id) throws UploadException {
    Vector keys = new Vector();
    Vector values = new Vector();

    keys.add("SessionKey");
    keys.add("Method");
    keys.add("PixelsID");

    values.add(sessionKey);
    values.add("FinishPixels");
    values.add(id);

    return buildMultipart(keys, values).trim();
  }

  /** Upload a single plane to the given OMEIS pixels file. */
  private int uploadPlane(byte[] b, int z, int c, int t, String id,
    boolean bigEndian) throws UploadException
  {
    Vector keys = new Vector();
    Vector values = new Vector();

    keys.add("SessionKey");
    keys.add("Method");
    keys.add("PixelsID");
    keys.add("theZ");
    keys.add("theC");
    keys.add("theT");
    keys.add("BigEndian");
    keys.add("Pixels");

    values.add(sessionKey);
    values.add("SetPlane");
    values.add(id);
    values.add("" + z);
    values.add("" + c);
    values.add("" + t);
    values.add(bigEndian ? "1" : "0");
    values.add(new String(b));

    int bytes = Integer.parseInt(buildMultipart(keys, values).trim());
    return bytes;
  }

  /**
   * Set everything up to begin the import.
   *
   * @param int size - the number of pixel bytes we expect to upload
   */
  private void setupImport(long size) throws UploadException {
    if (files != null) files.clear();

    try {
      rs = DataServer.getDefaultServices(server);
    }
    catch (Exception e) { throw new UploadException(e); }

    rc = rs.getRemoteCaller();
    rc.login(user, pass);

    df = (DataFactory) rs.getService(DataFactory.class);
    im = (ImportManager) rs.getService(ImportManager.class);
    rim = (RemoteImportManager) rs.getService(RemoteImportManager.class);
    pf = (PixelsFactory) rs.getService(PixelsFactory.class);

    FieldsSpecification fields = new FieldsSpecification();
    fields.addWantedField("id");
    fields.addWantedField("experimenter");
    fields.addWantedField("experimenter", "id");
    UserState userState = df.getUserState(fields);
    exp = userState.getExperimenter();

    try {
      r = pf.findRepository(size);
    }
    catch (Exception e) { throw new UploadException(e); }

    im.startImport(exp);
  }

  /**
   * Parse and upload the given MetadataStore and link
   * it to the given OMEIS ID.
   */
  private void uploadMetadata(MetadataStore store, String id)
    throws UploadException
  {
    uploadMetadata(store, id, null);
  }

  /**
   * Parse and upload the given MetadataStore and link it to the
   * given OMEIS ID.  The image is then placed in the given dataset.
   */
  private void uploadMetadata(MetadataStore store, String id, Integer dataset)
    throws UploadException
  {
    OMEXMLMetadataStore xml = (OMEXMLMetadataStore) store;

    // upload the OME-XML

    String xmlString = "";

    try {
      xmlString = ((OMENode) xml.getRoot()).writeOME(false);
    }
    catch (Exception e) { throw new UploadException(e); }

    Vector keys = new Vector();
    Vector values = new Vector();

    keys.add("SessionKey");
    keys.add("Method");
    keys.add("File");

    values.add(sessionKey);
    values.add("UploadFile");
    values.add(xmlString + "\n\n");

    // Import the XML into OMEDS; this will create a new Image, which we
    // can then (theoretically) populate with a Pixels file from OMEIS.

    String xmlID = buildMultipart(keys, values).trim();

    ArrayList v = new ArrayList();
    v.add(new Long(xmlID));

    List imageIDs = rim.importFiles(v);

    // set the Image information

    if (imageIDs == null) return;

    Image image = (Image) df.createNew(Image.class);
    image.setID(((Integer) imageIDs.get(0)).intValue());

    String name = xml.getImageName(null);
    if ((name == null || name.trim().equals("")) && files.size() > 0) {
      name = (String) files.get(0);
      try {
        name = name.substring(name.lastIndexOf(File.separator) + 1);
      }
      catch (Exception e) { }
    }

    image.setName(name);
    image.setOwner(exp);
    image.setInserted("now");
    image.setCreated("now");
    image.setDescription(xml.getDescription(null, null));
    ((MappedDTO) image).setNew(false);

    df.update(image);

    // upload the original files
    // TODO : this doesn't show up in the OME web interface

    ModuleExecution of = im.getOriginalFilesMEX();
    of.setImage(image);
    try {
      for (int i=0; i<files.size(); i++) {
        OriginalFile f = pf.uploadFile(r, of, new File((String) files.get(i)));
        f.setPath((String) files.get(i));
        df.markForUpdate(f);
      }
    }
    catch (Exception e) { throw new UploadException(e); }

    of.setStatus("FINISHED");
    df.update(of);

    ModuleExecution ii = im.getImageImportMEX(image);
    ii.setExperimenter(exp);

    df.update(ii);

    Criteria c = new Criteria();
    c.addWantedField("id");
    c.addWantedField("default_pixels");
    c.addFilter("id", "=", "" + image.getID());
    image = (Image) df.retrieve(Image.class, c);

    // define pixels information and link to pixels in OMEIS

    Pixels pixels = image.getDefaultPixels();
    pixels.setRepository(r);
    pixels.setImage(image);
    pixels.setModuleExecution(ii);
    pixels.setImageServerID(new Long(id));
    df.update(pixels);

    // create a thumbnail
    try {
      pf.setThumbnail(pixels, CompositingSettings.createDefaultPGISettings(
        xml.getSizeZ(null).intValue(), xml.getSizeC(null).intValue(),
        xml.getSizeT(null).intValue()));
    }
    catch (Exception e) { throw new UploadException(e); }

    ii.setStatus("FINISHED");
    df.update(ii);

    image.setDefaultPixels(pixels);
    df.update(image);

    // link image to the appropriate dataset
    if (dataset != null) {
      DatasetManager dm = (DatasetManager) rs.getService(DatasetManager.class);
      Dataset d = (Dataset) df.createNew(Dataset.class);
      ((MappedDTO) d).setNew(false);
      d.setID(dataset.intValue());
      Vector imgs = new Vector();
      imgs.add(image);
      dm.addImagesToDataset(d, imgs);
      df.update(d);
      df.update(image);
    }
  }

  // -- Main method --

  /**
   * A command-line tool for uploading data to an
   * OME server using client-side Java tools.
   */
  public static void main(String[] args) {
    String server = null, user = null, pass = null;
    Vector files = new Vector();

    // parse command-line arguments
    boolean doUsage = false;
    if (args.length == 0) doUsage = true;
    for (int i=0; i<args.length; i++) {
      if (args[i].startsWith("-")) {
        // argument is a command line flag
        String param = args[i];
        try {
          if (param.equalsIgnoreCase("-s")) server = args[++i];
          else if (param.equalsIgnoreCase("-u")) user = args[++i];
          else if (param.equalsIgnoreCase("-p")) pass = args[++i];
          else if (param.equalsIgnoreCase("-h") ||
            param.equalsIgnoreCase("-?"))
          {
            doUsage = true;
          }
          else {
            System.out.println("Error: unknown flag: " + param);
            System.out.println();
            doUsage = true;
            break;
          }
        }
        catch (ArrayIndexOutOfBoundsException exc) {
          if (i == args.length - 1) {
            System.out.println("Error: flag " + param +
              " must be followed by a parameter value.");
            System.out.println();
            doUsage = true;
            break;
          }
          else throw exc;
        }
      }
      else {
        files.add(args[i]);
      }
    }
    if (doUsage) {
      System.out.println("Usage: omeul [-s server.address] " +
        "[-u username] [-p password] filename");
      System.out.println();
      System.exit(1);
    }

    // ask for information if necessary
    BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));
    if (server == null) {
      System.out.print("Server address? ");
      try { server = cin.readLine(); }
      catch (IOException exc) { }
    }
    if (user == null) {
      System.out.print("Username? ");
      try { user = cin.readLine(); }
      catch (IOException exc) { }
    }
    if (pass == null) {
      System.out.print("Password? ");
      try { pass = cin.readLine(); }
      catch (IOException exc) { }
    }

    if (server == null || user == null || pass == null) {
      System.out.println("Error: could not obtain server login information");
      System.exit(2);
    }
    System.out.println("Using server " + server + " as user " + user);

    // create image uploader
/* CTR TODO
    OMEUploader uploader = new OMEUploader();
    uploader.addTaskListener(new TaskListener() {
      public void taskUpdated(TaskEvent e) {
        System.out.println(e.getStatusMessage());
      }
    });

    for (int i=0; i<files.size(); i++) {
      FilePattern fp = new FilePattern((String) files.get(i));
      int[] lengths = fp.getCount();
      if (lengths.length == 0) {
        lengths = new int[1];
        lengths[0] = 1;
      }
      
      loci.visbio.data.Dataset data = new loci.visbio.data.Dataset(
        (String) files.get(i), fp.getPattern(), fp.getFiles(), lengths, 
        new String[lengths.length]);
      uploader.upload(data, server, user, pass);
    }
*/
  }

}
