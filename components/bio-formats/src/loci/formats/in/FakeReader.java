//
// FakeReader.java
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

import loci.common.DataTools;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * FakeReader is the file format reader for faking input data.
 * It is mainly useful for testing.
 * <p>Examples:<ul>
 *  <li>showinf 'multi-series&amp;series=11&amp;sizeZ=3&amp;sizeC=5&amp;sizeT=7&amp;sizeY=50.fake' -series 9</li>
 *  <li>showinf '8bit-signed&amp;pixelType=int8&amp;sizeZ=3&amp;sizeC=5&amp;sizeT=7&amp;sizeY=50.fake'</li>
 *  <li>showinf '8bit-unsigned&amp;pixelType=uint8&amp;sizeZ=3&amp;sizeC=5&amp;sizeT=7&amp;sizeY=50.fake'</li>
 *  <li>showinf '16bit-signed&amp;pixelType=int16&amp;sizeZ=3&amp;sizeC=5&amp;sizeT=7&amp;sizeY=50.fake'</li>
 *  <li>showinf '16bit-unsigned&amp;pixelType=uint16&amp;sizeZ=3&amp;sizeC=5&amp;sizeT=7&amp;sizeY=50.fake'</li>
 *  <li>showinf '32bit-signed&amp;pixelType=int32&amp;sizeZ=3&amp;sizeC=5&amp;sizeT=7&amp;sizeY=50.fake'</li>
 *  <li>showinf '32bit-unsigned&amp;pixelType=uint32&amp;sizeZ=3&amp;sizeC=5&amp;sizeT=7&amp;sizeY=50.fake'</li>
 *  <li>showinf '32bit-floating&amp;pixelType=float&amp;sizeZ=3&amp;sizeC=5&amp;sizeT=7&amp;sizeY=50.fake'</li>
 *  <li>showinf '64bit-floating&amp;pixelType=double&amp;sizeZ=3&amp;sizeC=5&amp;sizeT=7&amp;sizeY=50.fake'</li>
 * </ul></p>
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/FakeReader.java">Trac</a>
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/FakeReader.java">SVN</a></dd></dl>
 */
public class FakeReader extends FormatReader {

  // -- Constants --

  private static final String TOKEN_SEPARATOR = "&";
  private static final int BOX_SIZE = 10;

  // -- Constructor --

  /** Constructs a new fake reader. */
  public FakeReader() { super("Simulated data", "fake"); }

  // -- IFormatReader API methods --

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int series = getSeries();
    int pixelType = getPixelType();
    int bpp = FormatTools.getBytesPerPixel(pixelType);
    boolean signed = FormatTools.isSigned(pixelType);
    boolean little = isLittleEndian();

    int[] zct = getZCTCoords(no);
    int zIndex = zct[0], cIndex = zct[1], tIndex = zct[2];

    for (int r=0; r<h; r++) {
      int yy = y + r;
      for (int c=0; c<w; c++) {
        int index = bpp * (w * r + c);
        int xx = x + c;

        // encode various information into the image plane
        long pixel = signed ? -xx : xx;
        if (yy < BOX_SIZE) {
          int grid = xx / BOX_SIZE;
          switch (grid) {
            case 0:
              pixel = series;
              break;
            case 1:
              pixel = no;
              break;
            case 2:
              pixel = zIndex;
              break;
            case 3:
              pixel = cIndex;
              break;
            case 4:
              pixel = tIndex;
              break;
          }
        }

        // if applicable, convert value to raw IEEE floating point bits
        switch (pixelType) {
          case FormatTools.FLOAT:
            pixel = Float.floatToIntBits(pixel);
            break;
          case FormatTools.DOUBLE:
            pixel = Double.doubleToLongBits(pixel);
            break;
        }
        DataTools.unpackBytes(pixel, buf, index, bpp, little);
      }
    }

    return buf;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    String noExt = id.substring(0, id.lastIndexOf("."));
    String[] tokens = noExt.split(TOKEN_SEPARATOR);

    String name = null;
    int sizeX = 512;
    int sizeY = 512;
    int sizeZ = 1;
    int sizeC = 1;
    int sizeT = 1;
    int thumbSizeX = 128;
    int thumbSizeY = 128;
    int pixelType = FormatTools.UINT8;
    int rgb = 1;
    int[] cLengths = null;
    String[] cTypes = null;
    String dimOrder = "XYZCT";
    boolean orderCertain = true;
    boolean little = true;
    boolean interleaved = false;
    boolean indexed = false;
    boolean falseColor = false;
    boolean metadataComplete = true;
    boolean thumbnail = false;

    int seriesCount = 1;

    for (String token : tokens) {
      if (name == null) {
        // first token is the image name
        name = token;
        continue;
      }
      int equals = token.indexOf("=");
      if (equals < 0) {
        LOGGER.warn("ignoring token: {}", token);
        continue;
      }
      String key = token.substring(0, equals);
      String value = token.substring(equals + 1);

      boolean bool = value.equals("true");
      int num = -1;
      try { num = Integer.parseInt(value); }
      catch (NumberFormatException exc) { }

      if (key.equals("sizeX")) sizeX = num;
      else if (key.equals("sizeY")) sizeY = num;
      else if (key.equals("sizeZ")) sizeZ = num;
      else if (key.equals("sizeC")) sizeC = num;
      else if (key.equals("sizeT")) sizeT = num;
      else if (key.equals("thumbSizeX")) thumbSizeX = num;
      else if (key.equals("thumbSizeY")) thumbSizeY = num;
      else if (key.equals("pixelType")) {
        pixelType = FormatTools.pixelTypeFromString(value);
      }
      else if (key.equals("rgb")) rgb = num;
      else if (key.equals("dimOrder")) dimOrder = value.toUpperCase();
      else if (key.equals("orderCertain")) orderCertain = bool;
      else if (key.equals("little")) little = bool;
      else if (key.equals("interleaved")) interleaved = bool;
      else if (key.equals("indexed")) indexed = bool;
      else if (key.equals("falseColor")) falseColor = bool;
      else if (key.equals("metadataComplete")) metadataComplete = bool;
      else if (key.equals("thumbnail")) thumbnail = bool;
      else if (key.equals("series")) seriesCount = num;
    }

    int effSizeC = sizeC / rgb;

    core = new CoreMetadata[seriesCount];
    for (int s=0; s<seriesCount; s++) {
      core[s] = new CoreMetadata();
      core[s].sizeX = sizeX;
      core[s].sizeY = sizeY;
      core[s].sizeZ = sizeZ;
      core[s].sizeC = sizeC;
      core[s].sizeT = sizeT;
      core[s].thumbSizeX = thumbSizeX;
      core[s].thumbSizeY = thumbSizeY;
      core[s].pixelType = pixelType;

      core[s].imageCount = sizeZ * effSizeC * sizeT;
      core[s].rgb = rgb > 1;
      core[s].dimensionOrder = dimOrder;
      core[s].orderCertain = orderCertain;
      core[s].littleEndian = little;
      core[s].interleaved = interleaved;
      core[s].indexed = indexed;
      core[s].falseColor = falseColor;
      core[s].metadataComplete = metadataComplete;
      core[s].thumbnail = thumbnail;
    }

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    MetadataTools.populatePixels(store, this);
    for (int s=0; s<seriesCount; s++) {
      String imageName = s > 0 ? name + " " + (s + 1) : name;
      store.setImageName(imageName, s);
      MetadataTools.setDefaultCreationDate(store, id, s);
    }
  }

}
