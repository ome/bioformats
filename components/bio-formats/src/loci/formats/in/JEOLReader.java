//
// JEOLReader.java
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

import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * JEOLReader is the file format reader for JEOL files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/JEOLReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/JEOLReader.java">SVN</a></dd></dl>
 */
public class JEOLReader extends FormatReader {

  // -- Constants --

  // -- Fields --

  private long pixelOffset;
  private String parameterFile;

  // -- Constructor --

  /** Constructs a new JEOL reader. */
  public JEOLReader() {
    super("JEOL", new String[] {"dat", "img"});
    domains = new String[] {FormatTools.SEM_DOMAIN};
    suffixSufficient = false;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(String) */
  public boolean isThisType(String name, boolean open) {
    if (checkSuffix(name, "dat")) {
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
    debug("JEOLReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessInputStream(id);
    core[0].littleEndian = true;
    in.order(isLittleEndian());

    parameterFile = id.substring(0, id.lastIndexOf(".")) + ".PAR";
    parameterFile = new Location(parameterFile).getAbsolutePath();
    if (!new Location(parameterFile).exists()) parameterFile = null;

    String magic = in.readString(2);

    if (magic.equals("MG")) {
      in.seek(0x63c);
      core[0].sizeX = in.readInt();
      core[0].sizeY = in.readInt();
      pixelOffset = in.getFilePointer() + 540;
    }
    else if (magic.equals("IM")) {
      int commentLength = in.readShort();
      core[0].sizeX = 1024;
      in.skipBytes(56);
      String comment = in.readString(commentLength);
      pixelOffset = in.getFilePointer();
      core[0].sizeY = (int) ((in.length() - pixelOffset) / getSizeX());
    }
    else {
      core[0].sizeX = 1024;
      core[0].sizeY = 1024;
      pixelOffset = 0;
    }

    core[0].pixelType = FormatTools.UINT8;
    core[0].sizeZ = 1;
    core[0].sizeC = 1;
    core[0].sizeT = 1;
    core[0].imageCount = 1;
    core[0].dimensionOrder = "XYZCT";

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    MetadataTools.populatePixels(store, this);

    MetadataTools.setDefaultCreationDate(store, id, 0);
  }

}
