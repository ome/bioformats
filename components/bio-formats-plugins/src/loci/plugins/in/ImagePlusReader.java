/*
 * #%L
 * Bio-Formats Plugins for ImageJ: a collection of ImageJ plugins including the
 * Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
 * Data Browser and Stack Slicer.
 * %%
 * Copyright (C) 2006 - 2017 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.plugins.in;

import ij.ImagePlus;
import ij.ImageStack;
import ij.io.FileInfo;
import ij.process.ImageProcessor;
import ij.process.LUT;

import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import loci.common.DateTools;
import loci.common.Location;
import loci.common.Region;
import loci.common.StatusEvent;
import loci.common.StatusListener;
import loci.common.StatusReporter;
import loci.formats.FilePattern;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.Modulo;
import loci.formats.meta.DummyMetadata;
import loci.formats.meta.IMetadata;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;
import loci.plugins.Slicer;
import loci.plugins.util.BFVirtualStack;
import loci.plugins.util.ImageProcessorReader;
import loci.plugins.util.LociPrefs;
import loci.plugins.util.LuraWave;
import loci.plugins.util.VirtualImagePlus;
import ome.units.UNITS;
import ome.units.quantity.Time;
import ome.xml.model.enums.DimensionOrder;
import ome.xml.model.primitives.Timestamp;

/**
 * A high-level reader for {@link ij.ImagePlus} objects.
 */
public class ImagePlusReader implements StatusReporter {

  // -- Constants --

  /** Special property for storing series number associated with the image. */
  public static final String PROP_SERIES = "Series";

  /** Special property prefix for storing planar LUTs. */
  public static final String PROP_LUT = "LUT-";

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

  public ImagePlus[] openThumbImagePlus() throws FormatException, IOException {
    List<ImagePlus> imps = readThumbImages();
    return imps.toArray(new ImagePlus[imps.size()]);
  }

  // -- StatusReporter methods --

  @Override
  public void addStatusListener(StatusListener l) {
    listeners.add(l);
  }

  @Override
  public void removeStatusListener(StatusListener l) {
    listeners.remove(l);
  }

  @Override
  public void notifyListeners(StatusEvent e) {
    for (StatusListener l : listeners) l.statusUpdated(e);
  }

  // -- Utility methods --

  /**
   * Creates an {@link ImagePlus} from the given image processors.
   *
   * @param title The title for the image.
   * @param procs List of image processors to compile into an image.
   */
  public static ImagePlus createImage(String title, List<ImageProcessor> procs) {
    final List<LUT> luts = new ArrayList<LUT>();
    final ImageStack stack = createStack(procs, null, luts);
    return createImage(title, stack, luts);
  }

  /**
   * Creates an {@link ImagePlus} from the given image stack.
   *
   * @param title The title for the image.
   * @param stack The image stack containing the image planes.
   * @param luts Optional list of plane-specific LUTs
   *   to store as image properties, for later use.
   */
  public static ImagePlus createImage(String title,
    ImageStack stack, List<LUT> luts)
  {
    final ImagePlus imp = new ImagePlus(title, stack);

    saveLUTs(imp, luts);
    return imp;
  }

  /**
   * Creates an image stack from the given image processors.
   *
   * @param procs List of image processors to compile into a stack.
   * @param labels Optional list of labels, one per plane.
   * @param luts Optional list for storing plane-specific LUTs, for later use.
   */
  public static ImageStack createStack(List<ImageProcessor> procs,
    List<String> labels, List<LUT> luts)
  {
    if (procs == null || procs.size() == 0) return null;

    final ImageProcessor ip0 = procs.get(0);
    final ImageStack stack = new ImageStack(ip0.getWidth(), ip0.getHeight());

    // construct image stack from list of image processors
    for (int i=0; i<procs.size(); i++) {
      final ImageProcessor ip = procs.get(i);
      final String label = labels == null ? null : labels.get(i);

      // HACK: ImageProcessorReader always assigns an ij.process.LUT object
      // as the color model. If we don't get one, we know ImageJ created a
      // default color model instead, which we can discard.
      if (luts != null) {
        final ColorModel cm = ip.getColorModel();
        if (cm instanceof LUT) {
          // plane has custom LUT attached; save it to the list
          final LUT lut = (LUT) cm;
          luts.add(lut);
          // discard custom LUT from ImageProcessor
          ip.setColorModel(ip.getDefaultColorModel());
        }
        else {
          // no LUT attached; save a placeholder
          luts.add(null);
        }
      }

      // add plane to image stack
      stack.addSlice(label, ip);
    }

    return stack;
  }

  // -- Helper methods - image reading --

  private List<ImagePlus> readImages() throws FormatException, IOException {
    return readImages(false); 
  } 

  private List<ImagePlus> readThumbImages() throws FormatException, IOException
  {
    return readImages(true);
  }

  private List<ImagePlus> readImages(boolean thumbnail)
    throws FormatException, IOException
  {
    final ImporterOptions options = process.getOptions();
    final ImageProcessorReader reader = process.getReader();

    List<ImagePlus> imps = new ArrayList<ImagePlus>();

    // beginning timing
    startTiming();

    // read in each image series
    for (int s=0; s<reader.getSeriesCount(); s++) {
      if (!options.isSeriesOn(s)) continue;
      final ImagePlus imp = readImage(s, thumbnail);
      imps.add(imp);
    }

    // concatenate compatible images
    imps = concatenate(imps);

    // colorize images, as appropriate
    imps = applyColors(imps);

    // split dimensions, as appropriate
    imps = splitDims(imps);

    // set virtual stack's reference count to match # of image windows
    // in this case, these is one window per enabled image series
    // when all image windows are closed, the Bio-Formats reader is closed
    if (options.isVirtual()) {
      process.getVirtualReader().setRefCount(imps.size());
    }

    // end timing
    finishTiming();

    return imps;
  }

  private ImagePlus readImage(int s, boolean thumbnail)
    throws FormatException, IOException
  {
    final ImporterOptions options = process.getOptions();
    final int zCount = process.getZCount(s);
    final int cCount = process.getCCount(s);
    final int tCount = process.getTCount(s);

    final List<LUT> luts = new ArrayList<LUT>();

    // create image stack
    final ImageStack stack;
    if (options.isVirtual()) stack = createVirtualStack(process, s, luts);
    else stack = readPlanes(process, s, luts, thumbnail);

    notifyListeners(new StatusEvent(1, 1, "Creating image"));

    // create title
    final String seriesName = process.getOMEMetadata().getImageName(s);
    final String file = process.getCurrentFile();
    final IFormatReader reader = process.getReader();
    final String title = constructImageTitle(reader,
      file, seriesName, options.isGroupFiles());

    // create image
    final ImagePlus imp;
    if (stack.isVirtual()) {
      VirtualImagePlus vip = new VirtualImagePlus(title, stack);
      vip.setReader(reader);
      imp = vip;
      saveLUTs(imp, luts);
    }
    else imp = createImage(title, stack, luts);

    // configure image

    // place metadata key/value pairs in ImageJ's info field
    // if concatenating images only store metadata on first series
    if (!options.isConcatenate() || s == 0) {
      final String metadata = process.getOriginalMetadata().toString();
      imp.setProperty("Info", metadata);
    }
    imp.setProperty(PROP_SERIES, s);

    // retrieve the spatial calibration information, if available
    final FileInfo fi = createFileInfo();
    new Calibrator(process).applyCalibration(imp);
    imp.setFileInfo(fi);
    imp.setDimensions(cCount, zCount, tCount);

    // open as a hyperstack, as appropriate
    final boolean hyper = !options.isViewStandard();
    imp.setOpenAsHyperStack(hyper);

    return imp;
  }

  private ImageStack createVirtualStack(ImportProcess process, int s,
    List<LUT> luts) throws FormatException, IOException
  {
    final ImporterOptions options = process.getOptions();
    final ImageProcessorReader reader = process.getReader();
    reader.setSeries(s);
    final int zCount = process.getZCount(s);
    final int cCount = process.getCCount(s);
    final int tCount = process.getTCount(s);
    final IMetadata meta = process.getOMEMetadata();
    final int imageCount = reader.getImageCount();

    // CTR FIXME: Make virtual stack work with different color modes?
    final BFVirtualStack virtualStack = new BFVirtualStack(options.getId(),
      reader, false, false, false);
    for (int i=0; i<imageCount; i++) {
      String label = constructSliceLabel(i, reader, meta, s, zCount, cCount, tCount);
      virtualStack.addSlice(label);
    }

    if (luts != null) {
      for (int c=0; c<cCount; c++) {
        int index = reader.getIndex(0, c, 0);
        ImageProcessor ip = reader.openProcessors(index)[0];
        final ColorModel cm = ip.getColorModel();
        final LUT lut = cm instanceof LUT ? (LUT) cm : null;
        luts.add(lut);
      }
    }

    return virtualStack;
  }

  private ImageStack readPlanes(ImportProcess process, int s, List<LUT> luts,
    boolean thumbnail)
    throws FormatException, IOException
  {
    final ImageProcessorReader reader = process.getReader();
    reader.setSeries(s);
    final int zCount = process.getZCount(s);
    final int cCount = process.getCCount(s);
    final int tCount = process.getTCount(s);
    final IMetadata meta = process.getOMEMetadata();

    // get list of planes to load
    final boolean[] load = getPlanesToLoad(s);
    int current = 0, total = 0;
    for (int j=0; j<load.length; j++) if (load[j]) total++;

    final List<ImageProcessor> procs = new ArrayList<ImageProcessor>();
    final List<String> labels = new ArrayList<String>();

    // read applicable image planes
    final Region region = process.getCropRegion(s);
    for (int i=0; i<load.length; i++) {
      if (!load[i]) continue;

      // limit message update rate
      updateTiming(s, current, current++, total);

      // get image processor for ith plane
      final ImageProcessor[] p = readProcessors(process, i, region, thumbnail);
      if (p == null || p.length == 0) {
        throw new FormatException("Cannot read plane #" + i);
      }
      // generate a label for ith plane
      String label = constructSliceLabel(i, reader, meta, s, zCount, cCount, tCount);

      for (ImageProcessor ip : p) {
        procs.add(ip);
        labels.add(label);
      }
    }

    return createStack(procs, labels, luts);
  }

  /**
   * HACK: This method mainly exists to prompt the user for a missing
   * LuraWave license code, in the case of LWF-compressed Flex.
   *
   * @see ImportProcess#setId()
   */
  private ImageProcessor[] readProcessors(ImportProcess process,
    int no, Region r, boolean thumbnail) throws FormatException, IOException
  {
    final ImageProcessorReader reader = process.getReader();
    final ImporterOptions options = process.getOptions();

    boolean first = true;
    for (int i=0; i<LuraWave.MAX_TRIES; i++) {
      String code = LuraWave.initLicenseCode();
      try {
        if (thumbnail) {
          return reader.openThumbProcessors(no);
        }
        return reader.openProcessors(no, r.x, r.y, r.width, r.height);
      }
      catch (FormatException exc) {
        if (options.isQuiet() || options.isWindowless()) throw exc;
        if (!LuraWave.isLicenseCodeException(exc)) throw exc;

        // prompt user for LuraWave license code
        code = LuraWave.promptLicenseCode(code, first);
        if (code == null) throw exc;
        if (first) first = false;
      }
    }
    throw new FormatException(LuraWave.TOO_MANY_TRIES);
  }

  // -- Helper methods - image post processing --

  private List<ImagePlus> concatenate(List<ImagePlus> imps) {
    final ImporterOptions options = process.getOptions();
    if (options.isConcatenate()) imps = new Concatenator().concatenate(imps);
    return imps;
  }

  private List<ImagePlus> applyColors(List<ImagePlus> imps) {
    return new Colorizer(process).applyColors(imps);
  }

  private List<ImagePlus> splitDims(List<ImagePlus> imps) {
    final ImporterOptions options = process.getOptions();

    final boolean sliceC = options.isSplitChannels();
    final boolean sliceZ = options.isSplitFocalPlanes();
    final boolean sliceT = options.isSplitTimepoints();
    if (sliceC || sliceZ || sliceT) {
      final String stackOrder = process.getStackOrder();
      final List<ImagePlus> slicedImps = new ArrayList<ImagePlus>();
      final Slicer slicer = new Slicer();
      for (ImagePlus imp : imps) {
        final ImagePlus[] results = slicer.reslice(imp,
          sliceC, sliceZ, sliceT, stackOrder);
        for (ImagePlus result : results) slicedImps.add(result);
      }
      imps = slicedImps;
    }
    return imps;
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

  // -- Helper methods -- miscellaneous --

  private FileInfo createFileInfo() {
    final FileInfo fi = new FileInfo();

    // populate common FileInfo fields
    String idDir = process.getIdLocation() == null ?
      null : process.getIdLocation().getParent();
    if (idDir != null && !idDir.endsWith(File.separator)) {
      idDir += File.separator;
    }
    fi.fileName = process.getIdName();
    fi.directory = idDir;

    // dump OME-XML to ImageJ's description field, if available
    fi.description = process.getOMEXML();

    return fi;
  }

  private boolean[] getPlanesToLoad(int s) {
    final ImageProcessorReader reader = process.getReader();
    final boolean[] load = new boolean[reader.getImageCount()];
    final int cBegin = process.getCBegin(s);
    final int cEnd = process.getCEnd(s);
    final int cStep = process.getCStep(s);
    final int zBegin = process.getZBegin(s);
    final int zEnd = process.getZEnd(s);
    final int zStep = process.getZStep(s);
    final int tBegin = process.getTBegin(s);
    final int tEnd = process.getTEnd(s);
    final int tStep = process.getTStep(s);
    for (int c=cBegin; c<=cEnd; c+=cStep) {
      for (int z=zBegin; z<=zEnd; z+=zStep) {
        for (int t=tBegin; t<=tEnd; t+=tStep) {
          int index = reader.getIndex(z, c, t);
          load[index] = true;
        }
      }
    }
    return load;
  }

  private String constructImageTitle(IFormatReader r,
    String file, String seriesName, boolean groupFiles)
  {
    String[] used = r.getUsedFiles();
    String title = file.substring(file.lastIndexOf(File.separator) + 1);
    if (used.length > 1 && groupFiles) {
      FilePattern fp = new FilePattern(new Location(file));
      title = fp.getPattern();
      if (title == null) {
        title = file;
        if (title.indexOf('.') != -1) {
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
    String sliceLabelPattern = LociPrefs.getSliceLabelPattern();

    String order = r.getDimensionOrder();
    int sizeC = r.getEffectiveSizeC();
    int sizeT = r.getSizeT();
    int sizeZ = r.getSizeZ();
    int seriesCount = r.getImageCount();
    int indexBase = LociPrefs.getSliceLabelBaseIndex();
    int[] coordinates = FormatTools.getZCTCoords(order, sizeZ, sizeC, sizeT, sizeZ*sizeC*sizeT, ndx);

    MetadataStore store = r.getMetadataStore();
    MetadataRetrieve retrieve = store instanceof MetadataRetrieve ? (MetadataRetrieve) store : new DummyMetadata();
    String filename = sliceLabelPattern.replaceAll(FormatTools.SERIES_NUM, String.format("%d", series));

    String imageName = retrieve.getImageName(series);
    if (imageName == null) imageName = "Series" + series;
    filename = sliceLabelPattern;
    
    filename = filename.replaceAll(FormatTools.SERIES_NUM, String.format("%d", series));
    filename = filename.replaceAll(FormatTools.SERIES_NAME, imageName);
    if (sizeC > 1) {
      int[] subC;
      String[] subCTypes;
      Modulo moduloC = r.getModuloC();
      if (moduloC.length() > 1) {
        subC = new int[] {r.getSizeC() / moduloC.length(), moduloC.length()};
        subCTypes = new String[] {moduloC.parentType, moduloC.type};
      } else {
        subC = new int[] {r.getSizeC()};
        subCTypes = new String[] {FormatTools.CHANNEL};
      }
      int[] subCPos = FormatTools.rasterToPosition(subC, coordinates[1]);
      StringBuffer channelString = new StringBuffer();
      for (int i=0; i<subC.length; i++) {
        boolean ch = subCTypes[i] == null || FormatTools.CHANNEL.equals(subCTypes[i]);
        channelString.append(ch ? "c" : subCTypes[i]);
        channelString.append(":");
        channelString.append(subCPos[i] + 1);
        channelString.append("/");
        channelString.append(subC[i]);
        if (i < subC.length - 1) channelString.append(", ");
      }
      filename = filename.replaceAll(FormatTools.CHANNEL_NUM, channelString.toString() + " ");
    }
    else {
      filename = filename.replaceAll(FormatTools.CHANNEL_NUM, "");
    }
    if (sizeZ > 1) {
      filename = filename.replaceAll(FormatTools.Z_NUM, "z:" + String.format("%d", coordinates[0] + 1) + "/" + String.format("%d", sizeZ) + " ");
    }
    else {
      filename = filename.replaceAll(FormatTools.Z_NUM, "");
    }
    if (sizeT > 1) {
      filename = filename.replaceAll(FormatTools.T_NUM, "t:" + String.format("%d", coordinates[2] + 1) + "/" + String.format("%d", sizeT) + " ");
    }
    else {
      filename = filename.replaceAll(FormatTools.T_NUM, "");
    }

    Timestamp timestamp = retrieve.getImageAcquisitionDate(series);
    long stamp = 0;
    String date = null;
    if (timestamp != null) {
      date = timestamp.getValue();
      if (retrieve.getPlaneCount(series) > ndx) {
        Time deltaT = retrieve.getPlaneDeltaT(series, ndx);
        if (deltaT != null) {
          stamp = (long) (deltaT.value(UNITS.SECOND).doubleValue() * 1000);
        }
      }
      stamp += DateTools.getTime(date, DateTools.ISO8601_FORMAT);
    }
    else {
      stamp = System.currentTimeMillis();
    }
    date = DateTools.convertDate(stamp, (int) DateTools.UNIX_EPOCH);

    filename = filename.replaceAll(FormatTools.TIMESTAMP, date);
    return filename;
  }

  private static String[] substringsBetween(String str, String open, String close) {
    if (str == null || open == null || close == null || open.length() == 0 || close.length() == 0) {
      return null;
    }
    int strLen = str.length();
    if (strLen == 0) {
      return new String [0];
    }
    int closeLen = close.length();
    int openLen = open.length();
    List<String> list = new ArrayList<String>();
    int pos = 0;
    while (pos < (strLen - closeLen)) {
      int start = str.indexOf(open, pos);
      if (start < 0) {
        break;
      }
      start += openLen;
      int end = str.indexOf(close, start);
      if (end < 0) {
        break;
      }
      list.add(str.substring(start, end));
      pos = end + closeLen;
    }
    if (list.isEmpty()) {
      return null;
    } 
    return (String[]) list.toArray(new String [list.size()]);
  }

  private static void saveLUTs(ImagePlus imp, List<LUT> luts) {
    // NB: Save individual planar LUTs as properties, for later access.
    // This step is necessary because ImageStack.addSlice only extracts the
    // pixels from the ImageProcessor, and does not preserve the ColorModel.
    // Later, Colorizer can use the LUTs when wrapping into a CompositeImage.
    for (int i=0; i<luts.size(); i++) {
      final LUT lut = luts.get(i);
      if (lut != null) {
        imp.setProperty(PROP_LUT + i, lut);
      }
    }
  }

}
