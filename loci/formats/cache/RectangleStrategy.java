//
// RectangleStrategy.java
//

package loci.formats.cache;

import java.util.Arrays;
import java.util.Vector;

public class RectangleStrategy extends CacheStrategy {

  // -- Fields --

  private int[] begins;
  private int[] ends;

  // -- Constructor --

  public RectangleStrategy(boolean forwardFirst, int[] lengths, int[] forward, 
    int[] backward) 
  {
    super(forwardFirst, lengths, forward, backward);
  }

  // -- ICacheStrategy API methods --

  /* @see loci.formats.cache.ICacheStrategy#getLoadList(int[]) */
  public int[][] getLoadList(int[] pos) throws CacheException {
    int[] positions = new int[pos.length];
    begins = new int[pos.length];
    ends = new int[pos.length];

    int total = 1;

    for (int i=0; i<pos.length; i++) {
      positions[i] = pos[i];
      begins[i] = pos[i] - backward[i];
      ends[i] = pos[i] + forward[i];
      if (begins[i] < 0) begins[i] = 0;
      total *= (ends[i] - begins[i] + 1); 
      if (begins[i] < 0) begins[i] += lengths[i];
      if (ends[i] >= lengths[i]) ends[i] %= lengths[i];
    }

    int count = 0;

    Vector list = new Vector();

    int firstNdx = 0;
    for (int i=0; i<priorities.length; i++) {
      if (priorities[i] == HIGH_PRIORITY) {
        firstNdx = i;
        break;
      }      
    }

    while (count < total) {
      int[] tmp = new int[positions.length];
      for (int i=0; i<tmp.length; i++) tmp[i] = positions[i];
      list.add(tmp);
      positions = increment(positions, firstNdx);
      if (Arrays.equals(positions, pos)) {
        firstNdx = nextPriority(firstNdx, priorities);
      }
      if (firstNdx == -1) {
        count = total;
        break;
      }
      count++; 
    }

    return (int[][]) list.toArray(new int[0][0]);
  }

  // -- Helper methods --
   
  private int[] increment(int[] pos, int ndx) {
    if (ndx == -1) return pos; 
    if (forwardFirst) {
      pos[ndx]++;
      if (pos[ndx] == lengths[ndx]) pos[ndx] = 0; 
      if (pos[ndx] > ends[ndx]) {
        pos[ndx] = begins[ndx];
        return increment(pos, nextPriority(ndx, priorities));
      }
    }
    else {
      pos[ndx]--;
      if (pos[ndx] == -1) pos[ndx] = lengths[ndx] - 1; 
      if (pos[ndx] < begins[ndx]) {
        pos[ndx] = ends[ndx];
        return increment(pos, nextPriority(ndx, priorities));
      }
    }
    return pos; 
  }

}
