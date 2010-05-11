//
// ImportProcess.java
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

import ij.IJ;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import loci.common.Location;
import loci.common.StatusEvent;
import loci.common.StatusListener;
import loci.common.StatusReporter;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.ChannelSeparator;
import loci.formats.DimensionSwapper;
import loci.formats.FilePattern;
import loci.formats.FileStitcher;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.meta.IMetadata;
import loci.formats.services.OMEXMLService;
import loci.plugins.util.IJStatusEchoer;
import loci.plugins.util.ImageProcessorReader;
import loci.plugins.util.LociPrefs;
import loci.plugins.util.VirtualReader;
import loci.plugins.util.WindowTools;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Manages the import preparation process.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/in/ImportProcess.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/in/ImportProcess.java">SVN</a></dd></dl>
 */
public class ImportProcess implements StatusReporter {

  // -- Fields --

  private List<StatusListener> listeners = new ArrayList<StatusListener>();

  /** Associated importer options. */
  private ImporterOptions options;

  // reader stack, from bottom to top
  private IFormatReader baseReader;
  private FileStitcher fileStitcher;
  private ChannelSeparator channelSeparator;
  private DimensionSwapper dimensionSwapper;
  private VirtualReader virtualReader;
  private ImageProcessorReader reader;
  
  /** Whether the process has been canceled. */
  private boolean cancel;

  protected String idName;
  protected Location idLoc;

  protected IMetadata meta;

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

  public ImporterOptions getOptions() { return options; }
  
  public boolean isWindowless() {
    if (options.isWindowless()) return true; // globally windowless
    return baseReader != null && LociPrefs.isWindowless(baseReader);
  }

  /** Valid only after {@link ImportStep#READER}. */
  public IFormatReader getBaseReader() { return baseReader; }
  /** Valid only after {@link ImportStep#READER}. */
  public String getIdName() { return idName; }
  /** Valid only after {@link ImportStep#READER}. */
  public Location getIdLocation() { return idLoc; }
  /** Valid only after {@link ImportStep#READER}. */
  public IMetadata getOMEMetadata() { return meta; }

  /** Valid only after {@link ImportStep#STACK}. */
  public FileStitcher getFileStitcher() { return fileStitcher; }
  /** Valid only after {@link ImportStep#STACK}. */
  public ChannelSeparator getChannelSeparator() { return channelSeparator; }
  /** Valid only after {@link ImportStep#STACK}. */
  public DimensionSwapper getDimensionSwapper() { return dimensionSwapper; }
  /** Valid only after {@link ImportStep#STACK}. */
  public VirtualReader getVirtualReader() { return virtualReader; }
  /** Valid only after {@link ImportStep#STACK}. */
  public ImageProcessorReader getReader() { return reader; }

  /** Valid only after {@link ImportStep#STACK}. */
  public String getCurrentFile() { return reader.getCurrentFile(); }
  /** Valid only after {@link ImportStep#STACK}. */
  public int getSeriesCount() { return getReader().getSeriesCount(); }
  /** Valid only after {@link ImportStep#STACK}. */
  public String getSeriesLabel(int s) {
    if (seriesLabels == null || s >= seriesLabels.length) return null;
    return seriesLabels[s];
  }

  /** Valid only after {@link ImportStep#METADATA}. */
  public ImporterMetadata getOriginalMetadata() { return metadata; }

  /**
   * Performs the import process, notifying status listeners at each step.
   * 
   * @return true if the process completed successfully.
   */
  public boolean process() throws FormatException, IOException {
    notifyListeners(ImportStep.READER);
    if (cancel) return false;
    initializeReader();

    notifyListeners(ImportStep.FILE);
    if (cancel) return false;
    initializeFile();

    notifyListeners(ImportStep.STACK);
    if (cancel) return false;
    initializeStack();

    notifyListeners(ImportStep.SERIES);
    if (cancel) return false;
    initializeSeries();

    notifyListeners(ImportStep.DIM_ORDER);
    if (cancel) return false;
    initializeDimOrder();

    notifyListeners(ImportStep.RANGE);
    if (cancel) return false;
    initializeRange();

    notifyListeners(ImportStep.CROP);
    if (cancel) return false;
    initializeCrop();

    notifyListeners(ImportStep.METADATA);
    if (cancel) return false;
    initializeMetadata();

    notifyListeners(ImportStep.COMPLETE);
    return true;
  }
  
  /** Cancels the import process. */
  public void cancel() { this.cancel = true; }
  
  /** Gets whether the import process was canceled. */
  public boolean wasCanceled() { return cancel; }

  // -- StatusReporter methods --

  public void addStatusListener(StatusListener l) {
    synchronized (listeners) {
      listeners.add(l);
    }
  }

  public void removeStatusListener(StatusListener l) {
    synchronized (listeners) {
      listeners.remove(l);
    }
  }

  public void notifyListeners(StatusEvent e) {
    synchronized (listeners) {
      for (StatusListener l : listeners) l.statusUpdated(e);
    }
  }

  // -- Helper methods - process steps --

  /** Performed following ImportStep.READER notification. */
  private void initializeReader() {
    computeNameAndLocation();
    createBaseReader();
  }

  /** Performed following ImportStep.FILE notification. */
  private void initializeFile() throws FormatException, IOException {
    saveDefaults();

    if (!options.isQuiet()) IJ.showStatus("Analyzing " + getIdName());
    baseReader.setMetadataFiltered(true);
    baseReader.setOriginalMetadataPopulated(true);
    baseReader.setGroupFiles(!options.isUngroupFiles());
    baseReader.setId(options.getId());
  }

  /** Performed following ImportStep.STACK notification. */
  private void initializeStack() throws FormatException, IOException {
    IFormatReader r = baseReader;

    if (options.isGroupFiles()) {
      r = fileStitcher = new FileStitcher(baseReader);

      // overwrite base filename with file pattern
      String id = options.getId();
      if (id == null) id = getCurrentFile();
      FilePattern fp = fileStitcher.findPattern(id);
      if (fp.isValid()) id = fp.getPattern();
      else id = getCurrentFile();
      options.setId(id);
      fileStitcher.setUsingPatternIds(true);
    }
    r.setId(options.getId());

    if (options.isVirtual() || !options.isMergeChannels() ||
      FormatTools.getBytesPerPixel(r.getPixelType()) != 1)
    {
      r = channelSeparator = new ChannelSeparator(r);
    }
    r = dimensionSwapper = new DimensionSwapper(r);
    r = virtualReader = new VirtualReader(r);
    reader = new ImageProcessorReader(r);
    reader.setId(options.getId());
    
    computeSeriesLabels();
  }

  /** Performed following ImportStep.SERIES notification. */
  private void initializeSeries() { }

  /** Performed following ImportStep.DIM_ORDER notification. */
  private void initializeDimOrder() {
    String dimOrder = options.getInputOrder();
    if (dimOrder != null) dimensionSwapper.swapDimensions(dimOrder);
  }

  /** Performed following ImportStep.RANGE notification. */
  private void initializeRange() { }

  /** Performed following ImportStep.CROP notification. */
  private void initializeCrop() { }

  /** Performed following ImportStep.METADATA notification. */
  private void initializeMetadata() {
    // only prepend a series name prefix to the metadata keys if multiple
    // series are being opened
    int seriesCount = getSeriesCount();
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
//    else if (options.isOMERO()) {
//      // NB: strip out username and password when opening from OMERO
//      StringTokenizer st = new StringTokenizer(id, "?&");
//      StringBuffer idBuf = new StringBuffer();
//      int tokenCount = 0;
//      while (st.hasMoreTokens()) {
//        String token = st.nextToken();
//        if (token.startsWith("username=") || token.startsWith("password=")) {
//          continue;
//        }
//        if (tokenCount == 1) idBuf.append("?");
//        else if (tokenCount > 1) idBuf.append("&");
//        idBuf.append(token);
//        tokenCount++;
//      }
//      idName = idBuf.toString();
//    }
  }

  /**
   * Initializes an {@link loci.formats.IFormatReader}
   * according to the current configuration.
   */
  private void createBaseReader() {
    if (options.isLocal() || options.isHTTP()) {
      if (!options.isQuiet()) IJ.showStatus("Identifying " + idName);
      ImageReader reader = LociPrefs.makeImageReader();
      try { baseReader = reader.getReader(options.getId()); }
      catch (FormatException exc) {
        WindowTools.reportException(exc, options.isQuiet(),
          "Sorry, there was an error reading the file.");
        return;
      }
      catch (IOException exc) {
        WindowTools.reportException(exc, options.isQuiet(),
          "Sorry, there was a I/O problem reading the file.");
        return;
      }
    }
//    else if (options.isOMERO()) {
//      // NB: avoid dependencies on optional loci.ome.io package
//      try {
//        ServiceFactory factory = new ServiceFactory();
//        OMEReaderWriterService service =
//          factory.getInstance(OMEReaderWriterService.class);
//        baseReader = service.newOMEROReader();
//      }
//      catch (DependencyException exc) {
//        WindowTools.reportException(exc, options.isQuiet(),
//          "Sorry, there was a problem constructing the OMERO I/O engine");
//      }
//      if (baseReader == null) return;
//    }
    else {
      WindowTools.reportException(null, options.isQuiet(),
        "Sorry, there has been an internal error: unknown data source");
    }
    Exception exc = null;
    try {
      ServiceFactory factory = new ServiceFactory();
      OMEXMLService service = factory.getInstance(OMEXMLService.class);
      meta = service.createOMEXMLMetadata();
    }
    catch (DependencyException de) { exc = de; }
    catch (ServiceException se) { exc = se; }
    if (exc != null) {
        WindowTools.reportException(exc, options.isQuiet(),
          "Sorry, there was a problem constructing the OME-XML metadata store");
    }
    baseReader.setMetadataStore(meta);

    if (!options.isQuiet()) IJ.showStatus("");

    Logger root = Logger.getRootLogger();
    root.setLevel(Level.INFO);
    root.addAppender(new IJStatusEchoer());
  }
  
  // -- Helper methods - ImportStep.FILE --

  /** Performed following ImportStep.FILE notification. */
  private void saveDefaults() {
    // save options as new defaults
    if (!options.isQuiet()) options.setFirstTime(false);
    options.saveOptions();
  }
  
  // -- Helper methods -- ImportStep.STACK --
  
  /** Initializes the seriesLabels derived value. */
  private void computeSeriesLabels() {
    int seriesCount = getSeriesCount();
    seriesLabels = new String[seriesCount];
    for (int i=0; i<seriesCount; i++) {
      getReader().setSeries(i);
      StringBuffer sb = new StringBuffer();
      sb.append("Series_");
      sb.append((i + 1));
      sb.append(": ");
      String name = getOMEMetadata().getImageName(i);
      if (name != null && name.length() > 0) {
        sb.append(name);
        sb.append(": ");
      }
      sb.append(getReader().getSizeX());
      sb.append(" x ");
      sb.append(getReader().getSizeY());
      sb.append("; ");
      sb.append(getReader().getImageCount());
      sb.append(" plane");
      if (getReader().getImageCount() > 1) {
        sb.append("s");
        if (getReader().isOrderCertain()) {
          sb.append(" (");
          boolean first = true;
          if (getReader().getEffectiveSizeC() > 1) {
            sb.append(getReader().getEffectiveSizeC());
            sb.append("C");
            first = false;
          }
          if (getReader().getSizeZ() > 1) {
            if (!first) sb.append(" x ");
            sb.append(getReader().getSizeZ());
            sb.append("Z");
            first = false;
          }
          if (getReader().getSizeT() > 1) {
            if (!first) sb.append(" x ");
            sb.append(getReader().getSizeT());
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

  private void notifyListeners(ImportStep step) {
    notifyListeners(new StatusEvent(step.getStep(),
      ImportStep.COMPLETE.getStep(), step.getMessage()));
  }

}
