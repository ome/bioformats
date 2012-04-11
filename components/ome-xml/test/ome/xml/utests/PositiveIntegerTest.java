
package ome.xml.utests;

import static org.testng.AssertJUnit.*;

import ome.xml.model.primitives.PositiveInteger;

import org.testng.annotations.Test;

/**
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/ome-xml/test/ome/xml/utests/PositiveIntegerTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/ome-xml/test/ome/xml/utests/PositiveIntegerTest.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class PositiveIntegerTest {

  @Test(expectedExceptions={IllegalArgumentException.class})
  public void testMinusOne() {
    new PositiveInteger(-1);
  }

  @Test(expectedExceptions={IllegalArgumentException.class})
  public void testZero() {
    new PositiveInteger(0);
  }

  @Test
  public void testPositiveOne() {
    new PositiveInteger(1);
  }

  @Test
  public void testEquivalence() {
    PositiveInteger a = new PositiveInteger(1);
    PositiveInteger b = new PositiveInteger(1);
    PositiveInteger c = new PositiveInteger(2);
    Integer d = new Integer(1);
    Integer e = new Integer(2);
    Integer f = new Integer(3);
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
    assertEquals("1", new PositiveInteger(1).toString());
  }
}
