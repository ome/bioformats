/*
 * #%L
 * Top-level reader and writer APIs
 * %%
 * Copyright (C) 2018 Open Microscopy Environment:
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

import loci.common.Constants;
import loci.formats.CoreMetadata;
import loci.formats.CoreMetadataList;

import loci.formats.Modulo;
import ome.units.quantity.Length;
import ome.units.unit.Unit;
import ome.units.UNITS;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.internal.junit.ArrayAsserts.assertArrayEquals;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for {@link loci.formats.CoreMetadataList}.
 */
public class CoreMetadataListTest {

  @DataProvider(name = "corelist")
  public Object[][] createList() {
    int[] expected0 = new int[]{};
    List<CoreMetadata> list0 = new ArrayList<>();

    int[] expected1 = new int[]{1};
    List<CoreMetadata> list1 = new ArrayList<>();
    list1.add(new CoreMetadata());

    int[] expected2 = new int[]{1, 1, 1, 1};
    List<CoreMetadata> list2 = new ArrayList<>();
    list2.add(new CoreMetadata());
    list2.add(new CoreMetadata());
    list2.add(new CoreMetadata());
    list2.add(new CoreMetadata());

    int[] expected3 = new int[]{5};
    List<CoreMetadata> list3 = new ArrayList<>();
    list3.add(new CoreMetadata());
    list3.add(new CoreMetadata());
    list3.add(new CoreMetadata());
    list3.add(new CoreMetadata());
    list3.add(new CoreMetadata());
    list3.get(0).resolutionCount = 5;
    list3.get(0).sizeX = 4096;
    list3.get(0).sizeY = 4096;
    list3.get(0).sizeZ = 1024;
    list3.get(1).sizeX = 8192;
    list3.get(1).sizeY = 8192;
    list3.get(1).sizeZ = 1024;
    list3.get(0).sizeX = 0;
    list3.get(0).sizeY = 0;
    list3.get(0).sizeZ = 0;
    list3.get(3).sizeX = 2048;
    list3.get(3).sizeY = 2048;
    list3.get(3).sizeZ = 512;
    list3.get(4).sizeX = 1024;
    list3.get(4).sizeY = 1024;
    list3.get(4).sizeZ = 256;

    int[] expected4 = new int[]{1, 4, 2, 1, 1, 1};

    List<CoreMetadata> list4 = new ArrayList();
    for (int i = 0; i < 10; ++i) {
      list4.add(new CoreMetadata());
    }

    int pos = 0;
    for (int v : expected4) {
      list4.get(pos).resolutionCount = v;
      pos += v;
    }

    return new Object[][]{
      {list0, expected0},
      {list1, expected1},
      {list2, expected2},
      {list3, expected3},
      {list4, expected4}
    };
  }

  @Test(dataProvider = "corelist")
  public void testResolutionCounts(List<CoreMetadata> data, int[] expected) {
    CoreMetadataList list = new CoreMetadataList();
    list.setFlattenedList(data);

    assertArrayEquals(expected, list.sizes());
    for (int i = 0; i < expected.length; ++i) {
      assertEquals(expected[i], list.get(i, 0).resolutionCount);
    }
  }

  @Test(dataProvider = "corelist")
  public void testFlattenedList(List<CoreMetadata> data, int[] expected) {
    CoreMetadataList list = new CoreMetadataList();
    list.setFlattenedList(data);

    List<CoreMetadata> flat = list.getFlattenedList();

    for (int i = 0; i < data.size(); i++) {
      CoreMetadata cdata = data.get(i);
      CoreMetadata cflat= flat.get(i);
      compareCoreMetadata(cflat, cdata, true);
    }
  }

  @Test(dataProvider = "corelist")
  public void testFlattenedSize(List<CoreMetadata> data, int[] expected) {
    CoreMetadataList list = new CoreMetadataList();
    list.setFlattenedList(data);

    int ncore = 0;
    for (int v : list.sizes()) {
      ncore += v;
    }
    assertEquals(ncore, list.flattenedSize());
  }

  @Test(dataProvider = "corelist")
  public void testCopy(List<CoreMetadata> data, int[] expected) {
    CoreMetadataList list = new CoreMetadataList();
    list.setFlattenedList(data);

    CoreMetadataList list2 = new CoreMetadataList(list);

    List<CoreMetadata> flat = list2.getFlattenedList();

    assertEquals(data.size(), flat.size());

    for (int i = 0; i < data.size(); i++) {
      CoreMetadata cdata = data.get(i);
      CoreMetadata cflat= flat.get(i);
      compareCoreMetadata(cflat, cdata, true);
    }
  }

  @Test(dataProvider = "corelist")
  public void testSeriesCopy(List<CoreMetadata> data, int[] expected) {
    CoreMetadataList list = new CoreMetadataList();
    list.setFlattenedList(data);

    CoreMetadataList list2 = new CoreMetadataList(list);

    List<CoreMetadata> flat = list2.getSeriesList();

    assertEquals(list.size(), flat.size());

    for (int i = 0; i < list.size(); i++) {
      CoreMetadata clist = list.get(i, 0);
      CoreMetadata cflat= flat.get(i);
      compareCoreMetadata(cflat, clist, false);
    }
  }


  @Test(dataProvider = "corelist")
  public void testFlattenedIndex(List<CoreMetadata> data, int[] expected) {
    CoreMetadataList list = new CoreMetadataList();
    list.setFlattenedList(data);

    int size = list.flattenedSize();
    for (int i = 0; i < size; ++i) {
      int[] idx2 = list.flattenedIndexes(i);
      int idx = list.flattenedIndex(idx2[0], idx2[1]);
      assertEquals(i, idx);
    }
  }

  @Test
  public void testAutomaticOrdering() {
    CoreMetadataList list = new CoreMetadataList();

    List<CoreMetadata> data = new ArrayList<>();
    data.add(new CoreMetadata());
    data.add(new CoreMetadata());
    data.add(new CoreMetadata());
    data.add(new CoreMetadata());
    data.add(new CoreMetadata());
    data.get(0).resolutionCount = 5;
    data.get(0).sizeX = 4096;
    data.get(0).sizeY = 4096;
    data.get(0).sizeZ = 1024;
    data.get(1).sizeX = 8192;
    data.get(1).sizeY = 8192;
    data.get(1).sizeZ = 1024;
    data.get(2).sizeX = 0;
    data.get(2).sizeY = 0;
    data.get(2).sizeZ = 0;
    data.get(3).sizeX = 2048;
    data.get(3).sizeY = 2048;
    data.get(3).sizeZ = 512;
    data.get(4).sizeX = 1024;
    data.get(4).sizeY = 1024;
    data.get(4).sizeZ = 256;

    list.setFlattenedList(data);

    list.reorder();

    assertEquals(8192, list.get(0,0).sizeX);
    assertEquals(8192, list.get(0,0).sizeY);
    assertEquals(1024, list.get(0,0).sizeZ);
    assertEquals(4096, list.get(0,1).sizeX);
    assertEquals(4096, list.get(0,1).sizeY);
    assertEquals(1024, list.get(0,1).sizeZ);
    assertEquals(2048, list.get(0,2).sizeX);
    assertEquals(2048, list.get(0,2).sizeY);
    assertEquals(512, list.get(0,2).sizeZ);
    assertEquals(1024, list.get(0,3).sizeX);
    assertEquals(1024, list.get(0,3).sizeY);
    assertEquals(256, list.get(0,3).sizeZ);
    assertEquals(0, list.get(0,4).sizeX);
    assertEquals(0, list.get(0,4).sizeY);
    assertEquals(0, list.get(0,4).sizeZ);
  }


  @Test
  public void testManualOrdering() {
    List<CoreMetadata> data = new ArrayList<>();
    data.add(new CoreMetadata());
    data.add(new CoreMetadata());
    data.add(new CoreMetadata());
    data.add(new CoreMetadata());
    data.add(new CoreMetadata());
    data.get(0).resolutionCount = 5;
    data.get(0).sizeX = 4096;
    data.get(0).sizeY = 4096;
    data.get(0).sizeZ = 1024;
    data.get(1).sizeX = 8192;
    data.get(1).sizeY = 8192;
    data.get(1).sizeZ = 1024;
    data.get(2).sizeX = 0;
    data.get(2).sizeY = 0;
    data.get(2).sizeZ = 0;
    data.get(3).sizeX = 2048;
    data.get(3).sizeY = 2048;
    data.get(3).sizeZ = 512;
    data.get(4).sizeX = 1024;
    data.get(4).sizeY = 1024;
    data.get(4).sizeZ = 256;

    CoreMetadataList list = new CoreMetadataList(1, data.size());
    list.add();

    for(int i = 0; i < data.size(); ++i) {
      list.set(0, i, data.get(i));
    }

    assertEquals(4096, list.get(0,0).sizeX);
    assertEquals(4096, list.get(0,0).sizeY);
    assertEquals(1024, list.get(0,0).sizeZ);
    assertEquals(8192, list.get(0,1).sizeX);
    assertEquals(8192, list.get(0,1).sizeY);
    assertEquals(1024, list.get(0,1).sizeZ);
    assertEquals(0, list.get(0,2).sizeX);
    assertEquals(0, list.get(0,2).sizeY);
    assertEquals(0, list.get(0,2).sizeZ);
    assertEquals(2048, list.get(0,3).sizeX);
    assertEquals(2048, list.get(0,3).sizeY);
    assertEquals(512, list.get(0,3).sizeZ);
    assertEquals(1024, list.get(0,4).sizeX);
    assertEquals(1024, list.get(0,4).sizeY);
    assertEquals(256, list.get(0,4).sizeZ);

    list.reorder();

    assertEquals(8192, list.get(0,0).sizeX);
    assertEquals(8192, list.get(0,0).sizeY);
    assertEquals(1024, list.get(0,0).sizeZ);
    assertEquals(4096, list.get(0,1).sizeX);
    assertEquals(4096, list.get(0,1).sizeY);
    assertEquals(1024, list.get(0,1).sizeZ);
    assertEquals(2048, list.get(0,2).sizeX);
    assertEquals(2048, list.get(0,2).sizeY);
    assertEquals(512, list.get(0,2).sizeZ);
    assertEquals(1024, list.get(0,3).sizeX);
    assertEquals(1024, list.get(0,3).sizeY);
    assertEquals(256, list.get(0,3).sizeZ);
    assertEquals(0, list.get(0,4).sizeX);
    assertEquals(0, list.get(0,4).sizeY);
    assertEquals(0, list.get(0,4).sizeZ);

  }

  public static void compareCoreMetadata(CoreMetadata observed, CoreMetadata expected,
                                         boolean includeResolutionCount) {
    assertEquals(expected.sizeX, observed.sizeX);
    assertEquals(expected.sizeY, observed.sizeY);
    assertEquals(expected.sizeZ, observed.sizeZ);
    assertEquals(expected.sizeC, observed.sizeC);
    assertEquals(expected.sizeT, observed.sizeT);
    assertEquals(expected.thumbSizeX, observed.thumbSizeX);
    assertEquals(expected.thumbSizeY, observed.thumbSizeY);
    assertEquals(expected.pixelType, observed.pixelType);
    assertEquals(expected.bitsPerPixel, observed.bitsPerPixel);
    assertEquals(expected.imageCount, observed.imageCount);
    assertEquals(expected.dimensionOrder, observed.dimensionOrder);
    assertEquals(expected.orderCertain, observed.orderCertain);
    assertEquals(expected.rgb, observed.rgb);
    assertEquals(expected.littleEndian, observed.littleEndian);
    assertEquals(expected.interleaved, observed.interleaved);
    assertEquals(expected.indexed, observed.indexed);
    assertEquals(expected.falseColor, observed.falseColor);
    assertEquals(expected.metadataComplete, observed.metadataComplete);
    assertEquals(expected.seriesMetadata, observed.seriesMetadata);
    assertEquals(expected.thumbnail, observed.thumbnail);
    if (includeResolutionCount) {
      assertEquals(expected.resolutionCount, observed.resolutionCount);
    }
    else {
      assertEquals(1, observed.resolutionCount);
    }
    // Skip Modulo fields for the purpose of this test.
  }
}
