/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.formats.in;

import java.io.IOException;

import loci.common.DateTools;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
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
    CoreMetadata m = core.get(0);

    m.littleEndian = true;
    in.order(isLittleEndian());

    while (in.getFilePointer() < MAX_HEADER_SIZE) {
      readVariable();
    }

    in.seek(pixelsOffset);
    m.sizeX = in.readShort();
    pixelsOffset += 2;

    m.sizeY = getSizeX();
    m.pixelType = FormatTools.UINT16;

    m.sizeZ = 1;
    m.sizeC = 1;
    m.sizeT = 1;
    m.imageCount = 1;
    m.dimensionOrder = "XYZCT";

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
    if (date != null) {
      date = DateTools.formatDate(date, "MMM dd yyyy HH:mm:ssSSS");
      if (date != null) {
        store.setImageAcquisitionDate(new Timestamp(date), 0);
      }
    }

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      store.setImageDescription(comment, 0);

      PositiveFloat sizeX =
        FormatTools.getPhysicalSizeX((double) xSize / getSizeX());
      PositiveFloat sizeY =
        FormatTools.getPhysicalSizeY((double) xSize / getSizeY());

      if (sizeX != null) {
        store.setPixelsPhysicalSizeX(sizeX, 0);
      }
      if (sizeY != null) {
        store.setPixelsPhysicalSizeY(sizeY, 0);
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
