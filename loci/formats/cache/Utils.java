// 
// Utils.java
//

package loci.formats.cache;

public class Utils {

  public static boolean equals(int[] one, int[] two) {
    if (one.length != two.length) return false;
    for (int i=0; i<one.length; i++) {
      if (one[i] != two[i]) return false;
    }
    return true; 
  }

  public static int nextPriority(int ndx, int[] priorities) {
    int basePriority = priorities[ndx];
    for (int i=0; i<priorities.length; i++) {
      if (priorities[i] >= basePriority && i != ndx) {
        if (!(priorities[i] == basePriority && i < ndx)) { 
          return i;
        } 
      }
    }
    return -1; 
  }

}
