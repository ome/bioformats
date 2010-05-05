package loci.plugins.in;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import ij.ImagePlus;

import java.io.IOException;

import loci.formats.FormatException;
import loci.plugins.util.BF;

import org.junit.Test;

public class ImporterTest {

  // -- ImporterTest methods --

  @Test
  public void testBasic() {
    try {
      final int sizeX = 477, sizeY = 393;
      final String path = constructFakeFilename("basic", sizeX, sizeY);
      final ImagePlus[] imps = BF.openImagePlus(path);
      assertNotNull(imps);
      assertTrue(imps.length == 1);
      assertNotNull(imps[0]);
      assertTrue(imps[0].getWidth() == sizeX);
      assertTrue(imps[0].getHeight() == sizeY);
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    catch (FormatException e) {
      fail(e.getMessage());
    }
    System.out.println("It worked!");//TEMP
  }

  @Test
  public void testCrop() {
    try {
      final ImporterOptions options = new ImporterOptions();
      final int sizeX = 253, sizeY = 511;
      final int cropSizeX = 112, cropSizeY = 457;
      final String path = constructFakeFilename("crop", sizeX, sizeY);
      options.setId(path);
      options.setCrop(true);
      // TODO: pass crop parameters to options somehow
      final ImagePlus[] imps = BF.openImagePlus(options);
      assertNotNull(imps);
      assertTrue(imps.length == 0);
      assertNotNull(imps[0]);
      assertTrue(imps[0].getWidth() == cropSizeX);
      assertTrue(imps[0].getHeight() == cropSizeY);
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    catch (FormatException e) {
      fail(e.getMessage());
    }
  }
  
  // -- Helper methods --

  private String constructFakeFilename(String title, int sizeX, int sizeY) {
    return title + "&sizeX=" + sizeX + "&sizeY=" + sizeY + ".fake";
  }

  // -- Main method --

  public static void main(String[] args) {
    ImporterTest test = new ImporterTest();
    test.testBasic();
    //test.testCrop();
  }

}
