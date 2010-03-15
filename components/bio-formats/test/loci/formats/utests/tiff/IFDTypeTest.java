//
// IFDTypeTest.java
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

import static org.testng.AssertJUnit.*;

import org.testng.annotations.Test;

import loci.common.enumeration.EnumException;
import loci.formats.tiff.IFDType;

/**
 * @author callan
 *
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/test/loci/formats/utests/tiff/IFDTypeTest.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/test/loci/formats/utests/tiff/IFDTypeTest.java">SVN</a></dd></dl>
 */
public class IFDTypeTest {

  @Test
  public void testLookupByte() {
    IFDType pi = IFDType.get(1);
    assertEquals(IFDType.BYTE, pi);
  }
  
  @Test(expectedExceptions={ EnumException.class })
  public void testUnknownCode() {
    IFDType.get(-1);
  }
}
