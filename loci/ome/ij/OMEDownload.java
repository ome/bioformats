import ij.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Hashtable;

import org.openmicroscopy.ds.*;
import org.openmicroscopy.ds.dto.*;
import org.openmicroscopy.ds.managers.*;
import org.openmicroscopy.ds.st.*;
import org.openmicroscopy.is.*;


/**
 * OMEDownload handles exporting images from
 * the Open Microscopy Environment.
 * @author Philip Huettl pmhuettl@wisc.edu
 */
public class OMEDownload{
  
  // -- Fields --

  /** Current OME upload thread. */
  private Thread upThread;

 /** Current OME upload data. */
  private ImageProcessor data;
  private ImagePlus imageP;
  
  /** Current OME upload server. */
  private String server;

  /** Current OME upload username. */
  private String username;

  /** Current OME upload password. */
  private String password;
  
  /** Image Stack space or time domain.*/
  private int domainIndex;

  /** Signals exit of plugin */
  private boolean cancelPlugin;
 
  // -- Runnable API methods --
 
  
  /**The getInput method prompts and receives user input to determine
  the OME login fields and whether the stack is in the time or space domain*/
  private void getInput(boolean b, OMELoginPanel d){
    String[] in=d.getInput(b);
    if (in==null) {
      cancelPlugin=true;
      return;
    }
    server=in[0];
    username=in[1];
    password=in[2];
  }//end of getInput method
  
  /**method that prints out a list of the users images*/
  public Image[] retrieveAllImages(DataFactory datafact){
    // get experimenter settings for the logged in user
    FieldsSpecification fs = new FieldsSpecification();
    fs.addWantedField("id");
    fs.addWantedField("experimenter");
    fs.addWantedField("experimenter", "id"); 
    UserState userState = datafact.getUserState(fs);
    Experimenter user = userState.getExperimenter();
    
    //Creating the criteria to search for
		//Specify which fields we want for the image.
    Criteria criteria = new Criteria();
		criteria.addWantedField("id");
		criteria.addWantedField("name");
		criteria.addWantedField("created");
    criteria.addWantedField("description");
		//Specify which fields we want for the pixels.
		criteria.addWantedField("default_pixels");
		criteria.addWantedField("default_pixels", "id");
    criteria.addWantedField("default_pixels", "PixelType");
    criteria.addWantedField("default_pixels","SizeC");
    criteria.addWantedField("default_pixels","SizeT");
    criteria.addWantedField("default_pixels","SizeX");
    criteria.addWantedField("default_pixels","SizeY");
    criteria.addWantedField("default_pixels","SizeZ");
    //specify the images to be owned by the current user
		criteria.addFilter("owner_id", new Integer(user.getID()));
    //retrieve the images using the given criteria
    return retrieveImages(datafact, criteria);
  }//end of retrieveAllImages method
  
  /**method that retrieves the Images from the database that match the 
  criteria given*/
  public static Image[] retrieveImages(DataFactory datafact, Criteria criteria){
    if ( criteria==null) return null;
    List l=datafact.retrieveList(Image.class, criteria);
    if ( l.size()==0) {
      return null;
    }
    Object[] ob=l.toArray();
    Image[] ima= new Image[ob.length];
    for ( int i=0;i<ob.length ;i++ ) {
      ima[i]=(Image)ob[i];
    }
    return ima;
  }//end of retrieveImages method
  
  /**method that initializes the Project criteria fields*/
  public void addProjectFields(Criteria criteria){
    //Specify which fields we want for the project.
		criteria.addWantedField("id");
		criteria.addWantedField("name");
		criteria.addWantedField("datasets");
		//Specify which fields we want for the datasets.
		criteria.addWantedField("datasets", "id");
		criteria.addWantedField("datasets", "name");
    criteria.addWantedField("datasets", "images");
  }//end of addProjectFields
  
  /**method that retrieves all of the projects*/
  public Project[] retrieveAllProjects(DataFactory datafact){
    Criteria criteria=new Criteria();
    addProjectFields(criteria);
    return retrieveProjects(datafact, criteria);
  }//end of retrieveAllProjects method
  
  /**method that retrieves the projects with the given criteria*/
  public Project[] retrieveProjects(DataFactory datafact, Criteria criteria){
    // get experimenter settings for the logged in user
    FieldsSpecification fs = new FieldsSpecification();
    fs.addWantedField("id");
    fs.addWantedField("experimenter");
    fs.addWantedField("experimenter", "id"); 
    UserState userState = datafact.getUserState(fs);
    Experimenter user = userState.getExperimenter();
		//Retrieve the user's projects.
		criteria.addFilter("owner_id", new Integer(user.getID()));
		criteria.addOrderBy("name");
    List l=datafact.retrieveList(Project.class, criteria);
    Object[] ob=l.toArray();
    Project[] ima= new Project[ob.length];
    for ( int i=0;i<ob.length ;i++ ) {
      ima[i]=(Project)ob[i];
    }
    return ima;
  }//end of retrieveProjects method
  
  /**method that retrieves all of current user's datasets from the database*/
  public Dataset[] retrieveAllDatasets(DataFactory datafact){
    FieldsSpecification fs = new FieldsSpecification();
    fs.addWantedField("id");
    fs.addWantedField("experimenter");
    fs.addWantedField("experimenter", "id"); 
    UserState userState = datafact.getUserState(fs);
    Experimenter user = userState.getExperimenter();
    
    Criteria criteria = new Criteria();
		//Specify which fields we want for the dataset.
		criteria.addWantedField("id");
		criteria.addWantedField("name");
		criteria.addFilter("owner_id", new Integer(user.getID()));
		criteria.addFilter("name", "NOT LIKE", "ImportSet");
		criteria.addOrderBy("name");
    return retrieveDatasets(datafact, criteria);
  }//end of retrieveAllDatasets method
  
  /**method that retrieves the datasets matching the given criteria*/      
  public Dataset[] retrieveDatasets(DataFactory datafact, Criteria criteria){
    List l=datafact.retrieveList(Dataset.class, criteria);
    Object[] ob=l.toArray();
    
    Dataset[] ima= new Dataset[ob.length];
    for ( int i=0;i<ob.length ;i++ ) {
      ima[i]=(Dataset)ob[i];
    }
    return ima;
  }//end fo retrieveDatasets method
  
  /**method that retrieves a list of experimenters from the database*/
  public String[][] retrieveExperimenters(DataFactory datafact){
    Criteria criteria = new Criteria();
		//Specify which fields we want for the project.
		criteria.addWantedField("FirstName");
    criteria.addWantedField("LastName");
		criteria.addOrderBy("LastName");
    List l=datafact.retrieveList("Experimenter", criteria);
    Object[] ob=l.toArray();
    String[][] exp= new String[3][ob.length];
    //pack the experimenter info into an array
    for ( int i=0; i<ob.length; i++ ) {
      exp[0][i]=((Experimenter)ob[i]).getFirstName();
      exp[1][i]=((Experimenter)ob[i]).getLastName();
      exp[2][i]=new Integer(((Experimenter)ob[i]).getID()).toString();
    }
    return exp;
  }//end of retrieveExperimenters method
  
  /**adds all image fields to an image criteria*/
  public static void addImageFields(Criteria criteria){
    criteria.addWantedField("id");
		criteria.addWantedField("name");
		criteria.addWantedField("created");
    criteria.addWantedField("description");
    criteria.addWantedField("owner");
    criteria.addWantedField("datasets");
    criteria.addWantedField("all_features");
    criteria.addWantedField("all_features", "name");
    criteria.addWantedField("all_features", "tag");
    criteria.addWantedField("all_features", "children");
    criteria.addWantedField("all_features", "parent_feature");
    criteria.addWantedField("owner", "FirstName");
    criteria.addWantedField("owner", "LastName");
    criteria.addWantedField("owner", "id");
    criteria.addWantedField("default_pixels");
    criteria.addWantedField("default_pixels", "id");
    criteria.addWantedField("default_pixels", "PixelType");
    criteria.addWantedField("default_pixels","SizeC");
    criteria.addWantedField("default_pixels","SizeT");
    criteria.addWantedField("default_pixels","SizeX");
    criteria.addWantedField("default_pixels","SizeY");
    criteria.addWantedField("default_pixels","SizeZ");
    criteria.addWantedField("default_pixels","FileSHA1");
    criteria.addWantedField("default_pixels","ImageServerID");
    
//    criteria.addWantedField("default_pixels","Repository");
//    criteria.addWantedField("default_pixels","Repository.ImageServerURL");
    
		FieldsSpecification repoFieldSpec = new FieldsSpecification();
    repoFieldSpec.addWantedField("Repository");
    repoFieldSpec.addWantedField("Repository", "ImageServerURL");
    criteria.addWantedFields("default_pixels", repoFieldSpec);
  }//end of addImageFields method
  
  public static final Criteria IMAGE_FIELDS = makeImageFields();
  private static Criteria makeImageFields() {
    Criteria criteria = new Criteria();
    criteria.addWantedField("id");
		criteria.addWantedField("name");
		criteria.addWantedField("created");
    criteria.addWantedField("description");
    criteria.addWantedField("owner");
    criteria.addWantedField("datasets");
    criteria.addWantedField("all_features");
    criteria.addWantedField("all_features", "name");
    criteria.addWantedField("all_features", "tag");
    criteria.addWantedField("all_features", "children");
    criteria.addWantedField("all_features", "parent_feature");
    criteria.addWantedField("owner", "FirstName");
    criteria.addWantedField("owner", "LastName");
    criteria.addWantedField("owner", "id");
    criteria.addWantedField("default_pixels");
    criteria.addWantedField("default_pixels", "id");
    criteria.addWantedField("default_pixels", "PixelType");
    criteria.addWantedField("default_pixels","SizeC");
    criteria.addWantedField("default_pixels","SizeT");
    criteria.addWantedField("default_pixels","SizeX");
    criteria.addWantedField("default_pixels","SizeY");
    criteria.addWantedField("default_pixels","SizeZ");
    criteria.addWantedField("default_pixels","FileSHA1");
    criteria.addWantedField("default_pixels","ImageServerID");
    //
    criteria.addWantedField("default_pixels","Repository");
    criteria.addWantedField("default_pixels","Repository.ImageServerURL");
//		FieldsSpecification repoFieldSpec = new FieldsSpecification();
//    repoFieldSpec.addWantedField("Repository");
//    repoFieldSpec.addWantedField("Repository", "ImageServerURL");
//    criteria.addWantedFields("default_pixels", repoFieldSpec);
    return criteria;
  }//end of makeImageFields method
  
  /**populates the criteria for a certain attribute*/
  public static Criteria makeAttributeFields(String[] attrName) {
    Criteria criteria=new Criteria();
    for (int i=0; i<attrName.length; i++){
      criteria.addWantedField(attrName[i]);
    }
    return criteria;
  }
  
  /**retrieves images using the object array that specifies
  String Project, String Owner, Integer ImageID, String ImageName, 
  and String Image Type*/
  private Image[] retrieveImages(DataFactory datafact, Object[] ob){
    //Unpack object array
    String project=(String)ob[0];
    String owner=(String)ob[1];
    int imageID=((Integer)ob[2]).intValue();
    String imageName=(String)ob[3];
    //array to return
    Image[] images;
    //create criteria to add filters to
    Criteria criteria=new Criteria();
    addImageFields(criteria);
    //check if an image ID was specified and add filter
    if ( imageID!=0) {
      criteria.addFilter("id", new Integer(imageID));
    }
    //check if an owner was specified and add filter
    if ( !owner.equals("All")) {
      String[][] users=retrieveExperimenters(datafact);
      for ( int i=0;i<users[0].length ;i++ ) {
        if ( owner.equals(users[0][i]+" "+users[1][i])) {
          criteria.addFilter("owner_id", Integer.valueOf(users[2][i]));
          i=users[0].length;
        }
      }
    }
    if ( !imageName.equals("")) {
      criteria.addFilter("name", imageName);
    }
    //check to see if a project was specified
    if ( !project.equals("All")) {
      Criteria c=new Criteria();
      addProjectFields(c);
      c.addFilter("name", project);
      Project[] p=retrieveProjects(datafact, c);
      if ( p.length!=1) {
        OMEDownPanel.error(IJ.getInstance(),
        "There was an error retrieving the specified project.", 
        "Database Error");
        cancelPlugin=true;
        return null;
      }
      //selecting the images in the project
      ArrayList al=getProjectImageIDs(p[0]), alin=new ArrayList();
      Image[] ima=retrieveImages(datafact, criteria);
      for ( int i=0;i<ima.length ;i++ ) {
        if ( al.contains(new Integer(ima[i].getID()))) {
          alin.add(ima[i]);
        }
      }
      alin.trimToSize();
      Image[] imas=new Image[alin.size()];
      for ( int j=0;j<alin.size() ;j++ ) {
        imas[j]=(Image)alin.get(j);
      }
      return imas;
    }else {
     return retrieveImages(datafact, criteria); 
    }
  }//end of retrieveImages(Object[] ob) method
  
  /**Method that retrieves an image from its ID*/
  public static Image getImagefromID(DataFactory datafactory, int imageID){
    Criteria criteria=new Criteria();
    addImageFields(criteria);
    criteria.addFilter("id", new Integer(imageID));
    Image[] imas=retrieveImages(datafactory, criteria);
    return imas[0];
  }//end of getImagefromID method
  
  /**Method that puts an image from OME back into ImageJ*/
  private void download(Image image, PixelsFactory pf, OMESidePanel omesp, DataFactory df){
    if (cancelPlugin) {
      return;
    }
    //get pixel data
    Pixels pix=image.getDefaultPixels();
    int sizeX, sizeY, sizeZ, sizeC, sizeT;
    sizeX=pix.getSizeX().intValue();
    sizeY=pix.getSizeY().intValue();
    sizeZ=pix.getSizeZ().intValue();
    sizeC=pix.getSizeC().intValue();
    sizeT=pix.getSizeT().intValue();
    String typeS=pix.getPixelType();
    Hashtable table=new Hashtable(4);
    table.put("uint16", new Integer(ImagePlus.GRAY16));
    table.put("uint8", new Integer(ImagePlus.GRAY8));
    table.put("float", new Integer(ImagePlus.GRAY32));
    table.put("color256", new Integer(ImagePlus.COLOR_256));
    table.put("colorRGB", new Integer(ImagePlus.COLOR_RGB));
    table.put("uint32", new Integer(17));
    table.put("int16", new Integer(ImagePlus.GRAY16));
    int type=((Integer)table.get(typeS)).intValue();
    if ( sizeC==3) {
      type=ImagePlus.COLOR_RGB;
    }
    //ImageJ can only have one varied axis so if there is more
    //than one varied axis, prompt the user for desired axis
    int z1=0;
    int t1=0;
    if ( sizeZ!=1 && sizeT!=1) {
      OMEDomainPanel domainPane=new OMEDomainPanel(IJ.getInstance(), sizeZ, sizeT);
      int[] results=domainPane.getInput();
      if (results==null) {
        cancelPlugin=true;
        return;
      }
      if ( results[0]==0) {
        z1=results[1];
        sizeZ=results[1]+1;
      }
      else {
        t1=results[1];
        sizeT=results[1]+1;
      }
    }
    //Create Stack to add planes to in ImageJ
    ImageStack is=new ImageStack(sizeX, sizeY);
    try {
      for (int t=t1; t<sizeT; t++) {
        for (int z=z1; z<sizeZ; z++) {
         byte[] pixelb = new byte[sizeX * sizeY];
         int[] pixeli=new int[sizeX*sizeY];
         short[] pixels=new short[sizeX*sizeY];
         double[] pixeld=new double[sizeX*sizeY];
         IJ.showStatus("OmeDownload: Loading data (t=" + t + ", z=" + z + ")...");
         if ( type==17) {
           byte[] pixs=pf.getPlane(pix, z, 0, t, true);
           for ( int i=0; i<pixeli.length; i++) {
             pixeli[i]=((pixs[4*i] & 0xff)<<24) +((pixs[4*i+1] & 0xff)<<16) +
             ((pixs[4*i+2] & 0xff)<<8) +(pixs[4*i+3] & 0xff);
             pixeld[i]=(new Integer(pixeli[i])).doubleValue();
           }
           FloatProcessor ip=new FloatProcessor(sizeX, sizeY, pixeld);
           //adding plane to the ImageStack
           is.addSlice("Z="+z+" T="+t, ip);
         }
         if (type==ImagePlus.COLOR_256 ) {
           pixelb=pf.getPlane(pix, z, 0, t, true);
           for ( int i=0;i<pixelb.length ;i++ ) {
             pixelb[i]=(byte)(128-pixelb[i]);
           }
           ByteProcessor ip=new ByteProcessor(sizeX, sizeY);
           ip.setPixels(pixelb);
           //adding plane to the ImageStack
           is.addSlice("Z="+z+" T="+t, ip);
         }
         if (type==ImagePlus.COLOR_RGB ) {
           byte []r=pf.getPlane(pix, z, 0, t, true);
           byte []g=pf.getPlane(pix, z, 1, t, true);
           byte []b=pf.getPlane(pix, z, 2, t, true);
           for (int i=0;i<pixeli.length ;i++ ) {
             pixeli[i]=((r[i] & 0xff)<<16)+((g[i] & 0xff)<<8) + (b[i] & 0xff);
           }
           ColorProcessor ip=new ColorProcessor(sizeX, sizeY, pixeli);
           //adding plane to the ImageStack
           is.addSlice("Z="+z+" T="+t, ip);
         }
         if (type==ImagePlus.GRAY16 ) {                     
           byte[] pixs= pf.getPlane(pix, z,0,t, true);
           for ( int i=0; i<pixels.length; i++) {
             pixels[i]=(short)(((pixs[2*i] & 0xff)<<8)+(pixs[2*i+1] & 0xff));
           }
           ShortProcessor ip=new ShortProcessor(sizeX, sizeY);
           ip.setPixels(pixels);
           //adding plane to the ImageStack
           is.addSlice("Z="+z+" T="+t, ip);
         }
         if (type==ImagePlus.GRAY32 ) {            
           byte[] pixs=pf.getPlane(pix, z, 0, t, true);
           for ( int i=0; i<pixeli.length; i++) {
             pixeli[i]=((pixs[4*i] & 0xff)<<24) +((pixs[4*i+1] & 0xff)<<16) +
             ((pixs[4*i+2] & 0xff)<<8) +(pixs[4*i+3] & 0xff);
             pixeld[i]=(double)Float.intBitsToFloat(pixeli[i]);
           }
           FloatProcessor ip=new FloatProcessor(sizeX, sizeY, pixeld);
           //adding plane to the ImageStack
           is.addSlice("Z="+z+" T="+t, ip);
         }
         if (type==ImagePlus.GRAY8 ) {
           byte[] pixsb = pf.getPlane(pix, z, 0, t, true);
           System.arraycopy(pixsb, 0, pixelb, 0, pixsb.length);
           ByteProcessor ip=new ByteProcessor(sizeX, sizeY);
           ip.setPixels(pixelb);
           //adding plane to the ImageStack
           is.addSlice("Z="+z+" T="+t, ip);
         }
       }
     }
    }catch(ImageServerException e){
      OMEDownPanel.error(IJ.getInstance(),
      "There was an error downloading the image.\n"+e.toString(), 
      "Download Error");
      cancelPlugin=true;
      e.printStackTrace();
      return;
    }
    //Create the ImagePlus in ImageJ and display it
    ImagePlus iPlus=new ImagePlus(image.getName(), is);
    
    //retrieve metadata to possibly edit
    Object[] metas=new Object[2];
    metas[0]=new Integer(image.getID());
    metas[1]=OMEMetaDataHandler.exportMeta(image, df);
    omesp.hashInImage(iPlus.getID(), metas);
    iPlus.show();
  }//end of download method
  
  /**method that returns all image IDs in a certain project*/
  public ArrayList getProjectImageIDs(Project project){
    Object[] ob=project.getDatasets().toArray();
    Dataset[] datasets=new Dataset[ob.length];
    for ( int i=0;i<datasets.length ;i++ ) {
      datasets[i]=(Dataset)ob[i];
    }
    ArrayList images=new ArrayList();
    for (int i=0;i<datasets.length ;i++ ) {
      Object[] ob1=datasets[i].getImages().toArray();
      for ( int j=0;j<ob1.length ;j++ ) {
        images.add(new Integer(((Image)ob1[j]).getID()));
      }
    }
    return images;
  }//end of getProjectImages method
  
  /**returns a list of images that the user chooses*/
  private Image[] getDownPicks(Image[] ima, DataFactory df, PixelsFactory pf){
    //table array
    Object[][] props=new Object[ima.length][4];
    //details array
    Object[][] details=new Object[ima.length][9];
    //build a hashtable of experimenters to display names
    String[][] expers=retrieveExperimenters(df);
    Hashtable hm=new Hashtable(expers.length);
    for ( int i=0;i<expers[0].length ;i++ ) {
      hm.put(new Integer(expers[2][i]), expers[1][i]+", "+expers[0][i]);
    }
    //assemble the table array
    for ( int i=0 ;i<props.length ;i++ ) {
      props[i][1]=ima[i].getName();
      props[i][2]=String.valueOf(ima[i].getID());
      details[i][1]=(String)hm.get(new Integer(ima[i].getOwner().getID()));
      props[i][3]=ima[i].getCreated();
      props[i][0]=new Boolean(false);
      details[i][8]=ima[i].getDescription();
      Pixels p=ima[i].getDefaultPixels();
      try {
        details[i][0]=pf.getThumbnail(p);
      }
      
      catch (NoClassDefFoundError e) {
          details[i][0]=null;
      }
      catch (Throwable t) {
        OMEDownPanel.error(IJ.getInstance(),
        "An exception occured.\n"+t.toString(), 
        "Error");
        IJ.showStatus("Error Downloading thumbnails.");
        t.printStackTrace();
        details[i][0]=null;
      }
      
      details[i][2]=p.getPixelType();
      details[i][3]=p.getSizeC().toString();
      details[i][4]=p.getSizeT().toString();
      details[i][5]=p.getSizeX().toString();
      details[i][6]=p.getSizeY().toString();
      details[i][7]=p.getSizeZ().toString();
    }
    String[] columns={"","Name","ID","Date Created"};
    //The details array has the following contents
    //"Thumbnail", "Owner", "Pixel Type", "Size C","Size T",
    //"Size X","Size Y","Size Z","Description"

    //create the table
    OMETablePanel tp=new OMETablePanel(IJ.getInstance(), props, columns,
    details);
    int[] results=tp.getInput();
    if ( results==null) {
      cancelPlugin=true;
      return null;
    }
    Image[] returns=new Image[results.length];
    for ( int i=0;i<results.length ;i++ ) {
      for ( int j=0;j<props.length ;j++ ) {
        if ( results[i]==Integer.parseInt((String)props[j][2])) {
          returns[i]=ima[j];
        }
      }
    }
    return returns;
  }//end of getDownPicks method

  /** Does the work for uploading data to OME. */
  public void run(OMESidePanel osp) {
    IJ.showProgress(0);
    IJ.showStatus("OmeDownload: Logging in...");
    OMELoginPanel lp=new OMELoginPanel(IJ.getInstance());

    // This code has been adapted from Doug Creager's TestImport example
    try {
      // login to OME
      boolean errorOrNot=false;
      boolean loggedIn=false;
      getInput(false, lp);
      if (cancelPlugin) {
        IJ.showProgress(1);
        IJ.showStatus("OmeDownload: Exited.");
        return;
      }
      DataServices rs = DataServer.getDefaultServices(server);
      RemoteCaller rc = rs.getRemoteCaller();
      while (!loggedIn){
        try {
          if ( cancelPlugin) {
            IJ.showProgress(1);
            IJ.showStatus("OmeDownload: Exited.");
            return;
          }
          if ( errorOrNot) {
            rs=DataServer.getDefaultServices(server);
            rc=rs.getRemoteCaller();
          }
          rc.login(username , password);
          loggedIn=true;
        }
        catch (Exception e) {
          errorOrNot=true;
          getInput(true, lp);
          if ( cancelPlugin) {
            IJ.showProgress(1);
            IJ.showStatus("OmeDownload: Exited.");
            return;
          }
        }
      }
      // retrieve helper classes needed for importing
      IJ.showStatus("OmeDownload: Getting image information...");
      IJ.showProgress(.1);
      DataFactory df = (DataFactory) rs.getService(DataFactory.class);
      ImportManager im = (ImportManager) rs.getService(ImportManager.class);
      PixelsFactory pf = (PixelsFactory) rs.getService(PixelsFactory.class);
      DatasetManager dm = (DatasetManager) rs.getService(DatasetManager.class);
      ConfigurationManager cm = (ConfigurationManager)
      rs.getService(ConfigurationManager.class);
      AnalysisEngineManager aem = (AnalysisEngineManager)
      rs.getService(AnalysisEngineManager.class);
      // start the import process
      IJ.showStatus("OmeDownload: Starting import...");
      im.startImport();
      IJ.showProgress(0.15);
  
      if ( cancelPlugin) {
        IJ.showProgress(1);
        IJ.showStatus("OmeDownload: Exited.");
        return;
      }
      //get database info to use in search
      IJ.showStatus("Getting database info..");
      String[][] owners=retrieveExperimenters(df);
      Project[] projects=retrieveAllProjects(df);
      //create search panel
      IJ.showStatus("Creating search panel...");
      OMEDownPanel dp=new OMEDownPanel(IJ.getInstance(), projects, owners);
      Image[] images=new Image[0];
      //do the image search
      IJ.showStatus("Searching for images...");
      while (images.length==0){
       Object[] objects=dp.search();
       if ( objects==null) {
         cancelPlugin=true;
         IJ.showProgress(1);
         IJ.showStatus("OmeDownload: Exited.");
         return;
       }
       //get search results
       images=retrieveImages(df, objects);
       if ( images.length==0) {
         OMELoginPanel.infoShow(IJ.getInstance(), "No images matched the specified criteria.",
         "OME Download");
       }else {
         //pick from results
         images=getDownPicks(images,df, pf);
       
         if ( cancelPlugin) {
          IJ.showProgress(1);
          IJ.showStatus("OmeDownload: Exited.");
           return;
          }
       }
      }
      //download into ImageJ
      for ( int i=0;i<images.length ;i++ ) {
        download(images[i], pf, osp, df);
        if ( cancelPlugin) {
          IJ.showProgress(1);
          IJ.showStatus("OmeDownload: Exited.");
          return;
        }
      }
       
      
      // log out
      IJ.showStatus("OmeDownload: Logging out...");
      IJ.showProgress(.99);
      rc.logout();
      IJ.showStatus("OmeDownload: Completed");
      OMELoginPanel.infoShow(IJ.getInstance(), "Download Completed Successfully.",
      "OME Download");
    }
    catch (Exception exc) {
      OMEDownPanel.error(IJ.getInstance(),
      "An error occured.\n"+exc.toString(), 
      "Error");
      IJ.showStatus("Error Downloading.");
      exc.printStackTrace();
    }

    upThread = null;
    IJ.showProgress(1);
  }
}