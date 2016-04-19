/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
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

import java.util.List;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.FileSystems;
import java.io.IOException;
import java.io.File;
import java.math.BigInteger;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertEqualsNoOrder;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import loci.formats.FilePattern;
import loci.formats.AxisGuesser;
import loci.common.Location;


public class FilePatternTest {

  private static final String SEPARATOR =
    FileSystems.getDefault().getSeparator();

  @DataProvider(name = "booleanStates")
  public Object[][] createBooleans() {
    return new Object[][] {{true}, {false}};
  }

  @DataProvider(name = "badPatterns")
  public Object[][] createBadPatterns() {
    return new Object[][] {
      {"<0-2><3-4"}, {"0-2><3-4>"},  // mismatch
      {"<<0-2>>"}, {"<0-2<3-4>>"},  // wrong order
    };
  }

  // We can't use Path.resolve for patterns: on Windows, illegal
  // characters would lead to InvalidPathException
  private static String resolveToString(Path head, String tail) {
    // Assumes head is a dir and tail does not contain the separator
    return head.toString() + SEPARATOR + tail;
  }

  private static String mkPattern(
      String[] prefixes, String[] blocks, String suffix) {
    if (prefixes.length != blocks.length) {
      throw new RuntimeException("arrays must have the same length");
    }
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < prefixes.length; i++) {
      sb.append(prefixes[i]);
      sb.append(blocks[i]);
    }
    sb.append(suffix);
    return sb.toString();
  }

  private static String[] mkFiles(Path dir, String[] basenames)
      throws IOException {
    Path[] paths = mkPaths(dir, basenames);
    String[] fullNames = new String[paths.length];
    for (int i = 0; i < paths.length; i++) {
      fullNames[i] = paths[i].toString();
    }
    return fullNames;
  }

  private static Path[] mkPaths(Path dir, String[] basenames)
      throws IOException {
    Path[] paths = new Path[basenames.length];
    for (int i = 0; i < basenames.length; i++) {
      paths[i] = Files.createFile(dir.resolve(basenames[i]));
      paths[i].toFile().deleteOnExit();
    }
    return paths;
  }

  private static String[] resolveAll(Path dir, String[] basenames) {
    String[] resolved = new String[basenames.length];
    for (int i = 0; i < basenames.length; i++) {
      resolved[i] = resolveToString(dir, basenames[i]);
    }
    return resolved;
  }

  @Test
  public void testNonRegex() {
    String[] prefixes = {"z", "c", "t"};
    String[] blocks = {"<0-1>", "<R,G,B>", "<10-30:10>"};
    String suffix = ".tif";
    String pattern = mkPattern(prefixes, blocks, suffix);
    FilePattern fp = new FilePattern(pattern);
    assertTrue(fp.isValid());
    assertFalse(fp.isRegex());
    assertEquals(fp.getPattern(), pattern);
    assertEquals(fp.getPrefixes(), prefixes);
    for (int i = 0; i < prefixes.length; i++) {
      assertEquals(fp.getPrefix(i), prefixes[i]);
    }
    assertEquals(fp.getPrefix(), prefixes[0]);
    assertEquals(fp.getBlocks(), blocks);
    for (int i = 0; i < blocks.length; i++) {
      assertEquals(fp.getBlock(i), blocks[i]);
    }
    assertEquals(fp.getSuffix(), suffix);
    assertEquals(fp.getCount(), new int[] {2, 3, 3});
    assertEquals(fp.getElements(), new String[][] {
        {"0", "1"}, {"R", "G", "B"}, {"10", "20", "30"}
      });
    assertEqualsNoOrder(fp.getFiles(), new String[] {
      "z0cRt10.tif", "z0cRt20.tif", "z0cRt30.tif",
      "z0cGt10.tif", "z0cGt20.tif", "z0cGt30.tif",
      "z0cBt10.tif", "z0cBt20.tif", "z0cBt30.tif",
      "z1cRt10.tif", "z1cRt20.tif", "z1cRt30.tif",
      "z1cGt10.tif", "z1cGt20.tif", "z1cGt30.tif",
      "z1cBt10.tif", "z1cBt20.tif", "z1cBt30.tif",
    });
    assertEquals(
        fp.getFirst(),
        new BigInteger[] {new BigInteger("0"), null, new BigInteger("10")}
    );
    assertEquals(
        fp.getLast(),
        new BigInteger[] {new BigInteger("1"), null, new BigInteger("30")}
    );
    assertEquals(
        fp.getStep(),
        new BigInteger[] {new BigInteger("1"), null, new BigInteger("10")}
    );
  }

  @Test(dataProvider = "booleanStates")
  public void testRegex(Boolean createFiles) throws IOException {
    String[] names = {"z0.tif", "z1.tif"};
    Path wd = Files.createTempDirectory("");
    wd.toFile().deleteOnExit();
    String pattern = resolveToString(wd, "z.*.tif");
    FilePattern fp = new FilePattern(pattern);
    assertTrue(fp.isValid());
    assertTrue(fp.isRegex());
    assertEquals(fp.getPattern(), pattern);
    if (!createFiles) {
      // pattern matches a single (nonexistent) file with name == pattern
      assertEquals(fp.getFiles(), new String[] {pattern});
      return;
    }
    String[] fullNames = mkFiles(wd, names);
    assertEqualsNoOrder(new FilePattern(pattern).getFiles(), fullNames);
  }

  @Test
  public void testSingleFile() {
    String pattern = "test.fake";
    FilePattern fp = new FilePattern(pattern);
    assertTrue(fp.isValid());
    assertTrue(fp.isRegex());
    assertEquals(fp.getPattern(), pattern);
    assertEquals(fp.getFiles(), new String[] {pattern});
  }

  @Test(dataProvider = "booleanStates")
  public void testFindPattern(Boolean createFiles) throws IOException {
    int minZ = 1, maxZ = 2;
    int minT = 1, maxT = 12;
    int minC = 9, maxC = 11;
    // 2nd prefix empty to test sub-block detection
    String[] prefixes = {"foo_", "", "c"};
    String[] formats = {"%d", "%02d", "%d"};
    String[] blocks = {
      String.format("<%d-%d>", minZ, maxZ),
      String.format("<%02d-%02d>", minT, maxT),
      String.format("<%d-%d>", minC, maxC),
    };
    String suffix = ".ext";
    String pattern = mkPattern(prefixes, blocks, suffix);
    List<String> names = new ArrayList<String>();
    names.add("outlier.ext");
    for (int z = minZ; z <= maxZ; z++) {
      for (int t = minT; t <= maxT; t++) {
        for (int c = minC; c <= maxC; c++) {
          names.add(mkPattern(prefixes, new String[] {
                String.format(formats[0], z),
                String.format(formats[1], t),
                String.format(formats[2], c),
              }, suffix));
        }
      }
    }
    String[] namesA = names.toArray(new String[names.size()]);
    if (!createFiles) {
      assertEquals(FilePattern.findPattern(namesA[1], null, namesA), pattern);
      // test excludeAxes
      String[] minCBlocks = new String[] {
        blocks[0], blocks[1], Integer.toString(minC)
      };
      int[] excl = new int[] {AxisGuesser.C_AXIS};
      assertEquals(FilePattern.findPattern(namesA[1], null, namesA, excl),
                   mkPattern(prefixes, minCBlocks, suffix));
      return;
    }
    Path wd = Files.createTempDirectory("");
    wd.toFile().deleteOnExit();
    String absPattern = resolveToString(wd, pattern);
    String[] fullNames = mkFiles(wd, namesA);
    assertEquals(FilePattern.findPattern(fullNames[1]), absPattern);
    assertEquals(FilePattern.findPattern(namesA[1], wd.toString()), absPattern);
    assertEquals(FilePattern.findPattern(new File(fullNames[1])), absPattern);
    assertEquals(FilePattern.findPattern(new Location(fullNames[1])),
                 absPattern);
    // test constructors that use findPattern
    FilePattern fp = new FilePattern(new Location(fullNames[1]));
    assertEquals(fp.getPattern(), absPattern);
    fp = new FilePattern(namesA[1], wd.toString());
    assertEquals(fp.getPattern(), absPattern);
  }

  @Test(dataProvider = "booleanStates")
  public void testFindSeriesPattern(boolean createFiles) throws IOException {
    String[] names = {
      "foo_s1_z1.fake",
      "foo_s1_z2.fake",
      "foo_s2_z1.fake",
      "foo_s2_z2.fake",
      "foo_s1_z1.ini",
      "foo_s1_z2.ini",
      "foo_s2_z1.ini",
      "foo_s2_z2.ini",
    };
    String[] fakePatterns = new String[] {
      "foo_s1_z<1-2>.fake", "foo_s2_z<1-2>.fake"
    };
    String[] iniPatterns = new String[] {
      "foo_s1_z<1-2>.ini", "foo_s2_z<1-2>.ini"
    };
    if (!createFiles) {
      assertEqualsNoOrder(
          FilePattern.findSeriesPatterns(names[0], null, names), fakePatterns
      );
      assertEqualsNoOrder(
          FilePattern.findSeriesPatterns(names[4], null, names), iniPatterns
      );
      return;
    }
    Path wd = Files.createTempDirectory("");
    wd.toFile().deleteOnExit();
    String[] fullNames = mkFiles(wd, names);
    assertEqualsNoOrder(
        FilePattern.findSeriesPatterns(fullNames[0]),
        resolveAll(wd, fakePatterns)
    );
    assertEqualsNoOrder(
        FilePattern.findSeriesPatterns(fullNames[4]),
        resolveAll(wd, iniPatterns)
    );
  }

  @Test(dataProvider = "badPatterns")
  public void testBadPatterns(String pattern) {
    FilePattern fp = new FilePattern(pattern);
    assertFalse(fp.isValid());
    assertNotNull(fp.getErrorMessage());
  }

  @Test
  public void testVarDir() {
    String[] prefixes = {"z"};
    String[] blocks = {"<0-1>"};
    String suffix = SEPARATOR + "foo.tif";
    String pattern = mkPattern(prefixes, blocks, suffix);
    FilePattern fp = new FilePattern(pattern);
    assertTrue(fp.isValid());
    assertFalse(fp.isRegex());
    assertEquals(fp.getPattern(), pattern);
    assertEquals(fp.getPrefixes(), prefixes);
    for (int i = 0; i < prefixes.length; i++) {
      assertEquals(fp.getPrefix(i), prefixes[i]);
    }
    assertEquals(fp.getPrefix(), prefixes[0]);
    assertEquals(fp.getBlocks(), blocks);
    for (int i = 0; i < blocks.length; i++) {
      assertEquals(fp.getBlock(i), blocks[i]);
    }
    assertEquals(fp.getSuffix(), suffix);
    assertEquals(fp.getCount(), new int[] {2});
    assertEquals(fp.getElements(), new String[][] {{"0", "1"}});
    assertEqualsNoOrder(fp.getFiles(), new String[] {
      "z0" + suffix, "z1" + suffix,
    });
    assertEquals(fp.getFirst(), new BigInteger[] {BigInteger.ZERO});
    assertEquals(fp.getLast(), new BigInteger[] {BigInteger.ONE});
    assertEquals(fp.getStep(), new BigInteger[] {BigInteger.ONE});
  }

}
