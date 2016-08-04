/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
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

package loci.formats.utests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;

import loci.common.Location;
import loci.formats.in.FakeReader;
import loci.formats.ome.OMEXMLMetadata;
import loci.formats.tools.FakeImage;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class FakeReaderTest {

  private File fake, fakeIni, fourChannelFake;

  private Location oneWell, twoWells, twoFields, twoPlates;

  private FakeReader reader;

  @BeforeMethod
  public void setUp() throws Exception {
    fake = File.createTempFile(this.getClass().getName(), ".fake");
    fourChannelFake = File.createTempFile("&sizeC=4&"
        + this.getClass().getName(), ".fake");
    fake.deleteOnExit();
    fourChannelFake.deleteOnExit();
    fakeIni = new File(fake.getAbsolutePath() + ".ini");
    RandomAccessFile raf = new RandomAccessFile(fakeIni, "rw");
    try {
        raf.write(new byte[0]);
    } finally {
        raf.close();
    }
    fakeIni.deleteOnExit();

    // With JDK 7 this code can be simplified (using New I/O)
    oneWell = new FakeImage(new Location(fake.getParent() + File.separator
        + this.getClass().getName() + System.currentTimeMillis() + ".fake"))
    .generateScreen(1, 1, 1, 1, 1);
    deleteTemporaryDirectoryOnExit(oneWell);

    twoWells = new FakeImage(new Location(fake.getParent() + File.separator
        + this.getClass().getName() + System.currentTimeMillis() + ".fake"))
    .generateScreen(1, 1, 1, 2, 1);
    deleteTemporaryDirectoryOnExit(twoWells);

    twoFields = new FakeImage(new Location(fake.getParent() + File.separator
        + this.getClass().getName() + System.currentTimeMillis() + ".fake"))
    .generateScreen(1, 1, 1, 1, 2);
    deleteTemporaryDirectoryOnExit(twoFields);

    twoPlates = new FakeImage(new Location(fake.getParent() + File.separator
        + this.getClass().getName() + System.currentTimeMillis() + ".fake"))
    .generateScreen(2, 2, 2, 2, 4);
    deleteTemporaryDirectoryOnExit(twoPlates);

    reader = new FakeReader();
  }

  @AfterMethod
  public void tearDown() throws Exception {
    if (fake.exists()) {
        fake.delete();
    }
    if (fakeIni.exists()) {
      fakeIni.delete();
    }
    reader.close();
  }

  @Test
  public void testCompanionFile() throws Exception {
    reader.setId(fake.getAbsolutePath());
    assertEquals(2, reader.getUsedFiles().length);
    assertEquals(2, reader.getSeriesUsedFiles().length);
    assertEquals(2, reader.getUsedFiles(false).length);
    assertEquals(2, reader.getSeriesUsedFiles(false).length);
    assertEquals(1, reader.getUsedFiles(true).length);
    assertEquals(1, reader.getSeriesUsedFiles(true).length);
  }

  @Test
  public void testNoCompanionFile() throws Exception {
    fakeIni.delete();
    reader.setId(fake.getAbsolutePath());
    assertEquals(1, reader.getUsedFiles().length);
    assertEquals(1, reader.getSeriesUsedFiles().length);
    assertEquals(1, reader.getUsedFiles(false).length);
    assertEquals(1, reader.getSeriesUsedFiles(false).length);
    assertEquals(0, reader.getUsedFiles(true).length);
    assertEquals(0, reader.getSeriesUsedFiles(true).length);
  }

  @Test
  public void testDefaultValues() throws Exception {
    reader.setId(fake.getAbsolutePath());
    assertEquals(512, reader.getSizeX());
  }

  @Test
  public void testValuesFromFilename() throws Exception {
    addToFileName("sizeX", "256");
    reader.setId(fake.getAbsolutePath());
    assertEquals(256, reader.getSizeX());
  }

  @Test
  public void testValuesFromIni() throws Exception {
    addToIniFile("sizeX", "128");
    reader.setId(fake.getAbsolutePath());
    assertEquals(128, reader.getSizeX());
  }

  @Test
  public void testGetSizeCWithFakeFile() throws Exception {
    reader.setId(fake.getAbsolutePath());
    int expectedChannelCount = 1;
    assertEquals(reader.getSizeC(), expectedChannelCount);
  }

  @Test
  public void testGetSizeCWithFourChannelFake() throws Exception {
    reader.setId(fourChannelFake.getAbsolutePath());
    int expectedChannelCount = 4;
    assertEquals(reader.getSizeC(), expectedChannelCount);
  }

  @Test
  public void testIsSingleFileReturnsTrueForOneWell() throws Exception {
    assertTrue(reader.isSingleFile(oneWell.getAbsolutePath()));
  }

  @Test
  public void testIsSingleFileReturnsFalseForTwoWells() throws Exception {
    assertFalse(reader.isSingleFile(twoWells.getAbsolutePath()));
  }

  @Test
  public void testIsThisTypeReturnsTrueForOneWell() {
    assertTrue(reader.isThisType(oneWell.getAbsolutePath()));
  }

  @Test
  public void testGetSeriesUsedFilesReturnsOneForOneWell() throws Exception {
    reader.setId(oneWell.getAbsolutePath());
    assertEquals(1, reader.getSeriesUsedFiles(false).length);
    assertEquals(0, reader.getSeriesUsedFiles(true).length);
  }

  @Test
  public void testGetSeriesUsedFilesReturnsTwoForTwoWells() throws Exception {
    reader.setId(twoWells.getAbsolutePath());
    assertEquals(2, reader.getSeriesUsedFiles(false).length);
    assertEquals(0, reader.getSeriesUsedFiles(true).length);
  }

  @Test
  public void testSetIdWithOneWell() throws Exception {
    reader.setId(oneWell.getAbsolutePath());
    assertEquals(1, reader.getOmeXmlMetadata().getWellCount(0));
    assertEquals(1, reader.getUsedFiles().length);
  }

  @Test
  public void testSetIdWithTwoWells() throws Exception {
    reader.setId(twoWells.getAbsolutePath());
    assertEquals(2, reader.getOmeXmlMetadata().getWellCount(0));
    assertEquals(2, reader.getUsedFiles().length);
  }

  @Test
  public void testGetSeriesCountWithTwoFields() throws Exception {
    reader.setId(twoFields.getAbsolutePath());
    assertEquals(2, reader.getSeriesCount());
  }

  @Test
  public void testGetOmeXmlMetadataWithTwoPlates() throws Exception {
    reader.setId(twoPlates.getAbsolutePath());
    OMEXMLMetadata metadata = reader.getOmeXmlMetadata();
    int i = reader.getImageCount();
    while (i >= 0) {
        assertEquals(metadata.getChannelCount(i--), reader.getSizeC());
    }
  }

  @Test
  public void testExtraMetadata() throws Exception {
    RandomAccessFile raf = new RandomAccessFile(fakeIni, "rw");
    try {
      StringBuilder sb = new StringBuilder();
      sb.append("\n[GlobalMetadata]\nfoo=bar\n");
      raf.writeUTF(sb.toString());
    } finally {
      raf.close();
    }
    reader.setId(fakeIni.getAbsolutePath());
    assertEquals(reader.getGlobalMetadata().get("foo"), "bar");
  }

  //
  // HELPERS
  //

  String stripSuffix(File f, String suffix) {
    String abs = f.getAbsolutePath();
    if (!abs.endsWith(suffix)) {
      throw new IllegalArgumentException(abs + " doesn't end with " + suffix);
    }
    return abs.substring(0, abs.length() - suffix.length());
  }

  void checkArgs(String... args) {
    if (args == null || args.length == 0) {
      throw new IllegalArgumentException("Nothing to do");
    } else if (args.length % 2 != 0) {
      throw new IllegalArgumentException("Args must come in pairs");
    }
  }

  void addToFileName(String...args) {
    checkArgs(args);
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < args.length / 2; i++) {
      sb.append("&");
      sb.append(args[i]);
      i++;
      sb.append("=");
      sb.append(args[i]);
    }

    String fakePrefix = stripSuffix(fake, ".fake");

    File newFake = new File(fakePrefix + sb.toString() + ".fake");
    assertEquals(true, fake.renameTo(newFake));
    fake = newFake;
    fake.deleteOnExit();

    File newProp = new File(fakePrefix + sb.toString() + ".fake.ini");
    assertEquals(true, fakeIni.renameTo(newProp));
    fakeIni = newProp;
    fakeIni.deleteOnExit();
  }

  void addToIniFile(String... args) throws Exception {
    checkArgs(args);
    FileOutputStream fos = new FileOutputStream(fakeIni);
    PrintWriter pw = new PrintWriter(fos, true);
    try {
      for (int i = 0; i < args.length / 2; i++) {
        pw.print(args[i]);
        pw.print("=");
        i++;
        pw.println(args[i]);
      }
    } finally {
      fos.close();
      pw.close();
    }
  }

  /** Removes fake SPW folders - deleteOnExit() has to be called on each. */
  void deleteTemporaryDirectoryOnExit(Location directoryRoot) {
    directoryRoot.deleteOnExit();
    Location[] children = directoryRoot.listFiles();
    if (children != null) {
      for (Location child : children) {
        if (child.isDirectory()) {
          deleteTemporaryDirectoryOnExit(child);
        } else {
          child.deleteOnExit();
        }
      }
    }
  }

}
