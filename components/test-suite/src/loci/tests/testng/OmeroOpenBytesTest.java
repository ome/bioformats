/*
 * #%L
 * OME Bio-Formats manual and automated test suite.
 * %%
 * Copyright (C) 2006 - 2012 Open Microscopy Environment:
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

import static org.testng.AssertJUnit.*;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import loci.common.Location;
import loci.formats.ChannelFiller;
import loci.formats.ChannelSeparator;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.MinMaxCalculator;

/**
 * Tests various permutations of <code>openBytes()</code> in an OMERO like
 * manner.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/test-suite/src/loci/tests/testng/OmeroOpenBytesTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/test-suite/src/loci/tests/testng/OmeroOpenBytesTest.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Chris Allan <callan at blackcat dot ca>
 */
public class OmeroOpenBytesTest
{
  private IFormatReader reader;

  private int imageCount;

  private int seriesCount;

  private int sizeX;

  private int sizeY;

  private int sizeZ;

  private int sizeC;

  private int sizeT;

  private int bpp;

  private int planeSize;

  private int topHalfSize;

  private int bottomHalfSize;

  private int topLeftQuarterSize;

  private int topRightQuarterSize;

  private int bottomLeftQuarterSize;

  private int bottomRightQuarterSize;

  @Parameters({"id", "inMemory"})
  @BeforeClass
  public void parse(String id, String inMemory) throws Exception {
    reader = new ImageReader();
    reader = new ChannelFiller(reader);
    reader = new ChannelSeparator(reader);
    reader = new MinMaxCalculator(reader);

    if (Boolean.parseBoolean(inMemory) && reader.isSingleFile(id)) {
      TestTools.mapFile(id);
    }

    reader.setId(id);
    seriesCount = reader.getSeriesCount();
  }

  private void assertBlock(int blockSize, int posX, int posY,
                           int width, int height) throws Exception {
    byte[] plane = new byte[planeSize];
    byte[] buf = new byte[blockSize];
    String planeDigest, bufDigest;
    for (int i = 0; i < imageCount; i++) {
      // Read the data as a full plane
      reader.openBytes(i, plane);
      // Read the data as a block
      try {
        reader.openBytes(i, buf, posX, posY, width, height);
      }
      catch (Exception e) {
        throw new RuntimeException(String.format(
            "openBytes(series:%d i:%d, buf.length:%d, x:%d, y:%d, w:%d, h:%d) " +
            "[sizeX: %d sizeY:%d bpp:%d] threw exception!",
            reader.getSeries(), i, buf.length, posX, posY, width, height,
            sizeX, sizeY, bpp), e);
      }
      // Compare hash digests
      planeDigest = TestTools.md5(
          plane, sizeX, sizeY, posX, posY, width, height, bpp);
      bufDigest = TestTools.md5(buf, 0, width * height * bpp);
      if (!planeDigest.equals(bufDigest)) {
        fail(String.format("MD5:%d;%d len:%d %s != %s",
            reader.getSeries(), i, blockSize, planeDigest, bufDigest));
      }
    }
  }

  private void assertRows(int blockSize) throws Exception {
    for (int series = 0; series < seriesCount; series++) {
      assertSeries(series);
      byte[] plane = new byte[planeSize];
      byte[] buf = new byte[blockSize];
      int maximumRowCount = buf.length / bpp / sizeX;
      String planeDigest, bufDigest;
      int pixelsToRead = sizeX * sizeY;
      int width = sizeX, height, posX = 0, posY = 0, actualBufSize;
      for (int i = 0; i < imageCount; i++) {
        // Read the data as a full plane
        reader.openBytes(i, plane);
        int offset = 0;
        while(pixelsToRead > 0) {
          // Prepare our read metadata
          height = maximumRowCount;
          if ((posY + height) > sizeY)
          {
            height = sizeY - posY;
          }
          actualBufSize = bpp * height * width;
          // Read the data as a block
          try {
            reader.openBytes(i, buf, posX, posY, width, height);
          }
          catch (Exception e) {
            throw new RuntimeException(String.format(
                "openBytes(series:%d i:%d, buf.length:%d, x:%d, y:%d, w:%d, " +
                "h:%d) [sizeX: %d sizeY:%d bpp:%d] threw exception!",
                series, i, buf.length, posX, posY, width, height, sizeX, sizeY,
                bpp), e);
          }
          // Compare hash digests
          planeDigest = TestTools.md5(plane, offset, actualBufSize);
          bufDigest = TestTools.md5(buf, 0, actualBufSize);
          if (!planeDigest.equals(bufDigest)) {
            fail(String.format("MD5:%s;%d offset:%d len:%d %s != %s",
                series, i, offset, actualBufSize, planeDigest, bufDigest));
          }
          // Update offsets, etc.
          offset += actualBufSize;
          posY += height;
          pixelsToRead -= height * width;
        }
      }
    }
  }

  private void assertSeries(int series) {
    reader.setSeries(series);
    sizeX = reader.getSizeX();
    sizeY = reader.getSizeY();
    sizeZ = reader.getSizeZ();
    sizeC = reader.getSizeC();
    sizeT = reader.getSizeT();
    imageCount = reader.getImageCount();
    bpp = FormatTools.getBytesPerPixel(reader.getPixelType());
    planeSize = sizeX * sizeY * bpp;
    topHalfSize = (sizeY / 2) * sizeX * bpp;
    bottomHalfSize = (sizeY - (sizeY / 2)) * sizeX * bpp;
    topLeftQuarterSize = (sizeY / 2) * (sizeX / 2) * bpp;
    topRightQuarterSize = (sizeY / 2) * (sizeX - (sizeX / 2)) * bpp;
    bottomLeftQuarterSize = (sizeY - (sizeY / 2)) * (sizeX / 2) * bpp;
    bottomRightQuarterSize =
      (sizeY - (sizeY / 2)) * (sizeX - (sizeX / 2)) * bpp;
  }

  @AfterClass
  public void tearDown() throws Exception {
    Location.mapFile(reader.getCurrentFile(), null);
    reader.close();
  }

  @Test
  public void testOpenBytesPlane() throws Exception {
    for (int series = 0; series < seriesCount; series++) {
      assertSeries(series);
      byte[] plane = new byte[planeSize];
      for (int i = 0; i < reader.getImageCount(); i++) {
        reader.openBytes(i, plane);
      }
    }
  }

  @Test
  public void testOpenBytesHalfPlane() throws Exception {
    for (int series = 0; series < seriesCount; series++) {
      assertSeries(series);
      byte[] plane = new byte[planeSize];
      byte[] topHalfPlane = new byte[topHalfSize];
      byte[] bottomHalfPlane = new byte[bottomHalfSize];
      String planeDigest, halfPlaneDigest;
      for (int i = 0; i < imageCount; i++) {
        // Check the digest for the first half of the plane against a full
        // plane
        reader.openBytes(i, plane);
        reader.openBytes(i, topHalfPlane, 0, 0, sizeX, sizeY / 2);
        planeDigest = TestTools.md5(plane, 0, topHalfSize);
        halfPlaneDigest = TestTools.md5(topHalfPlane, 0, topHalfSize);
        if (!planeDigest.equals(halfPlaneDigest)) {
          fail(String.format("First half MD5:%d;%d %s != %s",
              series, i, planeDigest, halfPlaneDigest));
        }
        // Check the digest for the second half of the plane against a full
        // plane
        reader.openBytes(i, bottomHalfPlane, 0, sizeY / 2, sizeX,
            sizeY - (sizeY / 2));
        planeDigest = TestTools.md5(plane, topHalfSize, bottomHalfSize);
        halfPlaneDigest = TestTools.md5(bottomHalfPlane, 0, bottomHalfSize);
        if (!planeDigest.equals(halfPlaneDigest)) {
          fail(String.format("Second half MD5:%d;%d %s != %s",
              series, i, planeDigest, halfPlaneDigest));
        }
      }
    }
  }

  @Test
  public void testQuartersActualSize() throws Exception {
    for (int series = 0; series < seriesCount; series++) {
      assertSeries(series);
      assertBlock(topLeftQuarterSize, 0, 0, sizeX / 2, sizeY / 2);
      assertBlock(topRightQuarterSize, sizeX / 2, 0,
                  sizeX - (sizeX / 2), sizeY / 2);
      assertBlock(bottomLeftQuarterSize, 0, sizeY / 2,
                  sizeX / 2, (sizeY - (sizeY / 2)));
      assertBlock(bottomRightQuarterSize, sizeX / 2, sizeY / 2,
                  sizeX - (sizeX / 2), sizeY - (sizeY / 2));
    }
  }

  @Test
  public void testQuartersTwiceActualSize() throws Exception {
    for (int series = 0; series < seriesCount; series++) {
      assertSeries(series);
      assertBlock(topLeftQuarterSize * 2, 0, 0, sizeX / 2, sizeY / 2);
      assertBlock(topRightQuarterSize * 2, sizeX / 2, 0,
                  sizeX - (sizeX / 2), sizeY / 2);
      assertBlock(bottomLeftQuarterSize * 2, 0, sizeY / 2,
                  sizeX / 2, (sizeY - (sizeY / 2)));
      assertBlock(bottomRightQuarterSize * 2, sizeX / 2, sizeY / 2,
                  sizeX - (sizeX / 2), sizeY - (sizeY / 2));
    }
  }

  @Test
  public void testOpenBytesBlocksByRow512KB() throws Exception {
    assertRows(524288);
  }

  @Test
  public void testOpenBytesBlocksByRow1MB() throws Exception {
    assertRows(1048576);
  }

  @Test
  public void testOpenBytesBlocksByRowPlaneSize() throws Exception {
    assertRows(sizeX * sizeY * bpp);
  }

}
