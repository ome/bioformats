/*
 * #%L
 * OME Bio-Formats manual and automated test suite.
 * %%
 * Copyright (C) 2006 - 2015 Open Microscopy Environment:
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

package loci.tests.testng;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
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

  public static final int THREAD_POOL_SIZE = 2;

  private int bytesPerPixel;

  private Map<Integer, String> hashDigests = new HashMap<Integer, String>();

  private TiffWriter writer;

  private File id;

  /** All the IFDs we have used for each "plane". */
  private Map<Integer, IFD> ifds = new HashMap<Integer, IFD>();

  /** Last IFD we used during a tile write operation. */
  private int lastIFD;

  /** Thread pool executor service. */
  private ExecutorService pool;

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
    writer.setWriteSequentially(false);
    writer.setInterleaved(true);
    writer.setBigTiff(bigTiff);
    writer.setId(output);
    bytesPerPixel = FormatTools.getBytesPerPixel(PIXEL_TYPE);
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
    pool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    short tileCount = (short) TestTools.forEachTile(new TileLoopIteration() {
      public void run(int z, int c, int t, int x, int y, int tileWidth,
          int tileHeight, int tileCount) {
        int planeNumber = FormatTools.getIndex(
            "XYZCT", SIZE_Z, SIZE_C, SIZE_T, SIZE_Z * SIZE_C * SIZE_T, z, c, t);
        if (planeNumber != lastIFD) {
          pool.shutdown();
          try {
            while (!pool.awaitTermination(30, TimeUnit.SECONDS)) {
              LOGGER.warn("Waiting for runnables to complete...");
            }
          } catch (InterruptedException e) {
            LOGGER.error("Caught interuption while waiting for termination.");
          }
          pool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
          lastIFD = planeNumber;
        }
        pool.submit(new TileRunnable(
            writer, z, c, t, x, y, tileWidth, tileHeight, tileCount));
      }
    }, SIZE_X, SIZE_Y, SIZE_Z, SIZE_C, SIZE_T, TILE_WIDTH, TILE_HEIGHT);
    pool.shutdown();
    while (!pool.awaitTermination(30, TimeUnit.SECONDS)) {
      LOGGER.warn("Waiting for runnables to complete...");
    }
    assertEquals(tileCount, 960);
    writer.close();
  }

  /*
  @Test(enabled=true)
  public void testPyramidWriteTiles() throws Exception {
    short tileCount = (short) TestTools.forEachTile(new TileLoopIteration() {
      public void run(int z, int c, int t, int x, int y, int tileWidth,
          int tileHeight, int tileCount) {
        byte[] tile = new byte[tileWidth * tileHeight * bytesPerPixel];
        ByteBuffer.wrap(tile).asShortBuffer().put(0, (short) tileCount);
        hashDigests.put(tileCount, TestTools.md5(tile));
        int planeNumber = FormatTools.getIndex(
            "XYZCT", SIZE_Z, SIZE_C, SIZE_T, SIZE_Z * SIZE_C * SIZE_T, z, c, t);
        IFD ifd;
        synchronized (ifds) {
          if (!ifds.containsKey(planeNumber)) {
            ifd = new IFD();
            ifd.put(IFD.TILE_WIDTH, TILE_WIDTH);
            ifd.put(IFD.TILE_LENGTH, TILE_HEIGHT);
            ifds.put(planeNumber, ifd);
          }
          ifd = ifds.get(planeNumber);
        }
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
  */

  @Test(dependsOnMethods={"testPyramidWriteTiles"}, enabled=true)
  public void testPyramidReadTilesMultiThreaded() throws Exception {
    pool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    for (int theC = 0; theC < SIZE_C; theC++) {
      pool.execute(new ChannelRunnable(theC));
    }
    pool.shutdown();
    while (!pool.awaitTermination(30, TimeUnit.SECONDS)) {
      LOGGER.warn("Waiting for channel runnables to complete...");
    }
  }

  class TileRunnable implements Runnable {

    private int tileNumber;
    private TiffWriter writer;
    private int z;
    private int c;
    private int t;
    private int x;
    private int y;
    private int tileWidth;
    private int tileHeight;

    public TileRunnable(TiffWriter writer, int z, int c, int t, int x, int y,
        int tileWidth, int tileHeight, int tileNumber) {
      this.z = z;
      this.c = c;
      this.t = t;
      this.x = x;
      this.y = y;
      this.tileWidth = tileWidth;
      this.tileHeight = tileHeight;
      this.writer = writer;
      this.tileNumber = tileNumber;
    }

    public void run() {
      byte[] tile = new byte[tileWidth * tileHeight * bytesPerPixel];
      ByteBuffer.wrap(tile).asShortBuffer().put(0, (short) tileNumber);
      hashDigests.put(tileNumber, TestTools.md5(tile));
      int planeNumber = FormatTools.getIndex(
          "XYZCT", SIZE_Z, SIZE_C, SIZE_T, SIZE_Z * SIZE_C * SIZE_T, z, c, t);
      IFD ifd;
      synchronized (ifds) {
        if (!ifds.containsKey(planeNumber)) {
          ifd = new IFD();
          ifd.put(IFD.TILE_WIDTH, TILE_WIDTH);
          ifd.put(IFD.TILE_LENGTH, TILE_HEIGHT);
          ifds.put(planeNumber, ifd);
        }
        ifd = ifds.get(planeNumber);
      }
      try {
        writer.saveBytes(planeNumber, tile, ifd, x, y, tileWidth, tileHeight);
      } catch (Exception e) {
        LOGGER.error("Exception while writing tile", e);
        throw new RuntimeException(e);
      }
    }
  }

  class ChannelRunnable implements Runnable {

    private int theC;

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
