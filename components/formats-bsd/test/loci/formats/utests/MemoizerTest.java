/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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
import static org.testng.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;

import loci.formats.Memoizer;
import loci.formats.in.DynamicMetadataOptions;
import loci.formats.in.FakeReader;
import loci.formats.in.MetadataLevel;
import loci.formats.in.MetadataOptions;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class MemoizerTest {

  public static class IdentityMetadataOptions implements MetadataOptions {

    private MetadataLevel level = MetadataLevel.ALL;
    private boolean validate;

    @Override
    public void setMetadataLevel(MetadataLevel metadataLevel) {
      level = metadataLevel;
    }

    @Override
    public MetadataLevel getMetadataLevel() {
      return level;
    }

    @Override
    public void setValidate(boolean validateMetadata) {
      validate = validateMetadata;
    }

    @Override
    public boolean isValidate() {
      return validate;
    }
  }

  public static final class ValueMetadataOptions
    extends IdentityMetadataOptions
  {

    @Override
    public boolean equals(Object other) {
      if (this == other) {
        return true;
      }
      if (other == null || getClass() != other.getClass()) {
        return false;
      }
      ValueMetadataOptions options = (ValueMetadataOptions) other;
      return getMetadataLevel() == options.getMetadataLevel() &&
        isValidate() == options.isValidate();
    }

    @Override
    public int hashCode() {
      int result = getMetadataLevel() == null ? 0 :
        getMetadataLevel().hashCode();
      return 31 * result + (isValidate() ? 1 : 0);
    }
  }

  private static class FailingInstallMemoizer extends Memoizer {

    FailingInstallMemoizer(FakeReader reader) {
      super(reader, 0);
    }

    @Override
    protected void installMemo(File source, File destination)
      throws IOException
    {
      throw new IOException("expected installation failure");
    }
  }

  private static class CachePathMemoizer extends Memoizer {

    String cachePath(String path) {
      return getCachePath(path);
    }
  }

  private static final String TEST_FILE =
    "test&pixelType=int8&sizeX=20&sizeY=20&sizeC=1&sizeZ=1&sizeT=1.fake";
  private static final String TMP_PREFIX = MemoizerTest.class.getName() + ".";

  private File idDir;
  private String id;
  private FakeReader reader;
  private final List<Path> temporaryDirectories = new ArrayList<Path>();

  private File createTempDir() throws Exception {
    Path directory = Files.createTempDirectory(TMP_PREFIX);
    temporaryDirectories.add(directory);
    return directory.toFile();
  }

  private static byte[] utf8(String value) {
    return value.getBytes(StandardCharsets.UTF_8);
  }

  private static void setDifferentModificationTime(File file,
    long previousTime) throws Exception
  {
    Files.setLastModifiedTime(file.toPath(),
      FileTime.fromMillis(previousTime + 2000));
    assertFalse(file.lastModified() == previousTime);
  }

  private static void deleteRecursively(Path root) throws IOException {
    if (!Files.exists(root)) {
      return;
    }
    Files.walkFileTree(root, new SimpleFileVisitor<Path>() {

      @Override
      public FileVisitResult preVisitDirectory(Path directory,
        BasicFileAttributes attributes) throws IOException
      {
        directory.toFile().setWritable(true);
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult visitFile(Path file,
        BasicFileAttributes attributes) throws IOException
      {
        Files.delete(file);
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult postVisitDirectory(Path directory,
        IOException failure) throws IOException
      {
        if (failure != null) {
          throw failure;
        }
        Files.delete(directory);
        return FileVisitResult.CONTINUE;
      }
    });
  }

  private static void checkMemo(Memoizer memoizer, String id)
      throws Exception {
    memoizer.setId(id);
    assertFalse(memoizer.isLoadedFromMemo());
    assertTrue(memoizer.isSavedToMemo());
    memoizer.close();
    memoizer.setId(id);
    assertTrue(memoizer.isLoadedFromMemo());
    assertFalse(memoizer.isSavedToMemo());
    memoizer.close();
  }

  private static void checkNoMemo(Memoizer memoizer, String id)
      throws Exception {
    memoizer.setId(id);
    assertFalse(memoizer.isLoadedFromMemo());
    assertFalse(memoizer.isSavedToMemo());
    memoizer.close();
  }

  private void checkMemoFile(File memoFile) {
    checkMemoFile(memoFile, idDir);
  }

  private void checkMemoFile(File memoFile, File memoDir) {
    File expMemoFile = new File(memoDir, "." + TEST_FILE + ".bfmemo");
    assertEquals(memoFile.getAbsolutePath(), expMemoFile.getAbsolutePath());
  }

  private File getExpectedCacheDirectory(File directory) {
    String sourcePath = idDir.getAbsolutePath();
    String cachePath;

    if (sourcePath.length() >= 3 &&
      Character.isLetter(sourcePath.charAt(0)) &&
      sourcePath.charAt(1) == ':' &&
      sourcePath.charAt(2) == File.separatorChar)
    {
      cachePath = Character.toUpperCase(sourcePath.charAt(0)) +
        sourcePath.substring(2);
    } else if (sourcePath.length() >= 2 &&
      sourcePath.charAt(0) == File.separatorChar &&
      sourcePath.charAt(1) == File.separatorChar)
    {
      cachePath = "UNC" + sourcePath.substring(1);
    } else {
      int first = 0;
      while (first < sourcePath.length() &&
        sourcePath.charAt(first) == File.separatorChar)
      {
        first++;
      }
      cachePath = sourcePath.substring(first);
    }
    return new File(directory, cachePath);
  }

  @BeforeMethod(alwaysRun = true)
  public void setUp() throws Exception {
    idDir = createTempDir();
    File tempFile = new File(idDir, TEST_FILE);
    tempFile.createNewFile();
    id = tempFile.getAbsolutePath();
    reader = new FakeReader(); // No setId !
  }

  @AfterMethod(alwaysRun = true)
  public void tearDown() throws Exception {
    Exception failure = null;
    if (reader != null) {
      try {
        reader.close();
      }
      catch (Exception e) {
        failure = e;
      }
    }
    for (int i = temporaryDirectories.size() - 1; i >= 0; i--) {
      try {
        deleteRecursively(temporaryDirectories.get(i));
      }
      catch (Exception e) {
        if (failure == null) {
          failure = e;
        }
        else {
          failure.addSuppressed(e);
        }
      }
    }
    temporaryDirectories.clear();
    if (failure != null) {
      throw failure;
    }
  }

  @Test
  public void testDefaultConstructor() throws Exception {
    try (Memoizer memoizer = new Memoizer()) {
      checkMemoFile(memoizer.getMemoFile(id));
    }
  }

  @Test
  public void testNullReader() throws Exception {
    try (Memoizer memoizer = new Memoizer(null)) {
      checkMemoFile(memoizer.getMemoFile(id));
    }
  }

  @Test
  public void testConstructorTimeElapsed() throws Exception {
    try (Memoizer memoizer = new Memoizer(0)) {
      checkMemoFile(memoizer.getMemoFile(id));
      checkMemo(memoizer, id);
    }
  }

  @Test
  public void testConstructorReader() throws Exception {
    try (Memoizer memoizer = new Memoizer(reader)) {
      checkMemoFile(memoizer.getMemoFile(id));
    }
  }

  @Test
  public void testConstructorReaderTimeElapsed() throws Exception {
    try (Memoizer memoizer = new Memoizer(reader, 0)) {
      checkMemoFile(memoizer.getMemoFile(id));
      checkMemo(memoizer, id);
    }
  }

  @Test
  public void testConstructorTimeElapsedDirectory() throws Exception {
    File directory = createTempDir();
    directory.delete();
    try (Memoizer memoizer = new Memoizer(0, directory)) {
      // Check non-existing memo directory returns null
      assertNull(memoizer.getMemoFile(id));
      directory.mkdirs();
      checkMemoFile(memoizer.getMemoFile(id),
        getExpectedCacheDirectory(directory));
      checkMemo(memoizer, id);
    }
  }

  @Test
  public void testConstructorTimeElapsedNull() throws Exception {
    try (Memoizer memoizer = new Memoizer(0, null)) {
      // Check null memo directory returns null
      assertNull(memoizer.getMemoFile(id));
      checkNoMemo(memoizer, id);
    }
  }

  @Test
  public void testConstructorReaderTimeElapsedDirectory() throws Exception {
    File directory = createTempDir();
    directory.delete();
    try (Memoizer memoizer = new Memoizer(reader, 0, directory)) {
      // Check non-existing memo directory returns null
      assertNull(memoizer.getMemoFile(id));
      directory.mkdirs();
      checkMemoFile(memoizer.getMemoFile(id),
        getExpectedCacheDirectory(directory));
      checkMemo(memoizer, id);
    }
  }

  @Test
  public void testConstructorReaderTimeElapsedNull() throws Exception {
    try (Memoizer memoizer = new Memoizer(reader, 0, null)) {
      // Check null memo directory returns null
      assertNull(memoizer.getMemoFile(id));
      checkNoMemo(memoizer, id);
    }
  }

  @Test
  public void testGetMemoFilePermissionsDirectory() throws Exception {
    File directory = createTempDir();
    try (Memoizer memoizer = new Memoizer(reader, 0, directory)) {
      if (directory.setWritable(false)) {
        assertNull(memoizer.getMemoFile(id));
      }
    }
  }

  @Test
  public void testGetMemoFilePermissionsInPlaceDirectory() throws Exception {
    try (Memoizer memoizer = new Memoizer(reader, 0, idDir)) {
      if (idDir.setWritable(false)) {
        assertNull(memoizer.getMemoFile(id));
      }
    }
  }

  @Test
  public void testGetMemoFilePermissionsInPlace() throws Exception {
    try (Memoizer memoizer = new Memoizer(reader)) {
      if (idDir.setWritable(false)) {
        assertNull(memoizer.getMemoFile(id));
      }
    }
  }

  @Test
  public void testRelocate() throws Exception {
    // Create an in-place memo file
    try (Memoizer memoizer = new Memoizer(reader, 0)) {
      memoizer.setId(id);
      memoizer.close();
      assertFalse(memoizer.isLoadedFromMemo());
      assertTrue(memoizer.isSavedToMemo());

      // Rename the directory (including the file and the memo file)
      File newidDir = new File(idDir.getAbsolutePath() + ".new");
      temporaryDirectories.add(newidDir.toPath());
      Files.move(idDir.toPath(), newidDir.toPath());
      File newtempFile = new File(newidDir, TEST_FILE);
      String newid = newtempFile.getAbsolutePath();

      // Try to reopen the file with the Memoizer
      memoizer.setId(newid);
      memoizer.close();
      assertTrue(memoizer.isLoadedFromMemo());
      assertFalse(memoizer.isSavedToMemo());
    }
  }

  @Test
  public void testDeleteMemo() throws Exception {
    // Create an in-place memo file
    try (Memoizer memoizer = new Memoizer(reader, 0)) {
      memoizer.setId(id);
      memoizer.close();
      assertFalse(memoizer.isLoadedFromMemo());
      assertTrue(memoizer.isSavedToMemo());

      // attempt to delete the memo file, and make sure it's really gone
      File currentMemoFile = memoizer.getMemoFile();
      assertTrue(currentMemoFile.exists());
      boolean success = memoizer.deleteMemo();
      assertTrue(success);
      assertFalse(currentMemoFile.exists());
    }
  }

  @Test
  public void testWrappedReader() throws Exception {
    try (Memoizer memoizer = new Memoizer(reader, 0)) {
      File memoFile = memoizer.getMemoFile(id);
      assertFalse(memoFile.exists());
      reader.setId(id);
      assertFalse(memoFile.exists());
      reader.close();
      checkMemo(memoizer, id);
    }
  }

  @Test
  public void testChangedDynamicOptionsInvalidateMemo() throws Exception {
    DynamicMetadataOptions firstOptions = new DynamicMetadataOptions();
    firstOptions.set("reader.option", "first");
    reader.setMetadataOptions(firstOptions);
    try (Memoizer first = new Memoizer(reader, 0)) {
      first.setId(id);
      assertTrue(first.isSavedToMemo());
    }

    FakeReader secondReader = new FakeReader();
    DynamicMetadataOptions secondOptions = new DynamicMetadataOptions();
    secondOptions.set("reader.option", "second");
    secondReader.setMetadataOptions(secondOptions);
    try (Memoizer second = new Memoizer(secondReader, 0)) {
      second.setId(id);
      assertFalse(second.isLoadedFromMemo());
    }
  }

  @Test
  public void testValueMetadataOptionsLoadMemo() throws Exception {
    reader.setMetadataOptions(new ValueMetadataOptions());
    try (Memoizer first = new Memoizer(reader, 0)) {
      first.setId(id);
      assertTrue(first.isSavedToMemo());
    }

    FakeReader secondReader = new FakeReader();
    secondReader.setMetadataOptions(new ValueMetadataOptions());
    try (Memoizer second = new Memoizer(secondReader, 0)) {
      second.setId(id);
      assertTrue(second.isLoadedFromMemo());
    }
  }

  @Test
  public void testIdentityMetadataOptionsSafelyMissMemo() throws Exception {
    reader.setMetadataOptions(new IdentityMetadataOptions());
    try (Memoizer first = new Memoizer(reader, 0)) {
      first.setId(id);
      assertTrue(first.isSavedToMemo());
    }

    FakeReader secondReader = new FakeReader();
    secondReader.setMetadataOptions(new IdentityMetadataOptions());
    try (Memoizer second = new Memoizer(secondReader, 0)) {
      second.setId(id);
      assertFalse(second.isLoadedFromMemo());
      assertTrue(second.isSavedToMemo());
    }
  }

  @Test
  public void testOptionsFileParticipatesInMemoCompatibility()
    throws Exception
  {
    File optionsFile = new File(id + ".bfoptions");
    byte[] firstContents = utf8("[options]\nreader.option=first\n");
    byte[] changedContents = utf8("[options]\nreader.option=other\n");
    assertEquals(firstContents.length, changedContents.length);
    Files.write(optionsFile.toPath(), firstContents);

    try (Memoizer first = new Memoizer(reader, 0)) {
      first.setId(id);
      assertTrue(first.isSavedToMemo());
    }

    try (Memoizer unchanged = new Memoizer(new FakeReader(), 0)) {
      unchanged.setId(id);
      assertTrue(unchanged.isLoadedFromMemo());
    }

    long previousTime = optionsFile.lastModified();
    Files.write(optionsFile.toPath(), changedContents);
    Files.setLastModifiedTime(optionsFile.toPath(),
      FileTime.fromMillis(previousTime));
    assertEquals(optionsFile.length(), (long) firstContents.length);
    assertEquals(optionsFile.lastModified(), previousTime);

    try (Memoizer changed = new Memoizer(new FakeReader(), 0)) {
      changed.setId(id);
      assertFalse(changed.isLoadedFromMemo());
      assertTrue(changed.isSavedToMemo());
    }

    try (Memoizer replacement = new Memoizer(new FakeReader(), 0)) {
      replacement.setId(id);
      assertTrue(replacement.isLoadedFromMemo());
    }
  }

  @Test
  public void testChangedCompanionFileInvalidatesMemo() throws Exception {
    File companion = new File(id + ".ini");
    Files.write(companion.toPath(), utf8("sizeX=20\n"));

    try (Memoizer first = new Memoizer(reader, 0)) {
      first.setId(id);
      assertTrue(first.isSavedToMemo());
    }

    try (Memoizer unchanged = new Memoizer(new FakeReader(), 0)) {
      unchanged.setId(id);
      assertTrue(unchanged.isLoadedFromMemo());
    }

    long previousTime = companion.lastModified();
    Files.write(companion.toPath(), utf8("sizeX=30\n"));
    setDifferentModificationTime(companion, previousTime);
    try (Memoizer changed = new Memoizer(new FakeReader(), 0)) {
      changed.setId(id);
      assertFalse(changed.isLoadedFromMemo());
      assertTrue(changed.isSavedToMemo());
      assertEquals(changed.getSizeX(), 30);
    }

    try (Memoizer replacement = new Memoizer(new FakeReader(), 0)) {
      replacement.setId(id);
      assertTrue(replacement.isLoadedFromMemo());
      assertEquals(replacement.getSizeX(), 30);
    }
  }

  @Test
  public void testAddedCompanionFileInvalidatesMemo() throws Exception {
    try (Memoizer first = new Memoizer(reader, 0)) {
      first.setId(id);
      assertTrue(first.isSavedToMemo());
    }

    File companion = new File(id + ".ini");
    Files.write(companion.toPath(), utf8("sizeX=30\n"));

    try (Memoizer changed = new Memoizer(new FakeReader(), 0)) {
      changed.setId(id);
      assertFalse(changed.isLoadedFromMemo());
      assertTrue(changed.isSavedToMemo());
      assertEquals(changed.getSizeX(), 30);
    }

    try (Memoizer replacement = new Memoizer(new FakeReader(), 0)) {
      replacement.setId(id);
      assertTrue(replacement.isLoadedFromMemo());
      assertEquals(replacement.getSizeX(), 30);
    }
  }

  @Test
  public void testDeletedCompanionFileInvalidatesMemo() throws Exception {
    File companion = new File(id + ".ini");
    Files.write(companion.toPath(), utf8("sizeX=30\n"));

    try (Memoizer first = new Memoizer(reader, 0)) {
      first.setId(id);
      assertTrue(first.isSavedToMemo());
    }
    assertTrue(companion.delete());

    try (Memoizer changed = new Memoizer(new FakeReader(), 0)) {
      changed.setId(id);
      assertFalse(changed.isLoadedFromMemo());
      assertTrue(changed.isSavedToMemo());
      assertEquals(changed.getSizeX(), 20);
    }

    try (Memoizer replacement = new Memoizer(new FakeReader(), 0)) {
      replacement.setId(id);
      assertTrue(replacement.isLoadedFromMemo());
      assertEquals(replacement.getSizeX(), 20);
    }
  }

  @Test
  public void testInstallFailureIsNotReportedAsSaved() throws Exception {
    try (Memoizer memoizer = new FailingInstallMemoizer(reader)) {
      File memoFile = memoizer.getMemoFile(id);
      memoizer.setId(id);
      assertFalse(memoizer.isSavedToMemo());
      assertFalse(memoFile.exists());
    }

    File[] files = idDir.listFiles();
    if (files == null) {
      throw new AssertionError("Could not list temporary test directory");
    }
    for (File file : files) {
      assertFalse(file.getName().contains(".bfmemo."));
    }
  }

  @Test
  public void testFailedReplacementIsNotReportedAsSaved() throws Exception {
    File companion = new File(id + ".ini");
    Files.write(companion.toPath(), utf8("sizeX=20\n"));

    File memoFile;
    try (Memoizer first = new Memoizer(reader, 0)) {
      first.setId(id);
      memoFile = first.getMemoFile();
      assertTrue(first.isSavedToMemo());
    }
    assertTrue(memoFile.exists());

    long previousTime = companion.lastModified();
    Files.write(companion.toPath(), utf8("sizeX=30\n"));
    setDifferentModificationTime(companion, previousTime);

    try (Memoizer failed =
      new FailingInstallMemoizer(new FakeReader()))
    {
      failed.setId(id);
      assertFalse(failed.isLoadedFromMemo());
      assertFalse(failed.isSavedToMemo());
      assertEquals(failed.getSizeX(), 30);
      assertTrue(memoFile.exists());
    }

    try (Memoizer replacement = new Memoizer(new FakeReader(), 0)) {
      replacement.setId(id);
      assertFalse(replacement.isLoadedFromMemo());
      assertTrue(replacement.isSavedToMemo());
      assertEquals(replacement.getSizeX(), 30);
    }
  }

  @Test
  public void testWindowsDriveIsPartOfCachePath() throws Exception {
    try (CachePathMemoizer memoizer = new CachePathMemoizer()) {
      String driveC = memoizer.cachePath("C:\\somedir\\foo.nd2");
      String driveD = memoizer.cachePath("D:\\somedir\\foo.nd2");

      assertEquals(driveC,
        new File("C" + File.separator + "somedir", "foo.nd2").getPath());
      assertEquals(driveD,
        new File("D" + File.separator + "somedir", "foo.nd2").getPath());
      assertFalse(driveC.equals(driveD));
    }
  }

  @Test
  public void testUncShareIsPartOfCachePath() throws Exception {
    try (CachePathMemoizer memoizer = new CachePathMemoizer()) {
      String first = memoizer.cachePath("\\\\server\\first\\foo.nd2");
      String second = memoizer.cachePath("\\\\server\\second\\foo.nd2");

      assertTrue(first.startsWith("UNC" + File.separator));
      assertTrue(second.startsWith("UNC" + File.separator));
      assertFalse(first.equals(second));
    }
  }

  @Test
  public void testWindowsMemoFilePreservesVolumeIdentity() throws Exception {
    if (File.separatorChar != '\\') {
      return;
    }
    File directory = createTempDir();
    try (Memoizer memoizer = new Memoizer(0, directory)) {
      File driveC = memoizer.getMemoFile("C:\\somedir\\foo.nd2");
      File driveD = memoizer.getMemoFile("D:\\somedir\\foo.nd2");
      File firstShare = memoizer.getMemoFile(
        "\\\\server\\first\\foo.nd2");
      File secondShare = memoizer.getMemoFile(
        "\\\\server\\second\\foo.nd2");

      assertFalse(driveC.equals(driveD));
      assertFalse(firstShare.equals(secondShare));
      assertTrue(driveC.toPath().startsWith(directory.toPath()));
      assertTrue(driveD.toPath().startsWith(directory.toPath()));
      assertTrue(firstShare.toPath().startsWith(directory.toPath()));
      assertTrue(secondShare.toPath().startsWith(directory.toPath()));
    }
  }

}
