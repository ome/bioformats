/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
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
import loci.formats.CoreMetadata;
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
  @Override
  public Object openPlane(int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, -1, x, y, w, h);
    BufferedImage img = openBufferedImage(no);
    return AWTImageTools.getSubimage(img, isLittleEndian(), x, y, w, h);
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
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

    CoreMetadata m = core.get(0);

    m.imageCount = numPages;

    m.sizeX = img.getWidth();
    m.sizeY = img.getHeight();
    m.sizeZ = 1;
    m.sizeC = img.getSampleModel().getNumBands();
    m.sizeT = numPages;

    m.rgb = m.sizeC > 1;

    m.dimensionOrder = "XYCZT";
    m.pixelType = AWTImageTools.getPixelType(img);
    m.interleaved = true;
    m.littleEndian = false;
    m.metadataComplete = true;
    m.indexed = false;
    m.falseColor = false;

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
