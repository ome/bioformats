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

package loci.plugins.importer;

import ij.IJ;

import java.io.IOException;
import java.util.StringTokenizer;

import loci.common.Location;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.ChannelSeparator;
import loci.formats.FileStitcher;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.meta.IMetadata;
import loci.formats.services.OMEReaderWriterService;
import loci.formats.services.OMEXMLService;
import loci.plugins.util.IJStatusEchoer;
import loci.plugins.util.ImageProcessorReader;
import loci.plugins.util.LociPrefs;
import loci.plugins.util.VirtualReader;
import loci.plugins.util.WindowTools;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Helper class for reading image data.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/importer/ImporterReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/importer/ImporterReader.java">SVN</a></dd></dl>
 */
public class ImporterReader {

  // -- Fields --

  /** Associated importer options. */
  protected ImporterOptions options;

  protected String idName;
  protected Location idLoc;

  protected String currentFile;

  protected ImageProcessorReader r;
  protected VirtualReader virtualReader;

  protected IMetadata meta;

  private IFormatReader baseReader;

  // -- Constructors --

  public ImporterReader() throws IOException {
    this(new ImporterOptions());
  }

  public ImporterReader(ImporterOptions options) {
    this.options = options;
    computeNameAndLocation();
    createBaseReader();
  }

  // -- ImporterReader API methods --

  // CTR TEMP
  public void prepareStuff() throws FormatException, IOException {
    baseReader.setMetadataFiltered(true);
    baseReader.setOriginalMetadataPopulated(true);
    baseReader.setGroupFiles(!options.isUngroupFiles());
    baseReader.setId(options.getId());
    currentFile = baseReader.getCurrentFile();
  }

  // CTR TEMP
  /** Initializes the ImagePlusReader derived value. */
  public void initializeReader() throws FormatException, IOException {
    if (options.isGroupFiles()) baseReader = new FileStitcher(baseReader, true);
    baseReader.setId(options.getId());
    if (options.isVirtual() || !options.isMergeChannels() ||
      FormatTools.getBytesPerPixel(baseReader.getPixelType()) != 1)
    {
      baseReader = new ChannelSeparator(baseReader);
    }
    virtualReader = new VirtualReader(baseReader);
    r = new ImageProcessorReader(virtualReader);
    r.setId(options.getId());
  }

  public boolean isWindowless() {
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
    else if (options.isOME() || options.isOMERO()) {
      // NB: strip out username and password when opening from OME/OMERO
      StringTokenizer st = new StringTokenizer(id, "?&");
      StringBuffer idBuf = new StringBuffer();
      int tokenCount = 0;
      while (st.hasMoreTokens()) {
        String token = st.nextToken();
        if (token.startsWith("username=") || token.startsWith("password=")) {
          continue;
        }
        if (tokenCount == 1) idBuf.append("?");
        else if (tokenCount > 1) idBuf.append("&");
        idBuf.append(token);
        tokenCount++;
      }
      idName = idBuf.toString();
    }
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
    else if (options.isOMERO()) {
      // NB: avoid dependencies on optional loci.ome.io package
      try {
        ServiceFactory factory = new ServiceFactory();
        OMEReaderWriterService service =
          factory.getInstance(OMEReaderWriterService.class);
        baseReader = service.newOMEROReader();
      }
      catch (DependencyException exc) {
        WindowTools.reportException(exc, options.isQuiet(),
          "Sorry, there was a problem constructing the OMERO I/O engine");
      }
      if (baseReader == null) return;
    }
    else if (options.isOME()) {
      // NB: avoid dependencies on optional loci.ome.io package
      try {
        ServiceFactory factory = new ServiceFactory();
        OMEReaderWriterService service =
          factory.getInstance(OMEReaderWriterService.class);
        baseReader = service.newOMEReader();
      }
      catch (DependencyException de) {
        WindowTools.reportException(de, options.isQuiet(),
          "Sorry, there was a problem constructing the OME I/O engine");
      }
      if (baseReader == null) return;
    }
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

}
