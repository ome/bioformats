//
// RandomPass.java
//

/** Generates a random 8-character password. */
public class RandomPass {

  private static final int PASS_LEN = 8;
  private static final char MIN_CHAR = '!';
  private static final char MAX_CHAR = '~';

  public static void main(String[] args) {
    int passLen = PASS_LEN;
    boolean alphaNum = false;
    for (String arg : args) {
      if (arg.equals("-nosymbols")) {
        alphaNum = true;
      }
      else {
        // assume argument is password character length
        passLen = Integer.parseInt(arg);
      }
    }
    StringBuffer sb = new StringBuffer(passLen + 2);
    int range = MAX_CHAR - MIN_CHAR + 1;
    int i = 0;
    while (i < passLen) {
      char c = (char) (range * Math.random() + MIN_CHAR);
      if (alphaNum) {
        boolean alpha = (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
        boolean num = c >= '0' && c <= '9';
        if (!alpha && !num) continue;
      }
      sb.append(c);
      i++;
    }
    System.out.println(sb);
  }

}
