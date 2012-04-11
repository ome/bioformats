
package ome.xml.utests;

import static org.testng.AssertJUnit.*;

import ome.xml.model.primitives.NonNegativeLong;

import org.testng.annotations.Test;

/**
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/ome-xml/test/ome/xml/utests/NonNegativeLongTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/ome-xml/test/ome/xml/utests/NonNegativeLongTest.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class NonNegativeLongTest {

  @Test(expectedExceptions={IllegalArgumentException.class})
  public void testMinusOne() {
    new NonNegativeLong(-1L);
  }

  @Test
  public void testZero() {
    new NonNegativeLong(0L);
  }

  @Test
  public void testPositiveOne() {
    new NonNegativeLong(1L);
  }

  @Test
  public void testEquivalence() {
    NonNegativeLong a = new NonNegativeLong(1L);
    NonNegativeLong b = new NonNegativeLong(1L);
    NonNegativeLong c = new NonNegativeLong(2L);
    Long d = new Long(1L);
    Long e = new Long(2L);
    Long f = new Long(3L);
    assertFalse(a == b);
    assertFalse(a == c);
    assertTrue(a.equals(b));
    assertFalse(a.equals(c));
    assertTrue(a.equals(d));
    assertFalse(a.equals(e));
    assertTrue(c.equals(e));
    assertFalse(a.equals(f));
  }

  @Test
  public void testToString() {
    assertEquals("1", new NonNegativeLong(1L).toString());
  }
}
