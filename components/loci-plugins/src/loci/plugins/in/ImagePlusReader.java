//
// ImagePlusReader.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
Data Browser, Stack Colorizer and Stack Slicer. Copyright (C) 2005-@year@
Melissa Linkert, Curtis Rueden and Christopher Peterson.

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

package loci.plugins.in;

import ij.ImagePlus;
import ij.ImageStack;
import ij.io.FileInfo;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import loci.common.Location;
import loci.common.Region;
import loci.common.StatusEvent;
import loci.common.StatusListener;
import loci.common.StatusReporter;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.ChannelMerger;
import loci.formats.FilePattern;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.meta.IMetadata;
import loci.formats.services.OMEXMLService;
import loci.plugins.util.BFVirtualStack;
import loci.plugins.util.ImagePlusTools;
import loci.plugins.util.ImageProcessorReader;
import loci.plugins.util.VirtualImagePlus;
import loci.plugins.util.VirtualReader;

import ome.xml.r201004.enums.DimensionOrder;
import ome.xml.r201004.enums.EnumerationException;

/**
 * A high-level reader for {@link ij.ImagePlus} objects.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/in/ImagePlusReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/in/ImagePlusReader.java">SVN</a></dd></dl>
 */
public class ImagePlusReader implements StatusReporter {

  // -- Fields --

  /** Importer options that control the reader's behavior. */
  protected ImporterOptions options;

  protected List<StatusListener> listeners = new Vector<StatusListener>();

  // -- Constructors --

  /**
   * Constructs an ImagePlusReader with the default options.
   * @throws IOException if the default options cannot be determined.
   */
  public ImagePlusReader() throws IOException {
    this(new ImporterOptions());
  }

  /** Constructs an ImagePlusReader with the given reader. */
  public ImagePlusReader(ImporterOptions options) {
    this.options = options;
  }

  // -- ImagePlusReader methods --

  /**
   * Opens one or more {@link ij.ImagePlus} objects
   * corresponding to the reader's associated options.
   */
  public ImagePlus[] openImagePlus() throws FormatException, IOException {
    List<ImagePlus> imps = readImages();
    return imps.toArray(new ImagePlus[0]);
  }

  // -- StatusReporter methods --

  public void addStatusListener(StatusListener l) {
    listeners.add(l);
  }

  public void removeStatusListener(StatusListener l) {
    listeners.remove(l);
  }

  public void notifyListeners(StatusEvent e) {
    for (StatusListener l : listeners) l.statusUpdated(e);
  }

  // -- Helper methods --

  private List<ImagePlus> readImages()
    throws FormatException, IOException
  {
    List<ImagePlus> imps = new ArrayList<ImagePlus>();

    // beginning timing
    long startTime = System.currentTimeMillis();
    long time = startTime;

    ImageProcessorReader r = options.getReader();

    if (options.isVirtual()) {
      int totalSeries = 0;
      for (int s=0; s<r.getSeriesCount(); s++) {
        if (options.isSeriesOn(s)) totalSeries++;
      }
      ((VirtualReader) r.getReader()).setRefCount(totalSeries);
    }

    for (int s=0; s<r.getSeriesCount(); s++) {
      if (!options.isSeriesOn(s)) continue;
      r.setSeries(s);

      boolean[] load = new boolean[r.getImageCount()];
      int cBegin = options.getCBegin(s);
      int cEnd = options.getCEnd(s);
      if (cEnd < 0) cEnd = r.getEffectiveSizeC() - 1;
      int cStep = options.getCStep(s);
      int zBegin = options.getZBegin(s);
      int zEnd = options.getZEnd(s);
      if (zEnd < 0) zEnd = r.getSizeZ() - 1;
      int zStep = options.getZStep(s);
      int tBegin = options.getTBegin(s);
      int tEnd = options.getTEnd(s);
      if (tEnd < 0) tEnd = r.getSizeT() - 1;
      int tStep = options.getTStep(s);
      for (int c=cBegin; c<=cEnd; c+=cStep) {
        for (int z=zBegin; z<=zEnd; z+=zStep) {
          for (int t=tBegin; t<=tEnd; t+=tStep) {
            //int index = r.isOrderCertain() ? r.getIndex(z, c, t) : c;
            int index = r.getIndex(z, c, t);
            load[index] = true;
          }
        }
      }
      int total = 0;
      for (int j=0; j<r.getImageCount(); j++) if (load[j]) total++;

      FileInfo fi = new FileInfo();

      // populate other common FileInfo fields
      String idDir = options.getIdLocation() == null ?
        null : options.getIdLocation().getParent();
      if (idDir != null && !idDir.endsWith(File.separator)) {
        idDir += File.separator;
      }
      fi.fileName = options.getIdName();
      fi.directory = idDir;

      ImageStack stackB = null; // for byte images (8-bit)
      ImageStack stackS = null; // for short images (16-bit)
      ImageStack stackF = null; // for floating point images (32-bit)
      ImageStack stackO = null; // for all other images (24-bit RGB)

      Region region = options.getCropRegion(s);
      if (region == null) region = new Region();
      int sizeX = r.getSizeX(), sizeY = r.getSizeY();
      if (options.doCrop()) {
        // bounds checking for cropped region
        if (region.x < 0) region.x = 0;
        if (region.y < 0) region.y = 0;
        if (region.width <= 0 || region.x + region.width > sizeX) {
          region.width = sizeX - region.x;
        }
        if (region.height <= 0 || region.y + region.height > sizeY) {
          region.height = sizeX - region.y;
        }
      }
      else {
        // obtain entire image plane
        region.x = region.y = 0;
        region.width = sizeX;
        region.height = sizeY;
      }

      int q = 0;
      String stackOrder = options.getStackOrder();
      if (stackOrder.equals(ImporterOptions.ORDER_DEFAULT)) {
        stackOrder = r.getDimensionOrder();
      }
      ((VirtualReader) r.getReader()).setOutputOrder(stackOrder);

      try {
        options.getOMEMetadata().setPixelsDimensionOrder(
          DimensionOrder.fromString(stackOrder), s);
      }
      catch (EnumerationException e) { }

      // dump OME-XML to ImageJ's description field, if available
      try {
        ServiceFactory factory = new ServiceFactory();
        OMEXMLService service = factory.getInstance(OMEXMLService.class);
        fi.description = service.getOMEXML(options.getOMEMetadata());
      }
      catch (DependencyException de) { }
      catch (ServiceException se) { }

      if (options.isVirtual()) {
        boolean doMerge = options.isMergeChannels();

        r.setSeries(s);
        // NB: ImageJ 1.39+ is required for VirtualStack
        BFVirtualStack virtualStackB = new BFVirtualStack(options.getId(),
          r, options.isColorize(), doMerge, options.isRecord());
        stackB = virtualStackB;
        if (doMerge) {
          for (int j=0; j<r.getImageCount(); j++) {
            int[] pos = r.getZCTCoords(j);
            if (pos[1] > 0) continue;
            String label = constructSliceLabel(
              new ChannelMerger(r).getIndex(pos[0], pos[1], pos[2]),
              new ChannelMerger(r), options.getOMEMetadata(), s,
              options.getZCount(s), options.getCCount(s),
              options.getTCount(s));
            virtualStackB.addSlice(label);
          }
        }
        else {
          for (int j=0; j<r.getImageCount(); j++) {
            String label = constructSliceLabel(j, r,
              options.getOMEMetadata(), s, options.getZCount(s),
              options.getCCount(s), options.getTCount(s));
            virtualStackB.addSlice(label);
          }
        }
      }
      else {
        // CTR CHECK
        //if (r.isIndexed()) colorModels = new IndexColorModel[r.getSizeC()];

        for (int i=0; i<r.getImageCount(); i++) {
          if (!load[i]) continue;

          // limit message update rate
          long clock = System.currentTimeMillis();
          if (clock - time >= 100) {
            String sLabel = r.getSeriesCount() > 1 ?
              ("series " + (s + 1) + ", ") : "";
            String pLabel = "plane " + (i + 1) + "/" + total;
            notifyListeners(new StatusEvent("Reading " + sLabel + pLabel));
            time = clock;
          }
          notifyListeners(new StatusEvent(q++, total, null));

          String label = constructSliceLabel(i, r,
            options.getOMEMetadata(), s, options.getZCount(s),
            options.getCCount(s), options.getTCount(s));

          // get image processor for ith plane
          ImageProcessor[] p;
          p = r.openProcessors(i, region.x, region.y,
            region.width, region.height);
          ImageProcessor ip = p[0];
          if (p.length > 1) {
            ip = ImagePlusTools.makeRGB(p).getProcessor();
          }
          if (ip == null) {
            throw new FormatException("Cannot read ImageProcessor #" + i);
          }

          // CTR CHECK
          //int channel = r.getZCTCoords(i)[1];
          //if (colorModels != null && p.length == 1) {
          //  colorModels[channel] = (IndexColorModel) ip.getColorModel();
          //}

          // add plane to image stack
          int w = region.width, h = region.height;
          if (ip instanceof ByteProcessor) {
            if (stackB == null) stackB = new ImageStack(w, h);
            stackB.addSlice(label, ip);
          }
          else if (ip instanceof ShortProcessor) {
            if (stackS == null) stackS = new ImageStack(w, h);
            stackS.addSlice(label, ip);
          }
          else if (ip instanceof FloatProcessor) {
            // merge image plane into existing stack if possible
            if (stackB != null) {
              ip = ip.convertToByte(true);
              stackB.addSlice(label, ip);
            }
            else if (stackS != null) {
              ip = ip.convertToShort(true);
              stackS.addSlice(label, ip);
            }
            else {
              if (stackF == null) stackF = new ImageStack(w, h);
              stackF.addSlice(label, ip);
            }
          }
          else if (ip instanceof ColorProcessor) {
            if (stackO == null) stackO = new ImageStack(w, h);
            stackO.addSlice(label, ip);
          }
        }
      }

      notifyListeners(new StatusEvent(1, 1, "Creating image"));

      ImagePlus impB = createImage(stackB, s, fi);
      ImagePlus impS = createImage(stackS, s, fi);
      ImagePlus impF = createImage(stackF, s, fi);
      ImagePlus impO = createImage(stackO, s, fi);
      if (impB != null) imps.add(impB);
      if (impS != null) imps.add(impS);
      if (impF != null) imps.add(impF);
      if (impO != null) imps.add(impO);
    }

    // CTR CHECK
    //Concatenator concatenator = new Concatenator(options);
    //imps = concatenator.concatenate(imps, stackOrder);

    // end timing
    long endTime = System.currentTimeMillis();
    double elapsed = (endTime - startTime) / 1000.0;
    if (r.getImageCount() == 1) {
      notifyListeners(new StatusEvent("Bio-Formats: " + elapsed + " seconds"));
    }
    else {
      long average = (endTime - startTime) / r.getImageCount();
      notifyListeners(new StatusEvent("Bio-Formats: " +
        elapsed + " seconds (" + average + " ms per plane)"));
    }

    return imps;
  }

  /**
   * Displays the given image stack according to
   * the specified parameters and import options.
   */
  private ImagePlus createImage(ImageStack stack, int series, FileInfo fi) {
    if (stack == null) return null;

    String seriesName = options.getOMEMetadata().getImageName(series);
    String file = options.getCurrentFile();
    IMetadata meta = options.getOMEMetadata();
    int cCount = options.getCCount(series);
    int zCount = options.getZCount(series);
    int tCount = options.getTCount(series);
    IFormatReader r = options.getReader();
    
    if (cCount == 0) cCount = r.getEffectiveSizeC();
    if (zCount == 0) zCount = r.getSizeZ();
    if (tCount == 0) tCount = r.getSizeT();

    String title = getTitle(r, file, seriesName, options.isGroupFiles());
    ImagePlus imp = null;
    if (options.isVirtual()) {
      imp = new VirtualImagePlus(title, stack);
      ((VirtualImagePlus) imp).setReader(r);
    }
    else imp = new ImagePlus(title, stack);

    // place metadata key/value pairs in ImageJ's info field
    String metadata = options.getOriginalMetadata().toString();
    imp.setProperty("Info", metadata);

    // retrieve the spatial calibration information, if available
    ImagePlusTools.applyCalibration(meta, imp, r.getSeries());
    imp.setFileInfo(fi);
    imp.setDimensions(cCount, zCount, tCount);

    boolean hyper = options.isViewHyperstack() || options.isViewBrowser();
    imp.setOpenAsHyperStack(hyper);
    int nSlices = imp.getNSlices();
    int nFrames = imp.getNFrames();

    if (options.isAutoscale() && !options.isVirtual()) {
      ImagePlusTools.adjustColorRange(imp, r);
    }
    else if (!(imp.getProcessor() instanceof ColorProcessor)) {
      // ImageJ may autoscale the images anyway, so we need to manually
      // set the display range to the min/max values allowed for
      // this pixel type
      imp.setDisplayRange(0, Math.pow(2, imp.getBitDepth()) - 1);
    }

    imp.setDimensions(imp.getStackSize() / (nSlices * nFrames),
      nSlices, nFrames);

    return imp;
  }

  /** Get an appropriate stack title, given the file name. */
  private String getTitle(IFormatReader r, String file, String seriesName,
    boolean groupFiles)
  {
    String[] used = r.getUsedFiles();
    String title = file.substring(file.lastIndexOf(File.separator) + 1);
    if (used.length > 1 && groupFiles) {
      FilePattern fp = new FilePattern(new Location(file));
      title = fp.getPattern();
      if (title == null) {
        title = file;
        if (title.indexOf(".") != -1) {
          title = title.substring(0, title.lastIndexOf("."));
        }
      }
      title = title.substring(title.lastIndexOf(File.separator) + 1);
    }
    if (seriesName != null && !file.endsWith(seriesName) &&
      r.getSeriesCount() > 1)
    {
      title += " - " + seriesName;
    }
    if (title.length() > 128) {
      String a = title.substring(0, 62);
      String b = title.substring(title.length() - 62);
      title = a + "..." + b;
    }
    return title;
  }

  /** Constructs slice label. */
  private String constructSliceLabel(int ndx, IFormatReader r,
    IMetadata meta, int series, int zCount, int cCount, int tCount)
  {
    r.setSeries(series);
    int[] zct = r.getZCTCoords(ndx);
    int[] subC = r.getChannelDimLengths();
    String[] subCTypes = r.getChannelDimTypes();
    StringBuffer sb = new StringBuffer();
    boolean first = true;
    if (cCount > 1) {
      if (first) first = false;
      else sb.append("; ");
      int[] subCPos = FormatTools.rasterToPosition(subC, zct[1]);
      for (int i=0; i<subC.length; i++) {
        boolean ch = subCTypes[i].equals(FormatTools.CHANNEL);
        sb.append(ch ? "c" : subCTypes[i]);
        sb.append(":");
        sb.append(subCPos[i] + 1);
        sb.append("/");
        sb.append(subC[i]);
        if (i < subC.length - 1) sb.append(", ");
      }
    }
    if (zCount > 1) {
      if (first) first = false;
      else sb.append("; ");
      sb.append("z:");
      sb.append(zct[0] + 1);
      sb.append("/");
      sb.append(r.getSizeZ());
    }
    if (tCount > 1) {
      if (first) first = false;
      else sb.append("; ");
      sb.append("t:");
      sb.append(zct[2] + 1);
      sb.append("/");
      sb.append(r.getSizeT());
    }
    // put image name at the end, in case it is long
    String imageName = meta.getImageName(series);
    if (imageName != null && !imageName.trim().equals("")) {
      sb.append(" - ");
      sb.append(imageName);
    }
    return sb.toString();
  }

}
