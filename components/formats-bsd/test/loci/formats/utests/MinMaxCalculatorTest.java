/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package loci.formats.utests;

import static org.testng.AssertJUnit.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import loci.common.Location;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MinMaxCalculator;
import loci.formats.in.FakeReader;
import loci.formats.meta.IMinMaxStore;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test case which outlines the problems seen in omero:#3599.
 *
 * @author Chris Allan <callan at blackcat dot ca>
 */
public class MinMaxCalculatorTest {

  private static final String TEST_FILE =
    "test&pixelType=int8&sizeX=20&sizeY=20&sizeC=1&sizeZ=1&sizeT=1.fake";

  private MinMaxCalculatorTestReader reader;

  private MinMaxCalculator minMaxCalculator;

  private TestMinMaxStore minMaxStore;

  private static int fullPlaneCallIndex;

  private int sizeX;

  private int sizeY;

  private int bpp;

  private int planeSize;

  @BeforeMethod
  public void setUp() throws Exception {
    fullPlaneCallIndex = 1;
    Location.mapId(TEST_FILE, TEST_FILE);
    reader = new MinMaxCalculatorTestReader();
    reader.setId(TEST_FILE);
    minMaxStore = new TestMinMaxStore();
    minMaxCalculator = new MinMaxCalculator(reader);
    minMaxCalculator.setMinMaxStore(minMaxStore);
    sizeX = reader.getSizeX();
    sizeY = reader.getSizeY();
    bpp = FormatTools.getBytesPerPixel(reader.getPixelType());
    planeSize = sizeY * sizeY * bpp;
  }

  @AfterMethod
  public void tearDown() throws Exception {
    minMaxCalculator.close();
    reader.close();
  }

  private void assertMinMax(double minimum, double maximum) throws Exception {
    Double[] min = minMaxCalculator.getPlaneMinimum(0);
    Double[] max = minMaxCalculator.getPlaneMaximum(0);
    Double globalMin = minMaxCalculator.getChannelGlobalMinimum(0);
    Double globalMax = minMaxCalculator.getChannelGlobalMaximum(0);
    Double knownGlobalMin = minMaxCalculator.getChannelKnownMinimum(0);
    Double knownGlobalMax = minMaxCalculator.getChannelKnownMaximum(0);
    assertTrue(minMaxCalculator.isMinMaxPopulated());
    assertNotNull(min);
    assertNotNull(max);
    assertNotNull(globalMin);
    assertNotNull(globalMax);
    assertNotNull(knownGlobalMin);
    assertNotNull(knownGlobalMax);
    assertEquals(1, min.length);
    assertEquals(1, max.length);
    assertEquals(minimum, min[0]);
    assertEquals(maximum, max[0]);
    assertEquals(minimum, globalMin);
    assertEquals(maximum, globalMax);
    assertEquals(minimum, knownGlobalMin);
    assertEquals(maximum, knownGlobalMax);
    List<List<double[]>> seriesGlobalMinimaMaxima = 
      minMaxStore.seriesGlobalMinimaMaxima;
    assertEquals(minMaxCalculator.getCoreIndex() + 1, seriesGlobalMinimaMaxima.size());
    List<double[]> channelGlobalMinimaMaxima = 
      seriesGlobalMinimaMaxima.get(0);
    assertEquals(1, channelGlobalMinimaMaxima.size());
    double[] channelGlobalMinMax = channelGlobalMinimaMaxima.get(0);
    channelGlobalMinMax[0] = minimum;
    channelGlobalMinMax[1] = maximum;
  }

  @Test
  public void testValidOpenBytes() throws Exception {
    byte[] a = new byte[planeSize / 2];
    byte[] b = new byte[planeSize / 2];
    int halfway = sizeY / 2;
    reader.openBytes(0, a, 0, 0, sizeX, halfway);
    reader.openBytes(0, b, 0, halfway, sizeX, halfway);
    assertEquals(-1, a[sizeX / 2]);
    assertEquals(1, a[sizeX / 2 + 1]);
    assertEquals(-2, b[sizeX / 2]);
    assertEquals(2, b[sizeX / 2 + 1]);
  }

  @Test
  public void testValidMinMax() throws Exception {
    minMaxCalculator.openBytes(0);
    assertMinMax(-2.0, 101.0);
  }

  @Test
  public void testValidMinMaxNoOutOfBufferInspection() throws Exception {
    byte[] buf = new byte[planeSize * 2];
    buf[buf.length - 1] = 120;  // This should not be calculated against
    minMaxCalculator.openBytes(0, buf, 0, 0, sizeX, sizeY);
    assertMinMax(-2.0, 101.0);
  }

  @Test
  public void testValidMinMaxDoesntRecalculateOnFullPlane() throws Exception {
    minMaxCalculator.openBytes(0);
    minMaxCalculator.openBytes(0);
    assertMinMax(-2.0, 101.0);
  }

  @Test
  public void testValidMinMaxFirstHalf() throws Exception {
    byte[] buf = new byte[planeSize / 2];
    int halfway = sizeY / 2;
    minMaxCalculator.openBytes(0, buf, 0, 0, sizeX, halfway);
    assertMinMax(-1.0, 1.0);
  }

  @Test
  public void testValidMinMaxSecondHalf() throws Exception {
    byte[] buf = new byte[planeSize / 2];
    int halfway = sizeY / 2;
    minMaxCalculator.openBytes(0, buf, 0, halfway, sizeX, halfway);
    assertMinMax(-2.0, 2.0);
  }

  @Test
  public void testValidMinMaxBothHalvesLowerFirst() throws Exception {
    byte[] buf = new byte[planeSize / 2];
    int halfway = sizeY / 2;
    minMaxCalculator.openBytes(0, buf, 0, 0, sizeX, halfway);
    minMaxCalculator.openBytes(0, buf, 0, halfway, sizeX, halfway);
    assertMinMax(-2.0, 2.0);
  }

  @Test
  public void testValidMinMaxBothHalvesUpperFirst() throws Exception {
    byte[] buf = new byte[planeSize / 2];
    int halfway = sizeY / 2;
    minMaxCalculator.openBytes(0, buf, 0, halfway, sizeX, halfway);
    minMaxCalculator.openBytes(0, buf, 0, 0, sizeX, halfway);
    assertMinMax(-2.0, 2.0);
  }

  /**
   * Checks that the min and max values for each core index do not change
   * when the resolutions are unflattened.
   */
  @Test
  public void testMultipleResolutions() throws Exception {
    minMaxCalculator.setCoreIndex(0);
    minMaxCalculator.openBytes(0);
    assertMinMax(-2.0, 101.0);
    minMaxCalculator.setCoreIndex(1);
    minMaxCalculator.openBytes(0);
    assertMinMax(-1.0, 102.0);

    MinMaxCalculator unflattened = new MinMaxCalculator(new MinMaxCalculatorTestReader());
    try {
      unflattened.setFlattenedResolutions(false);
      unflattened.setId(TEST_FILE);

      unflattened.setCoreIndex(0);
      unflattened.openBytes(0);
      assertEquals(-2.0, unflattened.getPlaneMinimum(0)[0]);
      assertEquals(103.0, unflattened.getPlaneMaximum(0)[0]);

      unflattened.setCoreIndex(1);
      unflattened.openBytes(0);
      assertEquals(-1.0, unflattened.getPlaneMinimum(0)[0]);
      assertEquals(104.0, unflattened.getPlaneMaximum(0)[0]);
    }
    finally {
      unflattened.close();
    }
  }

  /**
   * A testing implementation of {@link loci.formats.meta.IMinMaxStore} that
   * we'll use to ensure that the various methods are called with the correct
   * parameters.
   * 
   * @author Chris Allan <callan at blackcat dot ca>
   *
   */
  class TestMinMaxStore implements IMinMaxStore {

    public List<List<double[]>> seriesGlobalMinimaMaxima = 
      new ArrayList<List<double[]>>();

    /**
     * @see loci.formats.meta.IMinMaxStore#setChannelGlobalMinMax(int, double, double, int)
     */
    @Override
    public void setChannelGlobalMinMax(int channel, double minimum,
                                       double maximum, int series) {
      if (seriesGlobalMinimaMaxima.size() == series) {
        seriesGlobalMinimaMaxima.add(new ArrayList<double[]>());
      }
      List<double[]> channelGlobalMinimaMaxima = 
        seriesGlobalMinimaMaxima.get(series);
      if (channelGlobalMinimaMaxima.size() == channel) {
        channelGlobalMinimaMaxima.add(new double[2]);
      }
      double[] channelGlobalMinMax = channelGlobalMinimaMaxima.get(channel);
      channelGlobalMinMax[0] = minimum;
      channelGlobalMinMax[1] = maximum;
    }
  }

  /**
   * An extension of {@link loci.formats.in.FakeReader} that allows us to
   * control exactly what is in the data returned by the <code>openBytes</code>
   * class of methods.
   * 
   * @author Chris Allan <callan at blackcat dot ca>
   *
   */
  class MinMaxCalculatorTestReader extends FakeReader {

    /**
     * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
     */
    @Override
    public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
        throws FormatException, IOException {
      FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);
      int bpp = FormatTools.getBytesPerPixel(getPixelType());
      int sizeX = getSizeX();
      int sizeY = getSizeY();
      int planeSize = sizeX * sizeY * bpp;
      if (x != 0) {
        throw new FormatException("x != 0 not supported by this test!");
      }
      if (w != sizeX) {
        throw new FormatException("Width != sizeX not supported by this test!");
      }
      // When we're working with the first half of the rows of the image then
      // populate with (-1, 1) pixel values. (-2, 2) for the second half. This
      // will aid us during testing to ensure that minima and maxima are being
      // correctly re-calculated when using block based openBytes() access.
      int from, to;
      for (int i = 0; i < h; i++) {
        from = i * sizeX * bpp;
        to = ((i * sizeX) + sizeX - 1) * bpp;
        Arrays.fill(buf, from, to, (byte) 0x00);
        if (i + y == 0) {
          buf[(sizeX / 2) * bpp] = -1;
          buf[((sizeX / 2) + 1) * bpp] = 1;
        }
        if (i + y == 10) {
          buf[(sizeX / 2) * bpp] = -2;
          buf[((sizeX / 2) + 1) * bpp] = 2;
        }
      }
      // When working with a buffer that is equal to or larger than the plane
      // size we're going to place an index in its last pixel so that we can
      // check re-calculation and call counts.
      if (buf.length >= planeSize) {
        buf[planeSize - 1] = (byte) (100 + fullPlaneCallIndex);
        fullPlaneCallIndex++;
      }
      return buf;
    }

    @Override
    public void setId(String id) throws FormatException, IOException {
      super.setId(id);

      // insert a single sub-resolution

      core.add(new CoreMetadata(core.get(0)));
      core.get(0).resolutionCount++;
      core.get(1).sizeX /= 2;
      core.get(1).sizeY /= 2;
    }
  }
}
