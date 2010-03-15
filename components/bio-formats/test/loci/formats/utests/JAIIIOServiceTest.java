//
// JAIIIOServiceTest.java
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

import static org.testng.AssertJUnit.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.services.JAIIIOService;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author callan
 *
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/test/loci/formats/utests/JAIIIOServiceTest.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/test/loci/formats/utests/JAIIIOServiceTest.java">SVN</a></dd></dl>
 */
public class JAIIIOServiceTest {

  private static final int SIZE_X = 64;

  private static final int SIZE_Y = 96;

  private static final int[] CODE_BLOCK = new int[] { 4, 4 };

  private static final int IMAGE_TYPE = BufferedImage.TYPE_INT_ARGB;

  private JAIIIOService service;

  @BeforeMethod
  public void setUp() throws DependencyException {
    ServiceFactory sf = new ServiceFactory();
    service = sf.getInstance(JAIIIOService.class);
  }

  @Test
  public ByteArrayOutputStream testWriteImageLossy()
    throws IOException, ServiceException {
    BufferedImage image = new BufferedImage(SIZE_X, SIZE_Y, IMAGE_TYPE);
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    // Code block size minimum is 4x4
    service.writeImage(stream, image, false, CODE_BLOCK, 1.0);
    assertTrue(stream.size() > 0);
    return stream;
  }

  @Test
  public ByteArrayOutputStream testWriteImageLossless()
    throws IOException, ServiceException {
    BufferedImage image = new BufferedImage(SIZE_X, SIZE_Y, IMAGE_TYPE);
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    // Code block size minimum is 4x4
    service.writeImage(stream, image, true, CODE_BLOCK, 1.0);
    assertTrue(stream.size() > 0);
    return stream;
  }

  @Test
  public void testReadImageLossy() throws IOException, ServiceException {
    ByteArrayOutputStream outputStream = testWriteImageLossy();
    ByteArrayInputStream inputStream = 
      new ByteArrayInputStream(outputStream.toByteArray());
    BufferedImage image = service.readImage(inputStream);
    assertNotNull(image);
    assertEquals(SIZE_X, image.getWidth());
    assertEquals(SIZE_Y, image.getHeight());
  }

  @Test
  public void testReadImageLossless() throws IOException, ServiceException {
    ByteArrayOutputStream outputStream = testWriteImageLossless();
    ByteArrayInputStream inputStream = 
      new ByteArrayInputStream(outputStream.toByteArray());
    BufferedImage image = service.readImage(inputStream);
    assertNotNull(image);
    assertEquals(SIZE_X, image.getWidth());
    assertEquals(SIZE_Y, image.getHeight());
  }

}
