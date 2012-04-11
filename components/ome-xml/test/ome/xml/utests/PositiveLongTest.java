
package ome.xml.utests;

import static org.testng.AssertJUnit.*;

import ome.xml.model.primitives.PositiveLong;

import org.testng.annotations.Test;

/**
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/ome-xml/test/ome/xml/utests/PositiveLongTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/ome-xml/test/ome/xml/utests/PositiveLongTest.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class PositiveLongTest {

  @Test(expectedExceptions={IllegalArgumentException.class})
  public void testMinusOne() {
    new PositiveLong(-1L);
  }

  @Test(expectedExceptions={IllegalArgumentException.class})
  public void testZero() {
    new PositiveLong(0L);
  }

  @Test
  public void testPositiveOne() {
    new PositiveLong(1L);
  }

  @Test
  public void testEquivalence() {
    PositiveLong a = new PositiveLong(1L);
    PositiveLong b = new PositiveLong(1L);
    PositiveLong c = new PositiveLong(2L);
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
    assertEquals("1", new PositiveLong(1L).toString());
  }
}
