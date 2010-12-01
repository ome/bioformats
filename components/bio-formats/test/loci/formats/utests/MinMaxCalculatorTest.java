//
// MinMaxCalculatorTest.java
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
import static org.testng.AssertJUnit.assertNotNull;

import java.io.IOException;
import java.util.Arrays;

import loci.common.Location;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MinMaxCalculator;
import loci.formats.in.FakeReader;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * 
 * Test case which outlines the problems seen in omero:#3599.
 * 
 * @author Chris Allan <callan at blackcat dot ca>
 *
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://dev.loci.wisc.edu/trac/java/browser/trunk/components/bio-formats/test/loci/formats/utests/MinMaxCalculatorTest.java">Trac</a>,
 * <a href="http://dev.loci.wisc.edu/svn/java/trunk/components/bio-formats/test/loci/formats/utests/MinMaxCalculatorTest.java">SVN</a></dd></dl>
 */
public class MinMaxCalculatorTest {

  private static final String TEST_FILE =
    "test&pixelType=int8&sizeX=20&sizeY=20&sizeC=1&sizeZ=1&sizeT=1.fake";

  private MinMaxCalculatorTestReader reader;

  private MinMaxCalculator minMaxCalculator;

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
    minMaxCalculator = new MinMaxCalculator(reader);
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
    Double[] min = minMaxCalculator.getPlaneMinimum(0);
    Double[] max = minMaxCalculator.getPlaneMaximum(0);
    assertNotNull(min);
    assertNotNull(max);
    assertEquals(1, min.length);
    assertEquals(1, max.length);
    assertEquals(-2.0, min[0]);
    assertEquals(101.0, max[0]);
  }
  
  @Test
  public void testValidMinMaxDoesntRecalculateOnFullPlane() throws Exception {
    minMaxCalculator.openBytes(0);
    minMaxCalculator.openBytes(0);
    Double[] min = minMaxCalculator.getPlaneMinimum(0);
    Double[] max = minMaxCalculator.getPlaneMaximum(0);
    assertNotNull(min);
    assertNotNull(max);
    assertEquals(1, min.length);
    assertEquals(1, max.length);
    assertEquals(-2.0, min[0]);
    assertEquals(101.0, max[0]);
  }

  @Test
  public void testValidMinMaxFirstHalf() throws Exception {
    byte[] buf = new byte[planeSize / 2];
    int halfway = sizeY / 2;
    minMaxCalculator.openBytes(0, buf, 0, 0, sizeX, halfway);
    Double[] min = minMaxCalculator.getPlaneMinimum(0);
    Double[] max = minMaxCalculator.getPlaneMaximum(0);
    assertNotNull(min);
    assertNotNull(max);
    assertEquals(1, min.length);
    assertEquals(1, max.length);
    assertEquals(-1.0, min[0]);
    assertEquals(1.0, max[0]);
  }

  @Test
  public void testValidMinMaxSecondHalf() throws Exception {
    byte[] buf = new byte[planeSize / 2];
    int halfway = sizeY / 2;
    minMaxCalculator.openBytes(0, buf, 0, halfway, sizeX, halfway);
    Double[] min = minMaxCalculator.getPlaneMinimum(0);
    Double[] max = minMaxCalculator.getPlaneMaximum(0);
    assertNotNull(min);
    assertNotNull(max);
    assertEquals(1, min.length);
    assertEquals(1, max.length);
    assertEquals(-2.0, min[0]);
    assertEquals(2.0, max[0]);
  }

  @Test
  public void testValidMinMaxBothHalvesLowerFirst() throws Exception {
    byte[] buf = new byte[planeSize / 2];
    int halfway = sizeY / 2;
    minMaxCalculator.openBytes(0, buf, 0, 0, sizeX, halfway);
    minMaxCalculator.openBytes(0, buf, 0, halfway, sizeX, halfway);
    Double[] min = minMaxCalculator.getPlaneMinimum(0);
    Double[] max = minMaxCalculator.getPlaneMaximum(0);
    assertNotNull(min);
    assertNotNull(max);
    assertEquals(1, min.length);
    assertEquals(1, max.length);
    assertEquals(-2.0, min[0]);
    assertEquals(2.0, max[0]);
  }

  @Test
  public void testValidMinMaxBothHalvesUpperFirst() throws Exception {
    byte[] buf = new byte[planeSize / 2];
    int halfway = sizeY / 2;
    minMaxCalculator.openBytes(0, buf, 0, halfway, sizeX, halfway);
    minMaxCalculator.openBytes(0, buf, 0, 0, sizeX, halfway);
    Double[] min = minMaxCalculator.getPlaneMinimum(0);
    Double[] max = minMaxCalculator.getPlaneMaximum(0);
    assertNotNull(min);
    assertNotNull(max);
    assertEquals(1, min.length);
    assertEquals(1, max.length);
    assertEquals(-2.0, min[0]);
    assertEquals(2.0, max[0]);
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
  }
}
