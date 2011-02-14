//
// BaseModelNoBinDataReaderTest.java
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

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.io.File;

import loci.formats.ChannelFiller;
import loci.formats.ChannelSeparator;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.MinMaxCalculator;
import loci.formats.meta.IMetadata;
import loci.formats.ome.OMEXMLMetadataImpl;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/test/loci/formats/utests/BaseModelNoBinDataReaderTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/test/loci/formats/utests/BaseModelNoBinDataReaderTest.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Chris Allan <callan at blackcat dot ca>
 */
public class BaseModelNoBinDataReaderTest {

  private BaseModelMock mock;
  
  private File temporaryFile;

  private IFormatReader reader;

  private IMetadata metadata;

  @BeforeClass
  public void setUp() throws Exception {
    mock = new BaseModelMock();
    temporaryFile = File.createTempFile(this.getClass().getName(), ".ome");
    SPWModelReaderTest.writeMockToFile(mock, temporaryFile, false);
  }

  @AfterClass
  public void tearDown() throws Exception {
    temporaryFile.delete();
  }

  @Test
  public void testSetId() throws Exception {
    reader = new MinMaxCalculator(new ChannelSeparator(
        new ChannelFiller(new ImageReader())));
    metadata = new OMEXMLMetadataImpl();
    reader.setMetadataStore(metadata);
    reader.setId(temporaryFile.getAbsolutePath());
  }

  @Test(dependsOnMethods={"testSetId"})
  public void testSeriesCount() {
    assertEquals(1, reader.getSeriesCount());
  }

}
