//
// Jpeg2000GrindTest.java
//

/*
LOCI software automated test suite for TestNG. Copyright (C) 2007-@year@
Chris Allan. All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
  * Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
  * Neither the name of the UW-Madison LOCI nor the names of its
    contributors may be used to endorse or promote products derived from
    this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE UW-MADISON LOCI ``AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package loci.tests.testng;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import loci.tests.testng.TestTools.TileLoopIteration;
import ome.xml.model.enums.DimensionOrder;
import ome.xml.model.primitives.PositiveInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import loci.common.services.ServiceFactory;
import loci.formats.FormatTools;
import loci.formats.in.TiffReader;
import loci.formats.meta.IMetadata;
import loci.formats.out.TiffWriter;
import loci.formats.services.OMEXMLService;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffCompression;

/**
 * Test grinding in a multi-threaded environment a JPEG-2000 encoded TIFF.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/test-suite/src/loci/tests/testng/Jpeg2000GrindTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/test-suite/src/loci/tests/testng/Jpeg2000GrindTest.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Chris Allan <callan at blackcat dot ca>
 */
public class Jpeg2000GrindTest {

  private static final Logger LOGGER =
    LoggerFactory.getLogger(Jpeg2000GrindTest.class);

  public static final String PIXEL_TYPE = "uint16";

  public static final int SIZE_X = 5000;

  public static final int SIZE_Y = 4000;

  public static final int SIZE_Z = 1;

  public static final int SIZE_C = 3;

  public static final int SIZE_T = 1;

  public static final int TILE_WIDTH = 256;

  public static final int TILE_HEIGHT = 256;

  public static final int THREAD_POOL_SIZE = 3;

  private int bytesPerPixel;

  private List<String> hashDigests = new ArrayList<String>();

  private TiffWriter writer;

  private File id;

  /** Last IFD we used during a tile write operation. */
  private IFD lastIFD;

  /** Last z-section offset we used during a tile write operation. */
  private int lastZ = -1;

  /** Last channel offset we used during a tile write operation. */
  private int lastC = -1;

  /** Last timepoint offset  we used during a tile write operation. */
  private int lastT = -1;

  /**
   * Initializes the writer.
   * @param output The file where to write the compressed data.
   * @param compression The compression to use.
   * @param bigTiff Pass <code>true</code> to set the <code>bigTiff</code>
   * flag, <code>false</code> otherwise.
   * @throws Exception Thrown if an error occurred.
   */
  private void initializeWriter(String output, String compression,
                                boolean bigTiff)
      throws Exception
  {
    ServiceFactory sf = new ServiceFactory();
    OMEXMLService service = sf.getInstance(OMEXMLService.class);
    IMetadata metadata = service.createOMEXMLMetadata();
    metadata.setImageID("Image:0", 0);
    metadata.setPixelsID("Pixels:0", 0);
    metadata.setPixelsBinDataBigEndian(true, 0, 0);
    metadata.setPixelsDimensionOrder(DimensionOrder.XYZCT, 0);
    metadata.setPixelsType(
        ome.xml.model.enums.PixelType.fromString(PIXEL_TYPE), 0);
    metadata.setPixelsSizeX(new PositiveInteger(SIZE_X), 0);
    metadata.setPixelsSizeY(new PositiveInteger(SIZE_Y), 0);
    metadata.setPixelsSizeZ(new PositiveInteger(1), 0);
    metadata.setPixelsSizeC(new PositiveInteger(1), 0);
    metadata.setPixelsSizeT(new PositiveInteger(SIZE_Z * SIZE_C * SIZE_T), 0);
    metadata.setChannelID("Channel:0", 0, 0);
    metadata.setChannelSamplesPerPixel(new PositiveInteger(1), 0, 0);
    writer = new TiffWriter();
    writer.setMetadataRetrieve(metadata);
    writer.setCompression(compression);
    writer.setWriteSequentially(true);
    writer.setInterleaved(true);
    writer.setBigTiff(bigTiff);
    writer.setId(output);
    bytesPerPixel = FormatTools.getBytesPerPixel(PIXEL_TYPE);
  }

  /**
   * Retrieves the IFD that should be used for a given planar offset.
   * @param z Z-section offset requested.
   * @param c Channel offset requested.
   * @param t Timepoint offset requested.
   * @param w Tile width requested.
   * @param h Tile height requested.
   * @return A new or already allocated IFD for use when writing tiles.
   */
  private IFD getIFD(int z, int c, int t, int w, int h) {
    if (lastT != t || lastC != c || lastZ != z) {
      lastIFD = new IFD();
      lastIFD.put(IFD.TILE_WIDTH, w);
      lastIFD.put(IFD.TILE_LENGTH, h);
    }
    lastT = t;
    lastC = c;
    lastZ = z;
    return lastIFD;
  }

  @BeforeClass
  public void setup() throws Exception {
    id = File.createTempFile(Jpeg2000GrindTest.class.getName(), ".tif");
    initializeWriter(id.getAbsolutePath(),
                     TiffCompression.JPEG_2000.getCodecName(),
                     false);
  }

  @AfterClass
  public void tearDown() throws Exception {
    writer.close();
    id.delete();
  }

  @Test(enabled=true)
  public void testPyramidWriteTiles() throws Exception {
    short tileCount = (short) TestTools.forEachTile(new TileLoopIteration() {
      public void run(int z, int c, int t, int x, int y, int tileWidth,
          int tileHeight, int tileCount) {
        byte[] tile = new byte[tileWidth * tileHeight * bytesPerPixel];
        ByteBuffer.wrap(tile).asShortBuffer().put(0, (short) tileCount);
        hashDigests.add(TestTools.md5(tile));
        int planeNumber = FormatTools.getIndex(
            "XYZCT", SIZE_Z, SIZE_C, SIZE_T, SIZE_Z * SIZE_C * SIZE_T, z, c, t);
        IFD ifd = getIFD(z, c, t, tileWidth, tileHeight);
        try {
          writer.saveBytes(planeNumber, tile, ifd, x, y, tileWidth, tileHeight);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
        tileCount++;
      }
    }, SIZE_X, SIZE_Y, SIZE_Z, SIZE_C, SIZE_T, TILE_WIDTH, TILE_HEIGHT);
    assertEquals(tileCount, 960);
    writer.close();
  }

  @Test(dependsOnMethods={"testPyramidWriteTiles"}, enabled=true)
  public void testPyramidReadTilesMultiThreaded() throws Exception {
    final ExecutorService pool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    for (int theC = 0; theC < SIZE_C; theC++) {
      pool.execute(new ChannelRunnable(theC));
    }
    pool.shutdown();
    while (!pool.awaitTermination(30, TimeUnit.SECONDS)) {
      LOGGER.warn("Waiting for channel runnables to complete...");
    }
  }

  class ChannelRunnable implements Runnable {

    int theC;

    public ChannelRunnable(int theC) {
      this.theC = theC;
    }

    public void run() {
      final TiffReader reader = new TiffReader();
      try {
        reader.setId(id.getAbsolutePath());
      }
      catch (Exception e) {
        throw new RuntimeException(e);
      }
      assertEquals(reader.getImageCount(), SIZE_Z * SIZE_C * SIZE_T);
      assertEquals(reader.getSeriesCount(), 6);
      short tileCount = (short) TestTools.forEachTile(new TileLoopIteration() {
        public void run(int z, int c, int t, int x, int y, int tileWidth,
            int tileHeight, int tileCount) {
          try {
            tileCount += theC * 320;
            int planeNumber = FormatTools.getIndex(
                "XYZCT", SIZE_Z, SIZE_C, SIZE_T, SIZE_Z * SIZE_C * SIZE_T,
                z, theC, t);
            byte[] tile = null;
            try {
              tile = reader.openBytes(planeNumber, x, y, tileWidth,
                  tileHeight);
            }
            catch (Throwable throwable) {
              fail(String.format("Failure reading tile z:%d c:%d t:%d "
                  + "x:%d y:%d", z, theC, t, x, y), throwable);
            }
            String readDigest = TestTools.md5(tile);
            String writtenDigest = hashDigests.get(tileCount);
            if (!writtenDigest.equals(readDigest)) {
              fail(String.format("Hash digest mismatch z:%d c:%d t:%d "
                  + "x:%d y:%d -- %s != %s", z, theC, t, x, y, writtenDigest,
                  readDigest));
            }
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        }
      }, SIZE_X, SIZE_Y, SIZE_Z, 1, SIZE_T, TILE_WIDTH, TILE_HEIGHT);
      assertEquals(tileCount, 320);
    }
  }

}
