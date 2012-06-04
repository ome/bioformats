//
// QuesantReader.java
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

import loci.common.DateTools;
import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.Timestamp;

/**
 * QuesantReader is the file format reader for Quesant .afm files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/QuesantReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/QuesantReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class QuesantReader extends FormatReader {

  // -- Constants --

  public static final int MAX_HEADER_SIZE = 1024;

  // -- Fields --

  private int pixelsOffset;
  private double xSize = 0d;
  private String date = null, comment = null;

  // -- Constructor --

  /** Constructs a new Quesant reader. */
  public QuesantReader() {
    super("Quesant AFM", "afm");
    domains = new String[] {FormatTools.SEM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    in.seek(pixelsOffset);
    readPlane(in, x, y, w, h, buf);
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      pixelsOffset = 0;
      xSize = 0d;
      date = comment = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);

    core[0].littleEndian = true;
    in.order(isLittleEndian());

    while (in.getFilePointer() < MAX_HEADER_SIZE) {
      readVariable();
    }

    in.seek(pixelsOffset);
    core[0].sizeX = in.readShort();
    pixelsOffset += 2;

    core[0].sizeY = getSizeX();
    core[0].pixelType = FormatTools.UINT16;

    core[0].sizeZ = 1;
    core[0].sizeC = 1;
    core[0].sizeT = 1;
    core[0].imageCount = 1;
    core[0].dimensionOrder = "XYZCT";

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
    if (date != null) {
      date = DateTools.formatDate(date, "MMM dd yyyy HH:mm:ssSSS");
      store.setImageAcquisitionDate(new Timestamp(date), 0);
    }

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      store.setImageDescription(comment, 0);
      if (xSize > 0) {
        store.setPixelsPhysicalSizeX(
          new PositiveFloat((double) xSize / getSizeX()), 0);
        store.setPixelsPhysicalSizeY(
          new PositiveFloat((double) xSize / getSizeY()), 0);
      }
      else {
        LOGGER.warn("Expected positive value for PhysicalSize; got {}", xSize);
      }
    }
  }

  // -- Helper methods --

  private void readVariable() throws IOException {
    String code = in.readString(4);
    if (getMetadataOptions().getMetadataLevel() == MetadataLevel.MINIMUM &&
      !code.equals("IMAG"))
    {
      in.skipBytes(4);
      return;
    }

    int offset = in.readInt();
    long fp = in.getFilePointer();
    if (offset <= 0 || offset > in.length()) return;

    in.seek(offset);

    if (code.equals("SDES")) {
      String sdes = in.readCString().trim();
      if (comment == null) comment = sdes;
      else comment += " " + sdes;
    }
    else if (code.equals("DESC")) {
      int length = in.readShort();
      String desc = in.readString(length);
      if (comment == null) comment = desc;
      else comment += " " + desc;
    }
    else if (code.equals("DATE")) {
      date = in.readCString();
    }
    else if (code.equals("IMAG")) {
      pixelsOffset = offset;
    }
    else if (code.equals("HARD")) {
      xSize = in.readFloat();

      float scanRate = in.readFloat();
      float tunnelCurrent = (in.readFloat() * 10) / 32768;

      in.skipBytes(12);

      float integralGain = in.readFloat();
      float proportGain = in.readFloat();
      boolean isSTM = in.readShort() == 10;
      float dynamicRange = in.readFloat();

      addGlobalMeta("Scan rate (Hz)", scanRate);
      addGlobalMeta("Tunnel current", tunnelCurrent);
      addGlobalMeta("Is STM image", isSTM);
      addGlobalMeta("Integral gain", integralGain);
      addGlobalMeta("Proportional gain", proportGain);
      addGlobalMeta("Z dynamic range", dynamicRange);
    }
    in.seek(fp);
  }

}
