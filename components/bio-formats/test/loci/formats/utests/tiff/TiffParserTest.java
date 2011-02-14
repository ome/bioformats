//
// TiffParserTest.java
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

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

import java.io.IOException;
import java.lang.reflect.Constructor;

import loci.formats.FormatException;
import loci.formats.tiff.IFD;
import loci.formats.tiff.PhotoInterp;
import loci.formats.tiff.TiffParser;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/test/loci/formats/utests/tiff/TiffParserTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/test/loci/formats/utests/tiff/TiffParserTest.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author callan
 */
@Test(groups={ "tiffParserTests" })
public class TiffParserTest {

  private TiffParser tiffParser;
  
  private BaseTiffMock mock;

  @Parameters({ "mockClassName" })
  @BeforeMethod
  public void setUp(String mockClassName) throws Exception {
    Class mockClass = Class.forName(mockClassName);
    Constructor constructor = mockClass.getDeclaredConstructor();
    mock = (BaseTiffMock) constructor.newInstance();
    tiffParser = mock.getTiffParser();
  }
  
  @Test
  public void testHeader() throws IOException {
    assertTrue(tiffParser.isValidHeader());
    assertTrue(tiffParser.checkHeader());
    assertFalse(tiffParser.isBigTiff());
  }

  @Test
  public void testFirstOffset() throws IOException {
    assertEquals(8, tiffParser.getFirstOffset());
  }

  @Test
  public void testGetPhotoInterp() throws IOException, FormatException {
    assertEquals(PhotoInterp.RGB,
                 tiffParser.getFirstIFD().getPhotometricInterpretation());
  }

  @Test
  public void testGetImageLength() throws IOException, FormatException {
    assertEquals(mock.getImageLength(),
                 tiffParser.getFirstIFD().getImageLength());
  }

  @Test
  public void testGetImageWidth() throws IOException, FormatException {
    assertEquals(mock.getImageWidth(), tiffParser.getFirstIFD().getImageWidth());
  }

  @Test
  public void testGetBitsPerSample() throws IOException, FormatException {
    int[] bitsPerSample = tiffParser.getFirstIFD().getBitsPerSample();
    assertNotNull(bitsPerSample);
    assertEquals(mock.getSamplesPerPixel(), bitsPerSample.length);
    int[] BITS_PER_SAMPLE = mock.getBitsPerSample();
    for (int i = 0; i < BITS_PER_SAMPLE.length; i++) {
      int a = BITS_PER_SAMPLE[i];
      long b = bitsPerSample[i];
      if (a != b) {
        fail(String.format(
            "Bits per sample offset %d not equivilent: %d != %d", i, a, b));
      }
    }
  }

  @Test
  public void testGetSamplesPerPixel() throws IOException, FormatException {
    assertEquals(mock.getSamplesPerPixel(),
                 tiffParser.getFirstIFD().getSamplesPerPixel());
  }

  @Test
  public void testGetStripOffsets() throws IOException, FormatException {
    long[] stripOffsets = tiffParser.getFirstIFD().getStripOffsets();
    assertNotNull(stripOffsets);
    int[] STRIP_OFFSETS = mock.getStripOffsets();
    assertEquals(STRIP_OFFSETS.length, stripOffsets.length);
    for (int i = 0; i < STRIP_OFFSETS.length; i++) {
      int a = STRIP_OFFSETS[i];
      long b = stripOffsets[i]; 
      if (a != b) {
        fail(String.format(
            "Strip offset %d not equivilent: %d != %d", i, a, b));
      }
    }
  }

  @Test
  public void testGetRowsPerStrip() throws IOException, FormatException {
    long[] rowsPerStrip = tiffParser.getFirstIFD().getRowsPerStrip();
    assertNotNull(rowsPerStrip);
    int[] ROWS_PER_STRIP = mock.getRowsPerStrip();
    assertEquals(ROWS_PER_STRIP.length, rowsPerStrip.length);
    for (int i = 0; i < ROWS_PER_STRIP.length; i++) {
      int a = ROWS_PER_STRIP[i];
      long b = rowsPerStrip[i]; 
      if (a != b) {
        fail(String.format(
            "Rows per strip %d not equivilent: %d != %d", i, a, b));
      }
    }
  }

  @Test
  public void testGetXResolution() throws IOException, FormatException {
    int tag = IFD.X_RESOLUTION;
    assertTrue(mock.getXResolution().equals(
        tiffParser.getFirstIFD().getIFDRationalValue(tag)));
  }

  @Test
  public void testGetYResolution() throws IOException, FormatException {
    int tag = IFD.Y_RESOLUTION;
    assertTrue(mock.getYResolution().equals(
        tiffParser.getFirstIFD().getIFDRationalValue(tag)));
  }

  @Test
  public void testGetResolutionUnit() throws IOException, FormatException {
    int tag = IFD.RESOLUTION_UNIT;
    assertEquals(mock.getResolutionUnit(),
                 tiffParser.getFirstIFD().getIFDIntValue(tag));
  }

  @Test(expectedExceptions={ FormatException.class })
  public void testNonUniformRowsPerStrip() throws IOException, FormatException {
    mock = new NonUniformRowsPerStripMock();
    tiffParser = mock.getTiffParser();
    assertTrue(tiffParser.checkHeader());
    tiffParser.getFirstIFD().getRowsPerStrip();
  }

  @Test
  public void testBitsPerSampleMismatch() throws IOException, FormatException {
    mock = new BitsPerSampleSamplesPerPixelMismatchMock();
    tiffParser = mock.getTiffParser();
    assertTrue(tiffParser.checkHeader());
    IFD ifd = tiffParser.getFirstIFD();
    int[] bitsPerSample = ifd.getBitsPerSample();
    int[] mockBitsPerSample = mock.getBitsPerSample();
    assertEquals(bitsPerSample.length, ifd.getSamplesPerPixel());
    for (int i=0; i<mockBitsPerSample.length; i++) {
      assertEquals(bitsPerSample[i], mockBitsPerSample[i]);
    }
  }

  // TODO: Test wrong type exceptions
}
