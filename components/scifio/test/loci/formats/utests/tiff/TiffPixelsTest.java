//
// TiffPixelsTest.java
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

import java.io.IOException;

import loci.common.ByteArrayHandle;
import loci.common.RandomAccessInputStream;
import loci.common.RandomAccessOutputStream;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffCompression;
import loci.formats.tiff.TiffParser;
import loci.formats.tiff.TiffSaver;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests saving and reading TIFF pixel data that has been compressed using
 * various schemes.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/test/loci/formats/utests/tiff/TiffPixelsTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/test/loci/formats/utests/tiff/TiffPixelsTest.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class TiffPixelsTest {

  private static final int IMAGE_WIDTH = 64;

  private static final int IMAGE_LENGTH = 64;

  private static final int BITS_PER_PIXEL = 16;

  private IFD ifd = new IFD();

  private byte[] data;

  @BeforeMethod
  public void setUp() {
    ifd.put(IFD.IMAGE_WIDTH, IMAGE_WIDTH);
    ifd.put(IFD.IMAGE_LENGTH, IMAGE_LENGTH);
    ifd.put(IFD.BITS_PER_SAMPLE, new int[] { BITS_PER_PIXEL });
    ifd.put(IFD.SAMPLES_PER_PIXEL, 1);
    ifd.put(IFD.LITTLE_ENDIAN, Boolean.TRUE);
    ifd.put(IFD.ROWS_PER_STRIP, new long[] {IMAGE_LENGTH});
    ifd.put(IFD.STRIP_OFFSETS, new long[] {0});
    data = new byte[IMAGE_WIDTH * IMAGE_LENGTH * (BITS_PER_PIXEL / 8)];
    for (int i=0; i<data.length; i++) {
      data[i] = (byte) i;
    }
    ifd.put(IFD.STRIP_BYTE_COUNTS, new long[] {data.length});
  }

  @Test
  public void testUNCOMPRESSED() throws FormatException, IOException {
    ifd.put(IFD.COMPRESSION, TiffCompression.UNCOMPRESSED.getCode());
    byte[] plane = readSavedPlane();
    for (int i=0; i<plane.length; i++) {
      assertEquals(plane[i], data[i]);
    }
  }

  @Test(expectedExceptions={ FormatException.class })
  public void testCCITT_1D() throws FormatException, IOException {
    ifd.put(IFD.COMPRESSION, TiffCompression.CCITT_1D.getCode());
    byte[] plane = readSavedPlane();
    for (int i=0; i<plane.length; i++) {
      assertEquals(plane[i], data[i]);
    }
  }

  @Test(expectedExceptions={ FormatException.class })
  public void testGROUP_3_FAX() throws FormatException, IOException {
    ifd.put(IFD.COMPRESSION, TiffCompression.GROUP_3_FAX.getCode());
    byte[] plane = readSavedPlane();
    for (int i=0; i<plane.length; i++) {
      assertEquals(plane[i], data[i]);
    }
  }

  @Test(expectedExceptions={ FormatException.class })
  public void testGROUP_4_FAX() throws FormatException, IOException {
    ifd.put(IFD.COMPRESSION, TiffCompression.GROUP_4_FAX.getCode());
    byte[] plane = readSavedPlane();
    for (int i=0; i<plane.length; i++) {
      assertEquals(plane[i], data[i]);
    }
  }

  @Test
  public void testLZW() throws FormatException, IOException {
    ifd.put(IFD.COMPRESSION, TiffCompression.LZW.getCode());
    byte[] plane = readSavedPlane();
    for (int i=0; i<plane.length; i++) {
      assertEquals(plane[i], data[i]);
    }
  }

  @Test(expectedExceptions={ FormatException.class })
  public void testPACK_BITS() throws FormatException, IOException {
    ifd.put(IFD.COMPRESSION, TiffCompression.PACK_BITS.getCode());
    byte[] plane = readSavedPlane();
    for (int i=0; i<plane.length; i++) {
      assertEquals(plane[i], data[i]);
    }
  }

  @Test
  public void testPROPRIETARY_DEFLATE() throws FormatException, IOException {
    ifd.put(IFD.COMPRESSION, TiffCompression.PROPRIETARY_DEFLATE.getCode());
    byte[] plane = readSavedPlane();
    for (int i=0; i<plane.length; i++) {
      assertEquals(plane[i], data[i]);
    }
  }

  @Test
  public void testDEFLATE() throws FormatException, IOException {
    ifd.put(IFD.COMPRESSION, TiffCompression.DEFLATE.getCode());
    byte[] plane = readSavedPlane();
    for (int i=0; i<plane.length; i++) {
      assertEquals(plane[i], data[i]);
    }
  }

  // -- Helper method --

  private byte[] readSavedPlane() throws FormatException, IOException {
    ByteArrayHandle savedData = new ByteArrayHandle();
    RandomAccessOutputStream out = new RandomAccessOutputStream(savedData);
    RandomAccessInputStream in = new RandomAccessInputStream(savedData);
    TiffSaver saver = new TiffSaver(out, savedData);
    //saver.setInputStream(in);
    saver.writeImage(data, ifd, 0, FormatTools.UINT16, false);
    out.close();
    TiffParser parser = new TiffParser(in);
    byte[] plane = new byte[data.length];
    parser.getSamples(ifd, plane);
    in.close();
    return plane;
  }

}
