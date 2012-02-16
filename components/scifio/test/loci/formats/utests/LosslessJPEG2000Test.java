//
// LosslessJPEG2000Test.java
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
import java.io.IOException;

import loci.common.Location;
import loci.formats.FormatException;
import loci.formats.IFormatWriter;
import loci.formats.ImageReader;
import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;
import loci.formats.out.JPEG2000Writer;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test case which outlines the problems seen in omero:#6728.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/test/loci/formats/utests/LosslessJPEG2000Test.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/test/loci/formats/utests/LosslessJPEG2000Test.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert <melissa at glencoesoftware.com>
 */
public class LosslessJPEG2000Test {

  private static final byte[] PIXELS_8 = new byte[] {119};
  private static final byte[] PIXELS_16 = new byte[] {0, 119};

  private static final String FILE_8 = "8.jp2";
  private static final String FILE_16 = "16.jp2";

  @BeforeMethod
  public void setUp() throws Exception {
    Location.mapId(FILE_8,
      File.createTempFile("test", ".jp2").getAbsolutePath());
    Location.mapId(FILE_16,
      File.createTempFile("test", ".jp2").getAbsolutePath());

    IMetadata metadata8 = MetadataTools.createOMEXMLMetadata();
    MetadataTools.populateMetadata(metadata8, 0, "foo", false, "XYCZT",
      "uint8", 1, 1, 1, 1, 1, 1);
    IFormatWriter writer8 = new JPEG2000Writer();
    writer8.setMetadataRetrieve(metadata8);
    writer8.setId(FILE_8);
    writer8.saveBytes(0, PIXELS_8);
    writer8.close();

    IMetadata metadata16 = MetadataTools.createOMEXMLMetadata();
    MetadataTools.populateMetadata(metadata16, 0, "foo", false, "XYCZT",
      "uint16", 1, 1, 1, 1, 1, 1);
    IFormatWriter writer16 = new JPEG2000Writer();
    writer16.setMetadataRetrieve(metadata16);
    writer16.setId(FILE_16);
    writer16.saveBytes(0, PIXELS_16);
    writer16.close();
  }

  @Test
  public void testEquivalentPixels8Bit() throws Exception {
    ImageReader reader = new ImageReader();
    reader.setId(FILE_8);
    byte[] plane = reader.openBytes(0);
    assertEquals(plane.length, PIXELS_8.length);
    for (int q=0; q<plane.length; q++) {
      assertEquals(plane[q], PIXELS_8[q]);
    }
    reader.close();
  }

  @Test
  public void testEquivalentPixels16Bit() throws Exception {
    ImageReader reader = new ImageReader();
    reader.setId(FILE_16);
    byte[] plane = reader.openBytes(0);
    assertEquals(plane.length, PIXELS_16.length);
    for (int q=0; q<plane.length; q++) {
      assertEquals(plane[q], PIXELS_16[q]);
    }
    reader.close();
  }

}
