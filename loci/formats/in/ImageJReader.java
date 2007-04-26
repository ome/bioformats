//
// ImageJReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.in;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import loci.formats.*;

/**
 * ImageJReader is the superclass for file format readers that use
 * Wayne Rasband's excellent ImageJ program:
 *
 * DICOM, FITS, PGM, JPEG, GIF, LUT, BMP, TIFF, ZIP-compressed TIFF and ROI.
 */
public abstract class ImageJReader extends FormatReader {

  // -- Static fields --

  /** Message produced when attempting to use ImageJ without it installed. */
  private static final String NO_IJ =
    "ImageJ is required to read this file. Please " +
    "obtain ij.jar from http://rsb.info.nih.gov/ij/upgrade/";

  // -- Fields --

  /** Reflection tool for ImageJ calls. */
  private ReflectedUniverse r;

  /** Flag indicating ImageJ is not installed. */
  private boolean noImageJ = false;

  // -- Constructor --

  /** Constructs a new ImageJ reader. */
  public ImageJReader(String name, String suffix) {
    super(name, suffix);
    r = new ReflectedUniverse();

    try {
      r.exec("import ij.ImagePlus");
      r.exec("import ij.ImageStack");
      r.exec("import ij.io.Opener");
      r.exec("import ij.process.ImageProcessor");
      r.exec("opener = new Opener()");
      r.setVar("false", false);
    }
    catch (Throwable t) {
      noImageJ = true;
    }
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) { return false; }

  /* @see loci.formats.IFormatReader#openBytes(int) */
  public byte[] openBytes(int no) throws FormatException, IOException {
    byte[] b = ImageTools.getBytes(openImage(no), false, no);
    return b;
  }

  /* @see loci.formats.IFormatReader#openImage(int) */
  public BufferedImage openImage(int no)
    throws FormatException, IOException
  {
    if (no < 0 || no >= getImageCount()) {
      throw new FormatException("Invalid image number: " + no);
    }

    if (noImageJ) throw new FormatException(NO_IJ);

    try {
      Location file = new Location(currentId);
      r.setVar("dir", file.getParent() + System.getProperty("file.separator"));
      r.setVar("name", file.getName());
      synchronized (ImageJReader.class) {
        try {
          r.exec("state = Opener.getOpenUsingPlugins()");
          r.exec("Opener.setOpenUsingPlugins(false)");
        }
        catch (ReflectException exc) {
          // probably ImageJ version < 1.38f
        }
        r.exec("image = opener.openImage(dir, name)");
        try {
          r.exec("Opener.setOpenUsingPlugins(state)");
        }
        catch (ReflectException exc) {
          // probably ImageJ version < 1.38f
        }
      }
      r.exec("size = image.getStackSize()");
      Image img = (Image) r.exec("image.getImage()");

      BufferedImage b = ImageTools.makeBuffered(img);
      return b;
    }
    catch (ReflectException exc) {
      throw new FormatException(exc);
    }
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException { }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException { }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("ImageJReader.initFile(" + id + ")");
    super.initFile(id);

    status("Populating metadata");
    core.imageCount[0] = 1;
    BufferedImage img = openImage(0);

    core.sizeX[0] = img.getWidth();
    core.sizeY[0] = img.getHeight();

    core.rgb[0] = img.getRaster().getNumBands() > 1;

    core.sizeZ[0] = 1;
    core.sizeC[0] = core.rgb[0] ? 3 : 1;
    core.sizeT[0] = 1;
    core.currentOrder[0] = "XYCZT";
    core.pixelType[0] = ImageTools.getPixelType(img);
    core.interleaved[0] = true;
    core.littleEndian[0] = false;

    // populate the metadata store
    MetadataStore store = getMetadataStore();
    store.setPixels(
      new Integer(core.sizeX[0]),
      new Integer(core.sizeY[0]),
      new Integer(core.sizeZ[0]),
      new Integer(core.sizeC[0]),
      new Integer(core.sizeT[0]),
      new Integer(core.pixelType[0]),
      new Boolean(!core.littleEndian[0]),
      core.currentOrder[0],
      null,
      null);
    for (int i=0; i<core.sizeC[0]; i++) {
      store.setLogicalChannel(i, null, null, null, null, null, null, null);
    }
  }

}
