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

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.io.File;
import java.util.UUID;

import loci.formats.FormatTools;
import loci.formats.Memoizer;
import loci.formats.in.FakeReader;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 */
public class MemoizerTest {

  private static final String TEST_FILE =
    "test&pixelType=int8&sizeX=20&sizeY=20&sizeC=1&sizeZ=1&sizeT=1.fake";

  private File idDir;

  private String id;

  private FakeReader reader;

  private Memoizer memoizer;

  private static int fullPlaneCallIndex;

  private int sizeX;

  private int sizeY;

  private int bpp;

  private int planeSize;

  @BeforeMethod
  public void setUp() throws Exception {
    fullPlaneCallIndex = 1;
    // No mapping.
    // Location.mapId(TEST_FILE, TEST_FILE);
    reader = new FakeReader();
    try {
      String uuid = UUID.randomUUID().toString();
      idDir = new File(System.getProperty("java.io.tmpdir"), uuid);
      idDir.mkdirs();
      File tempFile = new File(idDir, TEST_FILE);
      tempFile.createNewFile();
      id = tempFile.getAbsolutePath();
      reader.setId(id);
      sizeX = reader.getSizeX();
      sizeY = reader.getSizeY();
      bpp = FormatTools.getBytesPerPixel(reader.getPixelType());
      planeSize = sizeY * sizeY * bpp;
    } finally {
      reader.close();
    }
    reader = new FakeReader(); // No setId !
  }

  @AfterMethod
  public void tearDown() throws Exception {
    memoizer.close();
    reader.close();
  }

  @Test
  public void testSimple() throws Exception {
      memoizer = new Memoizer(reader);
      File f = memoizer.getMemoFile(id);
      if (f != null && f.exists()) {
        f.delete();
      }
      // At this point we're sure that there's no memo file.
      reader.setId(id);
      reader.close();
      memoizer.setId(id);
      memoizer.close();
      memoizer.setId(id);
      memoizer.close();
  }

  public void testDefaultConstructor() throws Exception {
      memoizer = new Memoizer();
      File f = memoizer.getMemoFile(id);
      File memoFile = new File(idDir, "." + TEST_FILE + ".bfmemo");
      assertEquals(f.getAbsolutePath(), memoFile.getAbsolutePath());
  }

  @Test
  public void testConstructorTimeElapsed() throws Exception {
      memoizer = new Memoizer(0);
      File f = memoizer.getMemoFile(id);
      File memoFile = new File(idDir, "." + TEST_FILE + ".bfmemo");
      assertEquals(f.getAbsolutePath(), memoFile.getAbsolutePath());

      // Test multiple setId invocations
      memoizer.setId(id);
      assertFalse(memoizer.isLoadedFromMemo());
      assertTrue(memoizer.isSavedToMemo());
      memoizer.close();
      memoizer.setId(id);
      assertTrue(memoizer.isLoadedFromMemo());
      assertFalse(memoizer.isSavedToMemo());
      memoizer.close();
  }

  @Test
  public void testConstructorReader() throws Exception {
      memoizer = new Memoizer(reader);
      File f = memoizer.getMemoFile(id);
      File memoFile = new File(idDir, "." + TEST_FILE + ".bfmemo");
      assertEquals(f.getAbsolutePath(), memoFile.getAbsolutePath());
  }

  @Test
  public void testConstructorReaderTimeElapsed() throws Exception {
      memoizer = new Memoizer(reader, 0);
      File f = memoizer.getMemoFile(id);
      File memoFile = new File(idDir, "." + TEST_FILE + ".bfmemo");
      assertEquals(f.getAbsolutePath(), memoFile.getAbsolutePath());

      // Test multiple setId invocations
      memoizer.setId(id);
      assertFalse(memoizer.isLoadedFromMemo());
      assertTrue(memoizer.isSavedToMemo());
      memoizer.close();
      memoizer.setId(id);
      assertTrue(memoizer.isLoadedFromMemo());
      assertFalse(memoizer.isSavedToMemo());
      memoizer.close();
  }

  @Test
  public void testConstructorTimeElapsedDirectory() throws Exception {

    String uuid = UUID.randomUUID().toString();
    File directory = new File(System.getProperty("java.io.tmpdir"), uuid);
    memoizer = new Memoizer(0, directory);

    // Check non-existing memo directory returns null
    assertEquals(memoizer.getMemoFile(id), null);

    // Create memoizer directory and memoizer reader
    directory.mkdirs();

    String memoDir = idDir.getAbsolutePath();
    memoDir = memoDir.substring(memoDir.indexOf(File.separator) + 1);
    File memoFile = new File(directory, memoDir);
    memoFile = new File(memoFile, "." + TEST_FILE + ".bfmemo");
    File f = memoizer.getMemoFile(id);
    assertEquals(f.getAbsolutePath(), memoFile.getAbsolutePath());

    // Test multiple setId invocations
    memoizer.setId(id);
    assertFalse(memoizer.isLoadedFromMemo());
    assertTrue(memoizer.isSavedToMemo());
    memoizer.close();
    memoizer.setId(id);
    assertTrue(memoizer.isLoadedFromMemo());
    assertFalse(memoizer.isSavedToMemo());
    memoizer.close();
  }

  @Test
  public void testConstructorTimeElapsedNull() throws Exception {

    memoizer = new Memoizer(0, null);

    // Check null memo directory returns null
    assertEquals(memoizer.getMemoFile(id), null);

    // Test setId invocation
    memoizer.setId(id);
    assertFalse(memoizer.isLoadedFromMemo());
    assertFalse(memoizer.isSavedToMemo());
    memoizer.close();

  }

  @Test
  public void testConstructorReaderTimeElapsedDirectory() throws Exception {

    String uuid = UUID.randomUUID().toString();
    File directory = new File(System.getProperty("java.io.tmpdir"), uuid);
    memoizer = new Memoizer(reader, 0, directory);

    // Check non-existing memo directory returns null
    assertEquals(memoizer.getMemoFile(id), null);

    // Create memoizer directory and memoizer reader
    directory.mkdirs();

    String memoDir = idDir.getAbsolutePath();
    memoDir = memoDir.substring(memoDir.indexOf(File.separator) + 1);
    File memoFile = new File(directory, memoDir);
    memoFile = new File(memoFile, "." + TEST_FILE + ".bfmemo");
    File f = memoizer.getMemoFile(id);
    assertEquals(f.getAbsolutePath(), memoFile.getAbsolutePath());

    // Test multiple setId invocations
    memoizer.setId(id);
    assertFalse(memoizer.isLoadedFromMemo());
    assertTrue(memoizer.isSavedToMemo());
    memoizer.close();
    memoizer.setId(id);
    assertTrue(memoizer.isLoadedFromMemo());
    assertFalse(memoizer.isSavedToMemo());
    memoizer.close();
  }


  @Test
  public void testConstructorReaderTimeElapsedNull() throws Exception {

    memoizer = new Memoizer(reader, 0, null);

    // Check null memo directory returns null
    assertEquals(memoizer.getMemoFile(id), null);

    // Test setId invocation
    memoizer.setId(id);
    assertFalse(memoizer.isLoadedFromMemo());
    assertFalse(memoizer.isSavedToMemo());
    memoizer.close();
  }

  @Test
  public void testGetMemoFilePermissionsDirectory() throws Exception {
      String uuid = UUID.randomUUID().toString();
      File directory = new File(System.getProperty("java.io.tmpdir"), uuid);
      memoizer = new Memoizer(reader, 0, directory);

      // Check non-existing memo directory returns null
      assertEquals(memoizer.getMemoFile(id), null);

      // Create memoizer directory and memoizer reader
      directory.mkdirs();
      memoizer = new Memoizer(reader, 0, directory);

      // Check existing non-writeable memo directory returns null
      if (File.separator.equals("/")) {
        // File.setWritable() does not work properly on Windows
        directory.setWritable(false);
        assertEquals(memoizer.getMemoFile(id), null);
      }

      // Check existing writeable memo diretory returns a memo file
      directory.setWritable(true);
      String memoDir = idDir.getAbsolutePath();
      memoDir = memoDir.substring(memoDir.indexOf(File.separator) + 1);
      File memoFile = new File(directory, memoDir);
      memoFile = new File(memoFile, "." + TEST_FILE + ".bfmemo");
      assertEquals(memoizer.getMemoFile(id).getAbsolutePath(),
                   memoFile.getAbsolutePath());
  }

  @Test
  public void testGetMemoFilePermissionsInPlaceDirectory() throws Exception {
      String rootPath = id.substring(0, id.indexOf(File.separator) + 1);
      memoizer = new Memoizer(reader, 0, new File(rootPath));

      // Check non-writeable file directory returns null for in-place caching
      if (File.separator.equals("/")) {
        // File.setWritable() does not work properly on Windows
        idDir.setWritable(false);
        assertEquals(memoizer.getMemoFile(id), null);
      }

      // Check writeable file directory returns memo file beside file
      idDir.setWritable(true);
      File memoFile = new File(idDir, "." + TEST_FILE + ".bfmemo");
      assertEquals(memoizer.getMemoFile(id).getAbsolutePath(),
        memoFile.getAbsolutePath());
  }

  @Test
  public void testGetMemoFilePermissionsInPlace() throws Exception {
      memoizer = new Memoizer(reader);

      // Check non-writeable file directory returns null for in-place caching
      if (File.separator.equals("/")) {
        // File.setWritable() does not work properly on Windows
        idDir.setWritable(false);
        assertEquals(memoizer.getMemoFile(id), null);
      }
      // Check writeable file directory returns memo file beside file
      idDir.setWritable(true);
      File memoFile = new File(idDir, "." + TEST_FILE + ".bfmemo");
      assertEquals(memoizer.getMemoFile(id).getAbsolutePath(),
        memoFile.getAbsolutePath());
  }

  @Test
  public void testRelocate() throws Exception {
    // Create an in-place memo file
    memoizer = new Memoizer(reader, 0);
    memoizer.setId(id);
    memoizer.close();
    assertFalse(memoizer.isLoadedFromMemo());
    assertTrue(memoizer.isSavedToMemo());

    // Rename the directory (including the file and the memo file)
    String uuid = UUID.randomUUID().toString();
    File newidDir = new File(System.getProperty("java.io.tmpdir"), uuid);
    idDir.renameTo(newidDir);
    File newtempFile = new File(newidDir, TEST_FILE);
    String newid = newtempFile.getAbsolutePath();

    // Try to reopen the file with the Memoizer
    memoizer.setId(newid);
    memoizer.close();
    assertFalse(memoizer.isLoadedFromMemo());
    assertTrue(memoizer.isSavedToMemo());
  }

  public static void main(String[] args) throws Exception {
    MemoizerTest t = new MemoizerTest();
    t.setUp();
    try {
      t.testSimple();
    } finally {
      t.tearDown();
    }
  }
}
