//
// Ascii.java
//

/**
 * Outputs the readable ASCII table from 32 to 127.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/utils/Ascii.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/utils/Ascii.java">SVN</a></dd></dl>
 */
public class Ascii {

  public static void main(String[] args) {
    int max = 255;
    if (args.length > 0) max = Integer.parseInt(args[0]);
    if (max > 99999) max = 99999; // cap at 5 digits
    int digits = 1;
    int q = max;
    while (q >= 10) {
      q /= 10;
      digits++;
    }
    for (int i=32; i<=max; i++) {
      String s = "" + i;
      while (s.length() < digits) s = " " + s;
      System.out.print(s + " " + (char) i + "\t");
      if (i % 10 == 1) System.out.println();
    }
    System.out.println();
  }

}
