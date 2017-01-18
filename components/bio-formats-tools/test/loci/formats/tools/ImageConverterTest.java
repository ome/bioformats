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
import java.security.Permission;

import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.ImageWriter;
import loci.formats.FormatException;
import loci.formats.tools.ImageConverter;

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

  private File outFile;
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

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
  public void setUp() {
    System.setOut(new PrintStream(outContent));
    System.setErr(new PrintStream(errContent));
    System.setSecurityManager(new NoExitSecurityManager());
  }

  @AfterMethod
  public void tearDown() {
    System.setOut(null);
    System.setErr(null);
    System.setSecurityManager(null);
  }

  @DataProvider(name = "suffixes")
  public Object[][] createSuffixes() {
    return new Object[][] {{".ome.tiff"}, {".tif"}, {".ics"}};
  }

  public void checkImage() throws FormatException, IOException {
    IFormatReader r = new ImageReader();
    r.setId(outFile.getAbsolutePath());
    assertEquals(r.getSizeX(), 512);
    r.close();
  }

  @Test(dataProvider = "suffixes")
  public void testDefault(String suffix) throws FormatException, IOException {
    outFile = File.createTempFile("test", suffix);
    outFile.delete();
    String[] args = {"test.fake", outFile.getAbsolutePath()};
    try {
      ImageConverter.main(args);
    } catch (ExitException e) {
      outFile.deleteOnExit();
      assertEquals(e.status, 0);
      checkImage();
    }
  }

  @Test(dataProvider = "suffixes")
  public void testOverwrite(String suffix) throws FormatException, IOException {
    outFile = File.createTempFile("test", suffix);
    String[] args = {"-overwrite", "test.fake", outFile.getAbsolutePath()};
    try {
      ImageConverter.main(args);
    } catch (ExitException e) {
      outFile.deleteOnExit();
      assertEquals(e.status, 0);
      checkImage();
    }
  }

  @Test
  public void testBadArgument() throws FormatException, IOException {
    outFile = File.createTempFile("test", "ome.tif");
    outFile.deleteOnExit();
    String[] args = {"-foo", "test.fake", outFile.getAbsolutePath()};
    try {
      ImageConverter.main(args);
    } catch (ExitException e) {
      assertEquals(e.status, 1);
      assertEquals("Found unknown command flag: -foo; exiting.\n", outContent.toString());
    }
  }
}
