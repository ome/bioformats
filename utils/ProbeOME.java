//
// ProbeOME.java
//

import java.net.MalformedURLException;
import java.util.List;

import org.openmicroscopy.ds.Criteria;
import org.openmicroscopy.ds.DataFactory;
import org.openmicroscopy.ds.DataServer;
import org.openmicroscopy.ds.RemoteCaller;
import org.openmicroscopy.ds.RemoteException;
import org.openmicroscopy.ds.dto.Dataset;
import org.openmicroscopy.ds.dto.Image;
import org.openmicroscopy.ds.dto.Project;
import org.openmicroscopy.ds.st.Experimenter;
import org.openmicroscopy.ds.st.Pixels;
import org.openmicroscopy.is.ImageServerException;
import org.openmicroscopy.is.PixelsFactory;
import org.openmicroscopy.is.CompositingSettings;

/** Does some basic querying of an OME database and outputs the results. */
public class ProbeOME {

  public static void print(int tabs, int num, Object o) {
    String indent = "";
    for (int i=0; i<tabs; i++) indent += "\t";
    if (o instanceof Project) {
      Project p = (Project) o;
      System.out.println(indent + "Project #" + num + ":");
      System.out.println(indent + "\tid = " + p.getID());
      System.out.println(indent + "\tname = " + p.getName());
      System.out.println(indent + "\tdescription = " + p.getDescription());

      System.out.println(indent + "\tdatasets:");
      List datasets = p.getDatasets();
      if (datasets == null) System.out.println("\t\tnull");
      else {
        for (int j=0; j<datasets.size(); j++) {
          print(tabs + 2, j + 1, datasets.get(j));
        }
      }

      System.out.println("\towner:");
      Experimenter e = p.getOwner();
      if (e == null) System.out.println("\t\tnull");
      else {
        System.out.println("\t\tid = " + e.getID());
        System.out.println("\t\tDataDirectory = " + e.getDataDirectory());
        System.out.println("\t\tFirstName = " + e.getFirstName());
        System.out.println("\t\tLastName = " + e.getLastName());
        System.out.println("\t\tInstitution = " + e.getInstitution());
        System.out.println("\t\tEmail = " + e.getEmail());
      }
    }
    else if (o instanceof Dataset) {
      Dataset d = (Dataset) o;
      System.out.println(indent + "Dataset #" + num + ":");
      System.out.println(indent + "\tid = " + d.getID());
      System.out.println(indent + "\tname = " + d.getName());
    }
    else {
      String name = o.getClass().getName();
      System.out.println(indent + "Unknown data type: " + name);
    }
  }

  public static void main(String[] args) {
    RemoteCaller rc;
    String url = "http://skyking/shoola";
    try { rc = DataServer.getDefaultCaller(url); }
    catch (MalformedURLException exc) {
      System.out.println("Invalid server name.");
      exc.printStackTrace();
      return;
    }
    String username = "curtis";
    String password = "alpha++";
    System.out.println("Probing skyking OME database as user curtis...");
    try { rc.login(username, password); }
    catch (RemoteException exc) {
      System.out.println("Unable to login.");
      exc.printStackTrace();
      return;
    }

    // ------ Metadata retrieval ------
    DataFactory df = new DataFactory(rc);

    Criteria c = new Criteria();
    c.addWantedField("id");
    c.addWantedField("name");
    c.addWantedField("description");
    c.addWantedField("datasets");
    c.addWantedField("datasets", "id");
    c.addWantedField("datasets", "name");
    c.addWantedField("owner");
    c.addWantedField("owner", "id");
    c.addWantedField("owner", "DataDirectory");
    c.addWantedField("owner", "FirstName");
    c.addWantedField("owner", "LastName");
    c.addWantedField("owner", "Institution");
    c.addWantedField("owner", "Email");

    List list = (List) df.retrieveList(Project.class, c);
    System.out.println(list.size() + " elements in list.");
    for (int i=0; i<list.size(); i++) print(0, i + 1, list.get(i));

    // ------ Data retrieval ------

    // caller and dataFactory retrieved as above
    PixelsFactory pf = new PixelsFactory(df);

    // Load in Image #1, along with its name, and enough information
    // to get its default pixels from the image server
    c = new Criteria();
    c.addWantedField("id");
    c.addWantedField("name");
    c.addWantedField("default_pixels");
    c.addWantedField("default_pixels", "id");
    c.addWantedField("default_pixels", "ImageServerID");
    c.addWantedField("default_pixels", "Repository");
    c.addWantedField("default_pixels.Repository", "id");
    c.addWantedField("default_pixels.Repository", "ImageServerURL");

    // Perform the DB query
    Image image = (Image) df.load(Image.class, 1, c);
    Pixels pixels = image.getDefaultPixels();

    // Create some compositing settings
    CompositingSettings  cs = new CompositingSettings(0, 0);
    cs.setLevelBasis(CompositingSettings.GEOMETRIC_MEAN);
    cs.activateRedChannel(0, -4f, 4f, 1f);
    cs.activateRedChannel(1, -4f, 4f, 1f);
    cs.resizeImage(100, 100);

    // Retrieve an AWT image contain the specified compositing information
    java.awt.Image awtImage;
    try { awtImage = pf.getComposite(pixels, cs); }
    catch (ImageServerException exc) {
      System.out.println("Cannot acquire pixels.");
      exc.printStackTrace();
      return;
    }
  }

}
