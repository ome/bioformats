//
// SVSReader.java
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

import java.io.IOException;
import java.util.Hashtable;
import java.util.StringTokenizer;

import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.TiffTools;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;

/**
 * SVSReader is the file format reader for Aperio SVS TIFF files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/SVSReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/SVSReader.java">SVN</a></dd></dl>
 */
public class SVSReader extends BaseTiffReader {

  // -- Fields --

  private float[] pixelSize;

  // -- Constructor --

  /** Constructs a new SVS reader. */
  public SVSReader() { super("Aperio SVS", new String[] {"svs"}); }

  // -- IFormatReader API methods --

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    if (getSeriesCount() == 1) {
      return super.openBytes(no, buf, x, y, w, h);
    }
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);
    TiffTools.getSamples(ifds.get(series), in, buf, x, y, w, h);
    return buf;
  }

  // -- Internal BaseTiffReader API methods --

  /* @see loci.formats.BaseTiffReader#initStandardMetadata() */
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    core = new CoreMetadata[ifds.size()];

    pixelSize = new float[core.length];
    for (int i=0; i<core.length; i++) {
      setSeries(i);
      core[i] = new CoreMetadata();

      String comment = TiffTools.getComment(ifds.get(i));
      StringTokenizer st = new StringTokenizer(comment, "\n");
      while (st.hasMoreTokens()) {
        StringTokenizer tokens = new StringTokenizer(st.nextToken(), "|");
        while (tokens.hasMoreTokens()) {
          String t = tokens.nextToken();
          if (t.indexOf("=") == -1) addGlobalMeta("Comment", t);
          else {
            String key = t.substring(0, t.indexOf("=")).trim();
            String value = t.substring(t.indexOf("=") + 1).trim();
            addSeriesMeta(key, value);
            if (key.equals("MPP")) pixelSize[i] = Float.parseFloat(value);
          }
        }
      }
    }
    setSeries(0);

    // repopulate core metadata

    for (int s=0; s<core.length; s++) {
      IFD ifd = ifds.get(s);
      int p = TiffTools.getPhotometricInterpretation(ifd);
      int samples = TiffTools.getSamplesPerPixel(ifd);
      core[s].rgb = samples > 1 || p == TiffTools.RGB;

      core[s].sizeX = (int) TiffTools.getImageWidth(ifd);
      core[s].sizeY = (int) TiffTools.getImageLength(ifd);
      core[s].sizeZ = 1;
      core[s].sizeT = 1;
      core[s].sizeC = core[s].rgb ? samples : 1;
      core[s].littleEndian = TiffTools.isLittleEndian(ifd);
      core[s].indexed = p == TiffTools.RGB_PALETTE &&
        (get8BitLookupTable() != null || get16BitLookupTable() != null);
      core[s].imageCount = 1;
      core[s].pixelType = TiffTools.getPixelType(ifd);
      core[s].metadataComplete = true;
      core[s].interleaved = false;
      core[s].falseColor = false;
      core[s].dimensionOrder = "XYCZT";
    }
  }

  /* @see loci.formats.BaseTiffReader#initMetadataStore() */
  protected void initMetadataStore() throws FormatException {
    super.initMetadataStore();

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());

    for (int i=0; i<getSeriesCount(); i++) {
      store.setImageName("Series " + (i + 1), i);
    }
  }

}
