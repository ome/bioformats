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

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

import loci.common.Location;
import loci.formats.FormatTools;
import loci.formats.FileStitcher;
import loci.formats.FormatException;
import loci.formats.IFormatReader;
import loci.formats.in.FakeReader;
import loci.formats.in.MetadataLevel;
import loci.formats.in.MetadataOptions;
import loci.formats.in.DynamicMetadataOptions;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertEqualsNoOrder;
import static org.testng.Assert.assertNotNull;
import static loci.formats.FilePatternBlock.BLOCK_START;
import static loci.formats.FilePatternBlock.BLOCK_END;


public class FileStitcherTest {

  public static final String KEY = "test.option";
  public static final String VALUE = "foo";

  // expected core metadata for the final stitched image
  private static final int PIXEL_TYPE = FormatTools.UINT8;
  private static final int SIZE_X = 128;
  private static final int SIZE_Y = 64;
  private static final int SIZE_Z = 2;
  private static final int SIZE_T = 4;
  private static final int SIZE_C = 3;
  private static final String TEMPLATE =
    "%s&pixelType=%s&sizeX=%d&sizeY=%d&sizeZ=%d&sizeT=%d&sizeC=%d.fake";
  private static final String[] DIM_TAGS = {"Z", "T", "C"};
  private static final int[] DIMS = {SIZE_Z, SIZE_T, SIZE_C};

  private String mkBlock(String axis, int nElem) {
    if (nElem <= 1) {
      return String.format("%s0", axis);
    }
    return String.format("%s%s0-%d%s", axis, BLOCK_START, nElem - 1, BLOCK_END);
  }

  private static String[] mkBasenames(String[] filenames) {
    String[] basenames = new String[filenames.length];
    for (int i = 0; i < filenames.length; i++) {
      basenames[i] = new Location(filenames[i]).getName();
    }
    return basenames;
  }

  // cartesian product of 0-dims[i] ranges
  // [3, 2] => [[0, 0], [1, 0], [2, 0], [0, 1], [1, 1], [2, 1]]
  private static List<List<Integer>> product(List<Integer> dims) {
    List<List<Integer>> ret = new ArrayList<List<Integer>>();
    if (0 == dims.size()) {
      ret.add(new ArrayList<Integer>());
    } else {
      for (int i = 0; i < dims.get(dims.size() - 1); i++) {
        for (List<Integer> p: product(dims.subList(0, dims.size() - 1))) {
          p.add(i);
          ret.add(p);
        }
      }
    }
    return ret;
  }

  private static List<Integer> range(Integer len) {
    List<Integer> ret = new ArrayList<Integer>();
    for (int i = 0; i < len; i++) {
      ret.add(i);
    }
    return ret;
  }

  private static void check(String pattern, String[] filenames, Integer[] dims)
      throws IOException, FormatException {
    FileStitcher fs = new FileStitcher();
    fs.setId(pattern);
    assertEquals(fs.getFilePattern().getPattern(), pattern);
    assertEquals(fs.getImageCount(), SIZE_Z * SIZE_T * SIZE_C);
    assertEquals(fs.getSizeX(), SIZE_X);
    assertEquals(fs.getSizeY(), SIZE_Y);
    assertEquals(fs.getSizeZ(), SIZE_Z);
    assertEquals(fs.getSizeT(), SIZE_T);
    assertEquals(fs.getSizeC(), SIZE_C);
    assertEquals(fs.getPixelType(), PIXEL_TYPE);
    assertEqualsNoOrder(mkBasenames(fs.getUsedFiles()), filenames);
    checkPlanes(fs, dims);
    fs.close();
  }

  // FakeReader encodes 5 integers at the start of each image plane: s idx,
  // plane no., z idx, c idx, t idx. Since these indices are generated before
  // stitching, they are relative to the individual files. Given the pattern:
  //
  // Z0T0C<0-2>&pixelType=uint8&sizeX=128&sizeY=64&sizeZ=2&sizeT=4&sizeC=1.fake
  //
  // the set of encoded Z and T indices will be [0, 1] and [0, 1, 2, 3], while
  // that of C indices will be [0] because individual files have a flat (== 1)
  // C dimension. The set of plane nos. will be [0, 1, 2, 3, 4, 5, 6, 7] (the
  // number of planes in each file is 2 * 4 * 1).
  private static void checkPlanes(FileStitcher fs, Integer[] dims)
      throws IOException, FormatException {
    List<Set<Integer>>idxSets = new ArrayList<Set<Integer>>();
    for (int k = 0; k < 5; k++) {
      idxSets.add(new HashSet<Integer>());
    }
    int[] specialPixels;
    for (int i = 0; i < fs.getImageCount(); i++) {
      specialPixels = FakeReader.readSpecialPixels(fs.openBytes(i));
      for (int k = 0; k < 5; k++) {
        idxSets.get(k).add(specialPixels[k]);
      }
    }
    assertEquals(idxSets.get(0), new HashSet(range(1)));           // S
    assertEquals(idxSets.get(1),
                 new HashSet(range(dims[0] * dims[1] * dims[2]))); // no.
    assertEquals(idxSets.get(2), new HashSet(range(dims[0])));     // Z
    assertEquals(idxSets.get(3), new HashSet(range(dims[2])));     // C
    assertEquals(idxSets.get(4), new HashSet(range(dims[1])));     // T
  }

  @DataProvider(name = "dimZTC")
  public Object[][] createDimZTC() {
    return new Object[][] {
      {new Integer[] {SIZE_Z, SIZE_T, 1}},
      {new Integer[] {SIZE_Z, 1, SIZE_C}},
      {new Integer[] {1, SIZE_T, SIZE_C}},
      {new Integer[] {SIZE_Z, 1, 1}},
      {new Integer[] {1, SIZE_T, 1}},
      {new Integer[] {1, 1, SIZE_C}},
      {new Integer[] {1, 1, 1}}
    };
  }

  @DataProvider(name = "levels")
  public Object[][] createLevels() {
    return new Object[][] {
      {MetadataLevel.MINIMUM},
      {MetadataLevel.NO_OVERLAYS},
      {MetadataLevel.ALL}
    };
  }

  @Test(dataProvider = "dimZTC")
  public void testStitch(Integer[] dims) throws IOException, FormatException {
    // dims: ZCT dimensions for each individual file in the pattern
    // dimensions set to 1 must be spread out across a pattern block
    Integer[] patternDims = new Integer[] {1, 1, 1};
    StringBuilder blocks = new StringBuilder();
    for (int i = 0; i < dims.length; i++) {
      if (dims[i] == 1) {
        patternDims[i] = DIMS[i];
      }
      blocks.append(mkBlock(DIM_TAGS[i], patternDims[i]));
    }
    List<String> filenames = new ArrayList<String>();
    String tag;
    String ptString = FormatTools.getPixelTypeString(PIXEL_TYPE);
    for (List<Integer> idx: product(Arrays.asList(patternDims))) {
      tag = String.format("Z%dT%dC%d", idx.toArray(new Integer[idx.size()]));
      filenames.add(String.format(
          TEMPLATE, tag, ptString, SIZE_X, SIZE_Y, dims[0], dims[1], dims[2]));
    }
    String pattern = String.format(TEMPLATE, blocks.toString(), ptString,
                                   SIZE_X, SIZE_Y, dims[0], dims[1], dims[2]);
    check(pattern, filenames.toArray(new String[filenames.size()]), dims);
  }

  @Test
  public void testUnderlyingReaders() throws IOException, FormatException {
    FakeReader reader = new FakeReader();
    FileStitcher fs = new FileStitcher(reader);
    assertNotNull(fs.getUnderlyingReaders());
    fs.setId("test_z<0-2>.fake");
    assertNotNull(fs.getUnderlyingReaders());
    fs.close();
  }

  @Test
  public void testOptionsExplicit() throws IOException, FormatException {
    DynamicMetadataOptions opt = new DynamicMetadataOptions();
    opt.set(KEY, VALUE);
    FileStitcher fs = new FileStitcher();
    fs.setMetadataOptions(opt);
    fs.setId("test_z<0-2>.fake");
    for (IFormatReader r: fs.getUnderlyingReaders()) {
      MetadataOptions rOpt = r.getMetadataOptions();
      assertTrue(rOpt instanceof DynamicMetadataOptions);
      String v = ((DynamicMetadataOptions) rOpt).get(KEY);
      assertNotNull(v);
      assertEquals(v, VALUE);
    }
    fs.close();
  }

  @Test(dataProvider = "levels")
  public void testOptionsImplicit(MetadataLevel level)
      throws IOException, FormatException {
    FileStitcher fs = new FileStitcher();
    fs.getMetadataOptions().setMetadataLevel(level);
    fs.setId("test_z<0-2>.fake");
    for (IFormatReader r: fs.getUnderlyingReaders()) {
      assertEquals(r.getMetadataOptions().getMetadataLevel(), level);
    }
    fs.close();
  }

}
