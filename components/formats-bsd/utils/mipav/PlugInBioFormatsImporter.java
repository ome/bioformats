/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import gov.nih.mipav.model.file.FileInfoBase;
import gov.nih.mipav.model.file.FileInfoImageXML;
import gov.nih.mipav.model.file.FileUtility;
import gov.nih.mipav.model.structures.ModelImage;
import gov.nih.mipav.model.structures.ModelStorageBase;
import gov.nih.mipav.plugins.PlugInFile;
import gov.nih.mipav.view.MipavUtil;
import gov.nih.mipav.view.Preferences;
import gov.nih.mipav.view.ViewJFrameImage;
import gov.nih.mipav.view.ViewUserInterface;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import loci.common.DataTools;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.ChannelSeparator;
import loci.formats.DimensionSwapper;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.gui.GUITools;
import loci.formats.meta.IMetadata;
import loci.formats.services.OMEXMLService;
import ome.units.UNITS;
import ome.units.quantity.Length;
import ome.units.quantity.Time;

/**
 * A plugin for opening life sciences files in MIPAV using Bio-Formats.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class PlugInBioFormatsImporter implements PlugInFile {

  // -- Constants --

  /** Update progress bar no more often than this many milliseconds. */
  protected static final int PROGRESS_THRESHOLD = 100;

  // -- Fields --

  protected DimensionSwapper reader;
  protected JFileChooser chooser;

  // -- Constructor --

  public PlugInBioFormatsImporter() {
    reader = new DimensionSwapper(new ChannelSeparator());
  }

  // -- PlugInFile API methods --

  public boolean canReadImages() {
    return true;
  }

  public boolean canWriteImages() {
    return false;
  }

  public boolean isExtensionSupported(String ext) {
    String[] suffixes = reader.getSuffixes();
    ext = ext.toLowerCase();

    // suffixes are alphabetized; use binary search
    int min = 0, max = suffixes.length - 1;
    while (min <= max) {
      int index = (min + max) / 2;
      int value = ext.compareTo(suffixes[index]);
      if (value < 0) min = index + 1; // suffix is later in the list
      else if (value > 0) max = index - 1; // suffix is earlier in the list
      else return true; // found suffix
    }
    return false; // suffix not found
  }

  public void readImage() {
    final ViewUserInterface mipav = ViewUserInterface.getReference();

    // prompt user to choose a file
    if (chooser == null) {
      chooser = GUITools.buildFileChooser(reader);
      chooser.setCurrentDirectory(new File(Preferences.getImageDirectory()));
    }
    JFrame parent = mipav.getMainFrame();
    int rval = chooser.showOpenDialog(parent);
    if (rval != JFileChooser.APPROVE_OPTION) return; // user canceled
    final File file = chooser.getSelectedFile();

    // load the image in a separate thread
    Thread importerThread = new Thread("BioFormats-Importer") {
      public void run() {
        String name = file.getName();
        String dir = file.getParent();

        // open file using Bio-Formats
        setMessage(mipav, "Importing " + name + "...", true);
        String id = file.getPath();
        try {
          long tic = System.currentTimeMillis();
          IMetadata store;

          try {
            ServiceFactory factory = new ServiceFactory();
            OMEXMLService service = factory.getInstance(OMEXMLService.class);
            store = service.createOMEXMLMetadata();
          }
          catch (DependencyException exc) {
            throw new FormatException("Could not create OME-XML store.", exc);
          }
          catch (ServiceException exc) {
            throw new FormatException("Could not create OME-XML store.", exc);
          }

          reader.setMetadataStore(store);
          reader.setId(id);

          // MIPAV assumes 4-D data in XYZT order
          reader.setOutputOrder("XYZTC");

          // harvest some core metadata
          int imageCount = reader.getImageCount();
          boolean little = reader.isLittleEndian();
          int pixelType = reader.getPixelType();
          int bpp = FormatTools.getBytesPerPixel(pixelType);
          boolean floating = FormatTools.isFloatingPoint(pixelType);
          int sizeX = reader.getSizeX();
          int sizeY = reader.getSizeY();
          int sizeZ = reader.getSizeZ();
          int sizeT = reader.getSizeT();
          int sizeC = reader.getSizeC();
          String imageName = store.getImageName(0);

          if (sizeC > 1) {
            throw new FormatException(
              "Multichannel data is unsupported at the moment");
          }

          // compute MIPAV buffer type
          int mipavType;
          switch (pixelType) {
            case FormatTools.INT8:
              mipavType = ModelStorageBase.BYTE;
              break;
            case FormatTools.UINT8:
              mipavType = ModelStorageBase.UBYTE;
              break;
            case FormatTools.INT16:
              mipavType = ModelStorageBase.SHORT;
              break;
            case FormatTools.UINT16:
              mipavType = ModelStorageBase.USHORT;
              break;
            case FormatTools.INT32:
              mipavType = ModelStorageBase.INTEGER;
              break;
            case FormatTools.UINT32:
              mipavType = ModelStorageBase.UINTEGER;
              break;
            case FormatTools.FLOAT:
              mipavType = ModelStorageBase.FLOAT;
              break;
            case FormatTools.DOUBLE:
              mipavType = ModelStorageBase.DOUBLE;
              break;
            default:
              throw new FormatException("Unsupported pixel type: " + pixelType);
          }

          // harvest physical resolution
          Length dimPhysSizeX = store.getPixelsPhysicalSizeX(0);
          Length dimPhysSizeY = store.getPixelsPhysicalSizeY(0);
          Length dimPhysSizeZ = store.getPixelsPhysicalSizeZ(0);
          Time dimTimeInc = store.getPixelsTimeIncrement(0);
          float physSizeX = dimPhysSizeX == null ?
            1.0f : dimPhysSizeX.value(UNITS.MICROMETER).floatValue();
          float physSizeY = dimPhysSizeY == null ?
            1.0f : dimPhysSizeY.value(UNITS.MICROMETER).floatValue();
          float physSizeZ = dimPhysSizeZ == null ?
            1.0f : dimPhysSizeZ.value(UNITS.MICROMETER).floatValue();
          float timeInc = dimTimeInc == null ? 1.0f :
            dimTimeInc.value(UNITS.SECOND).floatValue();

          // compute dimensional extents
          int[] dimExtents = {sizeX, sizeY, sizeZ, sizeT};
          float[] res = {physSizeX, physSizeY, physSizeZ, timeInc};
          int[] units = {
            FileInfoBase.MICROMETERS, FileInfoBase.MICROMETERS,
            FileInfoBase.MICROMETERS, FileInfoBase.SECONDS
          };

          // create MIPAV image object
          ModelImage modelImage =
            new ModelImage(mipavType, dimExtents, imageName);

          // import planes into MIPAV image
          byte[] buf = new byte[bpp * sizeX * sizeY];
          for (int i=0; i<imageCount; i++) {
            setMessage(mipav,
              "Reading plane #" + (i + 1) + "/" + imageCount, false);
            reader.openBytes(i, buf);

            // convert byte array to appropriate primitive type
            int offset = i * buf.length;
            Object array = DataTools.makeDataArray(buf, bpp, floating, little);

            // assign data to MIPAV image object
            switch (mipavType) {
              case ModelStorageBase.BYTE:
              case ModelStorageBase.UBYTE:
                modelImage.importData(offset, (byte[]) array, false);
                break;
              case ModelStorageBase.SHORT:
              case ModelStorageBase.USHORT:
                modelImage.importData(offset, (short[]) array, false);
                break;
              case ModelStorageBase.INTEGER:
              case ModelStorageBase.UINTEGER:
                modelImage.importData(offset, (int[]) array, false);
                break;
              case ModelStorageBase.FLOAT:
                modelImage.importData(offset, (float[]) array, false);
                break;
              case ModelStorageBase.DOUBLE:
                modelImage.importData(offset, (double[]) array, false);
                break;
              default:
                throw new FormatException("Unknown buffer type: " + mipavType);
            }
          }
          setMessage(mipav, "Finishing import...", true);

          // create a FileInfo object for each image plane
          FileInfoBase[] fileInfo = new FileInfoBase[imageCount];
          for (int i=0; i<imageCount; i++) {
            // HACK: Use FileInfoImageXML since FileInfoBase is abstract.
            fileInfo[i] = new FileInfoImageXML(name, dir, FileUtility.XML);
            fileInfo[i].setExtents(dimExtents);
            fileInfo[i].setResolutions(res);
            fileInfo[i].setUnitsOfMeasure(units);
            fileInfo[i].setDataType(mipavType);
          }
          modelImage.setFileInfo(fileInfo);

          // scale color range and display MIPAV image
          modelImage.calcMinMax();
          new ViewJFrameImage(modelImage);

          long toc = System.currentTimeMillis();
          long time = toc - tic;
          long avg = time / imageCount;
          setMessage(mipav, name + ": Read " + imageCount + " planes in " +
            (time / 1000f) + " seconds (" + avg + " ms/plane)", true);
        }
        catch (FormatException exc) {
          exc.printStackTrace();
          MipavUtil.displayError(
            "An error occurred parsing the file: " + exc.getMessage());
        }
        catch (IOException exc) {
          exc.printStackTrace();
          MipavUtil.displayError(
            "An I/O error occurred reading the file: " + exc.getMessage());
        }
      }
    };
    importerThread.start();
  }

  public void writeImage(ModelImage image) {
    throw new IllegalStateException("Unsupported");
  }

  // -- Helper methods --

  private long lastTime = 0;

  private synchronized void setMessage(final ViewUserInterface mipav,
    final String message, final boolean force)
  {
    long time = System.currentTimeMillis();
    long elapsed = time - lastTime;
    if (elapsed >= PROGRESS_THRESHOLD || force) {
      lastTime = time;
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          mipav.setMessageText(message);
        }
      });
    }
  }

}
