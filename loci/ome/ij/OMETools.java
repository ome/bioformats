package loci.ome.ij;

import ij.*;
import ij.process.*;
import ij.gui.GenericDialog;

import java.awt.Panel;
import java.util.Hashtable;

import org.openmicroscopy.ds.*;
import org.openmicroscopy.ds.dto.*;
import org.openmicroscopy.ds.managers.*;
import org.openmicroscopy.ds.st.*;
import org.openmicroscopy.is.*;

import loci.browser.*;

/**
 * Handles uploading and downloading images.
 *
 * @author Philip Huettl pmhuettl@wisc.edu
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public class OMETools {

  // -- Fields --

  /** Current OME thread. */
  private Thread upThread;

  /** Current OME data. */
  private ImageProcessor data;
  private ImagePlus imageP;

  /** Current OME server. */
  private String server;

  /** Current OME username. */
  private String username;

  /** Current OME password. */
  private String password;

  /** Image Stack space or time domain.*/
  private int domainIndex;

  /** Signals exit of plugin */
  private static boolean cancelPlugin;

  /** Whether or not we're uploading. */
  private boolean upload;

  /** Current OMELoginPanel. */
  private OMELoginPanel lp;

  /** Data for the current image. */
  private DataFactory df;
  private PixelsFactory pf;
  private DatasetManager dm;
  private ConfigurationManager cm;
  private AnalysisEngineManager aem;
  private ImportManager im;
  private RemoteCaller rc;
  private DataServices rs;

  private LociDataBrowser viewer;


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

    if(upload) {
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
  }

  public static void setPlugin(boolean isCancelled) {
    cancelPlugin = isCancelled;
  }

  public void pluginCancelled() {
    IJ.showProgress(1);
    IJ.showStatus("OME: Exited.");
    cancelPlugin = false;
  }

  /**
   * Login to the OME database.
   * This code has been adapted from Doug Creager's TestImport example.
   */
  public void login(boolean isUp) {
    boolean error = false;
    boolean loggedIn = false;
    try {
      upload = isUp;
      IJ.showProgress(0);
      lp = new OMELoginPanel(IJ.getInstance());
      getInput(false, lp);
      if(cancelPlugin) {
        pluginCancelled();
        return;
      }
      rs = DataServer.getDefaultServices(server);
      rc = rs.getRemoteCaller();
      while(!loggedIn) {
        if(cancelPlugin) {
          pluginCancelled();
          return;
        }
        if(error) {
          rs = DataServer.getDefaultServices(server);
          rc = rs.getRemoteCaller();
        }
        rc.login(username, password);
        loggedIn = true;
      }
    }
    catch(Exception e) {
      OMEDownPanel.error(IJ.getInstance(),
        "The login information is not valid.", "Input Error");
      login(isUp);
      if(cancelPlugin) {
        pluginCancelled();
      }
    }
  }

  // Get helper classes.
  public void getHelpers() {
    try {
      IJ.showStatus("OME: Getting image information...");
      IJ.showProgress(.1);
      if(cancelPlugin) {
        pluginCancelled();
        return;
      }
      df = (DataFactory) rs.getService(DataFactory.class);
      im = (ImportManager) rs.getService(ImportManager.class);
      pf = (PixelsFactory) rs.getService(PixelsFactory.class);
      dm = (DatasetManager) rs.getService(DatasetManager.class);
      cm = (ConfigurationManager) rs.getService(ConfigurationManager.class);
      aem = (AnalysisEngineManager) rs.getService(AnalysisEngineManager.class);
    }
    catch(NullPointerException e) {
      cancelPlugin = true;
      getHelpers();
    }
  }

  // Set the stack domain (4th dimension)
  public int[] setDomain(int[] dims) {
    if(dims[0] == 0) {
      return new int[] {dims[1], 1};
    }
    else {
      return new int[] {1, dims[1]};
    }
  }

  // Logout of OME database
  public void logout() {
    IJ.showStatus("OME: Logging out...");
    IJ.showProgress(.99);
    rc.logout();
    IJ.showStatus("OME: Completed");
    OMELoginPanel.infoShow(IJ.getInstance(), "OME Transaction Completed",
      "OME");
  }

  public void finalCatch(Exception exc) {
    IJ.setColumnHeadings("Errors");
    IJ.write("An exception has occurred:  \n" + exc.toString());
    IJ.showStatus("Error uploading (see error console for details)");
    exc.printStackTrace();
  }

  // -- Upload methods --

  /** Does the work for uploading data to OME. */
  public void run(ImagePlus ip, Object[] metadata) {
    try {
      login(true);
      imageP = ip;
      getHelpers();

      Experimenter user = OMERetrieve.getUser(df);

      // start the import process
      IJ.showStatus("OmeUpload: Starting import...");
      im.startImport(user);
      IJ.showProgress(0.15);

      //getting the image to add the pixels to from OME
      Image omeImage = null;
      int omeID = ((Integer)metadata[0]).intValue();
      if (omeID != 0) {
        omeImage = OMERetrieve.getImagefromID(df, omeID);
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
      }
      else {
        image = (Image) df.createNew(Image.class);
        image.setName(imageP.getTitle());
        image.setOwner(user);
        image.setInserted("now");
        image.setCreated("now");
        image.setDescription("This image was uploaded from ImageJ");
      }
      df.markForUpdate(image);

      // extract image dimensions
      int sizeX = imageP.getWidth();
      int sizeY = imageP.getHeight();
      int type = imageP.getType();
      int bytesPerPix = 1;
      int sizeC = 1;
      boolean isFloat = false;
      switch (type) {
        case ImagePlus.COLOR_256:
          //in order to correctly upload you need to convert to an RGB image
          imageP = new ImagePlus(ip.getTitle(), new ColorProcessor(
            ((ByteProcessor) imageP.getProcessor()).createImage()));
          break;
        case ImagePlus.COLOR_RGB:
          sizeC = 3;
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
      int[] results = new int[] {domainIndex, imageP.getStackSize()};
      results = setDomain(results);
      int sizeT = results[1] + 1;
      int sizeZ = results[0] + 1;

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
            if (sizeC == 3) {
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
      CompositingSettings cs =
        CompositingSettings.createDefaultPGISettings(sizeZ, sizeC, sizeT);
      if (sizeC == 3) {
        cs.activateRedChannel(2,0,255,1);
        cs.activateGreenChannel(1,0,255,1);
        cs.activateBlueChannel(0,0,255,1);
      }
      else cs.activateGrayChannel(0,0,255,1);
      pf.setThumbnail(pix, cs);
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
      // The white and black level are set as constants because imageJ gives
      // the pixels truncated if you changed these values, this prevents the
      // min and max being set twice.
      Double whiteLevel = new Double(255);//proc.getMax());
      Double blackLevel = new Double(0);//proc.getMin());

      if(sizeC == 3) {
        disOp.setColorMap("RGB");
        disOp.setDisplayRGB(new Boolean(true));
      }
      else {
        disOp.setColorMap("monochrome");
        disOp.setDisplayRGB(new Boolean(false));
      }

      DisplayChannel ch;
      for(int i=0; i<sizeC; i++) {
        ch = (DisplayChannel) df.createNew("DisplayChannel");
        if(sizeC == 1) ch.setModuleExecution(ii);  // remove??
        ch.setImage(image);
        ch.setGamma(new Float(1));
        ch.setWhiteLevel(whiteLevel);
        ch.setBlackLevel(blackLevel);
        ch.setChannelNumber(new Integer(i));

        switch(i) {
          case 0:
            disOp.setGreyChannel(ch);
            disOp.setRedChannel(ch);
            disOp.setGreenChannel(ch);
            disOp.setBlueChannel(ch);
            disOp.setRedChannelOn(new Boolean(false));
            disOp.setGreenChannelOn(new Boolean(false));
            disOp.setBlueChannelOn(new Boolean(false));
            break;
          case 1:
            disOp.setGreenChannel(ch);
            break;
          case 2:
            disOp.setRedChannel(ch);
            disOp.setGreyChannel(ch);
            disOp.setRedChannelOn(new Boolean(true));
            disOp.setGreenChannelOn(new Boolean(true));
            disOp.setBlueChannelOn(new Boolean(true));
            break;
        }
        df.update(ch);
      }
      df.update(disOp);

      // execute the import analysis chain
      IJ.showStatus("OmeUpload: Executing import chain...");
      AnalysisChain chain = cm.getImportChain();
      IJ.showProgress(.95);

      logout();
    }
    catch(NullPointerException e) {
      // do nothing; this means that the user cancelled the login procedure
    }
    catch(IllegalArgumentException e) {
      // do nothing; this means that the user cancelled the login procedure
    }
    catch (Exception exc) {
      finalCatch(exc);
    }

    upThread = null;
    IJ.showProgress(1);
  }

  // -- Download methods --

  /** Method that puts an image from OME back into ImageJ */
  private void download(Image image, PixelsFactory pf, OMESidePanel omesp,
      DataFactory df) {

    if (cancelPlugin) {
      pluginCancelled();
      return;
    }

    //get pixel data

    Pixels pix = image.getDefaultPixels();
    int sizeX, sizeY, sizeZ, sizeC, sizeT;
    sizeX = pix.getSizeX().intValue();
    sizeY = pix.getSizeY().intValue();
    sizeZ = pix.getSizeZ().intValue();
    sizeC = pix.getSizeC().intValue();
    sizeT = pix.getSizeT().intValue();
    String typeS = pix.getPixelType();
    Hashtable table = new Hashtable(4);
    table.put("Uint16", new Integer(ImagePlus.GRAY16));
    table.put("uint16", new Integer(ImagePlus.GRAY16));
    table.put("Uint8", new Integer(ImagePlus.GRAY8));
    table.put("uint8", new Integer(ImagePlus.GRAY8));
    table.put("float", new Integer(ImagePlus.GRAY32));
    table.put("color256", new Integer(ImagePlus.COLOR_256));
    table.put("colorRGB", new Integer(ImagePlus.COLOR_RGB));
    table.put("Uint32", new Integer(17));
    table.put("uint32", new Integer(17));
    table.put("int16", new Integer(ImagePlus.GRAY16));
    table.put("int8", new Integer(ImagePlus.GRAY8));
    int type = ((Integer)table.get(typeS)).intValue();
    int redChanNum = 2;
    int greenChanNum = 1;
    int blueChanNum = 0;
    if (sizeC == 3) {
      type = ImagePlus.COLOR_RGB;
      IJ.showStatus("Finding color channel numbers.");
      //code that figures out the display options and
      //what color is what channel number
      String [] attrs = {"BlueChannel", "ColorMap","DisplayROIList",
        "GreenChannel", "GreyChannel", "Pixels", "RedChannel", "TStart",
        "TStop", "Zoom", "ZStart", "ZStop", "BlueChannelOn", "DisplayRGB",
        "GreenChannelOn", "RedChannelOn"};
      Criteria criteria = OMERetrieve.makeAttributeFields(attrs);
      criteria.addWantedField("image_id");
      criteria.addFilter("image_id", (new Integer(image.getID())).toString());
      //retrieve element to add with all columns loaded
      DisplayOptions element = (DisplayOptions)
        df.retrieve("DisplayOptions", criteria);
      DisplayChannel redChannel = element.getRedChannel();
      DisplayChannel greenChannel = element.getGreenChannel();
      DisplayChannel blueChannel = element.getBlueChannel();

      String [] attrsC = {"ChannelNumber"};
      DisplayChannel[] channels = {redChannel, greenChannel, blueChannel};
      int[] channelNum = {redChanNum, greenChanNum, blueChanNum};
      for(int i=0; i<sizeC; i++) {
        criteria = OMERetrieve.makeAttributeFields(attrsC);
        criteria.addWantedField("id");
        criteria.addFilter("id", new Integer(channels[i].getID()).toString());
        channels[i] = (DisplayChannel) df.retrieve("DisplayChannel", criteria);
        channelNum[i] = channels[i].getChannelNumber().intValue();
      }
      redChanNum = channelNum[0];
      greenChanNum = channelNum[1];
      blueChanNum = channelNum[2];
    }

    int z1 = 0;
    int t1 = 0;

    //Create Stack to add planes to in ImageJ

    ImageStack is = new ImageStack(sizeX, sizeY);
    try {
      for (int t=t1; t<sizeT; t++) {
        for (int z=z1; z<sizeZ; z++) {
          byte[] pixelb = new byte[sizeX * sizeY];
          int[] pixeli = new int[sizeX*sizeY];
          short[] pixels = new short[sizeX*sizeY];
          double[] pixeld = new double[sizeX*sizeY];
          IJ.showStatus("OmeDownload: Loading data (t=" + t + ", z=" + z +
            ")...");
          if (type == 17 || type == ImagePlus.GRAY32) {
            byte[] pixs = pf.getPlane(pix, z, 0, t, true);
            for (int i=0; i<pixeli.length; i++) {
              pixeli[i] = ((pixs[4*i] & 0xff)<<24) +
                ((pixs[4*i+1] & 0xff)<<16) +
                ((pixs[4*i+2] & 0xff)<<8) +
                (pixs[4*i+3] & 0xff);
              pixeld[i] = (new Integer(pixeli[i])).doubleValue();
            }
            FloatProcessor ip = new FloatProcessor(sizeX, sizeY, pixeld);
            // adding plane to the ImageStack
            is.addSlice("Z=" + z + " T=" + t, ip);
          }
          else if (type == ImagePlus.COLOR_256) {
            pixelb = pf.getPlane(pix, z, 0, t, true);
            for (int i=0; i<pixelb.length; i++) {
              pixelb[i] = (byte)(128-pixelb[i]);
            }
            ByteProcessor ip = new ByteProcessor(sizeX, sizeY);
            ip.setPixels(pixelb);
            // adding plane to the ImageStack
            is.addSlice("Z=" + z + " T=" + t, ip);
          }
          else if (type == ImagePlus.COLOR_RGB) {
            byte[] r = pf.getPlane(pix, z, redChanNum, t, true);
            byte[] g = pf.getPlane(pix, z, greenChanNum, t, true);
            byte[] b = pf.getPlane(pix, z, blueChanNum, t, true);
            for (int i=0; i<pixeli.length; i++) {
              pixeli[i] = ((r[i] & 0xff)<<16)+
                ((g[i] & 0xff)<<8)+ (b[i] & 0xff);
            }
            ColorProcessor ip = new ColorProcessor(sizeX, sizeY, pixeli);
            // adding plane to the ImageStack
            is.addSlice("Z=" + z + " T=" + t, ip);
          }
          else if (type == ImagePlus.GRAY16) {
            byte[] pixs = pf.getPlane(pix, z,0,t, true);
            for (int i=0; i<pixels.length; i++) {
              pixels[i] = (short)(((pixs[2*i] & 0xff)<<8)+
                (pixs[2*i+1] & 0xff));
            }
            ShortProcessor ip = new ShortProcessor(sizeX, sizeY);
            ip.setPixels(pixels);
            // adding plane to the ImageStack
            is.addSlice("Z=" + z + " T=" + t, ip);
          }
          else if (type == ImagePlus.GRAY8) {
            pixelb = pf.getPlane(pix, z, 0, t, true);
            ByteProcessor ip = new ByteProcessor(sizeX, sizeY);
            ip.setPixels(pixelb);
            // adding plane to the ImageStack
            is.addSlice("Z=" + z + " T=" + t, ip);
          }
        }
      }
    }
    catch(ImageServerException e) {
      OMEDownPanel.error(IJ.getInstance(),
        "There was an error downloading the image.\n" + e.toString(),
        "Download Error");
      cancelPlugin = true;
      pluginCancelled();
      e.printStackTrace();
      return;
    }

    //Create the ImagePlus in ImageJ and display it
    imageP = new ImagePlus(image.getName(), is);

    //retrieve metadata
    Object[] metas = new Object[2];
    metas[0] = new Integer(image.getID());
    if (OMESidePanel.yesNo(IJ.getInstance(),
      "Would you like to download\nmetadata associated with the\n" +
      "image " + image.getName() + " along with\n" +
      "the pixels?  (It takes a bit longer.)")) {
      IJ.showStatus("Retrieving metadata...");
      metas[1] = OMEMetaDataHandler.exportMeta(image, imageP, df);
    }
    IJ.showStatus("Displaying Image");
    viewer = new LociDataBrowser();
    boolean tp = (sizeT > 1);

    // HACK
    // we want to give LociDataBrowser information about
    // the dimensions of the stack, without messing with the API
    ij.io.FileInfo fi = new ij.io.FileInfo();
    fi.info = tp + " " + sizeZ;
    imageP.setFileInfo(fi);

    viewer.twoDimView(imageP);
    OMESidePanel.hashInImage(-1*image.getID(), metas);
  }

  /** returns a list of images that the user chooses */
  private Image[] getDownPicks(Image[] ima, DataFactory df, PixelsFactory pf) {
    //table array
    Object[][] props = new Object[ima.length][4];
    //details array
    Object[][] details = new Object[ima.length][10];
    //build a hashtable of experimenters to display names
    String[][] expers = OMERetrieve.retrieveExperimenters(df);
    Hashtable hm = new Hashtable(expers.length);
    for (int i=0; i<expers[0].length; i++) {
      hm.put(new Integer(expers[2][i]), expers[1][i] + ", " + expers[0][i]);
    }
    //assemble the table array
    Pixels p;
    for (int i=0 ; i<props.length; i++) {
      props[i][1] = ima[i].getName();
      props[i][2] = String.valueOf(ima[i].getID());
      details[i][1] = (String)hm.get(new Integer(ima[i].getOwner().getID()));
      props[i][3] = ima[i].getCreated();
      props[i][0] = new Boolean(false);
      details[i][8] = ima[i].getDescription();
      p = ima[i].getDefaultPixels();

      try {
        details[i][0] = pf.getThumbnail(p);
      }
      catch (NoClassDefFoundError e) {
        details[i][0] = null;
      }
      catch (Throwable t) {
        OMEDownPanel.error(IJ.getInstance(), "An exception occured.\n" +
          t.toString(), "Error");
        IJ.showStatus("Error Downloading thumbnails.");
        t.printStackTrace();
        details[i][0] = null;
      }

      details[i][2] = p.getPixelType();
      details[i][3] = p.getSizeC().toString();
      details[i][4] = p.getSizeT().toString();
      details[i][5] = p.getSizeX().toString();
      details[i][6] = p.getSizeY().toString();
      details[i][7] = p.getSizeZ().toString();
      details[i][9] = String.valueOf(ima[i].getID());
    }
    String[] columns = {"","Name","ID","Date Created"};

    //create the table
    OMETablePanel tp = new OMETablePanel(IJ.getInstance(), props, columns,
      details);
    int[] results = tp.getInput();
    if (results == null) {
      cancelPlugin = true;
      pluginCancelled();
      return null;
    }
    Image[] returns = new Image[results.length];
    for (int i=0; i<results.length; i++) {
      for (int j=0; j<props.length; j++) {
        if (results[i] == Integer.parseInt((String)props[j][2])) {
          returns[i] = ima[j];
        }
      }
    }

    return returns;
  }

  /** Does the work for downloading data from OME. */
  public void run(OMESidePanel osp) {
    try {
      login(false);
      getHelpers();

      //get database info to use in search
      IJ.showStatus("Getting database info..");
      String[][] owners = OMERetrieve.retrieveExperimenters(df);
      Project[] projects = OMERetrieve.retrieveAllProjects(df);
      //create search panel
      IJ.showStatus("Creating search panel...");
      OMEDownPanel dp = new OMEDownPanel(IJ.getInstance(), projects, owners);
      Image[] images = new Image[0];
      //do the image search
      IJ.showStatus("Searching for images...");
      while (images.length == 0) {
        Object[] objects = dp.search();
        if (objects == null) {
          cancelPlugin = true;
          pluginCancelled();
          return;
        }
        //get search results
        images = OMERetrieve.retrieveImages(df, objects);
        if(images == null) images = new Image[0];
        if (images.length == 0) {
          OMELoginPanel.infoShow(IJ.getInstance(),
            "No images matched the specified criteria.", "OME Download");
        }
        else {
          //pick from results
          images = getDownPicks(images,df, pf);
          if (cancelPlugin) {
            pluginCancelled();
            return;
          }
        }
      }
      //download into ImageJ
      for (int i=0; i<images.length; i++) {
        download(images[i], pf, osp, df);
        if (cancelPlugin) {
          pluginCancelled();
          return;
        }
      }
      logout();
    }
    catch(NullPointerException e) {
      e.printStackTrace();
      pluginCancelled();
    }
    catch(IllegalArgumentException f) {
      // do nothing; this means that the user cancelled the login procedure
    }
    catch (Exception exc) {
      finalCatch(exc);
    }

    upThread = null;
    IJ.showProgress(1);
  }
}
