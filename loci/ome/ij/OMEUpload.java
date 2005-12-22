import ij.*;
import ij.process.*;
import ij.gui.GenericDialog;

import java.awt.Panel;

import org.openmicroscopy.ds.*;
import org.openmicroscopy.ds.dto.*;
import org.openmicroscopy.ds.managers.*;
import org.openmicroscopy.ds.st.*;
import org.openmicroscopy.is.*;

/**
 * OMEUpload handles importing images into
 * the Open Microscopy Environment.
 * @author Philip Huettl pmhuettl@wisc.edu
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public class OMEUpload {
  
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
   
  /**
   * The getInput method prompts and receives user input to determine
   * the OME login fields and whether the stack is in the time or space domain
   */
  private void getInput(boolean b, OMELoginPanel d) {
    String[] in = d.getInput(b);
    if (in == null) {
      cancelPlugin = true;
      return;
    }
    server = in[0];
    username = in[1];
    password = in[2];
   
    // choose the 4th dimension
    GenericDialog gc = new GenericDialog("OME image export");
    Panel p = new Panel();
    String[] domains = {"Time", "Space"};
    gc.addChoice("Stack Domain:", domains, "Time");
    gc.showDialog();
    cancelPlugin = gc.wasCanceled();
    if(cancelPlugin) return;
    domainIndex = gc.getNextChoiceIndex();
  }
 
  /** Does the work for uploading data to OME. */
  public void run(ImagePlus ip, Object[] metadata) {
    imageP = ip;
    IJ.showProgress(0);
    OMELoginPanel lp = new OMELoginPanel(IJ.getInstance());
   	   
    // This code has been adapted from Doug Creager's TestImport example
    
    try {
      // login to OME
      boolean errorOrNot = false;
      boolean loggedIn = false;
      getInput(false, lp);
      if (cancelPlugin) { return; }
      DataServices rs = DataServer.getDefaultServices(server);
      RemoteCaller rc = rs.getRemoteCaller();
      while (!loggedIn) {
        try {
          if (cancelPlugin) { return; }
          if (errorOrNot) {
            rs = DataServer.getDefaultServices(server);
            rc = rs.getRemoteCaller();
          }
          rc.login(username, password);
          loggedIn = true;
        }
        catch (Exception e) {
          errorOrNot = true;
          getInput(true, lp);
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
      im.startImport(user);
      IJ.showProgress(0.15);
          
      //getting the image to add the pixels to from OME
      Image omeImage;
      int omeID = ((Integer)metadata[0]).intValue();
      if (omeID != 0) {
        omeImage = OMEDownload.getImagefromID(df, omeID);
        //This is where metadata could be imported into the database
      } 
      else {
        omeImage = null;
      }

      // locate a repository object to contain the pixels
      IJ.showStatus("OmeUpload: Finding repository...");
      Repository rep = pf.findRepository(0);
      IJ.showProgress(0.18);

      // create a new Image object for the multidimensional image
      IJ.showStatus("OmeUpload: Creating image entry...");
      Image image;
      if (omeImage != null) {
        image = omeImage;
        df.markForUpdate(image);
      }
      else {
        image = (Image) df.createNew(Image.class);
        image.setName(imageP.getTitle());
	image.setOwner(user);
	image.setInserted("now");
        image.setCreated("now");
        image.setDescription("This image was uploaded from ImageJ");
        df.markForUpdate(image);
      }

      // extract image dimensions
      int sizeX = imageP.getWidth();
      int sizeY = imageP.getHeight();
      int type = imageP.getType();
      int bytesPerPix = 1;
      int range = 1;
      boolean isFloat = false;
      switch (type) {
        case ImagePlus.COLOR_256:
          //in order to correctly upload you need to convert to an RGB image
          imageP = new ImagePlus(ip.getTitle(), new ColorProcessor(
            ((ByteProcessor) imageP.getProcessor()).createImage()));
          break;
        case ImagePlus.COLOR_RGB:
          bytesPerPix = 1;
          range = 3;
          break;
        case ImagePlus.GRAY16:
          bytesPerPix = 2;
          break;
        case ImagePlus.GRAY32:
          bytesPerPix = 4;
          isFloat = true;
          break;
      }

      //set domain of stack
      int zIndex, tIndex;
      if (domainIndex == 0) {
        tIndex = imageP.getStackSize();
        zIndex = 1;
      }
      else {
        zIndex = imageP.getStackSize();
        tIndex = 1;
      }

      //set dimensions of image
      int sizeZ = zIndex;
      int sizeT = tIndex;
      int sizeC = range;
      IJ.showProgress(.25);

      // get a MEX for the image's metadata
      IJ.showStatus("OmeUpload: Creating pixels file...");
      ModuleExecution ii = im.getImageImportMEX(image);
      ii.setExperimenter(user);
      
      // create a new pixels file on the image server to contain image pixels
      Pixels pix = pf.newPixels(rep, image, ii,
        sizeX, sizeY, sizeZ, sizeC, sizeT, bytesPerPix, isFloat, isFloat);

      // extract image pixels from each plane
      byte [] r = new byte[sizeX*sizeY];
      byte [] g = new byte[sizeX*sizeY];
      byte [] b = new byte[sizeX*sizeY];
      for (int t=0; t<sizeT; t++) {
        for (int z=0; z<sizeZ; z++) {
          for (int c=0; c<sizeC; c++) {
            byte[] pixels = new byte[sizeX * sizeY * bytesPerPix];
            IJ.showStatus("OmeUpload: Loading data (t=" + t + ", z=" + z + 
	      ", c=" + c + ")...");
            IJ.showProgress(.25+t/sizeT+z/sizeZ+c/sizeC);
            switch (type) {
              case ImagePlus.COLOR_RGB:
                ((ColorProcessor)imageP.getStack().getProcessor(
		  Math.max(z,t)+1)).getRGB(r, g, b);
                switch (c) {
                  case 2:
                    pixels = r;
                    break;
                  case 1:
                    pixels = g;
                    break;
                  case 0:
                    pixels = b;
                    break;
                }
                break;
                
              case ImagePlus.GRAY16:
                short[] pixsh = 
		  (short[]) imageP.getStack().getPixels(Math.max(z,t)+1);
                for (int i=0; i<pixsh.length; i++) {
                  pixels[2*i] = (byte) ((pixsh[i] & 0xff00) >> 8);
                  pixels[2*i+1] = (byte) (pixsh[i] & 0x00ff);
                }
                break;
                
              case ImagePlus.GRAY32:
                float[] pixsf = 
		  (float[]) imageP.getStack().getPixels(Math.max(z,t)+1);
                for (int i=0; i<pixsf.length; i++) {
                  pixels[4*i] = (byte) 
		    ((Float.floatToRawIntBits(pixsf[i]) & 0xff000000)>>24);
                  pixels[4*i+1] = (byte) 
		    ((Float.floatToRawIntBits(pixsf[i]) & 0x00ff0000)>>16);
                  pixels[4*i+2] = (byte) 
		    ((Float.floatToRawIntBits(pixsf[i]) & 0x0000ff00)>>8);
                  pixels[4*i+3] = (byte) 
		    (Float.floatToRawIntBits(pixsf[i]) & 0x000000ff);
                }
                break;
                
              default:
                byte[] pixsb = (byte[]) 
		  imageP.getStack().getPixels(Math.max(z,t)+1);
                System.arraycopy(pixsb, 0, pixels, 0, pixsb.length);
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
            if (range == 3) {
              switch(c) {
	        case 0: logical.setFluor("Blue"); break;
		case 1: logical.setFluor("Green"); break;
		case 2: logical.setFluor("Red"); break;
	      }		
              logical.setPhotometricInterpretation("RGB");
            }
	    else {
              logical.setFluor("Gray");
              logical.setPhotometricInterpretation("monochrome");
            }
            df.markForUpdate(logical);
              
            PixelChannelComponent physical = (PixelChannelComponent)
              df.createNew("PixelChannelComponent");
            physical.setImage(image);
            physical.setPixels(pix);
            physical.setIndex(new Integer(c));
            physical.setLogicalChannel(logical);
	    physical.setModuleExecution(ii);
            df.markForUpdate(physical);
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
      CompositingSettings compositingSettings =
        CompositingSettings.createDefaultPGISettings(sizeZ, sizeC, sizeT);
      if (sizeC == 3) {
        compositingSettings.activateRedChannel(2,0,255,1);
        compositingSettings.activateGreenChannel(1,0,255,1);
        compositingSettings.activateBlueChannel(0,0,255,1);
      }
      else compositingSettings.activateGrayChannel(0,0,255,1);
      pf.setThumbnail(pix, compositingSettings);
      IJ.showProgress(.6);
      
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
      IJ.showProgress(.9);
      
      // extract image display options and create display options
      DisplayOptions disOp = (DisplayOptions) df.createNew("DisplayOptions");
      disOp.setPixels(pix);
      disOp.setImage(image);
      disOp.setModuleExecution(ii);
      disOp.setTStart(new Integer(0));
      disOp.setTStop(new Integer(0));
      disOp.setZStart(new Integer(0));
      disOp.setZStop(new Integer(0));
      
      ImageProcessor proc = imageP.getProcessor();
      // The white and black level are set as constants because imageJ gives the
      // pixels truncated if you changed these values, this prevents the min and
      // max being set twice.
      Double whiteLevel = new Double(255);//proc.getMax());
      Double blackLevel = new Double(0);//proc.getMin());
      if (sizeC == 3) {
        disOp.setColorMap("RGB");
        disOp.setDisplayRGB(new Boolean(true));
        DisplayChannel red = (DisplayChannelDTO)df.createNew("DisplayChannel");
        red.setImage(image);
        red.setGamma(new Float(1));
        red.setWhiteLevel(whiteLevel);
        red.setBlackLevel(blackLevel);
        red.setChannelNumber(new Integer(2));
        DisplayChannel green = (DisplayChannel)df.createNew("DisplayChannel");
        green.setImage(image);
        green.setGamma(new Float(1));
        green.setWhiteLevel(whiteLevel);
        green.setBlackLevel(blackLevel);
        green.setChannelNumber(new Integer(1));
        DisplayChannel blue = (DisplayChannel)df.createNew("DisplayChannel");
        blue.setImage(image);
        blue.setGamma(new Float(1));
        blue.setWhiteLevel(whiteLevel);
        blue.setBlackLevel(blackLevel);
        blue.setChannelNumber(new Integer(0));
        
        disOp.setRedChannel(red);
        disOp.setGreenChannel(green);
        disOp.setBlueChannel(blue);
        disOp.setGreyChannel(red);
        disOp.setRedChannelOn(new Boolean(true));
        disOp.setGreenChannelOn(new Boolean(true));
        disOp.setBlueChannelOn(new Boolean(true));
        df.update(red);
        df.update(green);
        df.update(blue);
      }
      else {
        disOp.setColorMap("monochrome");
        disOp.setDisplayRGB(new Boolean(false));
        DisplayChannel gray = (DisplayChannel)df.createNew("DisplayChannel");
        gray.setModuleExecution(ii);
        gray.setImage(image);
        gray.setGamma(new Float(1));
        gray.setWhiteLevel(whiteLevel);
        gray.setBlackLevel(blackLevel);
        gray.setChannelNumber(new Integer(0));
        disOp.setGreyChannel(gray);
        disOp.setRedChannel(gray);
        disOp.setGreenChannel(gray);
        disOp.setBlueChannel(gray);
        disOp.setRedChannelOn(new Boolean(false));
        disOp.setGreenChannelOn(new Boolean(false));
        disOp.setBlueChannelOn(new Boolean(false));
        df.update(gray);
      }
      df.update(disOp);

      // execute the import analysis chain
      IJ.showStatus("OmeUpload: Executing import chain...");
      AnalysisChain chain = cm.getImportChain();
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
