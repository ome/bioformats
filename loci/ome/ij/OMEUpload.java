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

import loci.ome.xml.*;


/**
 * OMEUpload handles importing images into
 * the Open Microscopy Environment.
 * @author Philip Huettl pmhuettl@wisc.edu
 */
public class OMEUpload{
  
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
    TextField passField = new TextField("",8);
    TextField servField= new TextField("",8);
    TextField useField= new TextField("",8);
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
  public void run(ImagePlus ip, Object[] metadata) {
    imageP=ip;
//    System.out.println(ip.getOriginalFileInfo().fileName+
//    " is white zero: "+ip.getOriginalFileInfo().whiteIsZero);
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
      
      
      //getting the image to add the pixels to from OME
      Image omeImage;
      int omeID=((Integer)metadata[0]).intValue();
      if ( omeID!=0) {
        omeImage=OMEDownload.getImagefromID(df, omeID);
        OMEMetaDataHandler.importMeta((OMEElement)metadata[1],
          true, omeImage, df);
      } else {
        omeImage=null;
        
      }


      // start the import process
      IJ.showStatus("OmeUpload: Starting import...");
      im.startImport();
      IJ.showProgress(0.15);

      // create a dataset to contain the images that create
      //This code was taken out to reduce redundancy where
      //you have datasets with only one image
      /*
      IJ.showStatus("OmeUpload: Creating dataset...");
      Dataset importDataset = (Dataset) df.createNew(Dataset.class);
      List images = new ArrayList();
      importDataset.setName(imageP.getTitle());
      importDataset.setDescription("Dataset uploaded from " +
        "ImageJ" + " " + ImageJ.VERSION);
      importDataset.setOwner(user);
      df.markForUpdate(importDataset);
      IJ.showProgress(0.17);
      */

      //create a features to put metadata into
/*
      Feature quan=null;
      if (metadata!=null) {
        IJ.showStatus("OmeUpload: Creating image feature...");
        quan = (Feature) df.createNew(Feature.class);
        quan.setTag(((String)metadata[2]));
        quan.setName("Quantity");
      }
      Feature qual=null;
      if (metadata!=null) {
        IJ.showStatus("OmeUpload: Creating image feature...");
        qual = (Feature) df.createNew(Feature.class);
        qual.setTag(((String)metadata[3]));
        qual.setName("Quality");
      }
*/      

      IJ.showProgress(0.17);
      
      
      // create File objects for the files we want to upload
//      String ids="untitled";
//      try {
//         ids = imageP.getOriginalFileInfo().fileName;
//      }
//      catch (NullPointerException e) {}
//      File files = new File(ids);
//      long bytelen = files.length();
      
      // locate a repository object to contain the original files and pixels
//      IJ.showStatus("OmeUpload: Finding repository...");
      Repository rep = pf.findRepository(0);

      // ask the ImportManager for a MEX for the original files
//      ModuleExecution of = im.getOriginalFilesMEX();
//      IJ.showProgress(0.19);

      // upload each original file into the repository, using the MEX
//      IJ.showStatus("OmeUpload: Uploading file " + files.getName() + "...");
//      OriginalFile fileAttr = pf.uploadFile(rep, of, files);
            
      // once all of the files are uploaded, mark the MEX as completed
//      of.setStatus("FINISHED");
//      df.markForUpdate(of);
//      System.out.println("module execution marked for update");

      // create a new Image object for the multidimensional image
      IJ.showStatus("OmeUpload: Creating image entry...");
      Image image;
      if ( omeImage!=null) {
        image=omeImage;
        df.markForUpdate(image);
      }else{
        image = (Image) df.createNew(Image.class);
//        Criteria criteria=new Criteria();
//        OMEDownload.addImageFields(criteria);
//        image=(Image)df.load(Image.class, image.getID(), criteria);
        image.setName(imageP.getTitle());
        image.setOwner(user);
        image.setInserted("now");
        image.setCreated("now");
        image.setDescription("This image was uploaded from ImageJ");
//        OMEMetaDataHandler.importMeta((OMEElement)metadata[1], false, image, df);
        df.markForUpdate(image);
        //taken out as part of dataset redundancy
        //images.add(image);
      }
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
        sizeX, sizeY, sizeZ, sizeC, sizeT, bytesPerPix, isFloat, isFloat);

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
                  case 2:
                    pixels=r;
                    break;
                  case 1:
                    pixels=g;
                    break;
                  case 0:
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
              LogicalChannel logical = (LogicalChannel)
              df.createNew("LogicalChannel");
              logical.setImage(image);
              logical.setModuleExecution(ii);
              if (range==3) {
                if ( c==2) {
                  logical.setFluor("Red");
                  logical.setPhotometricInterpretation("red");
                }else if ( c==1) {
                  logical.setFluor("Green");
                  logical.setPhotometricInterpretation("green");
                }else if ( c==0) {
                  logical.setFluor("Blue");
                  logical.setPhotometricInterpretation("blue");
                }
              }else{
                logical.setFluor("Gray 00");
                logical.setPhotometricInterpretation("monochrome");
              }
              df.markForUpdate(logical);
              
              PixelChannelComponent physical = (PixelChannelComponent)
              df.createNew("PixelChannelComponent");
              physical.setImage(image);
              physical.setPixels(pix);
              physical.setIndex(new Integer(c));
              physical.setLogicalChannel(logical);
              df.markForUpdate(physical);
          }
        }
      }
      IJ.showProgress(.5);

      // close the pixels file on the image server
      IJ.showStatus("OmeUpload: Closing pixels file..");
      pf.finishPixels(pix);
      System.out.println("finished pixels");
      IJ.showProgress(.55);

      // create a default thumbnail for the image
      IJ.showStatus("OmeUpload: Creating PGI thumbnail...");
      pf.setThumbnail(pix, 
        CompositingSettings.createDefaultPGISettings(sizeZ, sizeC, sizeT));
      System.out.println("set thumbnail");
      IJ.showProgress(.6);
      
      // mark image import MEX as having completed executing
      ii.setStatus("FINISHED");
      df.markForUpdate(ii);
      System.out.println("mex marked for update");
      IJ.showProgress(.85);

      // commit all changes
      IJ.showStatus("OmeUpload: Committing changes...");
      df.updateMarked();
      System.out.println("datafactory update marked");
      IJ.showProgress(.87);

      // set default pixels entry
      image.setDefaultPixels(pix);
      System.out.println("before datafactory update");
      df.update(image);
      System.out.println("datafactory updated");

      // add the image to the dataset
      //part of dataset redundancy
      /*
      IJ.showStatus("OmeUpload: Adding image to dataset...");
      dm.addImagesToDataset(importDataset, images);
      images.clear();
      */
/*      
      //set the features image
      if ( quan!=null || qual!=null) {
        quan.setImage(image);
        qual.setImage(image);
        System.out.println("feature image set");
        df.update(quan);
        df.update(qual);
      }
*/      
      //set the imageAnnotation image
/*
      if ( ia!=null) {
        ia.setImage(image);
        System.out.println("Image set");
        df.update(ia);
      }
*/
          
      IJ.showProgress(.9);

      // execute the import analysis chain
      IJ.showStatus("OmeUpload: Executing import chain...");
      AnalysisChain chain = cm.getImportChain();
      //was taken out as part of dataset redundancy
      //aem.executeAnalysisChain(chain, importDataset);
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