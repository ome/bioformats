//
// SixteenBitLosslessJPEG2000Test.java
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

package loci.formats.utests;

import static org.testng.AssertJUnit.*;

import java.io.File;
import java.util.ArrayList;

import loci.common.DataTools;
import loci.common.Location;
import loci.formats.IFormatWriter;
import loci.formats.ImageReader;
import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;
import loci.formats.out.JPEG2000Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test case which outlines the problems seen in omero:#6728.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/test/loci/formats/utests/SixteenBitLosslessJPEG2000Test.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/test/loci/formats/utests/SixteenBitLosslessJPEG2000Test.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert <melissa at glencoesoftware.com>
 */
public class SixteenBitLosslessJPEG2000Test {

  private static final Logger LOGGER =
    LoggerFactory.getLogger(SixteenBitLosslessJPEG2000Test.class);

  private ArrayList<String> files = new ArrayList<String>();
  private byte[][] pixels = new byte[65536][];

  @BeforeMethod
  public void setUp() throws Exception {
    for (short v=Short.MIN_VALUE; v<Short.MAX_VALUE; v++) {
      int index = v + Short.MAX_VALUE + 1;
      pixels[index] = DataTools.shortToBytes(v, false);

      String file = index + ".jp2";
      Location.mapId(
        file, File.createTempFile("test", ".jp2").getAbsolutePath());
      files.add(file);

      IMetadata metadata16 = MetadataTools.createOMEXMLMetadata();
      MetadataTools.populateMetadata(metadata16, 0, "foo", false, "XYCZT",
        "uint16", 1, 1, 1, 1, 1, 1);
      IFormatWriter writer16 = new JPEG2000Writer();
      writer16.setMetadataRetrieve(metadata16);
      writer16.setId(file);
      writer16.saveBytes(0, pixels[index]);
      writer16.close();
    }
  }

  @Test
  public void testLosslessPixels() throws Exception {
    int failureCount = 0;
    for (int i=0; i<files.size(); i++) {
      ImageReader reader = new ImageReader();
      reader.setId(files.get(i));
      byte[] plane = reader.openBytes(0);
      for (int q=0; q<plane.length; q++) {
        if (plane[q] != pixels[i][q]) {
          LOGGER.debug("FAILED on {}",
            DataTools.bytesToShort(pixels[i], false));
          failureCount++;
          break;
        }
      }
      reader.close();
    }
    assertEquals(failureCount, 0);
  }

}
