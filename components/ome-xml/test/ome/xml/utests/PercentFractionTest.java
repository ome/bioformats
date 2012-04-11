
package ome.xml.utests;

import static org.testng.AssertJUnit.*;

import ome.xml.model.primitives.PercentFraction;

import org.testng.annotations.Test;

/**
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/ome-xml/test/ome/xml/utests/PercentFractionTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/ome-xml/test/ome/xml/utests/PercentFractionTest.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class PercentFractionTest {

  @Test(expectedExceptions={IllegalArgumentException.class})
  public void testMinusOne() {
    new PercentFraction(-1.0f);
  }

  @Test
  public void testZero() {
    new PercentFraction(0.0f);
  }

  @Test
  public void testPositiveOne() {
    new PercentFraction(1.0f);
  }

  @Test
  public void testEquivalence() {
    PercentFraction a = new PercentFraction(0.5f);
    PercentFraction b = new PercentFraction(0.5f);
    PercentFraction c = new PercentFraction(1.0f);
    Float d = new Float(0.5f);
    Float e = new Float(1.0f);
    Float f = new Float(1.5f);
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
    assertEquals("1.0", new PercentFraction(1.0f).toString());
  }
}
