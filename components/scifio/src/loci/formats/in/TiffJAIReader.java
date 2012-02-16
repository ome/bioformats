//
// TiffJAIReader.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

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

package loci.formats.in;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import loci.common.FileHandle;
import loci.common.IRandomAccess;
import loci.common.Location;
import loci.common.ReflectException;
import loci.common.ReflectedUniverse;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.MissingLibraryException;
import loci.formats.gui.AWTImageTools;
import loci.formats.meta.MetadataStore;

/**
 * TiffJAIReader is a file format reader for TIFF images. It uses the
 * Java Advanced Imaging library (javax.media.jai) to read the data.
 *
 * Much of this code was adapted from
 * <a href="http://java.sun.com/products/java-media/jai/forDevelopers/samples/MultiPageRead.java">this example</a>.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/TiffJAIReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/TiffJAIReader.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class TiffJAIReader extends BIFormatReader {

  // -- Constants --

  private static final String NO_JAI_MSG =
    "Java Advanced Imaging (JAI) is required to read some TIFF files. " +
    "Please install JAI from https://jai.dev.java.net/";

  // -- Fields --

  /** Reflection tool for JAI calls. */
  protected ReflectedUniverse r;

  // -- Constructor --

  /** Constructs a new TIFF reader that uses Java Image I/O. */
  public TiffJAIReader() {
    super("Tagged Image File Format", TiffReader.TIFF_SUFFIXES);
    domains = new String[] {FormatTools.GRAPHICS_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#openPlane(int, int, int, int, int int) */
  public Object openPlane(int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, -1, x, y, w, h);
    BufferedImage img = openBufferedImage(no);
    return AWTImageTools.getSubimage(img, isLittleEndian(), x, y, w, h);
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    LOGGER.info("Checking for JAI");
    try {
      r = new ReflectedUniverse();
      r.exec("import javax.media.jai.NullOpImage");
      r.exec("import javax.media.jai.OpImage");
      r.exec("import com.sun.media.jai.codec.FileSeekableStream");
      r.exec("import com.sun.media.jai.codec.ImageDecoder");
      r.exec("import com.sun.media.jai.codec.ImageCodec");
    }
    catch (ReflectException exc) {
      throw new MissingLibraryException(NO_JAI_MSG, exc);
    }

    super.initFile(id);

    LOGGER.info("Reading movie dimensions");

    // map Location to File or RandomAccessFile, if possible
    IRandomAccess ira = Location.getMappedFile(id);
    if (ira != null) {
      if (ira instanceof FileHandle) {
        FileHandle fh = (FileHandle) ira;
        r.setVar("file", fh.getRandomAccessFile());
      }
      else {
        throw new FormatException(
          "Unsupported handle type" + ira.getClass().getName());
      }
    }
    else {
      String mapId = Location.getMappedId(id);
      File file = new File(mapId);
      if (file.exists()) {
        r.setVar("file", file);
      }
      else throw new FileNotFoundException(id);
    }
    r.setVar("tiff", "tiff");
    r.setVar("param", null);

    // create TIFF decoder
    int numPages;
    try {
      r.exec("s = new FileSeekableStream(file)");
      r.exec("dec = ImageCodec.createImageDecoder(tiff, s, param)");
      numPages = ((Integer) r.exec("dec.getNumPages()")).intValue();
    }
    catch (ReflectException exc) {
      throw new FormatException(exc);
    }
    if (numPages < 0) {
      throw new FormatException("Invalid page count: " + numPages);
    }

    // decode first image plane
    BufferedImage img = openBufferedImage(0);
    if (img == null) throw new FormatException("Invalid image stream");

    LOGGER.info("Populating metadata");

    core[0].imageCount = numPages;

    core[0].sizeX = img.getWidth();
    core[0].sizeY = img.getHeight();
    core[0].sizeZ = 1;
    core[0].sizeC = img.getSampleModel().getNumBands();
    core[0].sizeT = numPages;

    core[0].rgb = core[0].sizeC > 1;

    core[0].dimensionOrder = "XYCZT";
    core[0].pixelType = AWTImageTools.getPixelType(img);
    core[0].interleaved = true;
    core[0].littleEndian = false;
    core[0].metadataComplete = true;
    core[0].indexed = false;
    core[0].falseColor = false;

    // populate the metadata store
    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
  }

  /** Obtains a BufferedImage from the given data source using JAI. */
  protected BufferedImage openBufferedImage(int no) throws FormatException {
    r.setVar("no", no);
    RenderedImage img;
    try {
      r.exec("img = dec.decodeAsRenderedImage(no)");
      img = (RenderedImage)
        r.exec("new NullOpImage(img, null, OpImage.OP_IO_BOUND, null)");
    }
    catch (ReflectException exc) {
      throw new FormatException(exc);
    }
    return AWTImageTools.convertRenderedImage(img);
  }

}
