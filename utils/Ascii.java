// Ascii.java

/** Outputs the readable ASCII table from 32 to 127. */
public class Ascii {

  public static void main(String[] args) {
    for (int i=32; i<128; i++) {
      String s = "" + i;
      if (i < 100) s = " " + s;
      if (i < 10) s = " " + s;
      System.out.print(s + " " + (char) i + "\t");
      if (i % 10 == 1) System.out.println();
    }
    System.out.println();
  }

}
