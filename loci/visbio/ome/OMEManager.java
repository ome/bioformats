//
// OMEManager.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2004 Curtis Rueden.

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

package loci.visbio.ome;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.openmicroscopy.ds.*;
import org.openmicroscopy.ds.dto.*;
import org.openmicroscopy.ds.managers.*;
import org.openmicroscopy.ds.st.*;
import org.openmicroscopy.is.*;
import loci.visbio.*;
import loci.visbio.data.DataManager;
import loci.visbio.help.HelpManager;
import loci.visbio.util.MathUtil;
import visad.FlatField;

/**
 * OMEManager is the manager for interaction between
 * VisBio and the Open Microscopy Environment.
 */
public class OMEManager extends LogicManager implements Runnable {

  // -- Fields --

  /** OME upload dialog. */
  private OMEUploadPane uploadPanel;

  /** Current OME upload thread. */
  private Thread upThread;

  /** Current OME upload data. */
  private loci.visbio.data.Dataset data;

  /** Current OME upload server. */
  private String server;

  /** Current OME upload username. */
  private String username;

  /** Current OME upload password. */
  private String password;


  // -- Constructor --

  /** Constructs an OME manager. */
  public OMEManager(VisBioFrame bio) { super(bio); }


  // -- OMEManager API methods --

  /** Brings up a dialog box allowing upload of the selected dataset. */
  public void upload() { uploadPanel.showDialog(bio); }

  /**
   * Uploads the given data to the specified OME server,
   * using the given username and password.
   */
  public synchronized void upload(loci.visbio.data.Dataset data,
    String server, String username, String password)
  {
    if (upThread != null) return;
    this.data = data;
    this.server = server;
    this.username = username;
    this.password = password;
    upThread = new Thread(this);
    upThread.start();
  }


  // -- LogicManager API methods --

  /** Called to notify the logic manager of a VisBio event. */
  public void doEvent(VisBioEvent evt) {
    int eventType = evt.getEventType();
    if (eventType == VisBioEvent.LOGIC_ADDED) {
      LogicManager lm = (LogicManager) evt.getSource();
      if (lm == this) doGUI();
    }
  }

  /** Gets the number of tasks required to initialize this logic manager. */
  public int getTasks() { return 2; }


  // -- Runnable API methods --

  /** Does the work for uploading data to OME. */
  public void run() {
    // This code has been adapted from Doug Creager's TestImport example
    try {
      // login to OME
      uploadPanel.setProgressValue(0);
      uploadPanel.setStatus("Logging in...");
      DataServices rs = DataServer.getDefaultServices(server);
      RemoteCaller rc = rs.getRemoteCaller();
      rc.login(username, password);

      // retrieve helper classes needed for importing
      DataFactory df = (DataFactory) rs.getService(DataFactory.class);
      ImportManager im = (ImportManager) rs.getService(ImportManager.class);
      PixelsFactory pf = (PixelsFactory) rs.getService(PixelsFactory.class);
      DatasetManager dm = (DatasetManager) rs.getService(DatasetManager.class);
      ConfigurationManager cm = (ConfigurationManager)
        rs.getService(ConfigurationManager.class);
      AnalysisEngineManager aem = (AnalysisEngineManager)
        rs.getService(AnalysisEngineManager.class);

      // get experimenter settings for the logged in user
      uploadPanel.setStatus("Retrieving user...");
      FieldsSpecification fs = new FieldsSpecification();
      fs.addWantedField("id");
      fs.addWantedField("experimenter");
      fs.addWantedField("experimenter", "id");
      UserState userState = df.getUserState(fs);
      Experimenter user = userState.getExperimenter();

      // start the import process
      uploadPanel.setStatus("Starting import...");
      im.startImport();

      // create a dataset to contain the images that create
      uploadPanel.setStatus("Creating dataset...");
      Dataset importDataset = (Dataset) df.createNew(Dataset.class);
      List images = new ArrayList();
      importDataset.setName(data.getName());
      importDataset.setDescription("Dataset uploaded from " +
        VisBio.TITLE + " " + VisBio.VERSION);
      importDataset.setOwner(user);
      df.markForUpdate(importDataset);

      // create File objects for the files we want to upload
      String[] ids = data.getFilenames();
      File[] files = new File[ids.length];
      long bytelen = 0;
      for (int i=0; i<ids.length; i++) {
        files[i] = new File(ids[i]);
        bytelen += files[i].length();
      }

      // locate a repository object to contain the original files and pixels
      uploadPanel.setStatus("Finding repository...");
      Repository rep = pf.findRepository(bytelen);

      // ask the ImportManager for a MEX for the original files
      ModuleExecution of = im.getOriginalFilesMEX();

      // upload each original file into the repository, using the MEX
      for (int i=0; i<files.length; i++) {
        uploadPanel.setStatus("Uploading file " + files[i].getName() + "...");
        OriginalFile fileAttr = pf.uploadFile(rep, of, files[i]);
      }

      // once all of the files are uploaded, mark the MEX as completed
      of.setStatus("FINISHED");
      df.markForUpdate(of);

      // create a new Image object for the multidimensional image
      uploadPanel.setStatus("Creating image entry...");
      Image image = (Image) df.createNew(Image.class);
      image.setName(data.getName());
      image.setOwner(user);
      image.setInserted("now");
      image.setCreated("now");
      image.setDescription("This image was uploaded from VisBio");
      df.markForUpdate(image);
      images.add(image);

      // extract image dimensions
      int sizeX = data.getImageWidth();
      int sizeY = data.getImageHeight();
      int[] lengths = data.getLengths();
      String[] types = data.getDimTypes();
      int range = data.getRangeCount();
      int zIndex = -1, tIndex = -1;
      for (int i=0; i<types.length; i++) {
        if (types[i].equalsIgnoreCase("time")) tIndex = i;
        else if (types[i].equalsIgnoreCase("slice")) zIndex = i;
      }
      int sizeZ = zIndex < 0 ? 1 : lengths[zIndex];
      int sizeT = tIndex < 0 ? 1 : lengths[tIndex];
      int bytesPerPix = 4;

      int sizeC = range;
      int numC = lengths.length + 1;
      if (zIndex >= 0) numC--;
      if (tIndex >= 0) numC--;
      int[] cIndex = new int[numC];
      int[] cLen = new int[numC];
      cIndex[0] = -1;
      cLen[0] = range;
      int cc = 0;
      for (int i=0; i<lengths.length; i++) {
        if (i == zIndex || i == tIndex) continue;
        cIndex[++cc] = i;
        cLen[cc] = lengths[i];
        sizeC *= cLen[cc];
      }

      // get a MEX for the image's metadata
      uploadPanel.setStatus("Creating pixels file...");
      ModuleExecution ii = im.getImageImportMEX(image);

      // create a new pixels file on the image server to contain image pixels
      Pixels pix = pf.newPixels(rep, image, ii,
        sizeX, sizeY, sizeZ, sizeC, sizeT, bytesPerPix, false, false);

      // extract image pixels from each plane
      int count = 0;
      int numImages = sizeZ * sizeC * sizeT;
      uploadPanel.setProgressMaximum(numImages);
      byte[] buf = new byte[sizeX * sizeY * bytesPerPix];
      for (int t=0; t<sizeT; t++) {
        for (int z=0; z<sizeZ; z++) {
          for (int c=0; c<sizeC; c+=range) {
            uploadPanel.setStatus("Loading data (t=" +
              t + ", z=" + z + ", c=" + c + ")...");

            // convert rasterized C value to multidimensional position
            int[] cPos = MathUtil.rasterToPosition(cLen, c);
            int[] pos = new int[lengths.length];
            if (zIndex >= 0) pos[zIndex] = z;
            if (tIndex >= 0) pos[tIndex] = t;
            for (int i=1; i<cPos.length; i++) pos[cIndex[i]] = cPos[i];

            // load appropriate image plane from disk
            FlatField ff = (FlatField) data.getData(pos, 2);
            float[][] samples = ff.getFloats(false);

            // upload an image plane for each range component
            for (int r=0; r<range; r++) {
              uploadPanel.setProgressValue(count);
              count++;
              uploadPanel.setStatus("Uploading image " +
                count + " / " + numImages + "...");

              // convert samples float array into big-endian byte buffer
              for (int q=0; q<sizeX*sizeY; q++) {
                int s = (int) samples[r][q];
                int qBase = bytesPerPix * q;
                for (int b=0; b<bytesPerPix; b++) {
                  int shift = 8 * (bytesPerPix - b - 1);
                  buf[qBase + b] = (byte) ((s << shift) % 256);
                }
              }

              // upload the byte buffer
              pf.setPlane(pix, z, c + r, t, buf, true);
            }
          }
        }
      }
      uploadPanel.setProgressValue(numImages);

      // close the pixels file on the image server
      uploadPanel.setStatus("Closing pixels file...");
      pf.finishPixels(pix);

      // create a default thumbnail for the image
      uploadPanel.setStatus("Creating PGI thumbnail...");
      pf.setThumbnail(pix, CompositingSettings.
        createDefaultPGISettings(sizeZ, sizeC, sizeT));

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

      uploadPanel.setStatus("Assigning channel components...");

      LogicalChannel logical = (LogicalChannel)
        df.createNew("LogicalChannel");
      logical.setImage(image);
      logical.setModuleExecution(ii);
      logical.setFluor("Gray 00");
      logical.setPhotometricInterpretation("monochrome");
      df.markForUpdate(logical);

      PixelChannelComponent physical = (PixelChannelComponent)
        df.createNew("PixelChannelComponent");
      physical.setImage(image);
      physical.setPixels(pix);
      physical.setIndex(new Integer(0));
      physical.setLogicalChannel(logical);
      df.markForUpdate(physical);

      // mark image import MEX as having completed executing
      ii.setStatus("FINISHED");
      df.markForUpdate(ii);

      // commit all changes
      uploadPanel.setStatus("Committing changes...");
      df.updateMarked();

      // set default pixels entry
      image.setDefaultPixels(pix);
      df.update(image);

      // add the image to the dataset
      uploadPanel.setStatus("Adding image to dataset...");
      dm.addImagesToDataset(importDataset, images);
      images.clear();

      // execute the import analysis chain
      uploadPanel.setStatus("Executing import chain...");
      AnalysisChain chain = cm.getImportChain();
      aem.executeAnalysisChain(chain, importDataset);

      // log out
      uploadPanel.setStatus("Logging out...");
      rc.logout();

      uploadPanel.setProgressValue(0);
      uploadPanel.setStatus("");
    }
    catch (Exception exc) {
      uploadPanel.setStatus("Error uploading (see error console for details)");
      exc.printStackTrace();
    }

    upThread = null;
  }


  // -- Helper methods --

  /** Adds OME-related GUI components to VisBio. */
  private void doGUI() {
    // upload panel
    bio.setSplashStatus("Initializing OME logic");
    DataManager dm = (DataManager) bio.getManager(DataManager.class);
    uploadPanel = new OMEUploadPane(this, bio.getProgressBar());

    // help window
    bio.setSplashStatus(null);
    HelpManager hm = (HelpManager) bio.getManager(HelpManager.class);
    if (hm != null) hm.addHelpTopic("Uploading to OME", "ome.html");
  }

}
