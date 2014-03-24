package ome.jxr.image.utests;

import static org.testng.AssertJUnit.assertTrue;
import ome.jxr.image.OutputBitdepth;

import org.testng.annotations.Test;

public class OutputBitdepthTest {

  @Test
  public void testFindByIdForReservedIds() {
    int[] reservedIds = {5,11,12,13,14};
    for (Integer id : reservedIds) {
      assertTrue(OutputBitdepth.RESERVED.equals(OutputBitdepth.findById(id)));
    }
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testFindByIdWithNegativeIdShouldThrowIAE() {
    int invalidId = -1;
    OutputBitdepth.findById(invalidId);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testFindByIdWithOutOfRangeIdShouldThrowIAE() {
    int invalidId = 16;
    OutputBitdepth.findById(invalidId);
  }

}
