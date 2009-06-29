//
// OMEROWriter.java
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
import loci.formats.FormatWriter;
import loci.formats.StatusEvent;
import loci.formats.StatusListener;
import loci.formats.meta.MetadataRetrieve;
import ome.api.IQuery;
import ome.api.IUpdate;
import ome.api.RawPixelsStore;
import ome.formats.OMEROMetadataStore;
import ome.formats.importer.OMEROWrapper;
import ome.system.Login;
import ome.system.Server;
import ome.system.ServiceFactory;

/**
 * Uploads images to an OMERO server.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/ome-io/src/loci/formats/ome/OMEWriter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/ome-io/src/loci/formats/ome/OMEWriter.java">SVN</a></dd></dl>
 */
public class OMEROWriter extends FormatWriter {

  // -- Constants --

  private static final String NO_OMERO_MSG =
    "OMERO client libraries not found. " +
    "Please install the OMERO-client, OMERO-common, OMERO-importer and " +
    "OMERO-model libraries from " +
    "https://skyking.microscopy.wisc.edu/svn/java/trunk/jar/";

  // -- Static fields --

  private static boolean noOMERO = false;

  // -- Fields --

  private static String username;
  private static String password;
  private static String serverName;
  private static String port;

  private static String imageName;

  /** OMERO raw pixels service */
  private static RawPixelsStore pservice;

  /** OMERO query service */
  private static IQuery iQuery;

  /** OMERO update service */
  private static IUpdate iUpdate;

  //private static Experimenter exp;

  /** OMERO service factory; all other services are retrieved from here. */
  private static ServiceFactory sf;
  private static OMEROMetadataStore store;

  private OMEROWrapper reader;

  /** Flag indicating the current session is valid. */
  private boolean validLogin = false;

  /** Authentication credentials. */
  private OMECredentials credentials;

  /** Image server. */
  private String omeis;

  private MetadataRetrieve metadata;

  // -- Constructor --

  /** Constructs a new OMERO writer. */
  public OMEROWriter() { super("OMERO", "*"); }

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

  public void saveBytes(byte[] bytes, int series, boolean lastInSeries,
     boolean last) throws FormatException, IOException
  {
    /*
    if (!validLogin) {
     // parse the ID string to get the server, user name and password

      serverName = currentId.substring(0, currentId.lastIndexOf("?"));
      int ndx = currentId.indexOf("&");
      if (currentId.indexOf("user") != -1) {
        username = currentId.substring(currentId.lastIndexOf("?") + 6, ndx);
          password = currentId.substring(ndx + 10);
        }
      else {
        throw new FormatException("Invalid ID - must be of the form " +
            "<server>?user=<username>&password=<password>");
      }
    }
    login();

    //credentials.imageID = -1;
    MetadataTools.convertMetadata(metadata, store);
    ArrayList<Pixels> pixList = ((ArrayList<Pixels>) store.getRoot());
    Pixels p = pixList.get(0);

    //todo fix this, null pointer error
    //p.getImage().setName(imageName);
    //ArrayList<Pixels> pixId = ((ArrayList<Pixels>) store.saveToDB());
    //store.addPixelsToDataset(arg0, arg1)

    long timestampIn;
    long timestampOut;
    long timestampDiff;
    long timeInSeconds;
    long hours, minutes, seconds;
    Date date = null;

    // record initial timestamp and record total running time for the import
    timestampIn = System.currentTimeMillis();
    date = new Date(timestampIn);
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    String myDate = formatter.format(date);

    int i = 1;
    //int bytesPerPixel = getBytesPerPixel(reader.getPixelType());
    //byte[] arrayBuf = new byte[store.* sizeY * bytesPerPixel];
    reader.setSeries(series);

    List<Image> imageList = (List<Image>) store.getRoot();
    ArrayList<ome.model.core.Pixels> pixelsList =
      (ArrayList<Pixels>) store.saveToDB();

    for (Image image : imageList) {
      // store.addImageToDataset(image, dataset);
    }

    close();
    */
  }

  //methods done so far:

  public static void login() throws FormatException {
    if (username == null) {
      throw new NullPointerException("username cannot be null");
    }
    if (password == null) {
      throw new NullPointerException("password cannot be null");
    }
    if (port == null) throw new NullPointerException("port cannot be null");
    if (serverName == null) {
      throw new NullPointerException("server cannot be null");
    }

     try {
      tryLogin();
    }
    catch (Exception e) {
      // TODO Auto-generated catch block
      //e.printStackTrace();
    }
    // notifyObservers(connectionStatus);
  }

  /**
   * @return
   * @throws Exception
   */
  private static boolean tryLogin() throws Exception {
    // attempt to log in
    if(!isValidLogin()) return false;
    try {
      Server server = new Server(serverName, Integer.parseInt(port));
      Login login = new Login(username, password);
      // Instantiate our service factory
      sf = new ServiceFactory(server, login);
      store = new OMEROMetadataStore(sf);
    }
    catch (Throwable t) {
      System.out.println(t.toString());
      return false;
    }
    System.out.println("login worked!");
    return true;
  }

  //create setId method??
  //calls trylogin() ??

  public OMEROMetadataStore getMetadataStore() {
    return store;
  }

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
   // TODO
  }

  public void setMetadata(MetadataRetrieve meta) {
    metadata = meta;
  }

  public boolean canDoStacks() { return true; }

  // -- Helper methods --

  private static boolean isValidLogin() throws Exception {
    System.out.println(username+ password+serverName+ port);
    try {
      store = new OMEROMetadataStore(username, password, serverName, port);
      store.getProjects();
    }
    //catch (EJBAccessException e)
    catch (Exception e) {
      return false;
    }
    return true;
  }

}
