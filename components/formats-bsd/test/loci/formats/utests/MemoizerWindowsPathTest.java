/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2026 Open Microscopy Environment
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
 * AND ANY EXPRESS OR IMPLIED WARRANTIES ARE DISCLAIMED.
 * #L%
 */

package loci.formats.utests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import loci.formats.Memoizer;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

public class MemoizerWindowsPathTest {

  private static class CachePathMemoizer extends Memoizer {

    String cachePath(String path) {
      return getCachePath(path);
    }
  }

  private Path directory;

  @AfterMethod(alwaysRun = true)
  public void tearDown() throws Exception {
    if (directory != null && Files.exists(directory)) {
      Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {

        @Override
        public FileVisitResult visitFile(Path file,
          BasicFileAttributes attributes) throws IOException
        {
          Files.delete(file);
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path current,
          IOException failure) throws IOException
        {
          if (failure != null) {
            throw failure;
          }
          Files.delete(current);
          return FileVisitResult.CONTINUE;
        }
      });
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
    directory = Files.createTempDirectory(getClass().getName() + ".");
    try (Memoizer memoizer = new Memoizer(0, directory.toFile())) {
      File driveC = memoizer.getMemoFile("C:\\somedir\\foo.nd2");
      File driveD = memoizer.getMemoFile("D:\\somedir\\foo.nd2");
      File firstShare = memoizer.getMemoFile(
        "\\\\server\\first\\foo.nd2");
      File secondShare = memoizer.getMemoFile(
        "\\\\server\\second\\foo.nd2");

      assertFalse(driveC.equals(driveD));
      assertFalse(firstShare.equals(secondShare));
      assertTrue(driveC.toPath().startsWith(directory));
      assertTrue(driveD.toPath().startsWith(directory));
      assertTrue(firstShare.toPath().startsWith(directory));
      assertTrue(secondShare.toPath().startsWith(directory));
    }
  }
}
