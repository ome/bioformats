//
// EditDistance.java
//

import java.io.*;

/** Computes minimum edit distance between two strings. */
public class EditDistance {

  public static void main(String[] args) throws IOException {
    String a = args.length > 0 ? args[0] : getString("First string? ");
    String b = args.length > 1 ? args[1] : getString("Second string? ");
    System.out.println("Distance = " + getEditDistance(a, b));
  }

  private static int getEditDistance(String s, String t) {
    // Leveshtein algorithm
    if (s == null) s = "";
    if (t == null) t = "";

    char[] a = s.toCharArray();
    char[] b = t.toCharArray();
    int[] w = new int[b.length + 1];

    int cur = 0, next = 0;

    for (int i=0; i<a.length; i++) {
      cur = i + 1;

      for (int j=0; j<b.length; j++) {
        next = min(
          w[j + 1] + 1,
          cur + 1,
          w[j] + (a[i] == b[j] ? 0 : 1)
        );

        w[j] = cur;
        cur = next;
      }

      w[b.length] = next;
    }

    return next;
  }

  private static int min(int a, int b, int c) {
    if (a < b && a < c) return a;
    return (b < c ? b : c);
  }

  private static String getString(String msg) throws IOException {
    System.out.print(msg);
    return new BufferedReader(new InputStreamReader(System.in)).readLine();
  }

}
