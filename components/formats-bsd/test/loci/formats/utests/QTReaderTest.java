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
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.util.Base64;

import loci.formats.Memoizer;
import loci.formats.in.QTReader;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class QTReaderTest {

  /** Six-frame 8x8 QTRLE movie with delta frames after the first frame. */
  private static final String QTRLE_MOVIE =
    "AAAAFGZ0eXBxdCAgAAACAHF0ICAAAAAId2lkZQAAAMdtZGF0AAAAnAAIAAAAAAAIAAAB+P8AAP8B+AD/AP8B+AAA" +
    "//8B+P//AP8B+P8A//8B+AD///8B+ICAgP8B+P////8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABwAA" +
    "AAAAAAcAAAAAAAAHAAAAAAAABwAAAAAAAAcAAAAAAALubW9vdgAAAGxtdmhkAAAAAAAAAAAAAAAAAAAD6AAAF3AA" +
    "AQAAAQAAAAAAAAAAAAAAAAEAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAA" +
    "AAAAAAAAAAAAAAAAAgAAAll0cmFrAAAAXHRraGQAAAADAAAAAAAAAAAAAAABAAAAAAAAF3AAAAAAAAAAAAAAAAAA" +
    "AAAAAAEAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAABAAAAAAAgAAAAIAAAAAAAkZWR0cwAAABxlbHN0AAAA" +
    "AAAAAAEAABdwAAAAAAABAAAAAAHRbWRpYQAAACBtZGhkAAAAAAAAAAAAAAAAAABAAAABgAB//wAAAAAALWhkbHIA" +
    "AAAAbWhscnZpZGUAAAAAAAAAAAAAAAAMVmlkZW9IYW5kbGVyAAABfG1pbmYAAAAUdm1oZAAAAAEAAAAAAAAAAAAA" +
    "ACxoZGxyAAAAAGRobHJ1cmwgAAAAAAAAAAAAAAAAC0RhdGFIYW5kbGVyAAAAJGRpbmYAAAAcZHJlZgAAAAAAAAAB" +
    "AAAADHVybCAAAAABAAABEHN0YmwAAACAc3RzZAAAAAAAAAABAAAAcHJsZSAAAAAAAAAAAQAAAABGRk1QAAACAAAA" +
    "AgAACAAIAEgAAABIAAAAAAAAAAETTGF2YzYwLjMxLjEwMiBxdHJsZQAAAAAAAAAAAAAAAAAY//8AAAAKZmllbAEA" +
    "AAAAEHBhc3AAAAABAAAAAQAAABhzdHRzAAAAAAAAAAEAAAAGAABAAAAAABRzdHNzAAAAAAAAAAEAAAABAAAAHHN0" +
    "c2MAAAAAAAAAAQAAAAEAAAAGAAAAAQAAACxzdHN6AAAAAAAAAAAAAAAGAAAAnAAAAAcAAAAHAAAABwAAAAcAAAAH" +
    "AAAAFHN0Y28AAAAAAAAAAQAAACQAAAAhdWR0YQAAABmpc3dyAA1VxExhdmY2MC4xNi4xMDA=";

  private File testDirectory;
  private File movie;
  private File memoDirectory;

  @BeforeMethod
  public void setUp() throws Exception {
    testDirectory = Files.createTempDirectory(
      QTReaderTest.class.getName()).toFile();
    movie = new File(testDirectory, "delta.mov");
    Files.write(movie.toPath(), Base64.getDecoder().decode(QTRLE_MOVIE));
    memoDirectory = new File(testDirectory, "memo");
    assertTrue(memoDirectory.mkdir());
  }

  @AfterMethod
  public void tearDown() {
    deleteOnExit(testDirectory);
  }

  @Test
  public void testVariableSampleSizesAfterLeadingAtoms() throws Exception {
    try (QTReader direct = new QTReader()) {
      direct.setId(movie.getAbsolutePath());
      assertEquals(direct.getImageCount(), 6);
      byte[] expected = direct.openBytes(0);
      for (int plane = 1; plane < direct.getImageCount(); plane++) {
        assertEquals(direct.openBytes(plane), expected);
      }
    }
  }

  @Test
  public void testMemoizedRandomAccessToDeltaFrames() throws Exception {
    byte[][] expected;
    try (QTReader direct = new QTReader()) {
      direct.setId(movie.getAbsolutePath());
      expected = new byte[direct.getImageCount()][];
      for (int plane = 0; plane < expected.length; plane++) {
        expected[plane] = direct.openBytes(plane);
      }
    }

    try (Memoizer seed = new Memoizer(new QTReader(), 0, memoDirectory)) {
      seed.setId(movie.getAbsolutePath());
      assertTrue(seed.isSavedToMemo());
    }

    for (int plane = 0; plane < expected.length; plane++) {
      try (Memoizer memoized =
        new Memoizer(new QTReader(), 0, memoDirectory))
      {
        memoized.setId(movie.getAbsolutePath());
        assertTrue(memoized.isLoadedFromMemo());
        assertEquals(memoized.openBytes(plane), expected[plane]);
      }
    }
  }

  private static void deleteOnExit(File file) {
    File[] children = file.listFiles();
    if (children != null) {
      for (File child : children) {
        deleteOnExit(child);
      }
    }
    file.deleteOnExit();
  }
}
