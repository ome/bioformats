//
// ImporterReader.java
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

import loci.common.Location;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.ChannelSeparator;
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
 * Helper class for managing Bio-Formats readers and associated metadata.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/in/ImporterPixels.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/in/ImporterPixels.java">SVN</a></dd></dl>
 */
public class ImportProcess {

  // -- Fields --

  /** Associated importer options. */
  protected ImporterOptions options;

  protected String idName;
  protected Location idLoc;

  private IFormatReader baseReader;
  protected ImageProcessorReader reader;
  protected VirtualReader virtualReader;

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

  // -- ImportProcess API methods --

  public ImporterOptions getOptions() { return options; }

  // CTR TEMP
  public void go() {
    computeNameAndLocation();
    createBaseReader();
  }

  // CTR TEMP
  public void prepareStuff() throws FormatException, IOException {
    if (!options.isQuiet()) IJ.showStatus("Analyzing " + getIdName());
    baseReader.setMetadataFiltered(true);
    baseReader.setOriginalMetadataPopulated(true);
    baseReader.setGroupFiles(!options.isUngroupFiles());
    baseReader.setId(options.getId());
  }

  // CTR TEMP
  public void initializeReader() throws FormatException, IOException {
    if (options.isGroupFiles()) {
      FileStitcher fileStitcher = new FileStitcher(baseReader);
      baseReader = fileStitcher;

      // overwrite base filename with file pattern
      String id = options.getId();
      if (id == null) id = getCurrentFile();
      FilePattern fp = fileStitcher.findPattern(id);
      if (fp.isValid()) id = fp.getPattern();
      else id = getCurrentFile();
      options.setId(id);
      fileStitcher.setUsingPatternIds(true);
    }

    baseReader.setId(options.getId());
    if (options.isVirtual() || !options.isMergeChannels() ||
      FormatTools.getBytesPerPixel(baseReader.getPixelType()) != 1)
    {
      baseReader = new ChannelSeparator(baseReader);
    }
    virtualReader = new VirtualReader(baseReader);
    reader = new ImageProcessorReader(virtualReader);
    reader.setId(options.getId());
  }

  public boolean isWindowless() {
    if (options.isWindowless()) return true; // globally windowless
    return baseReader != null && LociPrefs.isWindowless(baseReader);
  }

  // -- Helper methods --

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
    try {
      ServiceFactory factory = new ServiceFactory();
      OMEXMLService service = factory.getInstance(OMEXMLService.class);
      meta = service.createOMEXMLMetadata();
    }
    catch (DependencyException de) { }
    catch (ServiceException se) { }
    baseReader.setMetadataStore(meta);

    if (!options.isQuiet()) IJ.showStatus("");

    Logger root = Logger.getRootLogger();
    root.setLevel(Level.INFO);
    root.addAppender(new IJStatusEchoer());
  }

  // CTR TODO - refactor how ImportProcess works
  public String getIdName() { return idName; }
  public Location getIdLocation() { return idLoc; }
  public String getCurrentFile() { return baseReader.getCurrentFile(); }
  public ImageProcessorReader getReader() { return reader; }
  public IMetadata getOMEMetadata() { return meta; }
  public ImporterMetadata getOriginalMetadata() { return metadata; }
  public int getSeriesCount() { return getReader().getSeriesCount(); }

  public String getSeriesLabel(int s) {
    if (seriesLabels == null) computeSeriesLabels();
    return seriesLabels[s];
  }

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

  // -- CTR TEMP - methods to munge around with state --

  protected void saveDefaults() {
    // save options as new defaults
    if (!options.isQuiet()) options.setFirstTime(false);
    options.saveOptions();
  }

  /** Initializes the ImporterMetadata derived value. */
  protected void initializeMetadata() {
    // only prepend a series name prefix to the metadata keys if multiple
    // series are being opened
    int seriesCount = getSeriesCount();
    int numEnabled = 0;
    for (int s=0; s<seriesCount; s++) {
      if (options.isSeriesOn(s)) numEnabled++;
    }
    metadata = new ImporterMetadata(getReader(), this, numEnabled > 1);
  }

}
