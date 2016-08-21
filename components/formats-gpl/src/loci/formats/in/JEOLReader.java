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

import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

/**
 * JEOLReader is the file format reader for JEOL files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/JEOLReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/JEOLReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class JEOLReader extends FormatReader {

  // -- Fields --

  private long pixelOffset;
  private String parameterFile;

  // -- Constructor --

  /** Constructs a new JEOL reader. */
  public JEOLReader() {
    super("JEOL", new String[] {"dat", "img", "par"});
    domains = new String[] {FormatTools.SEM_DOMAIN};
    hasCompanionFiles = true;
    suffixSufficient = false;
    datasetDescription = "A single .dat file or an .img file with a " +
      "similarly-named .par file";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(String) */
  public boolean isThisType(String name, boolean open) {
    if (checkSuffix(name, "par") && open) {
      String base = new Location(name).getAbsoluteFile().getAbsolutePath();
      base = base.substring(0, base.lastIndexOf("."));
      String id = base + ".IMG";
      if (!new Location(id).exists()) {
        id = base + ".DAT";
      }
      if (!new Location(id).exists()) {
        return false;
      }
      return true;
    }
    if (checkSuffix(name, "dat") && open) {
      try {
        RandomAccessInputStream stream = new RandomAccessInputStream(name);
        if (stream.length() == (1024 * 1024)) return true;
      }
      catch (IOException e) { }
      return false;
    }
    return super.isThisType(name, open);
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 2;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    String magic = stream.readString(blockLen);
    return magic.equals("MG") || magic.equals("IM");
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  public String[] getSeriesUsedFiles(boolean noPixels) {
    if (noPixels) {
      return parameterFile == null ? null : new String[] {parameterFile};
    }
    String id = new Location(currentId).getAbsolutePath();
    return parameterFile == null ? new String[] {id} :
      new String[] {id, parameterFile};
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    in.seek(pixelOffset);
    readPlane(in, x, y, w, h, buf);
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      pixelOffset = 0;
      parameterFile = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (checkSuffix(id, "par")) {
      String base = new Location(id).getAbsoluteFile().getAbsolutePath();
      base = base.substring(0, base.lastIndexOf("."));
      id = base + ".IMG";
      if (!new Location(id).exists()) {
        id = base + ".DAT";
      }
      if (!new Location(id).exists()) {
        throw new FormatException("Could not find image file.");
      }
    }

    super.initFile(id);
    in = new RandomAccessInputStream(id);
    CoreMetadata m = core.get(0);
    m.littleEndian = true;
    in.order(isLittleEndian());

    parameterFile = id.substring(0, id.lastIndexOf(".")) + ".PAR";
    parameterFile = new Location(parameterFile).getAbsolutePath();
    if (!new Location(parameterFile).exists()) parameterFile = null;

    String magic = in.readString(2);

    if (magic.equals("MG")) {
      in.seek(0x63c);
      m.sizeX = in.readInt();
      m.sizeY = in.readInt();
      pixelOffset = in.getFilePointer() + 540;
    }
    else if (magic.equals("IM")) {
      int commentLength = in.readShort();
      m.sizeX = 1024;
      pixelOffset = in.getFilePointer() + commentLength + 56;
      m.sizeY = (int) ((in.length() - pixelOffset) / getSizeX());
    }
    else {
      m.sizeX = 1024;
      m.sizeY = 1024;
      pixelOffset = 0;
    }

    addGlobalMeta("Pixel data offset", pixelOffset);

    m.pixelType = FormatTools.UINT8;
    m.sizeZ = 1;
    m.sizeC = 1;
    m.sizeT = 1;
    m.imageCount = 1;
    m.dimensionOrder = "XYZCT";

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
  }

}
