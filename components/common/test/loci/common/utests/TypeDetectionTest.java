//
// TypeDetectionTest.java
//

/*
LOCI Common package: utilities for I/O, reflection and miscellaneous tasks.
Copyright (C) 2005-@year@ Melissa Linkert and Curtis Rueden.

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

package loci.common.utests;

import static org.testng.AssertJUnit.assertEquals;

import java.io.File;
import java.io.IOException;

import loci.common.BZip2Handle;
import loci.common.GZipHandle;
import loci.common.ZipHandle;

import org.testng.annotations.Test;

/**
 * Tests compressed IRandomAccess implementation type detection.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/common/test/loci/common/utests/TypeDetectionTest.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/common/test/loci/common/utests/TypeDetectionTest.java">SVN</a></dd></dl>
 *
 * @see loci.common.IRandomAcess
 */
public class TypeDetectionTest {

  @Test
  public void testBZip2TypeDetection() throws IOException {
    File invalidFile = File.createTempFile("invalid", ".bz2");
    invalidFile.deleteOnExit();
    assertEquals(BZip2Handle.isBZip2File(invalidFile.getAbsolutePath()), false);
  }

  @Test
  public void testGZipTypeDetection() throws IOException {
    File invalidFile = File.createTempFile("invalid", ".gz");
    invalidFile.deleteOnExit();
    assertEquals(GZipHandle.isGZipFile(invalidFile.getAbsolutePath()), false);
  }

  @Test
  public void testZipTypeDetection() throws IOException {
    File invalidFile = File.createTempFile("invalid", ".zip");
    invalidFile.deleteOnExit();
    assertEquals(ZipHandle.isZipFile(invalidFile.getAbsolutePath()), false);
  }
}
