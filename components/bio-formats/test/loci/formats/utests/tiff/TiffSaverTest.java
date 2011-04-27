//
// TiffSaverTest.java
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
import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;

import loci.common.ByteArrayHandle;
import loci.common.RandomAccessInputStream;
import loci.common.RandomAccessOutputStream;
import loci.formats.FormatException;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffParser;
import loci.formats.tiff.TiffSaver;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/test/loci/formats/utests/tiff/TiffSaverTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/test/loci/formats/utests/tiff/TiffSaverTest.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author callan
 */
public class TiffSaverTest {

  private RandomAccessOutputStream out;

  private RandomAccessInputStream in;

  private TiffSaver tiffSaver;

  private TiffParser tiffParser;

  private IFD ifd;

  private static final int INITIAL_CAPACITY = 1024 * 1024;  // 1MB

  @BeforeMethod
  public void setUp() throws IOException {
    ByteArrayHandle handle = new ByteArrayHandle(INITIAL_CAPACITY);
    out = new RandomAccessOutputStream(handle);
    in = new RandomAccessInputStream(handle);
    tiffSaver = new TiffSaver(out, handle);
    tiffParser = new TiffParser(in);

    ifd = new IFD();
    ifd.putIFDValue(IFD.IMAGE_WIDTH, 512);
    ifd.putIFDValue(IFD.IMAGE_DESCRIPTION, "comment");
  }

  @Test(expectedExceptions={ IllegalArgumentException.class })
  public void testNullOutputStream() throws IOException {
    RandomAccessOutputStream a = null;
    String b = null;
    tiffSaver = new TiffSaver(a, b);
    tiffSaver.writeHeader();
  }

  @Test(expectedExceptions={ IllegalArgumentException.class })
  public void testNullFilename() throws IOException {
    RandomAccessOutputStream a = 
      new RandomAccessOutputStream(new ByteArrayHandle());
    String b = null;
    tiffSaver = new TiffSaver(a, b);
    tiffSaver.writeHeader();
  }

  @Test(expectedExceptions={ IllegalArgumentException.class })
  public void testNullBytes() throws IOException {
    RandomAccessOutputStream a = 
      new RandomAccessOutputStream(new ByteArrayHandle());
    ByteArrayHandle b = null;
    tiffSaver = new TiffSaver(a, b);
    tiffSaver.writeHeader();
  }

  @Test
  public void testWriteHeaderBigEndianRegularTiff() throws IOException {
    tiffSaver.writeHeader();
    assertTrue(tiffParser.isValidHeader());
    assertFalse(tiffParser.checkHeader());
    assertFalse(tiffParser.isBigTiff());
  }

  @Test
  public void testWriteHeaderLittleEndianRegularTiff() throws IOException {
    tiffSaver.setLittleEndian(true);
    tiffSaver.writeHeader();
    assertTrue(tiffParser.isValidHeader());
    assertTrue(tiffParser.checkHeader());
    assertFalse(tiffParser.isBigTiff());
  }

  @Test
  public void testWriteHeaderBigEndianBigTiff() throws IOException {
    tiffSaver.setLittleEndian(false);
    tiffSaver.setBigTiff(true);
    tiffSaver.writeHeader();
    assertTrue(tiffParser.isValidHeader());
    assertFalse(tiffParser.checkHeader());
    assertTrue(tiffParser.isBigTiff());
  }

  @Test
  public void testWriteHeaderLittleEndianBigTiff() throws IOException {
    tiffSaver.setLittleEndian(true);
    tiffSaver.setBigTiff(true);
    tiffSaver.writeHeader();
    assertTrue(tiffParser.isValidHeader());
    assertTrue(tiffParser.checkHeader());
    assertTrue(tiffParser.isBigTiff());
  }

  @Test
  public void testOverwriteIFDValue() throws FormatException, IOException {
    out.seek(0);
    tiffSaver.setBigTiff(false);
    tiffSaver.writeHeader();
    tiffSaver.writeIFD(ifd, 0);

    tiffSaver.overwriteIFDValue(in, 0, IFD.IMAGE_WIDTH, 1024);
    assertEquals(1024, tiffParser.getFirstIFD().getImageWidth());
  }

  @Test
  public void testOverwriteComment() throws FormatException, IOException {
    out.seek(0);
    tiffSaver.writeHeader();
    tiffSaver.writeIFD(ifd, 0);
    tiffSaver.overwriteComment(in, "new comment");
    assertTrue("new comment".equals(tiffParser.getComment()));
  }

}
