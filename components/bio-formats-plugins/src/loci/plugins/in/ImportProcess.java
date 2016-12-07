/*
 * #%L
 * Bio-Formats Plugins for ImageJ: a collection of ImageJ plugins including the
 * Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
 * Data Browser and Stack Slicer.
 * %%
 * Copyright (C) 2006 - 2016 Open Microscopy Environment:
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

import ij.IJ;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import loci.common.DebugTools;
import loci.common.Location;
import loci.common.ReflectedUniverse;
import loci.common.Region;
import loci.common.StatusEvent;
import loci.common.StatusListener;
import loci.common.StatusReporter;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.ChannelFiller;
import loci.formats.ChannelSeparator;
import loci.formats.ClassList;
import loci.formats.DimensionSwapper;
import loci.formats.FileStitcher;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.MinMaxCalculator;
import loci.formats.TileStitcher;
import loci.formats.in.MetadataLevel;
import loci.formats.meta.IMetadata;
import loci.formats.services.OMEXMLService;
import loci.plugins.BF;
import loci.plugins.util.ImageProcessorReader;
import loci.plugins.util.LociPrefs;
import loci.plugins.util.LuraWave;
import loci.plugins.util.VirtualReader;
import loci.plugins.util.WindowTools;
import ome.xml.model.enums.DimensionOrder;
import ome.xml.model.enums.EnumerationException;


/**
 * Manages the import preparation process.
 *
 * After calling {@link #execute()}, the process will be ready to feed to
 * an {@link ImagePlusReader} to read in the actual {@link ij.ImagePlus}
 * objects.
 */
public class ImportProcess implements StatusReporter {

  // -- Fields --

  private List<StatusListener> listeners = new ArrayList<StatusListener>();

  /** Associated importer options. */
  private ImporterOptions options;

  /** Current step in the import preparation process. */
  private ImportStep step;

  // reader stack, from bottom to top
  private IFormatReader baseReader;
  private ImageReader imageReader;
  private FileStitcher fileStitcher;
  private ChannelFiller channelFiller;
  private ChannelSeparator channelSeparator;
  private DimensionSwapper dimensionSwapper;
  private MinMaxCalculator minMaxCalculator;
  private TileStitcher tileStitcher;
  private VirtualReader virtualReader;
  private ImageProcessorReader reader;

  /** Whether the process has been canceled. */
  private boolean cancel;

  protected String idName;
  protected Location idLoc;

  protected IMetadata meta;
  private String omeXML;

  private ImporterMetadata metadata;

  /** A descriptive label for each series. */
  private String[] seriesLabels;

  // -- Constructors --

  public ImportProcess() throws IOException {
    this(new ImporterOptions());
  }

  public ImportProcess(ImporterOptions options) {
    this.options = options;
  }

  // -- ImportProcess methods --

  /**
   * Performs the import preparation process,
   * notifying status listeners at each step.
   *
   * @return true if the preparation process completed successfully.
   */
  public boolean execute() throws FormatException, IOException {
    step(ImportStep.READER);
    if (cancel) return false;
    initializeReader();

    step(ImportStep.FILE);
    if (cancel) return false;
    initializeFile();

    step(ImportStep.STACK);
    if (cancel) return false;
    initializeStack();

    step(ImportStep.SERIES);
    if (cancel) return false;
    initializeSeries();

    step(ImportStep.DIM_ORDER);
    if (cancel) return false;
    initializeDimOrder();

    step(ImportStep.RANGE);
    if (cancel) return false;
    initializeRange();

    step(ImportStep.CROP);
    if (cancel) return false;
    initializeCrop();

    step(ImportStep.COLORS);
    if (cancel) return false;
    initializeColors();

    step(ImportStep.METADATA);
    if (cancel) return false;
    initializeMetadata();

    step(ImportStep.COMPLETE);
    return true;
  }

  /** Cancels the import process. */
  public void cancel() { this.cancel = true; }

  /** Gets whether the import process was canceled. */
  public boolean wasCanceled() { return cancel; }

  public ImporterOptions getOptions() { return options; }

  public boolean isWindowless() {
    if (options.isWindowless()) return true; // globally windowless
    return baseReader != null && LociPrefs.isWindowless(baseReader);
  }

  public void setSeriesList(String seriesList) {
    final int seriesCount = getSeriesCount();
    options.clearSeries();

    // remove illegal characters
    seriesList = seriesList.replaceAll("[^\\d,\\-]", "");

    // parse series list
    StringTokenizer st = new StringTokenizer(seriesList, ",");
    while (st.hasMoreTokens()) {
      final String token = st.nextToken();
      int dash = token.indexOf('-');
      if (dash < 0) {
        // single number
        try {
          final int s = Integer.parseInt(token) - 1;
          options.setSeriesOn(s, true);
        }
        catch (NumberFormatException exc) {
          // skip invalid series number
        }
      }
      else {
        // numerical range
        final String firstString = token.substring(0, dash);
        final String lastString = token.substring(dash + 1);
        try {
          final int first = Integer.parseInt(firstString) - 1;
          final int last = Integer.parseInt(lastString) - 1;
          for (int s = first; s <= last; s++) {
            if (s >= seriesCount) break; // skip out of bounds series
            options.setSeriesOn(s, true);
          }
        }
        catch (NumberFormatException exc) {
          // skip invalid numerical range
        }
      }
    }
  }

  // -- ImportProcess methods - post-READER --

  /** Valid only after {@link ImportStep#READER}. */
  public IFormatReader getBaseReader() {
    assertStep(ImportStep.READER);
    return baseReader;
  }
  /** Valid only after {@link ImportStep#READER}. */
  public ImageReader getImageReader() {
    assertStep(ImportStep.READER);
    return imageReader;
  }
  /** Valid only after {@link ImportStep#READER}. */
  public String getIdName() {
    assertStep(ImportStep.READER);
    return options.isLocal() ? idName : getImageReader().getCurrentFile();
  }
  /** Valid only after {@link ImportStep#READER}. */
  public Location getIdLocation() {
    assertStep(ImportStep.READER);
    return idLoc;
  }
  /** Valid only after {@link ImportStep#READER}. */
  public IMetadata getOMEMetadata() {
    assertStep(ImportStep.READER);
    return meta;
  }
  /** Valid only after {@link ImportStep#READER}. */
  public String getOMEXML() {
    if (omeXML == null) {
      // NB: Extract the OME-XML once, then keep it cached.
      try {
        ServiceFactory factory = new ServiceFactory();
        OMEXMLService service = factory.getInstance(OMEXMLService.class);
        omeXML = service.getOMEXML(getOMEMetadata());
      }
      catch (DependencyException de) { }
      catch (ServiceException se) { }
    }
    return omeXML;
  }

  // -- ImportProcess methods - post-STACK --

  /** Valid only after {@link ImportStep#STACK}. */
  public FileStitcher getFileStitcher() {
    assertStep(ImportStep.STACK);
    return fileStitcher;
  }
  /** Valid only after {@link ImportStep#STACK}. */
  public ChannelFiller getChannelFiller() {
    assertStep(ImportStep.STACK);
    return channelFiller;
  }
  /** Valid only after {@link ImportStep#STACK}. */
  public ChannelSeparator getChannelSeparator() {
    assertStep(ImportStep.STACK);
    return channelSeparator;
  }
  /** Valid only after {@link ImportStep#STACK}. */
  public DimensionSwapper getDimensionSwapper() {
    assertStep(ImportStep.STACK);
    return dimensionSwapper;
  }
  /** Valid only after {@link ImportStep#STACK}. */
  public MinMaxCalculator getMinMaxCalculator() {
    assertStep(ImportStep.STACK);
    return minMaxCalculator;
  }
  /** Valid only after {@link ImportStep#STACK}. */
  public VirtualReader getVirtualReader() {
    assertStep(ImportStep.STACK);
    return virtualReader;
  }
  /** Valid only after {@link ImportStep#STACK}. */
  public ImageProcessorReader getReader() {
    assertStep(ImportStep.STACK);
    return reader;
  }

  /** Valid only after {@link ImportStep#STACK}. */
  public String getCurrentFile() {
    assertStep(ImportStep.STACK);
    return reader.getCurrentFile();
  }
  /** Valid only after {@link ImportStep#STACK}. */
  public int getSeriesCount() {
    assertStep(ImportStep.STACK);
    return getReader().getSeriesCount();
  }
  /** Valid only after {@link ImportStep#STACK}. */
  public String getSeriesLabel(int s) {
    assertStep(ImportStep.STACK);
    return seriesLabels[s];
  }

  // stackOrder
  /** Valid only after {@link ImportStep#STACK}. */
  public String getStackOrder() {
    assertStep(ImportStep.STACK);
    String stackOrder = options.getStackOrder();
    if (stackOrder == null ||
      stackOrder.equals(ImporterOptions.ORDER_DEFAULT))
    {
      stackOrder = reader.getDimensionOrder();
    }
    return stackOrder;
  }

  // range options
  public int getCBegin(int s) { return options.getCBegin(s); }
  /** Valid only after {@link ImportStep#STACK}. */
  public int getCEnd(int s) {
    assertStep(ImportStep.STACK);
    int cEnd = options.getCEnd(s);
    if (cEnd >= 0) return cEnd;
    reader.setSeries(s);
    return reader.getEffectiveSizeC() - 1;
  }
  public int getCStep(int s) { return options.getCStep(s); }

  public int getZBegin(int s) { return options.getZBegin(s); }
  /** Valid only after {@link ImportStep#STACK}. */
  public int getZEnd(int s) {
    assertStep(ImportStep.STACK);
    int zEnd = options.getZEnd(s);
    if (zEnd >= 0) return zEnd;
    reader.setSeries(s);
    return reader.getSizeZ() - 1;
  }
  public int getZStep(int s) { return options.getZStep(s); }

  public int getTBegin(int s) { return options.getTBegin(s); }
  /** Valid only after {@link ImportStep#STACK}. */
  public int getTEnd(int s) {
    assertStep(ImportStep.STACK);
    int tEnd = options.getTEnd(s);
    if (tEnd >= 0) return tEnd;
    reader.setSeries(s);
    return reader.getSizeT() - 1;
  }
  public int getTStep(int s) { return options.getTStep(s); }

  // crop options
  /** Valid only after {@link ImportStep#STACK}. */
  public Region getCropRegion(int s) {
    assertStep(ImportStep.STACK);
    Region region = options.doCrop() ? options.getCropRegion(s) : null;
    reader.setSeries(s);
    int sizeX = reader.getSizeX(), sizeY = reader.getSizeY();
    if (region == null) {
      // entire image plane is the default region
      region = new Region(0, 0, sizeX, sizeY);
    }
    else {
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
    return region;
  }

  // -- ImportProcess methods - post-SERIES --

  /** Valid only after {@link ImportStep#SERIES}. */
  public int getCCount(int s) {
    assertStep(ImportStep.SERIES);
    if (!options.isSeriesOn(s)) return 0;
    return (getCEnd(s) - getCBegin(s) + getCStep(s)) / getCStep(s);
  }
  /** Valid only after {@link ImportStep#SERIES}. */
  public int getZCount(int s) {
    assertStep(ImportStep.SERIES);
    if (!options.isSeriesOn(s)) return 0;
    return (getZEnd(s) - getZBegin(s) + getZStep(s)) / getZStep(s);
  }
  /** Valid only after {@link ImportStep#SERIES}. */
  public int getTCount(int s) {
    assertStep(ImportStep.SERIES);
    if (!options.isSeriesOn(s)) return 0;
    return (getTEnd(s) - getTBegin(s) + getTStep(s)) / getTStep(s);
  }

  /**
   * Gets a projection of required memory in bytes.
   * Valid only after {@link ImportStep#SERIES}.
   */
  public long getMemoryUsage() {
    final int seriesCount = getSeriesCount();
    long total = 0;
    for (int s=0; s<seriesCount; s++) {
      if (!options.isSeriesOn(s)) continue;
      reader.setSeries(s);
      // determine size of one image plane
      final Region cropRegion = getCropRegion(s);
      final int bpp = FormatTools.getBytesPerPixel(reader.getPixelType());
      final long planeSize = bpp * cropRegion.width * cropRegion.height;
      // determine total number of image planes
      final int cCount = getCCount(s);
      final int zCount = getZCount(s);
      final int tCount = getTCount(s);
      final long planeCount = cCount * zCount * tCount;
      // determine active number of image planes
      final boolean isVirtual = options.isVirtual();
      final long activeChannels = options.isColorModeComposite() ? cCount : 1;
      final long activePlanes = isVirtual ? activeChannels : planeCount;
      // compute total memory footprint for this series
      final long seriesSize = planeSize * activePlanes;
      total += seriesSize;
    }
    return total;
  }

  // -- ImportProcess methods - post-METADATA --

  /** Valid only after {@link ImportStep#METADATA}. */
  public ImporterMetadata getOriginalMetadata() {
    assertStep(ImportStep.METADATA);
    return metadata;
  }

  // -- StatusReporter methods --

  @Override
  public void addStatusListener(StatusListener l) {
    synchronized (listeners) {
      listeners.add(l);
    }
  }

  @Override
  public void removeStatusListener(StatusListener l) {
    synchronized (listeners) {
      listeners.remove(l);
    }
  }

  @Override
  public void notifyListeners(StatusEvent e) {
    synchronized (listeners) {
      for (StatusListener l : listeners) l.statusUpdated(e);
    }
  }

  // -- Helper methods - process steps --

  /** Performed following ImportStep.READER notification. */
  private void initializeReader() throws FormatException, IOException {
    computeNameAndLocation();
    createBaseReader();
  }

  /** Performed following ImportStep.FILE notification. */
  private void initializeFile() throws FormatException, IOException {
    saveDefaults();

    BF.status(options.isQuiet(), "Analyzing " + getIdName());
    baseReader.setMetadataFiltered(true);
    baseReader.setGroupFiles(!options.isUngroupFiles() || options.isGroupFiles());
    if(options != null && !options.showROIs()){
      baseReader.getMetadataOptions().setMetadataLevel(
          MetadataLevel.NO_OVERLAYS);
    }
    baseReader.setId(options.getId());
    
    boolean mustGroup = baseReader.fileGroupOption(options.getId()) == FormatTools.MUST_GROUP;
    options.setMustGroup(mustGroup);
  }

  /** Performed following ImportStep.STACK notification. */
  private void initializeStack() throws FormatException, IOException {
    IFormatReader r = baseReader;

    if (options.isGroupFiles()) {
      r = fileStitcher = new FileStitcher(baseReader);

      // overwrite base filename with file pattern
      String id = options.getId();
      fileStitcher.setId(id);
      fileStitcher.setUsingPatternIds(true);
      fileStitcher.setCanChangePattern(false);
    }
    r.setId(options.getId());

    if (options.isGroupFiles()) {
      options.setId(fileStitcher.getFilePattern().getPattern());
    }

    final byte[][] lut8 = r.get8BitLookupTable();
    final int sizeC = r.getSizeC();
    r = channelFiller = new ChannelFiller(r);
    if (channelFiller.isFilled()) {
      BF.warn(options.isQuiet(), getIdName() + ": index values will be lost");
    }

    r = channelSeparator = new ChannelSeparator(r);
    r = dimensionSwapper = new DimensionSwapper(r);

    if (options.isAutoscale() || FormatTools.isFloatingPoint(r)) {
      r = minMaxCalculator = new MinMaxCalculator(r);
    }
    if (options.doStitchTiles()) {
      r = tileStitcher = new TileStitcher(r);
    }
    r = virtualReader = new VirtualReader(r);
    reader = new ImageProcessorReader(r);
    if(options != null && !options.showROIs()){
      baseReader.getMetadataOptions().setMetadataLevel(
          MetadataLevel.NO_OVERLAYS);
    }
    setId();

    computeSeriesLabels(reader);
  }

  /** Performed following ImportStep.SERIES notification. */
  private void initializeSeries() { }

  /** Performed following ImportStep.DIM_ORDER notification. */
  private void initializeDimOrder() {
    final int seriesCount = getSeriesCount();
    final String stackOrder = getStackOrder();

    for (int s=0; s<seriesCount; s++) {
      reader.setSeries(s);

      // set input order
      String dimOrder = options.getInputOrder(s);
      if (dimOrder != null) dimensionSwapper.swapDimensions(dimOrder);

      // set output order
      getDimensionSwapper().setOutputOrder(stackOrder);
      try {
        DimensionOrder order = DimensionOrder.fromString(stackOrder);
        getOMEMetadata().setPixelsDimensionOrder(order, s);
      }
      catch (EnumerationException e) { }
    }
  }

  /** Performed following ImportStep.RANGE notification. */
  private void initializeRange() { }

  /** Performed following ImportStep.CROP notification. */
  private void initializeCrop() { }

  /** Performed following ImportStep.COLORS notification. */
  private void initializeColors() { }

  /** Performed following ImportStep.METADATA notification. */
  private void initializeMetadata() {
    // only prepend a series name prefix to the metadata keys if multiple
    // series are being opened
    final int seriesCount = getSeriesCount();
    int numEnabled = 0;
    for (int s=0; s<seriesCount; s++) {
      if (options.isSeriesOn(s)) numEnabled++;
    }
    metadata = new ImporterMetadata(getReader(), this, numEnabled > 1);
  }

  // -- Helper methods - ImportStep.READER --

  /** Initializes the idName and idLoc derived values. */
  private void computeNameAndLocation() {
    String id = options.getId();

    idLoc = null;
    idName = id;
    if (options.isLocal()) {
      idLoc = new Location(id);
      idName = idLoc.getName();
    }
  }

  /**
   * Initializes an {@link loci.formats.IFormatReader}
   * according to the current configuration.
   */
  private void createBaseReader() throws FormatException, IOException {
    if (options.isLocal() || options.isHTTP()) {
      BF.status(options.isQuiet(), "Identifying " + idName);
      imageReader = LociPrefs.makeImageReader();
      baseReader = imageReader.getReader(options.getId());
    }
    else if (options.isOMERO()) {
      BF.status(options.isQuiet(), "Establishing server connection");
      try {
        ReflectedUniverse r = new ReflectedUniverse();
        r.exec("import loci.ome.io.OmeroReader");
        r.exec("baseReader = new OmeroReader()");
        ClassList<IFormatReader> classes =
          new ClassList<IFormatReader>(IFormatReader.class);
        r.setVar("classes", classes);
        r.exec("class = baseReader.getClass()");
        r.exec("classes.addClass(class)");
        imageReader = new ImageReader(classes);
        baseReader = imageReader.getReader(options.getId());
      }
      catch (Exception exc) {
        WindowTools.reportException(exc, options.isQuiet(),
          "Sorry, there was a problem communicating with the server.");
        cancel();
        return;
      }
    }
    else {
      WindowTools.reportException(null, options.isQuiet(),
        "Sorry, there has been an internal error: unknown data source");
      cancel();
      return;
    }

    // attach OME-XML metadata store
    Exception exc = null;
    try {
      ServiceFactory factory = new ServiceFactory();
      OMEXMLService service = factory.getInstance(OMEXMLService.class);
      meta = service.createOMEXMLMetadata();
      omeXML = null;
    }
    catch (DependencyException de) { exc = de; }
    catch (ServiceException se) { exc = se; }
    if (exc != null) {
        WindowTools.reportException(exc, options.isQuiet(),
          "Sorry, there was a problem constructing the OME-XML metadata store");
        throw new FormatException(exc);
    }
    baseReader.setMetadataStore(meta);

    BF.status(options.isQuiet(), "");
    DebugTools.enableIJLogging(IJ.debugMode);
  }

  // -- Helper methods - ImportStep.FILE --

  /** Performed following ImportStep.FILE notification. */
  private void saveDefaults() {
    // save options as new defaults
    if (!options.isQuiet()) options.setFirstTime(false);
    options.saveOptions();
  }

  // -- Helper methods -- ImportStep.STACK --

  /**
   * HACK: This method mainly exists to prompt the user for a missing
   * LuraWave license code, in the case of LWF-compressed Flex.
   *
   * @see ImagePlusReader#readProcessors(ImportProcess, int, Region, boolean)
   */
  private void setId() throws FormatException, IOException {
    boolean first = true;
    for (int i=0; i<LuraWave.MAX_TRIES; i++) {
      String code = LuraWave.initLicenseCode();
      try {
        reader.setId(options.getId());
        return;
      }
      catch (FormatException exc) {
        if (options.isQuiet() || options.isWindowless()) throw exc;
        if (!LuraWave.isLicenseCodeException(exc)) throw exc;

        // prompt user for LuraWave license code
        code = LuraWave.promptLicenseCode(code, first);
        if (code == null) throw exc;
        if (first) first = false;
        reader.close();
      }
    }
    throw new FormatException(LuraWave.TOO_MANY_TRIES);
  }

  private void computeSeriesLabels(IFormatReader r) {
    final int seriesCount = r.getSeriesCount();
    seriesLabels = new String[seriesCount];
    for (int i=0; i<seriesCount; i++) {
      r.setSeries(i);
      StringBuffer sb = new StringBuffer();
      sb.append("Series_");
      sb.append((i + 1));
      sb.append(": ");
      String name = getOMEMetadata().getImageName(i);
      if (name != null && name.length() > 0) {
        sb.append(name);
        sb.append(": ");
      }
      sb.append(r.getSizeX());
      sb.append(" x ");
      sb.append(r.getSizeY());
      sb.append("; ");
      sb.append(r.getImageCount());
      sb.append(" plane");
      if (r.getImageCount() > 1) {
        sb.append("s");
        if (r.isOrderCertain()) {
          sb.append(" (");
          boolean first = true;
          if (r.getEffectiveSizeC() > 1) {
            sb.append(r.getEffectiveSizeC());
            sb.append("C");
            first = false;
          }
          if (r.getSizeZ() > 1) {
            if (!first) sb.append(" x ");
            sb.append(r.getSizeZ());
            sb.append("Z");
            first = false;
          }
          if (r.getSizeT() > 1) {
            if (!first) sb.append(" x ");
            sb.append(r.getSizeT());
            sb.append("T");
            first = false;
          }
          sb.append(")");
        }
      }
      seriesLabels[i] = sb.toString();
      //seriesLabels[i] = seriesLabels[i].replaceAll(" ", "_");
    }
  }

  // -- Helper methods - miscellaneous --

  private void step(ImportStep step) {
    this.step = step;
    notifyListeners(new StatusEvent(step.getStep(),
      ImportStep.COMPLETE.getStep(), step.getMessage()));
  }

  private void assertStep(ImportStep importStep) {
    if (step.getStep() <= importStep.getStep()) {
      throw new IllegalStateException("Too early in import process: " +
        "current step is " + step + ", but must be after " + importStep);
    }
  }

}
