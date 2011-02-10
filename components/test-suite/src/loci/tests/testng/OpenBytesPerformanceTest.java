//
// OpenBytesPerformanceTest.java
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

/**
 * Performs various <code>openBytes()</code> performance tests.
 * 
 * @author Chris Allan <callan at blackcat dot ca>
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://dev.loci.wisc.edu/trac/java/browser/trunk/components/test-suite/src/loci/tests/testng/OpenBytesPerformanceTest.java">Trac</a>,
 * <a href="http://dev.loci.wisc.edu/svn/java/trunk/components/test-suite/src/loci/tests/testng/OpenBytesPerformanceTest.java">SVN</a></dd></dl>
 */
public class OpenBytesPerformanceTest
{
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
    assertSeries(0);
    optimalTileWidth = reader.getOptimalTileWidth();
    optimalTileHeight = reader.getOptimalTileHeight();
    int tilesWide = (int) Math.ceil(sizeX / optimalTileWidth);
    int tilesHigh = (int) Math.ceil(sizeY / optimalTileHeight);
    int x, y = 0;
    StopWatch stopWatch;
    for (int tileX = 0; tileX < tilesWide; tileX++) {
      for (int tileY = 0; tileY < tilesHigh; tileY++) {
        x = tileX * optimalTileWidth;
        y = tileY * optimalTileHeight;
        stopWatch = new Log4JStopWatch(filename + "_alloc_tile");
        reader.openBytes(0, x, y, optimalTileWidth, optimalTileHeight);
        stopWatch.stop();
      }
    }
  }

  @Test(dependsOnMethods={"setId"})
  public void testOpenBytesAllTilesPreAllocatedBuffer() throws Exception {
    assertSeries(0);
    optimalTileWidth = reader.getOptimalTileWidth();
    optimalTileHeight = reader.getOptimalTileHeight();
    int tilesWide = (int) Math.ceil(sizeX / optimalTileWidth);
    int tilesHigh = (int) Math.ceil(sizeY / optimalTileHeight);
    int x, y = 0;
    StopWatch stopWatch;
    byte[] buf = new byte[optimalTileWidth * optimalTileHeight
                          * reader.getBitsPerPixel() / 8];
    for (int tileX = 0; tileX < tilesWide; tileX++) {
      for (int tileY = 0; tileY < tilesHigh; tileY++) {
        x = tileX * optimalTileWidth;
        y = tileY * optimalTileHeight;
        stopWatch = new Log4JStopWatch(filename + "_prealloc_tile");
        reader.openBytes(0, buf, x, y, optimalTileWidth, optimalTileHeight);
        stopWatch.stop();
      }
    }
  }

}
