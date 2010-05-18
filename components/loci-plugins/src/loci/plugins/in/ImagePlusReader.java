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

import ij.CompositeImage;
import ij.ImagePlus;
import ij.ImageStack;
import ij.io.FileInfo;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.LUT;
import ij.process.ShortProcessor;

import java.awt.Color;
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
import loci.plugins.Slicer;
import loci.plugins.util.BFVirtualStack;
import loci.plugins.util.ImagePlusTools;
import loci.plugins.util.ImageProcessorReader;
import loci.plugins.util.VirtualImagePlus;

/**
 * A high-level reader for {@link ij.ImagePlus} objects.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/in/ImagePlusReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/in/ImagePlusReader.java">SVN</a></dd></dl>
 */
public class ImagePlusReader implements StatusReporter {

  // -- Fields --

  /**
   * Import preparation process managing Bio-Formats readers and other state.
   */
  protected ImportProcess process;

  protected List<StatusListener> listeners = new Vector<StatusListener>();

  // -- Constructors --

  /**
   * Constructs an ImagePlusReader with the default options.
   * @throws IOException if the default options cannot be determined.
   */
  public ImagePlusReader() throws IOException {
    this(new ImportProcess());
  }

  /**
   * Constructs an ImagePlusReader with the
   * given complete import preparation process.
   */
  public ImagePlusReader(ImportProcess process) {
    this.process = process;
  }

  // -- ImagePlusReader methods --

  /**
   * Opens one or more {@link ImagePlus} objects
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
    startTiming();

    ImageProcessorReader reader = process.getReader();
    ImporterOptions options = process.getOptions();

    if (options.isVirtual()) {
      int totalSeries = 0;
      for (int s=0; s<reader.getSeriesCount(); s++) {
        if (options.isSeriesOn(s)) totalSeries++;
      }
      process.getVirtualReader().setRefCount(totalSeries);
    }

    for (int s=0; s<reader.getSeriesCount(); s++) {
      if (!options.isSeriesOn(s)) continue;
      readSeries(s, imps);
    }

    // concatenate compatible images
    imps = concatenate(imps);

    // colorize images, as appropriate
    imps = applyColors(imps);

    // split dimensions, as appropriate
    imps = splitDims(imps);

    // end timing
    finishTiming();

    return imps;
  }

  private void readSeries(int s, List<ImagePlus> imps)
    throws FormatException, IOException
  {
    ImageProcessorReader reader = process.getReader();
    ImporterOptions options = process.getOptions();
    reader.setSeries(s);

    boolean[] load = getPlanesToLoad(s);
    int current = 0, total = 0;
    for (int j=0; j<reader.getImageCount(); j++) if (load[j]) total++;

    FileInfo fi = createFileInfo();

    ImageStack stackB = null; // for byte images (8-bit)
    ImageStack stackS = null; // for short images (16-bit)
    ImageStack stackF = null; // for floating point images (32-bit)
    ImageStack stackO = null; // for all other images (24-bit RGB)

    Region region = process.getCropRegion(s);

    if (options.isVirtual()) {
      boolean doMerge = false; //options.isMergeChannels();
      boolean doColorize = false; //options.isColorize();

      reader.setSeries(s);
      // NB: ImageJ 1.39+ is required for VirtualStack
      BFVirtualStack virtualStackB = new BFVirtualStack(options.getId(),
        reader, doColorize, doMerge, options.isRecord());
      stackB = virtualStackB;
      if (doMerge) {
        for (int j=0; j<reader.getImageCount(); j++) {
          int[] zct = reader.getZCTCoords(j);
          if (zct[1] > 0) continue;
          ChannelMerger channelMerger = new ChannelMerger(reader);
          int index = channelMerger.getIndex(zct[0], zct[1], zct[2]);
          String label = constructSliceLabel(index,
            channelMerger, process.getOMEMetadata(), s,
            process.getZCount(s), process.getCCount(s), process.getTCount(s));
          virtualStackB.addSlice(label);
        }
      }
      else {
        for (int j=0; j<reader.getImageCount(); j++) {
          String label = constructSliceLabel(j,
            reader, process.getOMEMetadata(), s,
            process.getZCount(s), process.getCCount(s), process.getTCount(s));
          virtualStackB.addSlice(label);
        }
      }
    }
    else {
      // CTR CHECK
      //if (r.isIndexed()) colorModels = new IndexColorModel[r.getSizeC()];

      for (int i=0; i<reader.getImageCount(); i++) {
        if (!load[i]) continue;

        // limit message update rate
        updateTiming(s, i, current++, total);

        String label = constructSliceLabel(i,
          reader, process.getOMEMetadata(), s,
          process.getZCount(s), process.getCCount(s), process.getTCount(s));

        // get image processor for ith plane
        ImageProcessor[] p;
        p = reader.openProcessors(i, region.x, region.y,
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

  private List<ImagePlus> concatenate(List<ImagePlus> imps) {
    final ImporterOptions options = process.getOptions();
    if (options.isConcatenate()) imps = new Concatenator().concatenate(imps);
    return imps;
  }

  private List<ImagePlus> applyColors(List<ImagePlus> imps) {
    final ImporterOptions options = process.getOptions();

    // CTR FIXME - problems with single channel data
    // CTR FIXME - problems with sizeC > 7
    // CTR FIXME - problems with default color mode
    int mode = -1;
    if (options.isColorModeComposite()) mode = CompositeImage.COMPOSITE;
    else if (options.isColorModeColorized()) mode = CompositeImage.COLOR;
    else if (options.isColorModeGrayscale()) mode = CompositeImage.GRAYSCALE;
    else if (options.isColorModeCustom()) mode = CompositeImage.COLOR;
    if (mode != -1) {
      List<ImagePlus> compositeImps = new ArrayList<ImagePlus>();
      for (ImagePlus imp : imps) {
        CompositeImage compImage = new CompositeImage(imp, mode);
        LUT[] luts = null;
        int series = (Integer) imp.getProperty("Series");
        if (options.isColorModeCustom()) luts = makeLUTs(series);
        if (luts != null) compImage.setLuts(luts);
        compositeImps.add(compImage);
      }
      imps = compositeImps;
    }
    return imps;
  }

  private List<ImagePlus> splitDims(List<ImagePlus> imps) {
    final ImporterOptions options = process.getOptions();

    boolean sliceC = options.isSplitChannels();
    boolean sliceZ = options.isSplitFocalPlanes();
    boolean sliceT = options.isSplitTimepoints();
    if (sliceC || sliceZ || sliceT) {
      String stackOrder = process.getStackOrder();
      List<ImagePlus> slicedImps = new ArrayList<ImagePlus>();
      for (ImagePlus imp : imps) {
        ImagePlus[] results = new Slicer().reslice(imp,
          sliceC, sliceZ, sliceT, stackOrder);
        for (ImagePlus result : results) slicedImps.add(result);
      }
      imps = slicedImps;
    }
    return imps;
  }
  

  private LUT[] makeLUTs(int series) {
    final ImageProcessorReader reader = process.getReader();
    reader.setSeries(series);
    LUT[] luts = new LUT[reader.getSizeC()];
    for (int c=0; c<luts.length; c++) luts[c] = makeLUT(series, c);
    return luts;
  }
  
  private LUT makeLUT(int series, int channel) {
    final ImporterOptions options = process.getOptions();
    Color color = options.getCustomColor(series, channel);
    if (color == null) color = options.getDefaultCustomColor(channel);
    return makeLUT(color);
  }
  
  private LUT makeLUT(Color color) {
    final int red = color.getRed();
    final int green = color.getGreen();
    final int blue = color.getBlue();
    final int lutLength = 256;
    byte[] r = new byte[lutLength];
    byte[] g = new byte[lutLength];
    byte[] b = new byte[lutLength];
    for (int i=0; i<lutLength; i++) {
      r[i] = (byte) (i * red / lutLength);
      g[i] = (byte) (i * green / lutLength);
      b[i] = (byte) (i * blue / lutLength);
    }
    return new LUT(r, g, b);
  }
    
  private FileInfo createFileInfo() {
    FileInfo fi = new FileInfo();

    // populate common FileInfo fields
    String idDir = process.getIdLocation() == null ?
      null : process.getIdLocation().getParent();
    if (idDir != null && !idDir.endsWith(File.separator)) {
      idDir += File.separator;
    }
    fi.fileName = process.getIdName();
    fi.directory = idDir;

    // dump OME-XML to ImageJ's description field, if available
    try {
      ServiceFactory factory = new ServiceFactory();
      OMEXMLService service = factory.getInstance(OMEXMLService.class);
      fi.description = service.getOMEXML(process.getOMEMetadata());
    }
    catch (DependencyException de) { }
    catch (ServiceException se) { }

    return fi;
  }

  private boolean[] getPlanesToLoad(int s) {
    ImageProcessorReader reader = process.getReader();
    boolean[] load = new boolean[reader.getImageCount()];
    int cBegin = process.getCBegin(s);
    int cEnd = process.getCEnd(s);
    int cStep = process.getCStep(s);
    int zBegin = process.getZBegin(s);
    int zEnd = process.getZEnd(s);
    int zStep = process.getZStep(s);
    int tBegin = process.getTBegin(s);
    int tEnd = process.getTEnd(s);
    int tStep = process.getTStep(s);
    for (int c=cBegin; c<=cEnd; c+=cStep) {
      for (int z=zBegin; z<=zEnd; z+=zStep) {
        for (int t=tBegin; t<=tEnd; t+=tStep) {
          //int index = r.isOrderCertain() ? r.getIndex(z, c, t) : c;
          int index = reader.getIndex(z, c, t);
          load[index] = true;
        }
      }
    }
    return load;
  }

  /**
   * Displays the given image stack according to
   * the specified parameters and import options.
   */
  private ImagePlus createImage(ImageStack stack, int series, FileInfo fi) {
    if (stack == null) return null;

    ImporterOptions options = process.getOptions();
    String seriesName = process.getOMEMetadata().getImageName(series);
    String file = process.getCurrentFile();
    IMetadata meta = process.getOMEMetadata();
    int cCount = process.getCCount(series);
    int zCount = process.getZCount(series);
    int tCount = process.getTCount(series);
    IFormatReader reader = process.getReader();

    String title = getTitle(reader, file, seriesName, options.isGroupFiles());
    ImagePlus imp = null;
    if (options.isVirtual()) {
      VirtualImagePlus vip = new VirtualImagePlus(title, stack);
      vip.setReader(reader);
      imp = vip;
    }
    else imp = new ImagePlus(title, stack);

    // place metadata key/value pairs in ImageJ's info field
    String metadata = process.getOriginalMetadata().toString();
    imp.setProperty("Info", metadata);
    imp.setProperty("Series", series);

    // retrieve the spatial calibration information, if available
    ImagePlusTools.applyCalibration(meta, imp, reader.getSeries());
    imp.setFileInfo(fi);
    imp.setDimensions(cCount, zCount, tCount);

    imp.setOpenAsHyperStack(true);

    if (options.isAutoscale()) {
      ImagePlusTools.adjustColorRange(imp, process.getMinMaxCalculator());
    }
    else if (!(imp.getProcessor() instanceof ColorProcessor)) {
      // ImageJ may autoscale the images anyway, so we need to manually
      // set the display range to the min/max values allowed for
      // this pixel type
      imp.setDisplayRange(0, Math.pow(2, imp.getBitDepth()) - 1);
    }

//    IFormatReader r = options.getReader();
//    boolean windowless = options.isWindowless();


    // NB: ImageJ 1.39+ is required for hyperstacks

//      boolean hyper = options.isViewHyperstack() || options.isViewBrowser();
//
//      boolean splitC = options.isSplitChannels();
//      boolean splitZ = options.isSplitFocalPlanes();
//      boolean splitT = options.isSplitTimepoints();
//
//      boolean customColorize = options.isCustomColorize();
//      boolean browser = options.isViewBrowser();
//      boolean virtual = options.isVirtual();
//
//      if (options.isColorize() || customColorize) {
//        byte[][][] lut =
//          Colorizer.makeDefaultLut(imp.getNChannels(), customColorize ? -1 : 0);
//        imp = Colorizer.colorize(imp, true, stackOrder, lut, r.getSeries(), null, options.isViewHyperstack());
//      }
//      else if (colorModels != null && !browser && !virtual) {
//        byte[][][] lut = new byte[colorModels.length][][];
//        for (int channel=0; channel<lut.length; channel++) {
//          lut[channel] = new byte[3][256];
//          colorModels[channel].getReds(lut[channel][0]);
//          colorModels[channel].getGreens(lut[channel][1]);
//          colorModels[channel].getBlues(lut[channel][2]);
//        }
//        imp = Colorizer.colorize(imp, true,
//          stackOrder, lut, r.getSeries(), null, hyper);
//      }
//    }

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

  // -- Helper methods - timing --

  private long startTime, time;

  private void startTiming() {
    startTime = time = System.currentTimeMillis();
  }

  private void updateTiming(int s, int i, int current, int total) {
    final ImageProcessorReader reader = process.getReader();

    long clock = System.currentTimeMillis();
    if (clock - time >= 100) {
      String sLabel = reader.getSeriesCount() > 1 ?
        ("series " + (s + 1) + ", ") : "";
      String pLabel = "plane " + (i + 1) + "/" + total;
      notifyListeners(new StatusEvent("Reading " + sLabel + pLabel));
      time = clock;
    }
    notifyListeners(new StatusEvent(current, total, null));
  }

  private void finishTiming() {
    final ImageProcessorReader reader = process.getReader();

    long endTime = System.currentTimeMillis();
    double elapsed = (endTime - startTime) / 1000.0;
    if (reader.getImageCount() == 1) {
      notifyListeners(new StatusEvent("Bio-Formats: " + elapsed + " seconds"));
    }
    else {
      long average = (endTime - startTime) / reader.getImageCount();
      notifyListeners(new StatusEvent("Bio-Formats: " +
        elapsed + " seconds (" + average + " ms per plane)"));
    }
  }

}
