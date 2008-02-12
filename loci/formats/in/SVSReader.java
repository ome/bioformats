//
// SVSReader.java
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

import java.io.IOException;
import java.util.*;
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * SVSReader is the file format reader for Aperio SVS TIFF files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/SVSReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/SVSReader.java">SVN</a></dd></dl>
 */
public class SVSReader extends BaseTiffReader {

  // -- Fields --

  private float[] pixelSize;

  // -- Constructor --

  /** Constructs a new SVS reader. */
  public SVSReader() { super("Aperio SVS TIFF", new String[] {"svs"}); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) { return false; }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    if (getSeriesCount() == 1) {
      return super.openBytes(no, buf, x, y, w, h);
    }

    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length, w, h);

    TiffTools.getSamples(ifds[series], in, buf, x, y, w, h);
    return swapIfRequired(buf);
  }

  // -- Internal BaseTiffReader API methods --

  /* @see loci.formats.BaseTiffReader#initStandardMetadata() */
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    pixelSize = new float[ifds.length];
    for (int i=0; i<ifds.length; i++) {
      String comment = TiffTools.getComment(ifds[i]);
      StringTokenizer st = new StringTokenizer(comment, "\n");
      while (st.hasMoreTokens()) {
        StringTokenizer tokens = new StringTokenizer(st.nextToken(), "|");
        while (tokens.hasMoreTokens()) {
          String t = tokens.nextToken();
          if (t.indexOf("=") == -1) addMeta("Comment", t);
          else {
            String key = t.substring(0, t.indexOf("=")).trim();
            String value = t.substring(t.indexOf("=") + 1).trim();
            addMeta("Series " + i + " " + key, value);
            if (key.equals("MPP")) pixelSize[i] = Float.parseFloat(value);
          }
        }
      }
    }

    // repopulate core

    core = new CoreMetadata(ifds.length);

    for (int s=0; s<ifds.length; s++) {
      int p =
        TiffTools.getIFDIntValue(ifds[s], TiffTools.PHOTOMETRIC_INTERPRETATION);
      int samples =
        TiffTools.getIFDIntValue(ifds[s], TiffTools.SAMPLES_PER_PIXEL);
      core.rgb[s] = samples > 1 || p == TiffTools.RGB;

      core.sizeX[s] = TiffTools.getIFDIntValue(ifds[s], TiffTools.IMAGE_WIDTH);
      core.sizeY[s] = TiffTools.getIFDIntValue(ifds[s], TiffTools.IMAGE_LENGTH);
      core.sizeZ[s] = 1;
      core.sizeT[s] = 1;
      core.sizeC[s] = core.rgb[s] ? samples : 1;
      core.littleEndian[s] = TiffTools.isLittleEndian(ifds[s]);
      core.indexed[s] = p == TiffTools.RGB_PALETTE &&
        (get8BitLookupTable() != null || get16BitLookupTable() != null);
      core.imageCount[s] = 1;

      int bitFormat =
        TiffTools.getIFDIntValue(ifds[s], TiffTools.SAMPLE_FORMAT);
      Object bpsObj = TiffTools.getIFDValue(ifds[s], TiffTools.BITS_PER_SAMPLE);
      int bps = 0;
      if (bpsObj instanceof int[]) bps = ((int[]) bpsObj)[0];
      else bps = ((Number) bpsObj).intValue();

      while (bps % 8 != 0) bps++;
      if (bps == 24 || bps == 48) bps /= 3;

      if (bitFormat == 3) {
        core.pixelType[s] = FormatTools.FLOAT;
        core.littleEndian[s] = true;
      }
      else if (bitFormat == 2) {
        switch (bps) {
          case 16:
            core.pixelType[s] = FormatTools.INT16;
            break;
          case 32:
            core.pixelType[s] = FormatTools.INT32;
            break;
          default:
            core.pixelType[s] = FormatTools.INT8;
        }
      }
      else {
        switch (bps) {
          case 16:
            core.pixelType[s] = FormatTools.UINT16;
            break;
          case 32:
            core.pixelType[s] = FormatTools.UINT32;
            break;
          default:
            core.pixelType[s] = FormatTools.UINT8;
        }
      }
    }
    Arrays.fill(core.metadataComplete, true);
    Arrays.fill(core.interleaved, false);
    Arrays.fill(core.falseColor, false);
    Arrays.fill(core.currentOrder, "XYCZT");
  }

  /* @see loci.formats.BaseTiffReader#initMetadataStore() */
  protected void initMetadataStore() {
    super.initMetadataStore();

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());

    for (int i=0; i<core.sizeX.length; i++) {
      store.setImageName("Series " + i, i);
    }
  }

}
