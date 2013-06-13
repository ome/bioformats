/*
 * #%L
 * OME SCIFIO package for reading and converting scientific file formats.
 * %%
 * Copyright (C) 2005 - 2013 Open Microscopy Environment:
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
 *
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package loci.formats.utests;

import static org.testng.AssertJUnit.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;

import loci.formats.in.FakeReader;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class FakeReaderTest {

  private File fake, fakeIni;

  private FakeReader reader;

  @BeforeMethod
  public void setUp() throws Exception {
    fake = File.createTempFile(this.getClass().getName(), ".fake");
    fake.deleteOnExit();
    fakeIni = new File(fake.getAbsolutePath() + ".ini");
    RandomAccessFile raf = new RandomAccessFile(fakeIni, "rw");
    try {
        raf.write(new byte[0]);
    } finally {
        raf.close();
    }
    fakeIni.deleteOnExit();
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

}
