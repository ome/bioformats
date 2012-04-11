/*
 * #%L
 * OME Bio-Formats manual and automated test suite.
 * %%
 * Copyright (C) 2006 - 2012 Board of Regents of the University of
 * Wisconsin-Madison, Glencoe Software, Inc., and University of Dundee.
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

import java.io.File;

import org.perf4j.StopWatch;
import org.perf4j.log4j.Log4JStopWatch;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import loci.formats.ChannelFiller;
import loci.formats.ChannelSeparator;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.MinMaxCalculator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Performs various <code>openBytes()</code> performance tests.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/test-suite/src/loci/tests/testng/OpenBytesPerformanceTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/test-suite/src/loci/tests/testng/OpenBytesPerformanceTest.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Chris Allan <callan at blackcat dot ca>
 */
public class OpenBytesPerformanceTest
{
  private static final Logger LOGGER =
      LoggerFactory.getLogger(OpenBytesPerformanceTest.class);

  private String id;

  private IFormatReader reader;

  private int imageCount;

  private int seriesCount;

  private int sizeX;

  private int sizeY;

  private int sizeZ;

  private int sizeC;

  private int sizeT;

  private int bpp;

  private int optimalTileHeight;

  private int optimalTileWidth;

  private int planeSize;

  private String filename;

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
  }

  @Parameters({"id"})
  @BeforeClass
  public void init(String id) throws Exception {
    this.id = id;
    filename = new File(id).getName();
  }

  @AfterClass
  public void tearDown() throws Exception {
    reader.close();
  }

  @Test
  public void setId() throws Exception {
    reader = new ImageReader();
    reader = new ChannelFiller(reader);
    reader = new ChannelSeparator(reader);
    reader = new MinMaxCalculator(reader);
    reader.setId(id);
    seriesCount = reader.getSeriesCount();
  }

  @Test(dependsOnMethods={"setId"})
  public void testOpenBytesAllTilesNewBuffer() throws Exception {
    for (int series = 0; series < seriesCount; series++) {
      assertSeries(series);
      for (int image = 0; image < imageCount; image++) {
        LOGGER.info("Reading from series {} image {}", series, image);
        optimalTileWidth = reader.getOptimalTileWidth();
        optimalTileHeight = reader.getOptimalTileHeight();
        LOGGER.info("Optimal tile {}x{}", optimalTileWidth, optimalTileHeight);
        int tilesWide = (int) Math.ceil((double) sizeX / optimalTileWidth);
        int tilesHigh = (int) Math.ceil((double) sizeY / optimalTileHeight);
        LOGGER.info("Tile counts {}x{}", tilesWide, tilesHigh);
        int x, y = 0;
        StopWatch stopWatch;
        for (int tileX = 0; tileX < tilesWide; tileX++) {
          for (int tileY = 0; tileY < tilesHigh; tileY++) {
            x = tileX * optimalTileWidth;
            y = tileY * optimalTileHeight;

            int actualTileWidth =
              (int) Math.min(optimalTileWidth, reader.getSizeX() - x);
            int actualTileHeight =
              (int) Math.min(optimalTileHeight, reader.getSizeY() - y);

            LOGGER.info("Reading tile at {}x{}", x, y);
            stopWatch = new Log4JStopWatch(String.format(
                "%s[%d:%d]_alloc_tile", filename, series, image));
            reader.openBytes(0, x, y, actualTileWidth, actualTileHeight);
            stopWatch.stop();
          }
        }
      }
    }
  }

  @Test(dependsOnMethods={"setId"})
  public void testOpenBytesAllTilesPreAllocatedBuffer() throws Exception {
    for (int series = 0; series < seriesCount; series++) {
      assertSeries(series);
      for (int image = 0; image < imageCount; image++) {
        LOGGER.info("Reading from series {} image {}", series, image);
        optimalTileWidth = reader.getOptimalTileWidth();
        optimalTileHeight = reader.getOptimalTileHeight();
        LOGGER.info("Optimal tile {}x{}", optimalTileWidth, optimalTileHeight);
        int tilesWide = (int) Math.ceil((double) sizeX / optimalTileWidth);
        int tilesHigh = (int) Math.ceil((double) sizeY / optimalTileHeight);
        LOGGER.info("Tile counts {}x{}", tilesWide, tilesHigh);
        int x, y = 0;
        StopWatch stopWatch;
        byte[] buf = new byte[optimalTileWidth * optimalTileHeight *
          FormatTools.getBytesPerPixel(reader.getPixelType())];
        LOGGER.info("Allocated buffer size: {}", buf.length);
        for (int tileX = 0; tileX < tilesWide; tileX++) {
          for (int tileY = 0; tileY < tilesHigh; tileY++) {
            x = tileX * optimalTileWidth;
            y = tileY * optimalTileHeight;

            int actualTileWidth =
              (int) Math.min(optimalTileWidth, reader.getSizeX() - x);
            int actualTileHeight =
              (int) Math.min(optimalTileHeight, reader.getSizeY() - y);

            LOGGER.info("Reading tile at {}x{}", x, y);
            stopWatch = new Log4JStopWatch(String.format(
                "%s[%d:%d]_prealloc_tile", filename, series, image));
            reader.openBytes(image, buf, x, y, actualTileWidth,
                             actualTileHeight);
            stopWatch.stop();
          }
        }
      }
    }
  }

}
