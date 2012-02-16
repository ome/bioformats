//
// OMETiffWriterBigTiffLargeImageWidthTest.java
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
package loci.formats.utests.tiff;

import static org.testng.AssertJUnit.*;

import java.io.File;

import loci.common.services.ServiceFactory;
import loci.formats.in.TiffReader;
import loci.formats.ome.OMEXMLMetadata;
import loci.formats.out.OMETiffWriter;
import loci.formats.services.OMEXMLService;

import ome.xml.model.enums.DimensionOrder;
import ome.xml.model.enums.PixelType;
import ome.xml.model.primitives.PositiveInteger;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/test/loci/formats/utests/tiff/OMETiffWriterBigTiffLargeImageWidthTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/test/loci/formats/utests/tiff/OMETiffWriterBigTiffLargeImageWidthTest.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author callan
 */
public class OMETiffWriterBigTiffLargeImageWidthTest {

  public static final int SIZE_X = 139256;

  public static final int SIZE_Y = 4;

  public static final int SIZE_Z = 100;

  public static final int SIZE_C = 1;

  public static final int SIZE_T = 20;

  private static final byte[] buf = new byte[] {
    0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
    0x08, 0x09, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15
  };

  private File target;

  private OMEXMLMetadata ms;

  @BeforeClass
  public void setUp() throws Exception {
    target = File.createTempFile("OMETiffWriterTest", ".ome.tiff");

    ServiceFactory sf = new ServiceFactory();
    OMEXMLService service = sf.getInstance(OMEXMLService.class);
    ms = service.createOMEXMLMetadata();
    ms.setImageID("Image:1", 0);
    ms.setPixelsID("Pixels:1", 0);
    ms.setPixelsDimensionOrder(DimensionOrder.XYZCT, 0);
    ms.setPixelsSizeX(new PositiveInteger(SIZE_X), 0);
    ms.setPixelsSizeY(new PositiveInteger(SIZE_Y), 0);
    ms.setPixelsSizeZ(new PositiveInteger(SIZE_Z), 0);
    ms.setPixelsSizeC(new PositiveInteger(SIZE_C), 0);
    ms.setPixelsSizeT(new PositiveInteger(SIZE_T), 0);
    ms.setPixelsType(PixelType.UINT8, 0);
    ms.setPixelsBinDataBigEndian(true, 0, 0);
    ms.setChannelID("Channel:1", 0, 0);
    ms.setChannelSamplesPerPixel(new PositiveInteger(1), 0, 0);
  }

  @AfterClass
  public void tearDown() throws Exception {
    target.delete();
  }

  @Test
  public void testImageWidthWrittenCorrectly() throws Exception {
    OMETiffWriter writer = new OMETiffWriter();
    writer.setBigTiff(true);
    writer.setMetadataRetrieve(ms);
    writer.setId(target.getAbsolutePath());
    writer.saveBytes(0, buf, 0, 0, buf.length, 1);
    writer.close();
    TiffReader reader = new TiffReader();
    reader.setId(target.getAbsolutePath());
    assertEquals(SIZE_X, reader.getSizeX());
    assertEquals(SIZE_Y, reader.getSizeY());
  }
}
