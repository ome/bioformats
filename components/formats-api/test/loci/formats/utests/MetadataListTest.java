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

import loci.formats.MetadataList;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.internal.junit.ArrayAsserts.assertArrayEquals;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

class Elem {
  public int value;
}

/**
 * Unit tests for {@link loci.formats.CoreMetadataList}.
 */
public class MetadataListTest {

  @Test
  public void testConstructEmpty() {
    MetadataList<Elem> list = new MetadataList<>();

    assertEquals(0, list.size());
  }

  @Test
  public void testConstructSize1() {
    MetadataList<Elem> list = new MetadataList<>(5);

    checkContainerSize(list, new int[] {0,0,0,0,0});
  }

  @Test
  public void testConstructSize2() {
    MetadataList<Elem> list = new MetadataList<>(4,3);

    checkContainerSize(list, new int[] {3,3,3,3});
  }

  @Test
  public void testConstructSize3() {
    MetadataList<Elem> list = new MetadataList<>(new int[] {5,4,3,2,1,0});

    checkContainerSize(list, new int[] {5,4,3,2,1,0});
  }

  @Test
  public void testACoreMetadatadd1() {
    MetadataList<Elem> list = new MetadataList<>();

    for (int i = 0; i < 3; i++) {
      list.add();
    }

    checkContainerSize(list, new int[] {0,0,0});
  }

  @Test
  public void testAdd1() {
    MetadataList<Elem> list = new MetadataList<>();

    for (int i = 0; i < 2; i++) {
      list.add(new Elem());
    }

    checkContainerSize(list, new int[] {1,1});
  }

  @Test
  public void testAdd2() {
    MetadataList<Elem> list = new MetadataList<>();

    int[] sizes = new int[] { 6,2,1,5,9,2,1 };

    for (int s : sizes) {
      list.add(s);
    }

    checkContainerSize(list, sizes);
  }


  @DataProvider(name = "itemlist")
  public Object[][] createList() {
    int[][] values0 = {
    };
    int[] expected0 = {};

    int[][] values1 = {
      {1}
    };
    int[] expected1 = {1};

    int[][] values2 = {
      {0, 1, 2}
    };
    int[] expected2 = {3};

    int[][] values3 = {
      {1},
      {1},
      {1},
      {1},
      {1},
      {1},
    };
    int[] expected3 = {1,1,1,1,1,1};

    int[][] values4 = {
      {0, 1, 2},
      {3},
      {4, 5},
      {6, 7, 8}
    };
    int[] expected4 = {3, 1, 2, 3};

    return new Object[][] {
      { values0, expected0 },
      { values1, expected1 },
      { values2, expected2 },
      { values3, expected3 },
      { values4, expected4 }
    };
  }

  @Test(dataProvider = "itemlist")
  public void testCountsAppend(int[][] data, int[] expected) {
    MetadataList<Elem> list = new MetadataList<>();

    for (int i = 0; i < data.length; i++) {
      list.add();
      for (int j = 0; j < data[i].length; j++) {
        Elem e = new Elem();
        e.value = data[i][j];
        list.add(i, e);
      }
    }

    checkContainerSize(list, expected);
    checkContainer(list, data);
  }

  @Test(dataProvider = "itemlist")
  public void testCountsSized(int[][] data, int[] expected) {
    MetadataList<Elem> list = new MetadataList<>(expected);
    assertArrayEquals(expected, list.sizes());

    for (int i = 0; i < data.length; i++) {
      for (int j = 0; j < data[i].length; j++) {
        Elem e = new Elem();
        e.value = data[i][j];
        list.set(i, j, e);
      }
    }

    checkContainerSize(list, expected);
    checkContainer(list, data);
  }

  @Test(dataProvider = "itemlist")
  public void testCopy(int[][] data, int[] expected) {
    MetadataList<Elem> list = new MetadataList<>();

    for (int i = 0; i < data.length; i++) {
      list.add();
      for (int j = 0; j < data[i].length; j++) {
        Elem e = new Elem();
        e.value = data[i][j];
        list.add(i, e);
      }
    }

    MetadataList<Elem> list2 = new MetadataList<>(list);

    checkContainerSize(list, expected);
    checkContainer(list, data);
    checkContainer(list2, data);
  }


  @Test
  public void testRemove1() {
    MetadataList<Elem> list = new MetadataList<>(new int[] {5,4,3,2,1,0});

    checkContainerSize(list, new int[] {5,4,3,2,1,0});

    list.clear();

    checkContainerSize(list, new int[] {});
  }

  @Test
  public void testRemove2() {
    MetadataList<Elem> list = new MetadataList<>(new int[] {5,4,3,2,1,0});

    checkContainerSize(list, new int[] {5,4,3,2,1,0});

    list.clear(2);
    list.clear(4);

    checkContainerSize(list, new int[] {5,4,0,2,0,0});
  }

  private void checkContainer(MetadataList<Elem> list, int[][] data) {
    for (int i = 0; i < data.length; i++) {
      for (int j = 0; j < data[i].length; j++) {
        assertEquals(list.get(i, j).value, data[i][j]);
      }
    }
  }

  private void checkContainerSize(MetadataList<Elem> list, int[] size) {
    assertEquals(size.length, list.size());
    assertArrayEquals(size, list.sizes());
    for (int i = 0; i < size.length; i++) {
      assertEquals(size[i], list.size(i));
    }
  }

}
