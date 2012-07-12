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

import Glacier2.CannotCreateSessionException;
import Glacier2.PermissionDeniedException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import loci.common.Constants;
import loci.common.DateTools;
import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import loci.formats.tools.ImageInfo;
import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;
import omero.RDouble;
import omero.RInt;
import omero.RString;
import omero.RTime;
import omero.ServerError;
import omero.api.GatewayPrx;
import omero.api.IAdminPrx;
import omero.api.IQueryPrx;
import omero.api.RawPixelsStorePrx;
import omero.api.ServiceFactoryPrx;
import omero.model.Channel;
import omero.model.Experimenter;
import omero.model.ExperimenterGroup;
import omero.model.ExperimenterGroupI;
import omero.model.Image;
import omero.model.IObject;
import omero.model.LogicalChannel;
import omero.model.Pixels;
import omero.sys.EventContext;
import omero.sys.ParametersI;

/**
 * Implementation of {@link loci.formats.IFormatReader}
 * for use in export from an OMERO Beta 4.2.x database.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/ome-io/src/loci/ome/io/OmeroReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/ome-io/src/loci/ome/io/OmeroReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class OmeroReader extends FormatReader {

  // -- Constants --

  public static final int DEFAULT_PORT = 4064;

  // -- Fields --

  private String server;
  private String username;
  private String password;
  private int thePort = DEFAULT_PORT;
  private String sessionID;
  private String group;
  private Long groupID = null;
  private boolean encrypted = true;

  private omero.client client;
  private RawPixelsStorePrx store;
  private Image img;
  private Pixels pix;

  // -- Constructors --

  public OmeroReader() {
    super("OMERO", "*");
  }

  // -- OmeroReader methods --

  public void setServer(String server) {
    this.server = server;
  }

  public void setPort(int port) {
    thePort = port;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setSessionID(String sessionID) {
    this.sessionID = sessionID;
  }

  public void setEncrypted(boolean encrypted) {
    this.encrypted = encrypted;
  }

  public void setGroupName(String group) {
    this.group = group;
  }

  public void setGroupID(Long groupID) {
    this.groupID = groupID;
  }

  // -- IFormatReader methods --

  @Override
  public boolean isThisType(String name, boolean open) {
    return name.startsWith("omero:");
  }

  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length, w, h);

    final int[] zct = FormatTools.getZCTCoords(this, no);

    final byte[] plane;
    try {
      plane = store.getPlane(zct[0], zct[1], zct[2]);
    }
    catch (ServerError e) {
      throw new FormatException(e);
    }

    RandomAccessInputStream s = new RandomAccessInputStream(plane);
    readPlane(s, x, y, w, h, buf);
    s.close();

    return buf;
  }

  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly && client != null) {
      client.closeSession();
    }
  }

  @Override
  protected void initFile(String id) throws FormatException, IOException {
    LOGGER.debug("OmeroReader.initFile({})", id);

    super.initFile(id);

    if (!id.startsWith("omero:")) {
      throw new IllegalArgumentException("Not an OMERO id: " + id);
    }

    // parse credentials from id string

    LOGGER.info("Parsing credentials");

    String address = server, user = username, pass = password;
    int port = thePort;
    long iid = -1;

    final String[] tokens = id.substring(6).split("\n");
    for (String token : tokens) {
      final int equals = token.indexOf("=");
      if (equals < 0) continue;
      final String key = token.substring(0, equals);
      final String val = token.substring(equals + 1);
      if (key.equals("server")) address = val;
      else if (key.equals("user")) user = val;
      else if (key.equals("pass")) pass = val;
      else if (key.equals("port")) {
        try {
          port = Integer.parseInt(val);
        }
        catch (NumberFormatException exc) { }
      }
      else if (key.equals("session")) {
        sessionID = val;
      }
      else if (key.equals("groupName")) {
        group = val;
      }
      else if (key.equals("groupID")) {
        groupID = new Long(val);
      }
      else if (key.equals("iid")) {
        try {
          iid = Long.parseLong(val);
        }
        catch (NumberFormatException exc) { }
      }
    }

    if (address == null) {
      throw new FormatException("Invalid server address");
    }
    if (user == null && sessionID == null) {
      throw new FormatException("Invalid username");
    }
    if (pass == null && sessionID == null) {
      throw new FormatException("Invalid password");
    }
    if (iid < 0) {
      throw new FormatException("Invalid image ID");
    }

    try {
      // authenticate with OMERO server

      LOGGER.info("Logging in");

      client = new omero.client(address, port);
      ServiceFactoryPrx serviceFactory = null;
      if (user != null && pass != null) {
        serviceFactory = client.createSession(user, pass);
      }
      else {
        serviceFactory = client.createSession(sessionID, sessionID);
      }

      if (!encrypted) {
        client = client.createClient(false);
        serviceFactory = client.getSession();
      }

      if (group != null || groupID != null) {
        IAdminPrx iAdmin = serviceFactory.getAdminService();
        IQueryPrx iQuery = serviceFactory.getQueryService();
        EventContext eventContext = iAdmin.getEventContext();
        ExperimenterGroup defaultGroup =
          iAdmin.getDefaultGroup(eventContext.userId);
        if (!defaultGroup.getName().getValue().equals(group) &&
          !new Long(defaultGroup.getId().getValue()).equals(groupID))
        {
          Experimenter exp = iAdmin.getExperimenter(eventContext.userId);

          ParametersI p = new ParametersI();
          p.addId(eventContext.userId);
          List<IObject> groupList = iQuery.findAllByQuery(
            "select distinct g from ExperimenterGroup as g " +
            "join fetch g.groupExperimenterMap as map " +
            "join fetch map.parent e " +
            "left outer join fetch map.child u " +
            "left outer join fetch u.groupExperimenterMap m2 " +
            "left outer join fetch m2.parent p " +
            "where g.id in " +
            "  (select m.parent from GroupExperimenterMap m " +
            "  where m.child.id = :id )", p);

          Iterator<IObject> i = groupList.iterator();

          ExperimenterGroup g = null;

          boolean in = false;
          while (i.hasNext()) {
            g = (ExperimenterGroup) i.next();
            if (g.getName().getValue().equals(group) ||
              new Long(g.getId().getValue()).equals(groupID))
            {
              in = true;
              groupID = g.getId().getValue();
              break;
            }
          }
          if (in) {
            iAdmin.setDefaultGroup(exp, iAdmin.getGroup(groupID));
            serviceFactory.setSecurityContext(
              new ExperimenterGroupI(groupID, false));
          }
        }
      }

      // get raw pixels store and pixels

      store = serviceFactory.createRawPixelsStore();

      final GatewayPrx gateway = serviceFactory.createGateway();
      img = gateway.getImage(iid);

      if (img == null) {
        throw new FormatException("Could not find Image with ID=" + iid +
          " in group '" + group + "'.");
      }

      long pixelsId = img.getPixels(0).getId().getValue();

      store.setPixelsId(pixelsId, false);

      pix = gateway.getPixels(pixelsId);
      final int sizeX = pix.getSizeX().getValue();
      final int sizeY = pix.getSizeY().getValue();
      final int sizeZ = pix.getSizeZ().getValue();
      final int sizeC = pix.getSizeC().getValue();
      final int sizeT = pix.getSizeT().getValue();
      final String pixelType = pix.getPixelsType().getValue().getValue();

      // populate metadata

      LOGGER.info("Populating metadata");

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

      RDouble x = pix.getPhysicalSizeX();
      Double px = x == null ? null : x.getValue();
      RDouble y = pix.getPhysicalSizeY();
      Double py = y == null ? null : y.getValue();
      RDouble z = pix.getPhysicalSizeZ();
      Double pz = z == null ? null : z.getValue();
      RDouble t = pix.getTimeIncrement();
      Double time = t == null ? null : t.getValue();

      RString imageName = img.getName();
      String name = imageName == null ? null : imageName.getValue();

      if (name != null) {
        currentId = name;
      }
      else {
        currentId = "Image ID " + iid;
      }

      RString imgDescription = img.getDescription();
      String description =
        imgDescription == null ? null : imgDescription.getValue();
      RTime date = img.getAcquisitionDate();

      MetadataStore store = getMetadataStore();
      MetadataTools.populatePixels(store, this);
      store.setImageName(name, 0);
      store.setImageDescription(description, 0);
      if (date != null) {
        store.setImageAcquisitionDate(new Timestamp(
          DateTools.convertDate(date.getValue(), (int) DateTools.UNIX_EPOCH)),
          0);
      }

      if (px != null && px > 0) {
        store.setPixelsPhysicalSizeX(new PositiveFloat(px), 0);
      }
      if (py != null && py > 0) {
        store.setPixelsPhysicalSizeY(new PositiveFloat(py), 0);
      }
      if (pz != null && pz > 0) {
        store.setPixelsPhysicalSizeZ(new PositiveFloat(pz), 0);
      }
      if (time != null) {
        store.setPixelsTimeIncrement(time, 0);
      }

      List<Channel> channels = pix.copyChannels();
      for (int c=0; c<channels.size(); c++) {
        LogicalChannel channel = channels.get(c).getLogicalChannel();

        RInt emWave = channel.getEmissionWave();
        RInt exWave = channel.getExcitationWave();
        RDouble pinholeSize = channel.getPinHoleSize();
        RString cname = channel.getName();

        Integer emission = emWave == null ? null : emWave.getValue();
        Integer excitation = exWave == null ? null : exWave.getValue();
        String channelName = cname == null ? null : cname.getValue();
        Double pinhole = pinholeSize == null ? null : pinholeSize.getValue();

        if (channelName != null) {
          store.setChannelName(channelName, 0, c);
        }
        if (pinhole != null) {
          store.setChannelPinholeSize(pinhole, 0, c);
        }
        if (emission != null && emission > 0) {
          store.setChannelEmissionWavelength(
            new PositiveInteger(emission), 0, c);
        }
        if (excitation != null && excitation > 0) {
          store.setChannelExcitationWavelength(
            new PositiveInteger(excitation), 0, c);
        }
      }
    }
    catch (CannotCreateSessionException e) {
      throw new FormatException(e);
    }
    catch (PermissionDeniedException e) {
      throw new FormatException(e);
    }
    catch (ServerError e) {
      throw new FormatException(e);
    }
  }

  /** A simple command line tool for downloading images from OMERO. */
  public static void main(String[] args) throws Exception {
    // parse OMERO credentials
    BufferedReader con = new BufferedReader(
      new InputStreamReader(System.in, Constants.ENCODING));

    System.out.print("Server? ");
    final String server = con.readLine();

    System.out.printf("Port [%d]? ", DEFAULT_PORT);
    final String portString = con.readLine();
    final int port = portString.equals("") ? DEFAULT_PORT :
      Integer.parseInt(portString);

    System.out.print("Username? ");
    final String user = con.readLine();

    System.out.print("Password? ");
    final String pass = new String(con.readLine());

    System.out.print("Group? ");
    final String group = con.readLine();

    System.out.print("Image ID? ");
    final int imageId = Integer.parseInt(con.readLine());
    System.out.print("\n\n");

    // construct the OMERO reader
    final OmeroReader omeroReader = new OmeroReader();
    omeroReader.setUsername(user);
    omeroReader.setPassword(pass);
    omeroReader.setServer(server);
    omeroReader.setPort(port);
    omeroReader.setGroupName(group);
    final String id = "omero:iid=" + imageId;
    try {
      omeroReader.setId(id);
    }
    catch (Exception e) {
      omeroReader.close();
      throw e;
    }
    omeroReader.close();

    // delegate the heavy lifting to Bio-Formats ImageInfo utility
    final ImageInfo imageInfo = new ImageInfo();
    imageInfo.setReader(omeroReader); // override default image reader

    String[] readArgs = new String[args.length + 1];
    System.arraycopy(args, 0, readArgs, 0, args.length);
    readArgs[args.length] = id;

    if (!imageInfo.testRead(readArgs)) {
      omeroReader.close();
      System.exit(1);
    }
  }

}
