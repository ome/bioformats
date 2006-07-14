package loci.ome.ij;

import ij.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.openmicroscopy.ds.*;
import org.openmicroscopy.ds.dto.*;
import org.openmicroscopy.ds.st.*;

/**
 * Utility methods for retrieving images.
 * @author Philip Huettl pmhuettl@wisc.ed
 * @author Melissa Linkert linkert at cs.wisc.edu
 */

public class OMERetrieve {

  /** method that prints out a list of the users images */
  public static Image[] retrieveAllImages(DataFactory datafact) {
    // get experimenter settings for the logged in user
    Experimenter user = getUser(datafact);

    //Creating the criteria to search for
    //Specify which fields we want for the image.

    Criteria criteria = new Criteria();
    makeAttributeFields(criteria, new String[] {"id", "name", "created",
      "description", "default_pixels"});

    //Specify which fields we want for the pixels.
    makeAttributeFields(criteria, "default_pixels", new String[] {"id",
      "PixelType", "SizeC", "SizeT", "SizeX", "SizeY", "SizeZ"});

    //specify the images to be owned by the current user
    criteria.addFilter("owner_id", new Integer(user.getID()));

    //retrieve the images using the given criteria
    return retrieveImages(datafact, criteria);
  }

  /**
   * method that retrieves the Images from the database that match the
   * criteria given
   */
  public static Image[] retrieveImages(DataFactory datafact, Criteria c) {
    if (c == null) return null;
    List l = retrieveList(Image.class, c, datafact);
    if (l == null) return null;
    if (l.size() == 0) {
      return null;
    }
    Object[] ob = l.toArray();
    Image[] ima = new Image[ob.length];
    for (int i=0; i<ob.length; i++) {
      ima[i] = (Image) ob[i];
    }
    return ima;
  }

  /** method that retrieves all of the projects */
  public static Project[] retrieveAllProjects(DataFactory datafact) {
    Criteria criteria = new Criteria();
    addProjectFields(criteria);
    return retrieveProjects(datafact, criteria);
  }

  /** method that retrieves the projects with the given criteria */
  public static Project[] retrieveProjects(DataFactory datafact,
      Criteria criteria) {
    // get experimenter settings for the logged in user
    Experimenter user = getUser(datafact);

    try {
      // Retrieve the user's projects.
      criteria.addFilter("owner_id", new Integer(user.getID()));
      criteria.addOrderBy("name");
      List l = retrieveList(Project.class, criteria, datafact);
      if (l == null) return new Project[0];
      Object[] ob = l.toArray();
      Project[] ima = new Project[ob.length];
      for (int i=0; i<ob.length; i++) {
        ima[i] = (Project)ob[i];
      }
      return ima;
    }
    catch (NullPointerException n) {
      return new Project[0];
    }
  }

  /** get the settings for the current user */
  public static Experimenter getUser(DataFactory df) {
    FieldsSpecification fs = new FieldsSpecification();
    fs.addWantedField("id");
    fs.addWantedField("experimenter");
    fs.addWantedField("experimenter", "id");
    try {
      UserState userState = df.getUserState(fs);
      return userState.getExperimenter();
    }
    catch (NullPointerException n) {
      return null;
    }
  }

  /** method that retrieves all of current user's datasets from the database */
  public static Dataset[] retrieveAllDatasets(DataFactory datafact) {
    Experimenter user = getUser(datafact);
    Criteria criteria = new Criteria();

    //Specify which fields we want for the dataset.
    makeAttributeFields(criteria, new String[] {"id", "name"});
    criteria.addFilter("owner_id", new Integer(user.getID()));
    criteria.addFilter("name", "NOT LIKE", "ImportSet");
    criteria.addOrderBy("name");
    return retrieveDatasets(datafact, criteria);
  }

  /** method that retrieves the datasets matching the given criteria */
  public static Dataset[] retrieveDatasets(DataFactory datafact,
      Criteria criteria) {
    List l = retrieveList(Dataset.class, criteria, datafact);
    Object[] ob = l.toArray();

    Dataset[] ima = new Dataset[ob.length];
    for (int i=0; i<ob.length; i++) {
      ima[i] = (Dataset)ob[i];
    }
    return ima;
  }

  /** method that retrieves a list of experimenters from the database */
  public static String[][] retrieveExperimenters(DataFactory datafact) {
    try {
      Criteria criteria = new Criteria();
      //Specify which fields we want for the project.
      makeAttributeFields(criteria, new String[] {"FirstName", "LastName"});
      criteria.addOrderBy("LastName");

      List l = retrieveList("Experimenter", criteria, datafact);
      Object[] ob = l.toArray();
      String[][] exp = new String[3][ob.length];
      //pack the experimenter info into an array
      for (int i=0; i<ob.length; i++) {
        exp[0][i] = ((Experimenter)ob[i]).getFirstName();
        exp[1][i] = ((Experimenter)ob[i]).getLastName();
        exp[2][i] = new Integer(((Experimenter)ob[i]).getID()).toString();
      }
      return exp;
    }
    catch (NullPointerException n) { return null; }
  }

  /**
   * retrieves images using the object array that specifies
   * String Project, String Owner, Integer ImageID, String ImageName,
   * and String Image Type
   */
  public static Image[] retrieveImages(DataFactory datafact, Object[] ob) {
    //Unpack object array
    String project = (String) ob[0];
    String owner = (String) ob[1];
    int imageID = ((Integer) ob[2]).intValue();
    String imageName = (String) ob[3];
    //array to return
    Image[] images;
    //create criteria to add filters to
    Criteria criteria = new Criteria();
    addImageFields(criteria);
    //check if an image ID was specified and add filter
    if (imageID != 0) {
      criteria.addFilter("id", new Integer(imageID));
    }
    //check if an owner was specified and add filter
    if (!owner.equals("All")) {
      String[][] users = retrieveExperimenters(datafact);
      for (int i=0; i<users[0].length; i++) {
        if (owner.equals(users[0][i] + " " + users[1][i])) {
          criteria.addFilter("owner_id", Integer.valueOf(users[2][i]));
          i = users[0].length;
        }
      }
    }
    if (!imageName.equals("")) {
      criteria.addFilter("name", imageName);
    }
    //check to see if a project was specified
    if (!project.equals("All")) {
      Criteria c = new Criteria();
      addProjectFields(c);
      c.addFilter("name", project);
      Project[] p = retrieveProjects(datafact, c);
      if (p.length != 1) {
        OMEDownPanel.error(IJ.getInstance(),
          "There was an error retrieving the specified project.",
          "Database Error");
        OMETools.setPlugin(true);
        return null;
      }
      //selecting the images in the project
      ArrayList al = getProjectImageIDs(p[0]);
      ArrayList alin = new ArrayList();
      Image[] ima = retrieveImages(datafact, criteria);
      for (int i=0; i<ima.length; i++) {
        if (al.contains(new Integer(ima[i].getID()))) {
          alin.add(ima[i]);
        }
      }
      alin.trimToSize();
      Image[] imas = new Image[alin.size()];
      for (int j=0; j<alin.size(); j++) {
        imas[j] = (Image)alin.get(j);
      }
      return imas;
    }
    else {
      return retrieveImages(datafact, criteria);
    }
  }

  /** Method that retrieves an image from its ID */
  public static Image getImagefromID(DataFactory datafactory, int imageID) {
    Criteria criteria = new Criteria();
    addImageFields(criteria);
    criteria.addFilter("id", new Integer(imageID));
    Image[] imas = retrieveImages(datafactory, criteria);
    if (imas == null) return null;
    return imas[0];
  }


  /** method that returns all image IDs in a certain project */
  public static ArrayList getProjectImageIDs(Project project) {
    Object[] ob = project.getDatasets().toArray();
    Dataset[] datasets = new Dataset[ob.length];
    for (int i=0; i<datasets.length; i++) {
      datasets[i] = (Dataset)ob[i];
    }
    ArrayList images = new ArrayList();
    for (int i=0; i<datasets.length; i++) {
      Object[] ob1 = datasets[i].getImages().toArray();
      for (int j=0; j<ob1.length; j++) {
        images.add(new Integer(((Image)ob1[j]).getID()));
      }
    }
    return images;
  }

  /** method that initializes the Project criteria fields */
  public static void addProjectFields(Criteria c) {
    makeAttributeFields(c, new String[] {"id", "name", "datasets"});
    makeAttributeFields(c, "datasets", new String[] {"id", "name", "images"});
  }

  /** adds all image fields to an image criteria */
  public static void addImageFields(Criteria c) {
    /*
    makeAttributeFields(c, new String[] {"id", "name", "created",
      "description", "owner", "datasets", "all_features", "default_pixels"});
    makeAttributeFields(c, "all_features", new String[] {"name", "tag",
      "children", "parent_feature"});
    makeAttributeFields(c, "owner", new String[] {"FirstName", "LastName",
      "id"});
    makeAttributeFields(c, "default_pixels", new String[] {"id", "PixelType",
      "SizeC", "SizeT", "SizeX", "SizeY", "SizeZ", "FileSHA1",
      "ImageServerID"});
    */

    c.addWantedField("id");
    c.addWantedField("name");
    c.addWantedField("description");
    c.addWantedField("inserted");
    c.addWantedField("created");
    c.addWantedField("owner");
    c.addWantedField("default_pixels");

    c.addWantedField("default_pixels", "id");
    c.addWantedField("default_pixels", "SizeX");
    c.addWantedField("default_pixels", "SizeY");
    c.addWantedField("default_pixels", "SizeZ");
    c.addWantedField("default_pixels", "SizeC");
    c.addWantedField("default_pixels", "SizeT");
    c.addWantedField("default_pixels", "PixelType");
    c.addWantedField("default_pixels", "Repository");
    c.addWantedField("default_pixels", "ImageServerID");
    c.addWantedField("default_pixels.Repository", "ImageServerURL");

    c.addWantedField("owner", "FirstName");
    c.addWantedField("owner", "LastName");
    c.addWantedField("owner", "id");

    FieldsSpecification fs = new FieldsSpecification();
    fs.addWantedField("Repository");
    fs.addWantedField("Repository", "ImageServerURL");
    c.addWantedFields("default_pixels", fs);
  }

  /** populates the criteria for a certain attribute */
  public static Criteria makeAttributeFields(String[] attr) {
    Criteria c = new Criteria();
    for(int i=0; i<attr.length; i++) {
      c.addWantedField(attr[i]);
    }
    return c;
  }

  /** populates the criteria for a certain attribute */
  public static void makeAttributeFields(Criteria c, String[] attr) {
    for(int i=0; i<attr.length; i++) {
      c.addWantedField(attr[i]);
    }
  }

  /** populates the criteria for a certain attribute */
  public static void makeAttributeFields(Criteria c, String name, String[] a) {
    for(int i=0; i<a.length; i++) {
      c.addWantedField(name, a[i]);
    }
  }

  /**
   * HACK: this replaces calls to DataFactory.retrieveList(String, Criteria),
   * since that method is broken for unknown reasons.
   */
  public static List retrieveList(String st, Criteria crit, DataFactory df) {
    List l = new Vector();
    int need = df.count(st, crit);
    int have = 0;
    while (have < need) {
      crit.setOffset(have);
      l.add(df.retrieve(st, crit));
      have++;
    }
    return l;
  }

  /**
   * HACK: this replaces calls to DataFactory.retrieveList(Class, Criteria),
   * since that method is broken for unknown reasons.
   */
  public static List retrieveList(Class st, Criteria crit, DataFactory df) {
    List l = new Vector();
    int need = df.count(st, crit);
    int have = 0;
    while (have < need) {
      crit.setOffset(have);
        l.add(df.retrieve(st, crit));
      have++;
    }
    return l;
  }
}
