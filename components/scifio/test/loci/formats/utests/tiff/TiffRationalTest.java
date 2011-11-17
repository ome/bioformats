//
// TiffRationalTest.java
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
import static org.testng.AssertJUnit.assertTrue;
import loci.formats.tiff.TiffRational;

import org.testng.annotations.Test;

/**
 * Unit tests for TIFF rationals.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/test/loci/formats/utests/tiff/TiffRationalTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/test/loci/formats/utests/tiff/TiffRationalTest.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class TiffRationalTest {

  @Test
  public void testEqualTiffRational() {
    TiffRational a = new TiffRational(1, 4);
    TiffRational b = new TiffRational(1, 4);
    assertTrue(a.equals(b));
    assertTrue(a.equals((Object) b));
    assertEquals(0, a.compareTo(b));
  }
  
  @Test
  public void testEqualObject() {
    TiffRational a = new TiffRational(1, 4);
    Object b = new Object();
    assertTrue(!(a.equals(b)));
  }

  @Test
  public void testNotEqual() {
    TiffRational a = new TiffRational(1, 4);
    TiffRational b = new TiffRational(1, 5);
    assertTrue(!(a.equals(b)));
  }

  @Test
  public void testGreaterThan() {
    TiffRational a = new TiffRational(1, 4);
    TiffRational b = new TiffRational(1, 5);
    assertEquals(1, a.compareTo(b));
  }

  @Test
  public void testLessThan() {
    TiffRational a = new TiffRational(1, 5);
    TiffRational b = new TiffRational(1, 4);
    assertEquals(-1, a.compareTo(b));
  }
}
