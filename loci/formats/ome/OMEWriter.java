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

import org.openmicroscopy.ds.*;
import org.openmicroscopy.ds.dto.*;
import org.openmicroscopy.ds.managers.*;
import org.openmicroscopy.ds.st.*;
import org.openmicroscopy.is.*;

/** Uploads images to an OME server. */
public class OMEWriter extends FormatWriter {

  // -- Fields --

  /** Data server. */
  private String server;

  /** Image server. */
  private String omeis;

  /** User name. */
  private String user;

  /** Password. */
  private String pass;

  /** Unique key for this session. */
  private String sessionKey;

  /** Flag indicating the current session is valid. */
  private boolean validLogin = false;

  /** ID of pixels. */
  private long pixelsId = -1;

  /** Number of planes written. */
  private int planesWritten = 0;

  private OMEXMLMetadataStore metadata;

  private DataServices rs;
  private RemoteCaller rc;
  private DataFactory df;
  private ImportManager im;
  private PixelsFactory pf;
  private Experimenter exp;
  private Repository r;

  // -- Constructor --

  public OMEWriter() {
    super("Open Microscopy Environment", "");
  }

  // -- OMEWriter API methods --

  public void setMetadataStore(OMEXMLMetadataStore store) {
    metadata = store;
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

  /* @see loci.formats.IFormatWriter#saveImage(Image, boolean) */
  public void saveImage(java.awt.Image image, boolean last)
    throws FormatException, IOException
  {
    byte[][] b = ImageTools.getPixelBytes(ImageTools.makeBuffered(image),
      !metadata.getBigEndian(null).booleanValue());
    for (int i=0; i<b.length; i++) {
      saveBytes(b[i], last && (i == b.length - 1));
    }
  }

  /* @see loci.formats.IFormatWriter#saveBytes(String, byte[], boolean) */
  public void saveBytes(byte[] bytes, boolean last)
    throws FormatException, IOException
  {
    if (!validLogin) {
      // parse the ID string to get the server, user name and password

      server = currentId.substring(0, currentId.lastIndexOf("?"));
      int ndx = currentId.indexOf("&");
      if (currentId.indexOf("user") != -1) {
        user = currentId.substring(currentId.lastIndexOf("?") + 6, ndx);
        pass = currentId.substring(ndx + 10);
      }
      else {
        throw new FormatException("Invalid ID - must be of the form " +
          "<server>?user=<username>&password=<password>");
      }
      login();

      // initialize necessary services

      df = (DataFactory) rs.getService(DataFactory.class);
      im = (ImportManager) rs.getService(ImportManager.class);
      pf = (PixelsFactory) rs.getService(PixelsFactory.class);

      FieldsSpecification fields = new FieldsSpecification();
      fields.addWantedField("id");
      fields.addWantedField("experimenter");
      fields.addWantedField("experimenter", "id");
      UserState userState = df.getUserState(fields);
      exp = userState.getExperimenter();

      try {
        r = pf.findRepository(0);
        r.setImageServerURL(omeis);
      }
      catch (Exception e) {
        throw new FormatException("Could not find repository.", e);
      }

      im.startImport(exp);

      if (metadata == null) {
        throw new FormatException("Metadata store not specified.");
      }
    }

    int z = metadata.getSizeZ(null).intValue();
    int c = metadata.getSizeC(null).intValue();
    int t = metadata.getSizeT(null).intValue();
    String order = metadata.getDimensionOrder(null);

    ImageServer is = ImageServer.getHTTPImageServer(omeis, sessionKey);
    if (pixelsId == -1) {
      try {
        String pixelTypeString = metadata.getPixelType(null);
        int pixelType = FormatTools.pixelTypeFromString(pixelTypeString);
        pixelsId = is.newPixels(metadata.getSizeX(null).intValue(),
          metadata.getSizeY(null).intValue(), z, c, t,
          FormatTools.getBytesPerPixel(pixelType), false,
          pixelTypeString.equals("float"));
      }
      catch (ImageServerException e) {
        throw new FormatException("Failed to create new pixels file.", e);
      }
    }

    try {
      String pixelTypeString = metadata.getPixelType(null);
      int pixelType = FormatTools.pixelTypeFromString(pixelTypeString);
      int planeLength = metadata.getSizeX(null).intValue() *
        metadata.getSizeY(null).intValue() *
        FormatTools.getBytesPerPixel(pixelType);
      byte[][] b = ImageTools.splitChannels(bytes, bytes.length / planeLength,
        FormatTools.getBytesPerPixel(pixelType), false, true);

      for (int ch=0; ch<b.length; ch++) {
        int[] coords = FormatTools.getZCTCoords(order, z, c, t, z*c*t,
          planesWritten);

        is.setPlane(pixelsId, coords[0], coords[1], coords[2], b[ch],
          metadata.getBigEndian(null).booleanValue());
        planesWritten++;
      }
    }
    catch (ImageServerException e) {
      throw new FormatException("Failed to upload plane.", e);
    }

    if (last) {
      try {
        pixelsId = is.finishPixels(pixelsId);
      }
      catch (ImageServerException e) {
        throw new FormatException("Failed to close pixels file.", e);
      }

      Image img = (Image) df.createNew(Image.class);
      img.setOwner(exp);
      img.setInserted("now");
      img.setCreated(metadata.getCreationDate(null));
      if (metadata.getImageName(null) != null) {
        img.setName(metadata.getImageName(null));
      }
      else img.setName("new image " + pixelsId);
      img.setDescription(metadata.getDescription(null));

      df.update(img);

      ModuleExecution ii = im.getImageImportMEX(img);
      ii.setExperimenter(exp);
      df.update(ii);

      Pixels pixels = (Pixels) df.createNew("Pixels");
      pixels.setRepository(r);
      pixels.setImage(img);
      pixels.setModuleExecution(ii);
      pixels.setImageServerID(new Long(pixelsId));
      pixels.setSizeX(metadata.getSizeX(null));
      pixels.setSizeY(metadata.getSizeY(null));
      pixels.setSizeZ(metadata.getSizeZ(null));
      pixels.setSizeC(metadata.getSizeC(null));
      pixels.setSizeT(metadata.getSizeT(null));
      pixels.setPixelType(metadata.getPixelType(null));

      try {
        pf.setThumbnail(pixels,
          CompositingSettings.createDefaultPGISettings(z, c, t));
      }
      catch (ImageServerException e) {
        throw new FormatException("Failed to create thumbnail.", e);
      }

      df.update(pixels);

      LogicalChannel logical = (LogicalChannel) df.createNew("LogicalChannel");
      logical.setImage(img);
      logical.setModuleExecution(ii);
      logical.setFluor("Gray 00");
      logical.setPhotometricInterpretation("monochrome");
      df.update(logical);

      PixelChannelComponent physical =
        (PixelChannelComponent) df.createNew("PixelChannelComponent");
      physical.setImage(img);
      physical.setPixels(pixels);
      physical.setIndex(new Integer(0));
      physical.setLogicalChannel(logical);
      physical.setModuleExecution(ii);
      df.update(physical);

      ii.setStatus("FINISHED");
      df.update(ii);

      img.setDefaultPixels(pixels);
      df.update(img);

      close();
    }
  }

  /* @see loci.formats.IFormatWriter#canDoStacks(String) */
  public boolean canDoStacks() { return true; }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    if (rc != null) rc.logout();
    pixelsId = -1;
    validLogin = false;
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
    while (server.lastIndexOf("/") > 7) {
      server = server.substring(0, server.lastIndexOf("/"));
    }
    omeis = server + "/cgi-bin/omeis";
    server += "/shoola";
    if (!server.startsWith("http://")) {
      server = "http://" + server;
      omeis = "http://" + omeis;
    }

    status("Logging in to " + server);

    try {
      rs = DataServer.getDefaultServices(server);
      rc = rs.getRemoteCaller();
      rc.login(user, pass);
      sessionKey = rc.getSessionKey();

      validLogin = true;
    }
    catch (Exception e) {
      validLogin = false;
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
    reader.setMetadataStore(new OMEXMLMetadataStore());
    reader.setId(id);
    uploader.setMetadataStore((OMEXMLMetadataStore) reader.getMetadataStore());
    for (int i=0; i<reader.getImageCount(); i++) {
      uploader.saveImage(reader.openImage(i), i == reader.getImageCount() - 1);
    }
    reader.close();
    uploader.close();
  }

}
