//
// CrosshairStrategy.java
//

package loci.formats.cache;

import java.util.Arrays;
import java.util.Vector;

public class CrosshairStrategy extends CacheStrategy {

  // -- Constructor --

  public CrosshairStrategy(boolean forwardFirst, int[] lengths, int[] forward, 
    int[] backward) 
  {
    super(forwardFirst, lengths, forward, backward);
  }

  // -- ICacheStrategy API methods --

  /* @see loci.formats.cache.ICacheStrategy#getLoadList(int[]) */
  public int[][] getLoadList(int[] pos) throws CacheException {
    int[] positions = new int[pos.length];
    int[] begins = new int[pos.length];
    int[] ends = new int[pos.length];
    int total = 0; 
    for (int i=0; i<positions.length; i++) {
      positions[i] = pos[i];
      total += (forward[i] + backward[i]); 
      begins[i] = pos[i] - backward[i];
      ends[i] = pos[i] + forward[i];
    
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
      positions = 
        increment(positions, firstNdx, begins[firstNdx], ends[firstNdx]);
      if (Arrays.equals(positions, pos)) {
        firstNdx = nextPriority(firstNdx, priorities); 
      }

      count++;
    }

    return (int[][]) list.toArray(new int[0][0]);
  }

  // -- Helper methods --

  private int[] increment(int[] pos, int ndx, int begin, int end) {
    if (forwardFirst) {
      pos[ndx]++;
      if (pos[ndx] == end) {
        pos[ndx] = begin;
      }
      if (pos[ndx] == lengths[ndx]) pos[ndx] = 0;
    }
    else {
      pos[ndx]--;
      if (pos[ndx] == begin) {
        pos[ndx] = end;
      }
      if (pos[ndx] == -1) pos[ndx] = lengths[ndx] - 1; 
    }
    return pos; 
  }

}
