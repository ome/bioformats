// OMEUpload_.java

import ij.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;
import ij.gui.GenericDialog;

import java.awt.TextField;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.openmicroscopy.ds.*;
import org.openmicroscopy.ds.dto.*;
import org.openmicroscopy.ds.managers.*;
import org.openmicroscopy.ds.st.*;
import org.openmicroscopy.is.*;


/**
 * OMEUpload is the plugin for ImageJ 
 * that handles the interaction between
 * ImageJ and the Open Microscopy Environment.
 * @author Philip Huettl pmhuettl@wisc.edu
 */
public class OMEUpload_ implements PlugInFilter {
  
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
 
  /*This method sets up the plugin filter for use. The arg string has the same function
  as in the run method of the PlugIn interface. You do not have to care for the
  argument imp—this is handled by ImageJ and the currently active image is passed.
  The setup method returns a flag word that represents the filters capabilities (i.e.
  which types of images it can handle).
  The following capability flags are defined in PlugInFilter:
  static int DOES 16 The plugin filter handles 16 bit grayscale images.
  static int DOES 32 The plugin filter handles 32 bit floating point grayscale images.
  static int DOES 8C The plugin filter handles 8 bit color images.
  static int DOES 8G The plugin filter handles 8 bit grayscale images.
  static int DOES ALL The plugin filter handles all types of images.
  static int DOES RGB The plugin filter handles RGB images.
  static int DOES STACKS The plugin filter supports stacks, ImageJ will call it for each slice in a stack.
  static int DONE If the setup method returns DONE the run method will not be called.
  static int NO CHANGES The plugin filter does not change the pixel data.
  static int NO IMAGE REQUIRED The plugin filter does not require an image to be open.
  static int NO UNDO The plugin filter does not require undo.
  static int ROI REQUIRED The plugin filter requires a region of interest (ROI).
  Static int STACK REQUIRED The plugin filter requires a stack.
  static int SUPPORTS MASKING Plugin filters always work on the bounding rectangle of the
  ROI. If this flag is set and there is a non-rectangular ROI, ImageJ will restore the pixels
  that are inside the bounding rectangle but outside the ROI.*/
  public int setup(String arg, ImagePlus imp) {
    if (arg.equals("about"))
      {showAbout(); return DONE;}
      imageP=imp;
      return DOES_ALL+NO_CHANGES+NO_UNDO;
    }
    void showAbout() {
		IJ.showMessage("About OMEUpload_...",
   			"This plug-in takes the image and uploads it into the\n" +
			"OME database."
		);
	}
  
  /*The getInput method prompts and receives user input to determine
  the OME login fields and whether the stack is in the time or space domain*/
  private void getInput(boolean b){
    IJ.showStatus("OmeUpload: Logging in...");
    GenericDialog gc= new GenericDialog("ImageJ to OME image Export");
    if ( b) {
      gc.addMessage("Information incorrect, please try again.");
    }
    // sets up the dialog box with the necessary prompts
    java.awt.Panel curt = new java.awt.Panel();
    java.awt.GridLayout grid=new java.awt.GridLayout(3,2);
    curt.setLayout(grid);
    TextField passField = new TextField(8);
    TextField servField= new TextField(8);
    TextField useField= new TextField(8);
    passField.setEchoChar('*');    
    curt.add(new java.awt.Label("Server:"), "1");
    curt.add(servField, "2");    
    curt.add(new java.awt.Label("Username:"),"3");
    curt.add(useField, "4");    
    curt.add(new java.awt.Label("Password:"),"5");
    curt.add(passField, "6");    
    gc.addPanel(curt);
    String[] domains=new String[2];
    domains[0]="Time";
    domains[1]= "Space";
    gc.addChoice("Stack Domain:", domains, "Space");
    gc.showDialog();
    cancelPlugin=gc.wasCanceled();
    //Handles the cancel of the dialog
    if (cancelPlugin) {
      return;
    }
    //Allows the server input to be more flexible
    server=servField.getText();
    if (server.startsWith("http:")) {
        server = server.substring(5);
    }
    while (server.startsWith("/")) server = server.substring(1);
    int slash = server.indexOf("/");
    if (slash >= 0) server = server.substring(0, slash);
    int colon = server.indexOf(":");
    if (colon >= 0) server = server.substring(0, colon);
    server = "http://" + server + "/shoola/";
    username=useField.getText();
    password=passField.getText();
    domainIndex=gc.getNextChoiceIndex();
  }
 


  /** Does the work for uploading data to OME. */
  public void run(ImageProcessor ip) {
    IJ.showProgress(0);

    // This code has been adapted from Doug Creager's TestImport example
    
    try {
      
      // login to OME
      boolean errorOrNot=false;
      boolean loggedIn=false;
      getInput(false);
      if (cancelPlugin) {
        return;
      }
      DataServices rs = DataServer.getDefaultServices(server);
      RemoteCaller rc = rs.getRemoteCaller();
      while (!loggedIn){
        try {
          if ( cancelPlugin) {
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
          getInput(true);
        }
      }

      // retrieve helper classes needed for importing
      IJ.showStatus("OmeUpload: Getting image information...");
      IJ.showProgress(.1);
      DataFactory df = (DataFactory) rs.getService(DataFactory.class);
      ImportManager im = (ImportManager) rs.getService(ImportManager.class);
      PixelsFactory pf = (PixelsFactory) rs.getService(PixelsFactory.class);
      DatasetManager dm = (DatasetManager) rs.getService(DatasetManager.class);
      ConfigurationManager cm = (ConfigurationManager)
      rs.getService(ConfigurationManager.class);
      AnalysisEngineManager aem = (AnalysisEngineManager)
      rs.getService(AnalysisEngineManager.class);

      // get experimenter settings for the logged in user
      FieldsSpecification fs = new FieldsSpecification();
      fs.addWantedField("id");
      fs.addWantedField("experimenter");
      fs.addWantedField("experimenter", "id");
      UserState userState = df.getUserState(fs);
      Experimenter user = userState.getExperimenter();

      // start the import process
      IJ.showStatus("OmeUpload: Starting import...");
      im.startImport();
      IJ.showProgress(0.15);

      // create a dataset to contain the images that create
      IJ.showStatus("OmeUpload: Creating dataset...");
      Dataset importDataset = (Dataset) df.createNew(Dataset.class);
      List images = new ArrayList();
      importDataset.setName(imageP.getTitle());
      importDataset.setDescription("Dataset uploaded from " +
        "ImageJ" + " " + ImageJ.VERSION);
      importDataset.setOwner(user);
      df.markForUpdate(importDataset);
      IJ.showProgress(0.17);

      // create File objects for the files we want to upload
      String ids="untitled";
      try {
         ids = imageP.getOriginalFileInfo().fileName;
      }
      catch (NullPointerException e) {}
      File files = new File(ids);
      long bytelen = files.length();
      
      // locate a repository object to contain the original files and pixels
      IJ.showStatus("OmeUpload: Finding repository...");
      Repository rep = pf.findRepository(bytelen);

      // ask the ImportManager for a MEX for the original files
      ModuleExecution of = im.getOriginalFilesMEX();
      IJ.showProgress(0.19);

      // upload each original file into the repository, using the MEX
      for (int i=0; i<files.length(); i++) {
        IJ.showStatus("OmeUpload: Uploading file " + files.getName() + "...");
        OriginalFile fileAttr = pf.uploadFile(rep, of, files);
      }
            
      // once all of the files are uploaded, mark the MEX as completed
      of.setStatus("FINISHED");
      df.markForUpdate(of);

      // create a new Image object for the multidimensional image
      IJ.showStatus("OmeUpload: Creating image entry...");
      Image image = (Image) df.createNew(Image.class);
      image.setName(imageP.getTitle());
      image.setOwner(user);
      image.setInserted("now");
      image.setCreated("now");
      image.setDescription("This image was uploaded from ImageJ");
      df.markForUpdate(image);
      images.add(image);
      IJ.showProgress(0.2);

      // extract image dimensions
      int sizeX = imageP.getWidth();
      int sizeY = imageP.getHeight();
      int type = imageP.getType();
      int bytesPerPix;
      int range=1;
      boolean isFloat=false;
        switch (type)
        {
          case ImagePlus.COLOR_256:
            bytesPerPix=1;
            break;
          case ImagePlus.COLOR_RGB:
            bytesPerPix=1;
            range=3;
            break;
          case ImagePlus.GRAY16:
            bytesPerPix=2;
            break;
          case ImagePlus.GRAY32:
            bytesPerPix=4;
            isFloat=true;
            break;
          case ImagePlus.GRAY8:
          default:
            bytesPerPix=1;
            break;
        }

      //set domain of stack
      int zIndex , tIndex;
      if ( domainIndex==0) {
        tIndex=imageP.getStackSize();
        zIndex=1;
      }
      else {
        zIndex= imageP.getStackSize();
        tIndex=1;
      }

      //set dimensions of image
      int sizeZ = zIndex;
      int sizeT = tIndex;
      int sizeC = range;
      IJ.showProgress(.25);

      // get a MEX for the image's metadata
      IJ.showStatus("OmeUpload: Creating pixels file...");
      ModuleExecution ii = im.getImageImportMEX(image);

      // create a new pixels file on the image server to contain image pixels
      Pixels pix = pf.newPixels(rep, image, ii,
        sizeX, sizeY, sizeZ, sizeC, sizeT, bytesPerPix, false, isFloat);

      // extract image pixels from each plane
      byte [] r=new byte[sizeX*sizeY];
      byte [] g=new byte[sizeX*sizeY];
      byte [] b=new byte[sizeX*sizeY];
      for (int t=0; t<sizeT; t++) {
        for (int z=0; z<sizeZ; z++) {
          for (int c=0; c<sizeC; c++) {
            byte[] pixels = new byte[sizeX * sizeY * bytesPerPix];
            IJ.showStatus("OmeUpload: Loading data (t=" + t + ", z=" + z + ", c=" + c + ")...");
            switch (type) {
              case ImagePlus.COLOR_256:
                pixels= (byte[])imageP.getStack().getPixels(Math.max(z,t)+1);
                for ( int i=0;i<pixels.length ;i++ ) {
                  pixels[i]=(byte)(255-(pixels[i]+127));
                }
                break;
                
              case ImagePlus.COLOR_RGB:
                ((ColorProcessor)imageP.getStack().getProcessor(Math.max(z,t)+1)).getRGB(r, g, b);
                switch (c) {
                  case 0:
                    pixels=r;
                    break;
                  case 1:
                    pixels=g;
                    break;
                  case 2:
                    pixels=b;
                    break;
                }
                break;
                
              case ImagePlus.GRAY16:
                short[] pixsh= (short[])imageP.getStack().getPixels(Math.max(z,t)+1);
                for ( int i=0; i<pixsh.length; i++) {
                  pixels[2*i]=(byte)((pixsh[i] & 0xff00)>>8);
                  pixels[2*i+1]=(byte)(pixsh[i] & 0x00ff);
                }
                break;
                
              case ImagePlus.GRAY32:
                float[] pixsf= (float[])imageP.getStack().getPixels(Math.max(z,t)+1);
                for ( int i=0; i<pixsf.length; i++) {
                  pixels[4*i]=(byte)((Float.floatToRawIntBits(pixsf[i]) & 0xff000000)>>24);
                  pixels[4*i+1]=(byte)((Float.floatToRawIntBits(pixsf[i]) & 0x00ff0000)>>16);
                  pixels[4*i+2]=(byte)((Float.floatToRawIntBits(pixsf[i]) & 0x0000ff00)>>8);
                  pixels[4*i+3]=(byte)(Float.floatToRawIntBits(pixsf[i]) & 0x000000ff);
                }
                break;
                
              case ImagePlus.GRAY8:
              default:
                byte[] pixsb = (byte[])imageP.getStack().getPixels(Math.max(z,t)+1);
                System.arraycopy(pixsb, 0, pixels, 0, pixsb.length);
                break;
           }
              // upload the byte buffer
              pf.setPlane(pix, z, c, t, pixels, true);
          }
        }
      }
      IJ.showProgress(.5);

      // close the pixels file on the image server
      IJ.showStatus("OmeUpload: Closing pixels file..");
      pf.finishPixels(pix);
      IJ.showProgress(.55);

      // create a default thumbnail for the image
      IJ.showStatus("OmeUpload: Creating PGI thumbnail...");
      pf.setThumbnail(pix, CompositingSettings.
        createDefaultPGISettings(sizeZ, sizeC, sizeT));
      IJ.showProgress(.6);
      
      // This next piece of metadata is necessary for all
      // images; otherwise, the standard OME viewers will not be
      // able to display the image.  The PixelChannelComponent
      // attribute represents one channel index in the pixels
      // file; there should be at least one of these for each
      // channel in the image.  The LogicalChannel attribute
      // describes a logical channel, which might comprise more
      // than one channel index in the pixels file.  (Usually it
      // doesn't.)  The mutators listed below are the minimum
      // necessary to fully represents the image's channels;
      // there are others which might be populated if the
      // metadata exists in the original file.  As with the
      // Pixels attribute, the channel attributes should use the
      // image import MEX received earlier from the
      // ImportManager.
      IJ.showStatus("OmeUpload: Assigning channel components...");
      LogicalChannel logical = (LogicalChannel)
        df.createNew("LogicalChannel");
      logical.setImage(image);
      logical.setModuleExecution(ii);
      logical.setFluor("Gray 00");
      logical.setPhotometricInterpretation("monochrome");
      df.markForUpdate(logical);
      IJ.showProgress(.7);

      PixelChannelComponent physical = (PixelChannelComponent)
        df.createNew("PixelChannelComponent");
      physical.setImage(image);
      physical.setPixels(pix);
      physical.setIndex(new Integer(0));
      physical.setLogicalChannel(logical);
      df.markForUpdate(physical);
      IJ.showProgress(.8);

      // mark image import MEX as having completed executing
      ii.setStatus("FINISHED");
      df.markForUpdate(ii);
      IJ.showProgress(.85);

      // commit all changes
      IJ.showStatus("OmeUpload: Committing changes...");
      df.updateMarked();
      IJ.showProgress(.87);

      // set default pixels entry
      image.setDefaultPixels(pix);
      df.update(image);

      // add the image to the dataset
      IJ.showStatus("OmeUpload: Adding image to dataset...");
      dm.addImagesToDataset(importDataset, images);
      images.clear();
      IJ.showProgress(.9);

      // execute the import analysis chain
      IJ.showStatus("OmeUpload: Executing import chain...");
      AnalysisChain chain = cm.getImportChain();
      aem.executeAnalysisChain(chain, importDataset);
      IJ.showProgress(.95);

      // log out
      IJ.showStatus("OmeUpload: Logging out...");
      IJ.showProgress(.99);
      rc.logout();
      IJ.showStatus("OmeUpload: Completed");
      IJ.showMessage("Upload Completed Successfully.");
    }
    catch (Exception exc) {
      IJ.setColumnHeadings("Errors");
      IJ.write("An exception has occurred:  \n" + exc.toString());
      IJ.showStatus("Error uploading (see error console for details)");
      exc.printStackTrace();
    }

    upThread = null;
    IJ.showProgress(1);
  }
}
