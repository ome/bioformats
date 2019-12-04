/*
 * #%L
 * Bio-Formats command line tools for reading and converting files
 * %%
 * Copyright (C) 2017 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package loci.formats.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Arrays;

import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.ImageWriter;
import loci.formats.FormatException;
import loci.formats.tools.ImageConverter;
import loci.formats.out.OMETiffWriter;

import org.apache.commons.lang.ArrayUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Tests the functionality of ImageConverter
 */
public class ImageConverterTest {

  private Path tempDir;
  private File outFile;
  private int width = 512;
  private final SecurityManager oldSecurityManager = System.getSecurityManager();
  private final PrintStream oldOut = System.out;
  private final PrintStream oldErr = System.err;

  protected static class ExitException extends SecurityException {
    public final int status;
    public ExitException(int status) {
      this.status = status;
    }
  }

  private static class NoExitSecurityManager extends SecurityManager {
    @Override
    public void checkPermission(Permission perm) {}

    @Override
    public void checkPermission(Permission perm, Object context) {}

    @Override
    public void checkExit(int status) {
      super.checkExit(status);
      throw new ExitException(status);
    }
  }

  @BeforeMethod
  public void setUp() throws IOException {
    System.setSecurityManager(new NoExitSecurityManager());

    tempDir = Files.createTempDirectory(this.getClass().getName());
    tempDir.toFile().deleteOnExit();
    width = 512;
  }

  @AfterMethod
  public void resetSecurityManager() {
    System.setSecurityManager(oldSecurityManager);
  }

  @AfterClass
  public void tearDown() {
    System.setOut(oldOut);
    System.setErr(oldErr);
  }

  @DataProvider(name = "suffixes")
  public Object[][] createSuffixes() {
    return new Object[][] {{".ome.tiff"}, {".tif"}, {".ics"}};
  }

  @DataProvider(name = "options")
  public Object[][] createOptions() {
    return new Object[][] {{"-z 2"}, {"-series 0 -z 2"}, {"-channel 1"}, 
      {"-series 0 -channel 1"}, {"-series 0 -timepoint 3"}, {"-timepoint 3"}, {"-series 1"}, 
      {"-series 1 -channel 1"}, {"-series 1 -timepoint 3"}, {"-series 1 -channel 1 -timepoint 3"}};
  }

  public void checkImage(String outFileToCheck, int expectedWidth) throws FormatException, IOException {
    IFormatReader r = new ImageReader();
    r.setId(outFileToCheck);
    assertEquals(r.getSizeX(), expectedWidth);
    r.close();
  }

  public void checkImage() throws FormatException, IOException {
    checkImage(outFile.getAbsolutePath(), width);
  }

  public void assertConversion(String[] args) throws FormatException, IOException {
    assertConversion(args, outFile.getAbsolutePath(), width);
  }

  public void assertConversion(String[] args, String outFileToCheck, int expectedWidth) throws FormatException, IOException {
    try {
      ImageConverter.main(args);
    } catch (ExitException e) {
      outFile.deleteOnExit();
      assertEquals(e.status, 0);
      checkImage(outFileToCheck, expectedWidth);
    }
  }

  public void testConstructor() throws FormatException, IOException {
    outFile = tempDir.resolve("test.ome.tiff").toFile();
    outFile.deleteOnExit();
    ImageConverter converter = new ImageConverter();
    boolean status = converter.testConvert(new ImageWriter(), new String[] {"test.fake", outFile.getAbsolutePath()});
    assertEquals(status, 0);
    checkImage();
  }

  @Test(dataProvider = "suffixes")
  public void testDefault(String suffix) throws FormatException, IOException {
    outFile = tempDir.resolve("test" + suffix).toFile();
    String[] args = {"test.fake", outFile.getAbsolutePath()};
    assertConversion(args);
  }

  @Test(dataProvider = "options")
  public void testOptions(String options) throws FormatException, IOException {
    outFile = tempDir.resolve("test.ome.tiff").toFile();
    String[] optionsArgs = options.split(" ");
    ArrayList<String> argsList = new ArrayList<String>();
    argsList.add("test&sizeZ=3&sizeC=2&sizeT=4&series=2.fake");
    argsList.addAll(Arrays.asList(optionsArgs));
    argsList.add(outFile.getAbsolutePath());
    String [] args = new String[argsList.size()];
    assertConversion(argsList.toArray(args));
  }

  @Test(dataProvider = "suffixes")
  public void testOverwrite(String suffix) throws FormatException, IOException {
    outFile = Files.createTempFile(tempDir, "test", suffix).toFile();
    String[] args = {"-overwrite", "test.fake", outFile.getAbsolutePath()};
    assertConversion(args);
  }

  @Test
  public void testBadArgument() throws FormatException, IOException {
    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outContent));
    outFile = tempDir.resolve("test.ome.tiff").toFile();
    String[] args = {"-foo", "test.fake", outFile.getAbsolutePath()};
    try {
      ImageConverter.main(args);
    } catch (ExitException e) {
      assertEquals(e.status, 1);
      assertEquals(
        "Found unknown command flag: -foo; exiting." +
        System.getProperty("line.separator"), outContent.toString());
    }
  }

  @Test
  public void testCompanion() throws FormatException, IOException {
    outFile = tempDir.resolve("test.ome.tiff").toFile();
    File compFile = tempDir.resolve("test.companion.ome").toFile();
    String[] args = {
      "-option", OMETiffWriter.COMPANION_KEY, compFile.getAbsolutePath(),
      "test.fake", outFile.getAbsolutePath()
    };
    try {
      ImageConverter.main(args);
    } catch (ExitException e) {
      outFile.deleteOnExit();
      compFile.deleteOnExit();
      assertEquals(e.status, 0);
      assertTrue(compFile.exists());
      checkImage();
    }
  }

  @Test
  public void testSPWSeries() throws FormatException, IOException {
    outFile = tempDir.resolve("plate.ome.tiff").toFile();
    String[] args = {
            "-series", "0",
            "plate&plates=1&fields=2.fake", outFile.getAbsolutePath()
    };
    assertConversion(args);
  }

  @Test
  public void testCrop() throws FormatException, IOException {
    outFile = tempDir.resolve("test.ome.tiff").toFile();
    String[] args = {
      "-tilex", "128", "-tiley", "128",
      "-crop", "256,256,256,256", "test.fake", outFile.getAbsolutePath()};
    width = 256;
    try {
      ImageConverter.main(args);
    } catch (ExitException e) {
      outFile.deleteOnExit();
      assertEquals(e.status, 0);
      checkImage();
    }
  }

  @Test
  public void testCropOddTileSize() throws FormatException, IOException {
    outFile = tempDir.resolve("odd-test.ome.tiff").toFile();
    String[] args = {
      "-tilex", "128", "-tiley", "128",
      "-crop", "123,127,129,131", "test.fake", outFile.getAbsolutePath()
    };
    width = 129;
    try {
      ImageConverter.main(args);
    }
    catch (ExitException e) {
      outFile.deleteOnExit();
      assertEquals(e.status, 0);
      checkImage();
    }
  }
  
  @Test
  public void testCropLargertThanTileSize() throws FormatException, IOException {
    outFile = tempDir.resolve("large-crop.ome.tiff").toFile();
    String[] args = {
      "-tilex", "128", "-tiley", "128",
      "-crop", "0,0,256,256", "test&sizeX=128&sizeY=128.fake", outFile.getAbsolutePath()
    };
    width = 128;
    try {
      ImageConverter.main(args);
    }
    catch (ExitException e) {
      outFile.deleteOnExit();
      assertEquals(e.status, 0);
      checkImage();
    }
  }

  @Test(dataProvider = "options")
  public void testTileOptions(String options) throws FormatException, IOException {
    outFile = tempDir.resolve("tile-options.ome.tiff").toFile();
    String[] optionsArgs = options.split(" ");
    String[] tileArgs = {"-tilex", "128", "-tiley", "128"};
    ArrayList<String> argsList = new ArrayList<String>();
    argsList.add("test&sizeZ=3&sizeC=2&sizeT=4&series=3&sizeX=512&sizeY=512.fake");
    argsList.addAll(Arrays.asList(optionsArgs));
    argsList.addAll(Arrays.asList(tileArgs));
    argsList.add(outFile.getAbsolutePath());
    String [] args = new String[argsList.size()];
    assertConversion(argsList.toArray(args));
  }

  @Test(dataProvider = "options")
  public void testIndividualTiles(String options) throws FormatException, IOException {
    outFile = tempDir.resolve("seperate-tiles_%x_%y_%m.ome.tiff").toFile();
    String[] optionsArgs = options.split(" ");
    String[] tileArgs = {"-tilex", "256", "-tiley", "256"};
    ArrayList<String> argsList = new ArrayList<String>();
    argsList.add("test&sizeZ=3&sizeC=2&sizeT=4&series=3&sizeX=512&sizeY=512.fake");
    argsList.addAll(Arrays.asList(optionsArgs));
    argsList.addAll(Arrays.asList(tileArgs));
    argsList.add(outFile.getAbsolutePath());
    String [] args = new String[argsList.size()];
    File outFileToCheck = outFile = tempDir.resolve("seperate-tiles_0_0_0.ome.tiff").toFile();
    assertConversion(argsList.toArray(args), outFileToCheck.getAbsolutePath(), 256);
  }
}
