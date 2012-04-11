
package ome.xml.utests;

import static org.testng.AssertJUnit.*;

import ome.xml.model.enums.Binning;
import ome.xml.model.enums.Correction;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.Immersion;
import ome.xml.model.enums.handlers.BinningEnumHandler;
import ome.xml.model.enums.handlers.CorrectionEnumHandler;
import ome.xml.model.enums.handlers.ImmersionEnumHandler;
import ome.xml.model.primitives.NonNegativeInteger;

import org.testng.annotations.Test;

/**
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/ome-xml/test/ome/xml/utests/EnumHandlerTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/ome-xml/test/ome/xml/utests/EnumHandlerTest.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class EnumHandlerTest {

  @Test
  public void testImmersionOI() throws EnumerationException {
    ImmersionEnumHandler handler = new ImmersionEnumHandler();
    Immersion v = (Immersion) handler.getEnumeration("OI");
    assertEquals(Immersion.OIL, v);
  }

  @Test
  public void testImmersionOIWithWhitespace() throws EnumerationException {
    ImmersionEnumHandler handler = new ImmersionEnumHandler();
    Immersion v = (Immersion) handler.getEnumeration("   OI  ");
    assertEquals(Immersion.OIL, v);
  }

  @Test
  public void testImmersionDRY() throws EnumerationException {
    ImmersionEnumHandler handler = new ImmersionEnumHandler();
    Immersion v = (Immersion) handler.getEnumeration("DRY");
    assertEquals(Immersion.AIR, v);
  }

  @Test
  public void testImmersionDRYWithWhitespace() throws EnumerationException {
    ImmersionEnumHandler handler = new ImmersionEnumHandler();
    Immersion v = (Immersion) handler.getEnumeration("   DRY  ");
    assertEquals(Immersion.AIR, v);
  }

  @Test
  public void testImmersionWl() throws EnumerationException {
    ImmersionEnumHandler handler = new ImmersionEnumHandler();
    Immersion v = (Immersion) handler.getEnumeration("Wl");
    assertEquals(Immersion.WATER, v);
  }

  @Test
  public void testBinning1x1WithWhitespace() throws EnumerationException {
    BinningEnumHandler handler = new BinningEnumHandler();
    Binning v = (Binning) handler.getEnumeration("   1 x 1  ");
    assertEquals(Binning.ONEXONE, v);
  }

  @Test
  public void testBinning2x2WithWhitespace() throws EnumerationException {
    BinningEnumHandler handler = new BinningEnumHandler();
    Binning v = (Binning) handler.getEnumeration("   2 x 2  ");
    assertEquals(Binning.TWOXTWO, v);
  }

  @Test
  public void testBinning4x4WithWhitespace() throws EnumerationException {
    BinningEnumHandler handler = new BinningEnumHandler();
    Binning v = (Binning) handler.getEnumeration("   4 x 4  ");
    assertEquals(Binning.FOURXFOUR, v);
  }

  @Test
  public void testBinning8x8WithWhitespace() throws EnumerationException {
    BinningEnumHandler handler = new BinningEnumHandler();
    Binning v = (Binning) handler.getEnumeration("   8 x 8  ");
    assertEquals(Binning.EIGHTXEIGHT, v);
  }
}
