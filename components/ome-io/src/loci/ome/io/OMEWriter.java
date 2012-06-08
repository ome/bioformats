/*
 * #%L
 * OME database I/O package for communicating with OME and OMERO servers.
 * %%
 * Copyright (C) 2005 - 2012 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.ome.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;

import loci.common.Constants;
import loci.common.DateTools;
import loci.common.ReflectException;
import loci.common.ReflectedUniverse;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.common.xml.XMLTools;
import loci.formats.FileStitcher;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.FormatWriter;
import loci.formats.ImageTools;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.services.OMEXMLService;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * Uploads images to an OME server.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/ome-io/src/loci/ome/io/OMEWriter.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/ome-io/src/loci/ome/io/OMEWriter.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class OMEWriter extends FormatWriter {

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
      LOGGER.debug(OMEUtils.NO_OME_MSG, e);
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

  private String[] originalFiles;

  // -- Constructor --

  public OMEWriter() {
    super("Open Microscopy Environment", "");
  }

  // -- IFormatWriter API methods --

  /**
   * @see loci.formats.IFormatWriter#saveBytes(int, byte[], int, int, int, int)
   */
  public void saveBytes(int no, byte[] bytes, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    if (!hasOMEJava) throw new FormatException(OMEUtils.NO_OME_MSG);
    if (currentId != null && credentials == null) {
      // parse the ID string to get the server, user name and password

      credentials = new OMECredentials(currentId);
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
      }
      catch (ReflectException e) {
        throw new FormatException(e);
      }

      if (metadataRetrieve == null) {
        throw new FormatException("Metadata store not specified.");
      }
    }

    try {
      r.exec("im.startImport(exp)");

      r.setVar("IMAGE_CLASS", "org.openmicroscopy.ds.dto.Image");
      r.exec("IMAGE_CLASS = Class.forName(IMAGE_CLASS)");
      r.exec("img = df.createNew(IMAGE_CLASS)");

      // upload original files

      if (originalFiles != null) {
        r.exec("of = im.getOriginalFilesMEX()");
        for (int i=0; i<originalFiles.length; i++) {
          r.setVar("file", new File(originalFiles[i]));
          r.exec("ofile = pf.uploadFile(repository, of, file)");
        }
        r.exec("df.updateMarked()");
        r.exec("of.setImage(img)");
      }
    }
    catch (ReflectException e) {
      LOGGER.debug("Original file upload failed", e);
    }

    int sizeX = metadataRetrieve.getPixelsSizeX(series).getValue().intValue();
    int sizeY = metadataRetrieve.getPixelsSizeY(series).getValue().intValue();
    int z = metadataRetrieve.getPixelsSizeZ(series).getValue().intValue();
    int c = metadataRetrieve.getPixelsSizeC(series).getValue().intValue();
    int t = metadataRetrieve.getPixelsSizeT(series).getValue().intValue();
    String order = metadataRetrieve.getPixelsDimensionOrder(series).toString();
    String pixelTypeString = metadataRetrieve.getPixelsType(series).toString();
    int pixelType = FormatTools.pixelTypeFromString(pixelTypeString);
    int bpp = FormatTools.getBytesPerPixel(pixelType);
    boolean bigEndian =
      metadataRetrieve.getPixelsBinDataBigEndian(series, 0).booleanValue();

    try {
      r.exec("sessionKey = rc.getSessionKey()");
      r.exec("is = ImageServer.getHTTPImageServer(omeis, sessionKey)");
      if (credentials.imageID == -1) {
        r.setVar("x", sizeX);
        r.setVar("y", sizeY);
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
      int planeLength = sizeX * sizeY * bpp;
      int nChannels = bytes.length / planeLength;

      for (int ch=0; ch<nChannels; ch++) {
        int[] coords = FormatTools.getZCTCoords(order, z, c, t, z*c*t,
          planesWritten);

        byte[] b =
          ImageTools.splitChannels(bytes, ch, nChannels, bpp, false, true);

        r.setVar("zndx", coords[0]);
        r.setVar("cndx", coords[1]);
        r.setVar("tndx", coords[2]);
        r.setVar("bytes", b);
        r.setVar("bigEndian", bigEndian);

        r.setVar("pixelsId", credentials.imageID);
        r.exec("is.setPlane(pixelsId, zndx, cndx, tndx, bytes, bigEndian)");
        planesWritten++;
      }
    }
    catch (ReflectException e) {
      throw new FormatException("Failed to upload plane.", e);
    }

    if (series == metadataRetrieve.getImageCount() - 1) {
      try {
        r.exec("pixelsId = is.finishPixels(pixelsId)");
        credentials.imageID = ((Long) r.getVar("pixelsId")).longValue();

        r.setVar("NOW", "now");

        String creationDate =
            metadataRetrieve.getImageAcquisitionDate(series).getValue();
        if (creationDate == null) {
          creationDate =
            DateTools.convertDate(System.currentTimeMillis(), DateTools.UNIX);
        }

        r.setVar("creationDate", creationDate);

        if (metadataRetrieve.getImageName(series) != null) {
          r.setVar("imageName", metadataRetrieve.getImageName(series));
        }
        else r.setVar("imageName", "new image " + credentials.imageID);
        r.setVar("imageDescription",
          metadataRetrieve.getImageDescription(series));

        r.exec("img.setOwner(exp)");
        r.exec("img.setInserted(NOW)");
        r.exec("img.setCreated(creationDate)");
        r.exec("img.setName(imageName)");
        r.exec("img.setDescription(imageDescription)");
        r.exec("df.update(img)");

        r.exec("ii = im.getImageImportMEX(img)");
        r.exec("ii.setExperimenter(exp)");
        r.exec("df.updateMarked()");
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

        // upload original metadata, if available

        boolean isOMEXML = false;
        try {
          ServiceFactory factory = new ServiceFactory();
          OMEXMLService service = factory.getInstance(OMEXMLService.class);
          isOMEXML = service.isOMEXMLMetadata(metadataRetrieve);
        }
        catch (DependencyException e) {
          LOGGER.warn("OMEXMLService not available", e);
        }

        if (isOMEXML) {
          r.setVar("metadata", metadataRetrieve);
          Hashtable meta = (Hashtable) r.exec("metadata.getOriginalMetadata()");
          String[] keys = (String[]) meta.keySet().toArray(new String[0]);
          r.setVar("ORIGINAL_METADATA", "OriginalMetadata");
          r.setVar("NAME", "Name");
          r.setVar("VALUE", "Value");
          for (int i=0; i<keys.length; i++) {
            r.exec("attribute = df.createNew(ORIGINAL_METADATA)");
            r.setVar("K", XMLTools.sanitizeXML(keys[i]));
            r.setVar("V", XMLTools.sanitizeXML((String) meta.get(keys[i])));
            r.exec("attribute.setStringElement(NAME, K)");
            r.exec("attribute.setStringElement(VALUE, V)");
            r.exec("attribute.setImage(img)");
            r.exec("attribute.setModuleExecution(ii)");
            r.exec("df.update(attribute)");
          }
        }

        r.setVar("FINISHED", "FINISHED");

        if (originalFiles != null) {
          r.exec("of.setStatus(FINISHED)");
          r.exec("df.update(of)");
        }

        r.exec("ii.setStatus(FINISHED)");
        r.exec("df.update(ii)");

        r.exec("img.setDefaultPixels(pixels)");
        r.exec("df.update(img)");

        r.exec("im.finishImport()");
      }
      catch (ReflectException e) {
        throw new FormatException(e);
      }

      planesWritten = 0;
      credentials.imageID = -1;
    }
  }

  /* @see loci.formats.IFormatWriter#canDoStacks(String) */
  @Override
  public boolean canDoStacks() { return true; }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  @Override
  public void close() throws IOException {
    try {
      r.exec("rc.logout()");
    }
    catch (ReflectException e) { }
    credentials = null;
    planesWritten = 0;
    if (currentId != null) metadataRetrieve = null;
    currentId = null;
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

    LOGGER.info("Logging in to {}", credentials.server);

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

  // -- OMEWriter API methods --

  public void setOriginalFiles(String[] filenames) {
    originalFiles = filenames;
  }

  // -- Main method --

  public static void main(String[] args) throws Exception {
    Logger root = Logger.getRootLogger();
    root.setLevel(Level.INFO);
    root.addAppender(new ConsoleAppender(new PatternLayout("%m%n")));

    String server = null, user = null, pass = null;
    String id = null;
    int series = -1;

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
          else if (param.equalsIgnoreCase("-series")) {
            series = Integer.parseInt(args[++i]);
          }
          else if (param.equalsIgnoreCase("-h") || param.equalsIgnoreCase("-?"))
          {
            doUsage = true;
          }
          else {
            LOGGER.warn("Unknown flag: {}", param);
            doUsage = true;
            break;
          }
        }
        catch (ArrayIndexOutOfBoundsException exc) {
          if (i == args.length - 1) {
            LOGGER.warn(
              "Flag {} must be followed by a parameter value.", param);
            doUsage = true;
            break;
          }
          else throw exc;
        }
      }
      else {
        if (id == null) id = args[i];
        else {
          LOGGER.warn("Unknown argument: {}", args[i]);
        }
      }
    }

    if (id == null) doUsage = true;
    if (doUsage) {
      LOGGER.info("Usage: omeul [-s server.address] " +
        "[-u username] [-p password] [-series series.number] filename");
      System.exit(1);
    }

    // ask for information if necessary
    BufferedReader cin = new BufferedReader(
      new InputStreamReader(System.in, Constants.ENCODING));
    if (server == null) {
      LOGGER.info("Server address? ");
      try { server = cin.readLine(); }
      catch (IOException exc) { }
    }
    if (user == null) {
      LOGGER.info("Username? ");
      try { user = cin.readLine(); }
      catch (IOException exc) { }
    }
    if (pass == null) {
      LOGGER.info("Password? ");
      try { pass = cin.readLine(); }
      catch (IOException exc) { }
    }

    if (server == null || user == null || pass == null) {
      LOGGER.error("Could not obtain server login information");
      System.exit(2);
    }
    LOGGER.info("Using server {} as user {}", server, user);

    // create image uploader
    OMEWriter uploader = new OMEWriter();

    FileStitcher reader = new FileStitcher();
    reader.setOriginalMetadataPopulated(true);

    try {
      ServiceFactory factory = new ServiceFactory();
      OMEXMLService service = factory.getInstance(OMEXMLService.class);
      reader.setMetadataStore(service.createOMEXMLMetadata());
    }
    catch (DependencyException e) {
      LOGGER.warn("OMEXMLService not available", e);
    }
    catch (ServiceException e) {
      LOGGER.warn("Could not parse OME-XML", e);
    }

    reader.setId(id);

    uploader.setMetadataRetrieve((MetadataRetrieve) reader.getMetadataStore());
    uploader.setOriginalFiles(reader.getUsedFiles());
    uploader.setId(server + "?user=" + user + "&password=" + pass);

    int start = series == -1 ? 0 : series;
    int end = series == -1 ? reader.getSeriesCount() : series + 1;

    for (int s=start; s<end; s++) {
      reader.setSeries(s);
      uploader.setSeries(s);
      for (int i=0; i<reader.getImageCount(); i++) {
        uploader.saveBytes(i, reader.openBytes(i));
      }
    }
    reader.close();
    uploader.close();
  }

}
