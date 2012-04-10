//
// JPEG2000Writer.java
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

package loci.formats.out;

import java.io.IOException;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.FormatWriter;
import loci.formats.codec.CompressionType;
import loci.formats.codec.JPEG2000Codec;
import loci.formats.codec.JPEG2000CodecOptions;
import loci.formats.meta.MetadataRetrieve;

/**
 * JPEG2000Writer is the file format writer for JPEG2000 files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/out/JPEG2000Writer.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/out/JPEG2000Writer.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class JPEG2000Writer extends FormatWriter {

  // -- Fields --

  // -- Constructor --

  /** Creates a new instance. */
  public JPEG2000Writer() {
    super("JPEG-2000", "jp2");
    compressionTypes = new String[] {CompressionType.J2K_LOSSY.getCompression(), 
        CompressionType.J2K.getCompression()};
    //The default codec options
    options = JPEG2000CodecOptions.getDefaultOptions();
  }

  // -- IFormatWriter API methods --

  /**
   * @see loci.formats.IFormatWriter#saveBytes(int, byte[], int, int, int, int)
   */
  public void saveBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    checkParams(no, buf, x, y, w, h);
    
    /*
    if (!isFullPlane(x, y, w, h)) {
      throw new FormatException(
        "JPEG2000Writer does not yet support saving image tiles.");
    }
    */
    //MetadataRetrieve retrieve = getMetadataRetrieve();
    //int width = retrieve.getPixelsSizeX(series).getValue().intValue();
    //int height = retrieve.getPixelsSizeY(series).getValue().intValue();
   
    out.write(compressBuffer(no, buf, x, y, w, h));
  }

  /**
   * Compresses the buffer.
   * 
   * @param no the image index within the current file, starting from 0.
   * @param buf the byte array that represents the image tile.
   * @param x the X coordinate of the upper-left corner of the image tile.
   * @param y the Y coordinate of the upper-left corner of the image tile.
   * @param w the width (in pixels) of the image tile.
   * @param h the height (in pixels) of the image tile.
   * @throws FormatException if one of the parameters is invalid.
   * @throws IOException if there was a problem writing to the file.
   */
  public byte[] compressBuffer(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    checkParams(no, buf, x, y, w, h);
    MetadataRetrieve retrieve = getMetadataRetrieve();
    boolean littleEndian =
      !retrieve.getPixelsBinDataBigEndian(series, 0).booleanValue();
    int bytesPerPixel = FormatTools.getBytesPerPixel(
      FormatTools.pixelTypeFromString(
      retrieve.getPixelsType(series).toString()));
    int nChannels = getSamplesPerPixel();

    //To be on the save-side
    if (options == null) options = JPEG2000CodecOptions.getDefaultOptions();
    options = new JPEG2000CodecOptions(options);
    options.width = w;
    options.height = h;
    options.channels = nChannels;
    options.bitsPerSample = bytesPerPixel * 8;
    options.littleEndian = littleEndian;
    options.interleaved = interleaved;
    options.lossless = compression == null || 
    compression.equals(CompressionType.J2K.getCompression());
    options.colorModel = getColorModel();

    return new JPEG2000Codec().compress(buf, options);
  }
    
  /**
   * Overridden to indicate that stacks are not supported. 
   * @see loci.formats.IFormatWriter#canDoStacks() 
   */
  public boolean canDoStacks() { return false; }

  /**
   * Overridden to return the formats supported by the writer.
   * @see loci.formats.IFormatWriter#getPixelTypes(String) 
   */
  public int[] getPixelTypes(String codec) {
    return new int[] {FormatTools.INT8, FormatTools.UINT8, FormatTools.INT16,
      FormatTools.UINT16, FormatTools.INT32, FormatTools.UINT32};
  }

}
