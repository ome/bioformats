//
// OMEWriter.java
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

import java.io.*;
import loci.formats.*;
import loci.formats.meta.MetadataRetrieve;

/**
 * Uploads images to an OME server.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/ome/OMEWriter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/ome/OMEWriter.java">SVN</a></dd></dl>
 */
public class OMEWriter extends FormatWriter {

  // -- Constants --

  /** Message to display if OME-Java is not found. */
  private static final String NO_OME_JAVA = "OME-Java not found.  Please " +
    "download ome-java.jar from http://www.loci.wisc.edu/ome/formats.html";

  // -- Static fields --

  private static boolean hasOMEJava = true;
  private static ReflectedUniverse r = createReflectedUniverse();

  private static ReflectedUniverse createReflectedUniverse() {
    r = null;
    try {
      r = new ReflectedUniverse();
      r.exec("import java.lang.Class");
      r.exec("import org.openmicroscopy.ds.DataFactory");
      r.exec("import org.openmicroscopy.ds.DataServer");
      r.exec("import org.openmicroscopy.ds.DataServices");
      r.exec("import org.openmicroscopy.ds.FieldsSpecification");
      r.exec("import org.openmicroscopy.ds.RemoteCaller");
      r.exec("import org.openmicroscopy.ds.dto.ModuleExecution");
      r.exec("import org.openmicroscopy.ds.dto.UserState");
      r.exec("import org.openmicroscopy.ds.managers.ImportManager");
      r.exec("import org.openmicroscopy.ds.st.Experimenter");
      r.exec("import org.openmicroscopy.ds.st.LogicalChannel");
      r.exec("import org.openmicroscopy.ds.st.Pixels");
      r.exec("import org.openmicroscopy.ds.st.Repository");
      r.exec("import org.openmicroscopy.is.CompositingSettings");
      r.exec("import org.openmicroscopy.is.ImageServer");
      r.exec("import org.openmicroscopy.is.PixelsFactory");
    }
    catch (ReflectException e) {
      if (debug) LogTools.trace(e);
      hasOMEJava = false;
    }
    return r;
  }

  // -- Fields --

  /** Authentication credentials. */
  private OMECredentials credentials;

  /** Image server. */
  private String omeis;

  /** Number of planes written. */
  private int planesWritten = 0;

  private MetadataRetrieve metadata;

  // -- Constructor --

  public OMEWriter() {
    super("Open Microscopy Environment", "");
  }

  // -- Internal OMEWriter API methods --

  /** Fires a status update event. */
  protected void status(String message) {
    status(new StatusEvent(message));
  }

  /** Fires a status update event. */
  protected void status(int progress, int maximum, String message) {
    status(new StatusEvent(progress, maximum, message));
  }

  /** Fires a status update event. */
  protected void status(StatusEvent e) {
    StatusListener[] l = getStatusListeners();
    for (int i=0; i<l.length; i++) l[i].statusUpdated(e);
  }

  // -- IFormatWriter API methods --

  /* @see loci.formats.IFormatWriter#setMetadata(MetadataRetrieve) */
  public void setMetadata(MetadataRetrieve meta) {
    metadata = meta;
  }

  /* @see loci.formats.IFormatWriter#saveImage(Image, boolean) */
  public void saveImage(java.awt.Image image, boolean last)
    throws FormatException, IOException
  {
    byte[][] b = ImageTools.getPixelBytes(ImageTools.makeBuffered(image),
      !metadata.getPixelsBigEndian(0, 0).booleanValue());
    for (int i=0; i<b.length; i++) {
      saveBytes(b[i], last && (i == b.length - 1));
    }
  }

  /* @see loci.formats.IFormatWriter#saveBytes(String, byte[], boolean) */
  public void saveBytes(byte[] bytes, boolean last)
    throws FormatException, IOException
  {
    if (!hasOMEJava) throw new FormatException(NO_OME_JAVA);
    if (currentId != null && credentials == null) {
      // parse the ID string to get the server, user name and password

      credentials = OMEUtils.parseCredentials(currentId);
      login();
      credentials.imageID = -1;

      // initialize necessary services

      try {
        r.setVar("DATA_FACTORY_CLASS", "org.openmicroscopy.ds.DataFactory");
        r.exec("DATA_FACTORY_CLASS = Class.forName(DATA_FACTORY_CLASS)");
        r.setVar("PIXELS_FACTORY_CLASS", "org.openmicroscopy.is.PixelsFactory");
        r.exec("PIXELS_FACTORY_CLASS = Class.forName(PIXELS_FACTORY_CLASS)");
        r.setVar("IMPORT_MANAGER_CLASS",
          "org.openmicroscopy.ds.managers.ImportManager");
        r.exec("IMPORT_MANAGER_CLASS = Class.forName(IMPORT_MANAGER_CLASS)");
        r.exec("df = rs.getService(DATA_FACTORY_CLASS)");
        r.exec("im = rs.getService(IMPORT_MANAGER_CLASS)");
        r.exec("pf = rs.getService(PIXELS_FACTORY_CLASS)");

        r.setVar("ID", "id");
        r.setVar("EXPERIMENTER", "experimenter");
        r.exec("fields = new FieldsSpecification()");
        r.exec("fields.addWantedField(ID)");
        r.exec("fields.addWantedField(EXPERIMENTER)");
        r.exec("fields.addWantedField(EXPERIMENTER, ID)");
        r.exec("userState = df.getUserState(fields)");
        r.exec("exp = userState.getExperimenter()");

        r.setVar("zero", 0l);
        r.setVar("omeis", omeis);
        r.exec("repository = pf.findRepository(zero)");
        r.exec("repository.setImageServerURL(omeis)");

        r.exec("im.startImport(exp)");
      }
      catch (ReflectException e) {
        throw new FormatException(e);
      }

      if (metadata == null) {
        throw new FormatException("Metadata store not specified.");
      }
    }

    int x = metadata.getPixelsSizeX(0, 0).intValue();
    int y = metadata.getPixelsSizeY(0, 0).intValue();
    int z = metadata.getPixelsSizeZ(0, 0).intValue();
    int c = metadata.getPixelsSizeC(0, 0).intValue();
    int t = metadata.getPixelsSizeT(0, 0).intValue();
    String order = metadata.getPixelsDimensionOrder(0, 0);
    String pixelTypeString = metadata.getPixelsPixelType(0, 0);
    int pixelType = FormatTools.pixelTypeFromString(pixelTypeString);
    int bpp = FormatTools.getBytesPerPixel(pixelType);
    boolean bigEndian = metadata.getPixelsBigEndian(0, 0).booleanValue();

    try {
      r.exec("sessionKey = rc.getSessionKey()");
      r.exec("is = ImageServer.getHTTPImageServer(omeis, sessionKey)");
      if (credentials.imageID == -1) {
        r.setVar("x", x);
        r.setVar("y", y);
        r.setVar("z", z);
        r.setVar("c", c);
        r.setVar("t", t);
        r.setVar("bpp", bpp);
        r.setVar("bigEndian", bigEndian);
        r.setVar("float", pixelTypeString.equals("float"));
        r.setVar("pixelType", pixelTypeString);
        r.exec("pixelsId = is.newPixels(x, y, z, c, t, bpp, bigEndian, float)");
        credentials.imageID = ((Long) r.getVar("pixelsId")).longValue();
      }
    }
    catch (ReflectException e) {
      throw new FormatException(e);
    }

    try {
      int planeLength = x * y * bpp;
      byte[][] b = ImageTools.splitChannels(bytes, bytes.length / planeLength,
        bpp, false, true);

      for (int ch=0; ch<b.length; ch++) {
        int[] coords = FormatTools.getZCTCoords(order, z, c, t, z*c*t,
          planesWritten);

        r.setVar("zndx", coords[0]);
        r.setVar("cndx", coords[1]);
        r.setVar("tndx", coords[2]);
        r.setVar("bytes", b[ch]);
        r.setVar("bigEndian", bigEndian);

        r.setVar("pixelsId", credentials.imageID);
        r.exec("is.setPlane(pixelsId, zndx, cndx, tndx, bytes, bigEndian)");
        planesWritten++;
      }
    }
    catch (ReflectException e) {
      throw new FormatException("Failed to upload plane.", e);
    }

    if (last) {
      try {
        r.exec("pixelsId = is.finishPixels(pixelsId)");
        credentials.imageID = ((Long) r.getVar("pixelsId")).longValue();

        r.setVar("NOW", "now");
        r.setVar("creationDate", metadata.getImageCreationDate(0));
        if (metadata.getImageName(0) != null) {
          r.setVar("imageName", metadata.getImageName(0));
        }
        else r.setVar("imageName", "new image " + credentials.imageID);
        r.setVar("imageDescription", metadata.getImageDescription(0));

        r.setVar("IMAGE_CLASS", "org.openmicroscopy.ds.dto.Image");
        r.exec("IMAGE_CLASS = Class.forName(IMAGE_CLASS)");
        r.exec("img = df.createNew(IMAGE_CLASS)");
        r.exec("img.setOwner(exp)");
        r.exec("img.setInserted(NOW)");
        r.exec("img.setCreated(creationDate)");
        r.exec("img.setName(imageName)");
        r.exec("img.setDescription(imageDescription)");
        r.exec("df.update(img)");

        r.exec("ii = im.getImageImportMEX(img)");
        r.exec("ii.setExperimenter(exp)");
        r.exec("df.update(ii)");

        r.setVar("PIXELS", "Pixels");
        r.exec("pixels = df.createNew(PIXELS)");
        r.exec("pixels.setRepository(repository)");
        r.exec("pixels.setImage(img)");
        r.exec("pixels.setModuleExecution(ii)");
        r.setVar("pixelsIdObj", new Long(credentials.imageID));
        r.exec("pixels.setImageServerID(pixelsIdObj)");
        r.exec("pixels.setSizeX(x)");
        r.exec("pixels.setSizeY(y)");
        r.exec("pixels.setSizeZ(z)");
        r.exec("pixels.setSizeC(c)");
        r.exec("pixels.setSizeT(t)");
        r.exec("pixels.setPixelType(pixelType)");

        r.exec("settings = " +
          "CompositingSettings.createDefaultPGISettings(z, c, t)");
        r.exec("pf.setThumbnail(pixels, settings)");
        r.exec("df.update(pixels)");

        r.setVar("GRAY", "Gray 00");
        r.setVar("LOGICAL_CHANNEL", "LogicalChannel");
        r.setVar("PHOTOMETRIC_INTERPRETATION", "monochrome");
        r.exec("logical = df.createNew(LOGICAL_CHANNEL)");
        r.exec("logical.setImage(img)");
        r.exec("logical.setModuleExecution(ii)");
        r.exec("logical.setFluor(GRAY)");
        r.exec("logical.setPhotometricInterpretation(" +
          "PHOTOMETRIC_INTERPRETATION)");
        r.exec("df.update(logical)");

        r.setVar("PIXEL_CHANNEL_COMPONENT", "PixelChannelComponent");
        r.setVar("zeroObj", new Integer(0));
        r.exec("physical = df.createNew(PIXEL_CHANNEL_COMPONENT)");
        r.exec("physical.setImage(img)");
        r.exec("physical.setPixels(pixels)");
        r.exec("physical.setIndex(zeroObj)");
        r.exec("physical.setLogicalChannel(logical)");
        r.exec("physical.setModuleExecution(ii)");
        r.exec("df.update(physical)");

        r.setVar("FINISHED", "FINISHED");
        r.exec("ii.setStatus(FINISHED)");
        r.exec("df.update(ii)");

        r.exec("img.setDefaultPixels(pixels)");
        r.exec("df.update(img)");

        close();
      }
      catch (ReflectException e) {
        throw new FormatException(e);
      }
    }
  }

  /* @see loci.formats.IFormatWriter#canDoStacks(String) */
  public boolean canDoStacks() { return true; }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    try {
      r.exec("rc.logout()");
    }
    catch (ReflectException e) { }
    credentials = null;
    planesWritten = 0;
    metadata = null;
  }

  // -- StatusReporter API methods --

  /* @see loci.formats.StatusReporter#addStatusListener(StatusListener) */
  public void addStatusListener(StatusListener l) {
    synchronized (statusListeners) {
      if (!statusListeners.contains(l)) statusListeners.add(l);
    }
  }

  /* @see loci.formats.StatusReporter#removeStatusListener(StatusListener) */
  public void removeStatusListener(StatusListener l) {
    synchronized (statusListeners) {
      statusListeners.remove(l);
    }
  }

  /* @see loci.formats.StatusReporter#getStatusListeners() */
  public StatusListener[] getStatusListeners() {
    synchronized (statusListeners) {
      StatusListener[] l = new StatusListener[statusListeners.size()];
      statusListeners.copyInto(l);
      return l;
    }
  }

  // -- Helper methods --

  private void login() throws FormatException {
    while (credentials.server.lastIndexOf("/") > 7) {
      int slash = credentials.server.lastIndexOf("/");
      credentials.server = credentials.server.substring(0, slash);
    }
    omeis = credentials.server + "/cgi-bin/omeis";
    credentials.server += "/shoola";
    if (!credentials.server.startsWith("http://")) {
      credentials.server = "http://" + credentials.server;
      omeis = "http://" + omeis;
    }

    status("Logging in to " + credentials.server);

    try {
      r.setVar("server", credentials.server);
      r.setVar("user", credentials.username);
      r.setVar("pass", credentials.password);
      r.exec("rs = DataServer.getDefaultServices(server)");
      r.exec("rc = rs.getRemoteCaller()");
      r.exec("rc.login(user, pass)");
    }
    catch (ReflectException e) {
      throw new FormatException("Login failed", e);
    }
  }

  // -- Main method --

  public static void main(String[] args) throws Exception {
    String server = null, user = null, pass = null;
    String id = null;

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
          else if (param.equalsIgnoreCase("-h") || param.equalsIgnoreCase("-?"))
          {
            doUsage = true;
          }
          else {
            LogTools.println("Error: unknown flag: "+ param);
            LogTools.println();
            doUsage = true;
            break;
          }
        }
        catch (ArrayIndexOutOfBoundsException exc) {
          if (i == args.length - 1) {
            LogTools.println("Error: flag " + param +
              " must be followed by a parameter value.");
            LogTools.println();
            doUsage = true;
            break;
          }
          else throw exc;
        }
      }
      else {
        if (id == null) id = args[i];
        else {
          LogTools.println("Error: unknown argument: " + args[i]);
          LogTools.println();
        }
      }
    }

    if (id == null) doUsage = true;
    if (doUsage) {
      LogTools.println("Usage: omeul [-s server.address] " +
        "[-u username] [-p password] filename");
      LogTools.println();
      System.exit(1);
    }

    // ask for information if necessary
    BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));
    if (server == null) {
      LogTools.print("Server address? ");
      try { server = cin.readLine(); }
      catch (IOException exc) { }
    }
    if (user == null) {
      LogTools.print("Username? ");
      try { user = cin.readLine(); }
      catch (IOException exc) { }
    }
    if (pass == null) {
      LogTools.print("Password? ");
      try { pass = cin.readLine(); }
      catch (IOException exc) { }
    }

    if (server == null || user == null || pass == null) {
      LogTools.println("Error: could not obtain server login information");
      System.exit(2);
    }
    LogTools.println("Using server " + server + " as user " + user);

    // create image uploader
    OMEWriter uploader = new OMEWriter();
    uploader.addStatusListener(new StatusListener() {
      public void statusUpdated(StatusEvent e) {
        LogTools.println(e.getStatusMessage());
      }
    });

    uploader.setId(server + "?user=" + user + "&password=" + pass);

    FileStitcher reader = new FileStitcher();
    reader.setMetadataStore(MetadataTools.createOMEXMLMetadata());
    reader.setId(id);
    uploader.setMetadata((MetadataRetrieve) reader.getMetadataStore());
    for (int i=0; i<reader.getImageCount(); i++) {
      uploader.saveImage(reader.openImage(i), i == reader.getImageCount() - 1);
    }
    reader.close();
    uploader.close();
  }

}
