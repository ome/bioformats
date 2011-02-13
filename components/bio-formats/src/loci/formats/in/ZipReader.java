//
// ZipReader.java
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
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.ZipHandle;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.ImageReader;

/**
 * Reader for Zip files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/ZipReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/ZipReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class ZipReader extends FormatReader {

  // -- Fields --

  private ImageReader reader;

  // -- Constructor --

  public ZipReader() {
    super("Zip", "zip");
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    return reader.get8BitLookupTable();
  }

  /* @see loci.formats.IFormatReader#get16BitLookupTable() */
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    return reader.get16BitLookupTable();
  }

  /* @see loci.formats.IFormatReader#setGroupFiles(boolean) */
  public void setGroupFiles(boolean groupFiles) {
    super.setGroupFiles(groupFiles);
    if (reader != null) reader.setGroupFiles(groupFiles);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    return reader.openBytes(no, buf, x, y, w, h);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (reader != null) reader.close(fileOnly);
    if (!fileOnly) reader = null;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    reader = new ImageReader();

    reader.setMetadataOptions(getMetadataOptions());
    reader.setMetadataFiltered(isMetadataFiltered());
    reader.setOriginalMetadataPopulated(isOriginalMetadataPopulated());
    reader.setNormalized(isNormalized());
    reader.setMetadataStore(getMetadataStore());

    in = new RandomAccessInputStream(id);

    ZipInputStream zip = new ZipInputStream(in);
    while (true) {
      ZipEntry ze = zip.getNextEntry();
      if (ze == null) break;
      ZipHandle handle = new ZipHandle(id, ze);
      Location.mapFile(ze.getName(), handle);
    }

    ZipHandle base = new ZipHandle(id);
    reader.setId(base.getEntryName());

    metadataStore = reader.getMetadataStore();
    core = reader.getCoreMetadata();
    metadata = reader.getGlobalMetadata();
  }

}
