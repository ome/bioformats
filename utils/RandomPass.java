//
// RandomPass.java
//

/** Generates a random 8-character password. */
public class RandomPass {

  private static final int PASS_LEN = 8;
  private static final char MIN_CHAR = '!';
  private static final char MAX_CHAR = '~';

  public static void main(String[] args) {
    StringBuffer sb = new StringBuffer(PASS_LEN + 2);
    int range = MAX_CHAR - MIN_CHAR + 1;
    for (int i=0; i<PASS_LEN; i++) {
      char c = (char) (range * Math.random() + MIN_CHAR);
      sb.append(c);
    }
    System.out.println(sb);
  }

}
