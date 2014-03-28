package ome.jxr.image.utests;

import static org.testng.AssertJUnit.assertTrue;
import ome.jxr.image.BitDepth;

import org.testng.annotations.Test;

public class BitDepthTest {

  @Test
  public void testFindByIdForReservedIds() {
    int[] reservedIds = {5,11,12,13,14};
    for (Integer id : reservedIds) {
      assertTrue(BitDepth.RESERVED.equals(BitDepth.findById(id)));
    }
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testFindByIdWithNegativeIdShouldThrowIAE() {
    int invalidId = -1;
    BitDepth.findById(invalidId);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testFindByIdWithOutOfRangeIdShouldThrowIAE() {
    int invalidId = 16;
    BitDepth.findById(invalidId);
  }

}
