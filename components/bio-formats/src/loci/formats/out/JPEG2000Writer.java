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

import loci.common.RandomAccessOutputStream;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.FormatWriter;
import loci.formats.MetadataTools;
import loci.formats.codec.CodecOptions;
import loci.formats.codec.JPEG2000Codec;
import loci.formats.meta.MetadataRetrieve;

/**
 * JPEG2000Writer is the file format writer for JPEG2000 files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/out/JPEG2000Writer.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/out/JPEG2000Writer.java">SVN</a></dd></dl>
 */
public class JPEG2000Writer extends FormatWriter {

  // -- Fields --

  private RandomAccessOutputStream out;

  // -- Constructor --

  public JPEG2000Writer() {
    super("JPEG-2000", "jp2");
  }

  // -- IFormatWriter API methods --

  /* @see loci.formats.IFormatWriter#saveBytes(byte[], int, boolean, boolean) */
  public void saveBytes(byte[] buf, int series, boolean lastInSeries,
    boolean last) throws FormatException, IOException
  {
    MetadataRetrieve retrieve = getMetadataRetrieve();
    MetadataTools.verifyMinimumPopulated(retrieve, series);
    boolean littleEndian =
      !retrieve.getPixelsBigEndian(series, 0).booleanValue();
    int width = retrieve.getPixelsSizeX(series, 0).intValue();
    int height = retrieve.getPixelsSizeY(series, 0).intValue();
    int bytesPerPixel = FormatTools.getBytesPerPixel(
      FormatTools.pixelTypeFromString(retrieve.getPixelsPixelType(series, 0)));
    Integer channels = retrieve.getLogicalChannelSamplesPerPixel(series, 0);
    if (channels == null) {
      LOGGER.warn("SamplesPerPixel #0 is null.  It is assumed to be 1.");
    }
    int nChannels = channels == null ? 1 : channels.intValue();

    out = new RandomAccessOutputStream(currentId);

    CodecOptions options = new CodecOptions();
    options.width = width;
    options.height = height;
    options.channels = nChannels;
    options.bitsPerSample = bytesPerPixel * 8;
    options.littleEndian = littleEndian;
    options.interleaved = interleaved;

    out.write(new JPEG2000Codec().compress(buf, options));
    out.close();
  }

  /* @see loci.formats.IFormatWriter#canDoStacks() */
  public boolean canDoStacks() { return false; }

  /* @see loci.formats.IFormatWriter#getPixelTypes() */
  public int[] getPixelTypes() {
    return new int[] {FormatTools.INT8, FormatTools.UINT8, FormatTools.INT16,
      FormatTools.UINT16};
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    if (out != null) out.close();
    out = null;
    currentId = null;
  }

}
