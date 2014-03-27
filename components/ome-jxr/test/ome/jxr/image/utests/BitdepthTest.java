package ome.jxr.image.utests;

import static org.testng.AssertJUnit.assertTrue;
import ome.jxr.image.Bitdepth;

import org.testng.annotations.Test;

public class BitdepthTest {

  @Test
  public void testFindByIdForReservedIds() {
    int[] reservedIds = {5,11,12,13,14};
    for (Integer id : reservedIds) {
      assertTrue(Bitdepth.RESERVED.equals(Bitdepth.findById(id)));
    }
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testFindByIdWithNegativeIdShouldThrowIAE() {
    int invalidId = -1;
    Bitdepth.findById(invalidId);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testFindByIdWithOutOfRangeIdShouldThrowIAE() {
    int invalidId = 16;
    Bitdepth.findById(invalidId);
  }

}
