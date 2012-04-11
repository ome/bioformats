
package loci.formats.utests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

import java.io.IOException;

import loci.common.Location;
import loci.formats.ChannelFiller;
import loci.formats.ChannelMerger;
import loci.formats.ChannelSeparator;
import loci.formats.CoreMetadata;
import loci.formats.DimensionSwapper;
import loci.formats.FileStitcher;
import loci.formats.FormatException;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.MinMaxCalculator;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/test/loci/formats/utests/WrapperTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/test/loci/formats/utests/WrapperTest.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class WrapperTest {

  private static final String TEST_FILE =
    "test&pixelType=uint8&sizeX=128&sizeY=64&sizeC=2&sizeZ=4&sizeT=5&series=3.fake";

  @DataProvider(name = "wrappers")
  public Object[][] createWrappers() {
    Location.mapId(TEST_FILE, TEST_FILE);
    Object[][] wrappers = new Object[][] {
      {new ChannelFiller()},
      {new ChannelMerger()},
      {new ChannelSeparator()},
      {new DimensionSwapper()},
      {new FileStitcher()},
      {new ImageReader()},
      {new MinMaxCalculator()}
    };
    for (int i=0; i<wrappers.length; i++) {
      IFormatReader reader = (IFormatReader) wrappers[i][0];
      try {
        reader.setId(TEST_FILE);
      }
      catch (FormatException e) { e.printStackTrace(); }
      catch (IOException e) { e.printStackTrace(); }
    }
    return wrappers;
  }

  @Test(dataProvider = "wrappers")
  public void testCoreMetadata(IFormatReader reader) {
    assertNotNull(reader.getCurrentFile());
    CoreMetadata[] core = reader.getCoreMetadata();
    assertEquals(core.length, reader.getSeriesCount());
    for (int i=0; i<reader.getSeriesCount(); i++) {
      reader.setSeries(i);
      assertEquals(core[i].sizeX, reader.getSizeX());
      assertEquals(core[i].sizeY, reader.getSizeY());
      assertEquals(core[i].sizeZ, reader.getSizeZ());
      assertEquals(core[i].sizeC, reader.getSizeC());
      assertEquals(core[i].sizeT, reader.getSizeT());
      assertEquals(core[i].pixelType, reader.getPixelType());
      assertEquals(core[i].imageCount, reader.getImageCount());
      assertEquals(core[i].dimensionOrder, reader.getDimensionOrder());
      assertEquals(core[i].littleEndian, reader.isLittleEndian());
      assertEquals(core[i].rgb, reader.isRGB());
      assertEquals(core[i].interleaved, reader.isInterleaved());
      assertEquals(core[i].indexed, reader.isIndexed());
    }
  }
}
