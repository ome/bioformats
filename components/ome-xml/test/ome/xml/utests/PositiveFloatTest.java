
package ome.xml.utests;

import static org.testng.AssertJUnit.*;

import ome.xml.model.primitives.PositiveFloat;

import org.testng.annotations.Test;

/**
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/ome-xml/test/ome/xml/utests/PositiveFloatTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/ome-xml/test/ome/xml/utests/PositiveFloatTest.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class PositiveFloatTest {

  @Test(expectedExceptions={IllegalArgumentException.class})
  public void testMinusOne() {
    new PositiveFloat(-1.0);
  }

  @Test(expectedExceptions={IllegalArgumentException.class})
  public void testZero() {
    new PositiveFloat(0.0);
  }

  @Test
  public void testPositiveOne() {
    new PositiveFloat(1.0);
  }

  @Test
  public void testEquivalence() {
    PositiveFloat a = new PositiveFloat(0.1);
    PositiveFloat b = new PositiveFloat(0.1);
    PositiveFloat c = new PositiveFloat(2.0);
    Double d = new Double(0.1);
    Double e = new Double(2.0);
    Double f = new Double(3.0);
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
    assertEquals("0.1", new PositiveFloat(0.1).toString());
  }
}
