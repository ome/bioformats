package loci.plugins.in;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import ij.ImagePlus;

import java.io.IOException;

import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.plugins.util.BF;

import org.junit.Test;

public class ImporterTest {

  // -- ImporterTest methods --

  @Test
  public void testBasic() {
    final int sizeX = 477, sizeY = 393;
    final int pixelType = FormatTools.UINT8;
    final String path = constructFakeFilename("basic", sizeX, sizeY, pixelType);
    ImagePlus[] imps = null;
    
    try {
      imps = BF.openImagePlus(path);
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    catch (FormatException e) {
      fail(e.getMessage());
    }
    
    assertNotNull(imps);
    assertTrue(imps.length == 1);
    assertNotNull(imps[0]);
    assertTrue(imps[0].getWidth() == sizeX);
    assertTrue(imps[0].getHeight() == sizeY);
    System.out.println("It worked!");//TEMP
  }

  @Test
  public void testCrop() {
    final int sizeX = 253, sizeY = 511;
    final int cropSizeX = 112, cropSizeY = 457;
    final int pixelType = FormatTools.UINT8;
    final String path = constructFakeFilename("crop", sizeX, sizeY, pixelType);
    ImagePlus[] imps = null;
    try {
	    final ImporterOptions options = new ImporterOptions();
      options.setId(path);
      options.setCrop(true);
      // TODO: pass crop parameters to options somehow
      imps = BF.openImagePlus(options);
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    catch (FormatException e) {
      fail(e.getMessage());
    }

    assertNotNull(imps);
    assertTrue(imps.length == 0);
    assertNotNull(imps[0]);
    assertTrue(imps[0].getWidth() == cropSizeX);
    assertTrue(imps[0].getHeight() == cropSizeY);
  }
  
  // -- Helper methods --

  private String constructFakeFilename(String title,
    int sizeX, int sizeY, int pixelType)
  {
    return title +
      "&pixelType=" + FormatTools.getPixelTypeString(pixelType) +
      "&sizeX=" + sizeX + "&sizeY=" + sizeY + ".fake";
  }

  // -- Main method --

  public static void main(String[] args) {
    ImporterTest test = new ImporterTest();
    test.testBasic();
    //test.testCrop();
    System.exit(0);
  }

}
