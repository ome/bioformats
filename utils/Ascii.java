//
// Ascii.java
//

/** Outputs the readable ASCII table from 32 to 127. */
public class Ascii {

  public static void main(String[] args) {
    int max = 255;
    if (args.length > 0) max = Integer.parseInt(args[0]);
    for (int i=32; i<=max; i++) {
      String s = "" + i;
      if (i < 100) s = " " + s;
      if (i < 10) s = " " + s;
      System.out.print(s + " " + (char) i + "\t");
      if (i % 10 == 1) System.out.println();
    }
    System.out.println();
  }

}
